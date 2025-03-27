//Copyright 2021 Sean Murdock

package com.getsimplex.steptimer.service;

import com.getsimplex.steptimer.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sean on 8/10/2016.
 */
@WebSocket()
public class DeviceWebSocketHandler {

    private static Gson gson = new Gson();
    private static Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
    public static String TOKEN_KEY= "userToken";
    private static Logger logger = Logger.getLogger(DeviceWebSocketHandler.class.getName());

    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        // Extract the deviceId from the query parameters
        String query = session.getUpgradeRequest().getQueryString();
        String deviceId = "defaultDeviceId"; // Default deviceId if none is provided

        if (query != null && query.contains("deviceId=")) {
            deviceId = query.split("deviceId=")[1].split("&")[0]; // Extract deviceId from query string
        }

        Map headers = session.getUpgradeRequest().getHeaders();
        // Retrieve the suresteps.session.token header
        String token = session.getUpgradeRequest().getHeader("suresteps.session.token");

        if (token == null || token.isEmpty()) {
            logger.log(Level.WARNING, "Missing suresteps.session.token header. Closing session.");
            session.close();
            return;
        }

        // Validate the token and retrieve the authenticated user

        if (SessionValidator.tokenIsExpired(token)) {
            logger.log(Level.WARNING, "Invalid or expired token. Closing session.");
            session.close();
            return;
        }

        String authenticatedUser = SessionValidator.emailFromToken(token);
        if(authenticatedUser!=null & !authenticatedUser.isEmpty()) {
            // Set up the DeviceInterest object
            DeviceInterest deviceInterest = new DeviceInterest();
            deviceInterest.setDeviceId(deviceId); // Use the extracted deviceId
            deviceInterest.setInterestedSession(session);
            deviceInterest.setInterestedUser(authenticatedUser); // Set the interested user based on the authenticated user

            // Route messages for the specified deviceId to this user's WebSocket
            MessageIntake.route(deviceInterest);

            logger.log(Level.INFO, "WebSocket connection established for user: " + authenticatedUser + " and deviceId: " + deviceId);
        } else {
            logger.log(Level.WARNING, "Unable to retrieve authenticated user. Closing session.");
            session.close();
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int code, String message){
//        String shortMessage = "StopReading~";
//        SessionMessageResponse sessionMessage = new SessionMessageResponse();
//        sessionMessage.message=shortMessage;
//        sessionMessage.session=session;
//        ValidationResponse validationResponse = new ValidationResponse();
//        validationResponse.setOriginType(MessageSourceTypes.SERVICE);
//        sessionMessage.validationResponse=validationResponse;
//        MessageIntake.route(sessionMessage);

        DeviceInterestEnded deviceInterestEnded = new DeviceInterestEnded();
//        deviceInterestEnded.setDeviceId("1234");//this is just for demonstration purposes, we can change this to an actual device ID
        deviceInterestEnded.setInterestedSession(session);
//        deviceInterestEnded.setInterestedUser("clinicmanager");
        MessageIntake.route(deviceInterestEnded);// this should prevent trying to send updates to a closed socket
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception{
        if ("StartDemo".equals(message)){ //Demo mode
            session.getRemote().sendString("startTimer");
            for (int i=1;i<=30;i++) {

                Thread.sleep(1000);//sleep for a second

                session.getRemote().sendString("stepCount:"+i);
                System.out.println("Step count: "+i);
            }
        } else if (message.contains("StartReading~")){
            SessionMessageResponse sessionMessage = new SessionMessageResponse();
            sessionMessage.message=message;
            sessionMessage.session=session;
            ValidationResponse validationResponse = new ValidationResponse();
            validationResponse.setOriginType(MessageSourceTypes.BROWSER);
            sessionMessage.validationResponse=validationResponse;
            MessageIntake.route(sessionMessage);
        }
        else {
            Gson gson = new Gson();

            Map<String, String> jsonProps = gson.fromJson(message, stringStringMap);
            if (jsonProps.containsKey(TOKEN_KEY)) {
                String token = jsonProps.get(TOKEN_KEY);
                try {//if they have an invalid (not trusted) token or they have an expired token, close the session
                    //ValidationResponse validationResponse = SessionValidator.validateSession(token, session);
                    ValidationResponse validationResponse = new ValidationResponse();
                    validationResponse.setExpired(false);
                    validationResponse.setTrusted(true);
                    if (validationResponse.getTrusted() && !validationResponse.getExpired()) {
                        if (!SessionValidator.sessionTokens.containsKey(session)) {//java cache from Redis
                            SessionValidator.sessionTokens.put(session, token);
                        }

                        SessionMessageResponse sessionMessage = new SessionMessageResponse();
                        sessionMessage.message = message;
                        sessionMessage.session = session;

                        if (jsonProps.get("interestedUser")!=null && !jsonProps.get("interestedUser").isEmpty()){
                            sessionMessage.messageType=MessageSourceTypes.DEVICE;
                        }

                        MessageIntake.route(sessionMessage);

                    } else if (!validationResponse.getTrusted() || validationResponse.getExpired()) {
                        if (SessionValidator.sessionTokens.containsKey(session)) {
                            SessionValidator.sessionTokens.remove(session);
                            session.close();
                        }
                    }

                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    session.close();
                }
            } else {
                session.close();
            }
        }

    }

}
