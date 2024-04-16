//Copyright 2021 Sean Murdock

package com.getsimplex.steptimer.model;

import io.netty.channel.Channel;
import org.eclipse.jetty.websocket.api.Session;

/**
 * Created by sean on 9/7/2016.
 */
public class DeviceInterestEnded {

    Session interestedSession;//when the socket closes we don't know what user was connected, but we know what session was connected

    Channel interestedChannel;//when the socket closes we don't know what user was connected, but we know what channel was connected


    public Session getInterestedSession() {
        return interestedSession;
    }

    public void setInterestedSession(Session interestedSession) {
        this.interestedSession = interestedSession;
    }

    public Channel getInterestedChannel() {
        return interestedChannel;
    }

    public void setInterestedChannel(Channel interestedChannel) {
        this.interestedChannel = interestedChannel;
    }
}
