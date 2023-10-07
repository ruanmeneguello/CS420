//© 2021 Sean Murdock

package com.getsimplex.steptimer.model;

import org.eclipse.jetty.websocket.api.Session;

import io.netty.channel.Channel;

/**
 * Created by sean on 9/7/2016.
 */
public class DeviceInterest {

    Session interestedSession;

    Channel interestedChannel;
    String interestedUser;
    String deviceId;



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

    public String getInterestedUser() {
        return interestedUser;
    }

    public void setInterestedUser(String interestedUser) {
        this.interestedUser = interestedUser;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
