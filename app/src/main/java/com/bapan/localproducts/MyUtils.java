package com.bapan.localproducts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MyUtils {
Context context;
TextView currentbutton;
    //public static String BASE_URL = "http://192.168.43.180/test/";
    public static String BASE_URL = "https://s-karts.000webhostapp.com/";

    public MyUtils(Context context, TextView currentbutton) {
        this.context = context;
        this.currentbutton = currentbutton;
    }

    int currentbuttonid = R.id.textviewMarket;
    public void changeButtonColor(TextView tv, int col1, int col2){
    currentbutton.setTextColor(col2);
    tv.setTextColor(col1);
    currentbutton = tv;
    }
    public static String validateProductData(String image,String title,String price,String details){
        String errormessage;
    if(image.length() < 10){
        errormessage = "Product image is not chosen.";
        }else if(title.length() < 1){
        errormessage = "Enter product title";
    }else if(Integer.parseInt(price) < 1){
        errormessage = "Enter proper price";
    }else if(details.length() < 1){
        errormessage = "Enter proper product details.";
        }else {
        errormessage = "false";
    }
        
    
    return errormessage;
    }
    public static String validateUpdateData(){
        String errormessage = "false";
        return errormessage;
    }
    public static String getTimeDuration(int timeDuration){
    int days,hours,minutes;
    if(timeDuration >= 86400){
        days = timeDuration/86400;
        hours = (timeDuration - (days*86400))/3600;
        return days+"d "+hours+"h";
    }else if(timeDuration >= 3600){
        hours = timeDuration/3600;
        minutes = (timeDuration - (hours*3600))/60;
        return hours+"h "+minutes+"m";
    }else {
        minutes = timeDuration/60;
        return minutes+"m";
    }
    }
    public static boolean isInternetConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else {
            return false;
        }
    }
    
}
