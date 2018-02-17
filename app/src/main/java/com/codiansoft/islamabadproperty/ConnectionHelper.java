package com.codiansoft.islamabadproperty;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CodianSoft on 17/02/2018.
 */

public class ConnectionHelper {

    private Context context;

    public ConnectionHelper(Context context) {
        this.context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            //Toast.makeText(context, "Oops ! Connect your Internet", Toast.LENGTH_LONG).show();
            return false;
        }

    }
}

