package org.uberfire.java.nio.fs.jgit.ws.jms;

import java.io.Serializable;

import org.uberfire.java.nio.file.WatchEvent;

public class MessageWrapper implements Serializable {

    private final String nodeId;
    private final WatchEvent watchEvent;

    public MessageWrapper(String nodeId,
                          WatchEvent watchEvent) {

        this.nodeId = nodeId;
        this.watchEvent = watchEvent;
    }

    public String getNodeId() {
        return nodeId;
    }

    public WatchEvent getWatchEvent() {
        return watchEvent;
    }
}