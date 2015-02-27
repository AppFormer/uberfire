package org.uberfire.client.type;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

public class JSClientResourceType implements ClientResourceType {

    private final JSNativeClientResourceType nativeClientResourceType;

    public JSClientResourceType( final JSNativeClientResourceType nativeClientResourceType ) {
        this.nativeClientResourceType = nativeClientResourceType;
    }

    @Override
    public String getShortName() {
        return nativeClientResourceType.getShortName();
    }

    @Override
    public String getDescription() {
        return nativeClientResourceType.getDescription();
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "";
    }

    @Override
    public int getPriority() {
        return nativeClientResourceType.getPriority();
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "";
    }

    @Override
    public boolean accept( Path path ) {
            RegExp r = RegExp.compile( nativeClientResourceType.getAcceptRegex() );
            return r.test( path.toURI() );
    }

    @Override
    public IsWidget getIcon() {
        String iconCSSClass = nativeClientResourceType.getIconClass();
        FlowPanel panel = new FlowPanel();
        panel.addStyleName( iconCSSClass );
        return panel;
    }

}
