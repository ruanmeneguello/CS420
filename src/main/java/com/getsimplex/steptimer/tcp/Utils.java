package com.getsimplex.steptimer.tcp;

import java.net.SocketAddress;

class Utils {

    static void log(String message){
        System.out.println(message);
    }

    static void log(SocketAddress socketAddress, String message){
        System.out.println("< " + socketAddress + " > : " + message);
    }


}