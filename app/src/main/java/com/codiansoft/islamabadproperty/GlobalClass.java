package com.codiansoft.islamabadproperty;

import android.content.Context;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by CodianSoft on 16/02/2018.
 */

public class GlobalClass {
    public static PropertyModel selected_property;

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

}
