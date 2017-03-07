/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import org.slf4j.Logger;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * Searches for runtime plugins and frameworks by parsing the directory listing at <tt>${appBase}/plugins/</tt> or
 * <tt>${appBase}/plugins/</tt> respectively. Any {@code <a>} tag with an href whose URI ends in <tt>.js</tt> will
 * qualify as a runtime plugin or framework, and its contents will be fetched with a separate request. Normally, this
 * will be compatible with any web server's built-in directory listing feature. If it isn't, just manually add an
 * <tt>index.html</tt> file that has links to the .js files you care about.
 */
@Dependent
@Alternative
public class RuntimePluginsServiceProxyClientImpl implements RuntimePluginsServiceProxy {

    @Inject
    private Logger logger;

    @Override
    public void getTemplateContent(final String contentUrl,
                                   final ParameterizedCommand<String> command) {
        RequestBuilder contentRb = new RequestBuilder(RequestBuilder.GET,
                                                      "plugins/" + contentUrl);
        try {
            contentRb.sendRequest(null,
                                  new RequestCallback() {

                                      @Override
                                      public void onResponseReceived(Request request,
                                                                     Response response) {
                                          command.execute(response.getText());
                                      }

                                      @Override
                                      public void onError(Request request,
                                                          Throwable ex) {
                                          logger.warn("Error in template content request for " + contentUrl,
                                                      ex);
                                          command.execute(null);
                                      }
                                  });
        } catch (RequestException ex) {
            logger.warn("Couldn't load template content at " + contentUrl,
                        ex);
            command.execute(null);
        }
    }

    @Override
    public void listFrameworksContent(ParameterizedCommand<Collection<String>> command) {
        findAndFetchContent("frameworks/",
                            ".js",
                            "framework",
                            command);
    }

    @Override
    public void listPluginsContent(final ParameterizedCommand<Collection<String>> command) {
        findAndFetchContent("plugins/",
                            ".js",
                            "plugin",
                            command);
    }

    private void findAndFetchContent(final String relativeUri,
                                     final String filenameExtension,
                                     final String fileType,
                                     final ParameterizedCommand<Collection<String>> doWhenFinished) {
        RequestBuilder rb = new RequestBuilder(RequestBuilder.GET,
                                               relativeUri);
        try {
            rb.sendRequest(null,
                           new RequestCallback() {

                               @Override
                               public void onResponseReceived(Request request,
                                                              Response response) {
                                   final List<String> pluginUrls = new ArrayList<String>();
                                   final List<String> pluginContents = new ArrayList<String>();
                                   final int[] contentErrors = new int[1];

                                   Element detachedDiv = DOM.createDiv();
                                   detachedDiv.setInnerHTML(response.getText());
                                   NodeList<com.google.gwt.dom.client.Element> links = detachedDiv.getElementsByTagName("a");
                                   for (int i = 0; i < links.getLength(); i++) {
                                       AnchorElement aElem = (AnchorElement) links.getItem(i);

                                       // DOM spec says these are already absolutized for us. Nice!
                                       String href = aElem.getHref();

                                       if (href.endsWith(filenameExtension)) {
                                           pluginUrls.add(href);
                                       }
                                   }

                                   if (pluginUrls.size() == 0) {
                                       doWhenFinished.execute(Collections.<String>emptyList());
                                       return;
                                   }

                                   for (final String href : pluginUrls) {
                                       RequestBuilder contentRb = new RequestBuilder(RequestBuilder.GET,
                                                                                     href);
                                       try {
                                           contentRb.sendRequest(null,
                                                                 new RequestCallback() {

                                                                     @Override
                                                                     public void onResponseReceived(Request request,
                                                                                                    Response response) {
                                                                         if (response.getStatusCode() == 200) {
                                                                             pluginContents.add(response.getText());
                                                                         } else {
                                                                             contentErrors[0]++;
                                                                         }
                                                                         if (pluginContents.size() >= pluginUrls.size() + contentErrors[0]) {
                                                                             doWhenFinished.execute(pluginContents);
                                                                         }
                                                                     }

                                                                     @Override
                                                                     public void onError(Request request,
                                                                                         Throwable exception) {
                                                                         logger.warn("Error in " + fileType + " content request for " + href);
                                                                         contentErrors[0]++;
                                                                         if (pluginContents.size() >= pluginUrls.size() + contentErrors[0]) {
                                                                             doWhenFinished.execute(pluginContents);
                                                                         }
                                                                     }
                                                                 });
                                       } catch (RequestException ex) {
                                           logger.warn("Failed to send request for " + fileType + " " + href,
                                                       ex);
                                           contentErrors[0]++;
                                           if (pluginContents.size() >= pluginUrls.size() + contentErrors[0]) {
                                               doWhenFinished.execute(pluginContents);
                                           }
                                       }
                                   }
                               }

                               @Override
                               public void onError(Request request,
                                                   Throwable ex) {
                                   logger.warn("Error in " + fileType + " list request. Not loading JS " + fileType + "s.",
                                               ex);
                                   doWhenFinished.execute(Collections.<String>emptyList());
                               }
                           });
        } catch (RequestException ex) {
            logger.warn("Couldn't load JS " + fileType + ". Continuing without runtime " + fileType + "s.",
                        ex);
            doWhenFinished.execute(Collections.<String>emptyList());
        }
    }
}
