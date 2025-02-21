//Copyright 2021 Sean Murdock

package com.getsimplex.steptimer.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SendWhatsApp {
    private static String ACCOUNT_SID ="";
    private static String AUTH_TOKEN = "";
    private static String TWILIO_PHONE= "";


    static {

        ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
        AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
        TWILIO_PHONE = System.getenv("TWILIO_PHONE");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void send(String destinationPhone, String contentSid, String templateVariable) throws Exception{

        String formattedPhone = getFormattedPhone(destinationPhone);
        formattedPhone = formattedPhone.replaceAll("-","");

      //  Message message =Message.creator(destination, origin, text).create();
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:"+formattedPhone),//to
                        new com.twilio.type.PhoneNumber("whatsapp:+17146778438"),//from
                        "")
                .setContentVariables("{\"1\":\""+templateVariable+"\"}")//'You have received an alert from a user in sector '+sector+'. Please respond to the customer.
                .setContentSid(contentSid)//this is the id of the template that was approved by Whatsapp
                .setMessagingServiceSid("MG3fb3149d7c324e90ef40fe08fe6ffb96")//this is the Service ID for the WhatsApp Service
                .create();

        System.out.println("**** Sent Text: ID "+message.getSid());

    }

    public static String getFormattedPhone(String inputPhone) throws Exception{
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(inputPhone, "CG");
        String formattedPhone = phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        formattedPhone = formattedPhone.replace(" ","");
        return formattedPhone;
    }

}
