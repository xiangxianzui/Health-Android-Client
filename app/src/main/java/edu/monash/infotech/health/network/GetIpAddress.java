package edu.monash.infotech.health.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 4/15/2016.
 */
public class GetIpAddress {
    String ip = "";

    public String getIP(){
        InetAddress address;
        try{
            address = InetAddress.getLocalHost();
            ip = address.getHostAddress().toString();
        } catch (UnknownHostException e){
            e.printStackTrace();
            ip = "172.16.120.42";
        }
        return ip;
    }
}
