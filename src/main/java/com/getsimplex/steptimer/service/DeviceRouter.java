//Copyright 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.getsimplex.steptimer.com.getsimplex.steptimer.sensormessages.SensorDeserializer;
import com.getsimplex.steptimer.com.getsimplex.steptimer.sensormessages.SensorMessage;
import com.getsimplex.steptimer.model.DeviceInterest;
import com.getsimplex.steptimer.model.DeviceInterestEnded;
import com.getsimplex.steptimer.model.DeviceMessage;
import io.netty.channel.Channel;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by sean on 8/16/2016.
 */
public class DeviceRouter extends UntypedActor {
    private static Logger logger = Logger.getLogger(DeviceRouter.class.getName());
    private static HashMap<String, DeviceInterest> deviceRegistry = new HashMap<>();//this is for the websocket to show the user the activity for the unique device
    private static HashMap<Channel, DeviceInterest> channelRegistry = new HashMap<>();//if a channel is disconnected, we can find the device to remove it from the list
    private static HashMap<Session, DeviceInterest> sessionRegistry = new HashMap<>();//if a session is disconnected, we can find the device to remove it from the list


    public void onReceive(Object object){
        if (object instanceof DeviceMessage) {
            DeviceMessage deviceMessage = (DeviceMessage) object;		
            logger.info("DeviceRouter received payload: "+deviceMessage.getMessage()+" with timestamp: "+deviceMessage.getDate());
            try {
                if (deviceRegistry.containsKey(deviceMessage.getDeviceId())){
                    if (deviceRegistry.get(deviceMessage.getDeviceId()).getInterestedSession()!=null) {
                        deviceRegistry.get(deviceMessage.getDeviceId()).getInterestedSession().getRemote().sendString(deviceMessage.getMessage());//web socket
                    } else if (deviceRegistry.get(deviceMessage.getDeviceId()).getInterestedChannel()!=null){
                        deviceRegistry.get((deviceMessage.getDeviceId())).getInterestedChannel().writeAndFlush(deviceMessage.getMessage());//tcp socket
                    } else{
                        logger.severe("Unable to find a channel or a session for device ID: "+deviceMessage.getDeviceId());
                    }
                }
            } catch (Exception e){
                logger.severe("Unable to transmit to socket message: "+deviceMessage.getMessage()+ "due to: "+e.getMessage());
            }

        } else if (object instanceof DeviceInterest){

            //TO-DO: if a user tries to start a session with a device that is already in use, we need to prevent that from happening
            // so we need to block their request and give some kind of message that the device is already in use, unless it is the same user
            // or if the session has been cancelled or has completed

            DeviceInterest deviceInterest = (DeviceInterest) object;
            if(!deviceRegistry.containsKey(deviceInterest.getDeviceId())){
                deviceRegistry.put(deviceInterest.getDeviceId(),deviceInterest);
            } else {
                DeviceInterest existingDeviceInterest = deviceRegistry.get(deviceInterest.getDeviceId());
                //Websocket
                if(existingDeviceInterest.getInterestedSession()!=null && existingDeviceInterest.getInterestedSession().isOpen()){
                    existingDeviceInterest.getInterestedSession().close();//we are moving to a different subscriber
                }
                //TCP Socket
                else if (deviceInterest.getInterestedChannel()!=null && existingDeviceInterest.getInterestedChannel().isActive()){
                    existingDeviceInterest.getInterestedChannel().close();//we are moving to a different subscriber
                }
                logger.info("Device: "+deviceInterest.getDeviceId()+" was already being monitored on a socket.");
                logger.info("Moving interest in Device: "+deviceInterest.getDeviceId()+" to a new socket  as per latest request.");
                deviceRegistry.put(deviceInterest.getDeviceId(),deviceInterest);
            }
        } else if (object instanceof DeviceInterestEnded){
            DeviceInterestEnded deviceInterestEnded = (DeviceInterestEnded) object;
            if (deviceInterestEnded.getInterestedChannel()!=null && channelRegistry.containsKey(deviceInterestEnded.getInterestedChannel())){
                DeviceInterest deviceInterest = channelRegistry.get(deviceInterestEnded.getInterestedChannel());//device Id could be an email or another identifier
                channelRegistry.remove(deviceInterestEnded.getInterestedChannel());//lookup device by channel

                deviceRegistry.remove(deviceInterest.getDeviceId());//remove from further notifications

            } else if (deviceInterestEnded.getInterestedSession()!=null && sessionRegistry.containsKey(deviceInterestEnded.getInterestedSession())){
                DeviceInterest deviceInterest = sessionRegistry.get(deviceInterestEnded.getInterestedSession());

                sessionRegistry.remove(deviceInterestEnded.getInterestedSession());//lookup device by session

                deviceRegistry.remove(deviceInterest.getDeviceId());//remove from further notifications
            }
        }


    }

}
