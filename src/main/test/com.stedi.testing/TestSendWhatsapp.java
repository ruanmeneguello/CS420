package com.stedi.testing;

import com.getsimplex.steptimer.utils.SendWhatsApp;

public class TestSendWhatsapp {
    private static String CONTENT_SID= "";

    static{
        CONTENT_SID = System.getenv("TWILIO_SECTOR_MESSAGE_SID");
    }

    public static void main (String[] args) throws Exception{

        SendWhatsApp.send("8017190908",  CONTENT_SID,"9");
    }

}
