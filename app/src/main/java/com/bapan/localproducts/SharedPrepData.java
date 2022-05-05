package com.bapan.localproducts;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrepData {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    public SharedPrepData(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("myData",context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void setID(String id){
        editor.putString("idKey",id);
        editor.apply();
    }
    public String getId(){
        return sp.getString("idKey","none");
    }
    
    public void setLastButtonId(int id){
        editor.putInt("idButton",id);
        editor.apply();
    }
    public int getLsatButtonId(){
        return sp.getInt("idButton",R.id.textviewMarket);
    }
    public void setPlacecode(String placecode){
        editor.putString("placecodeKey",placecode);
        editor.apply();
    }
    public String getPlacecode(){
        return sp.getString("placecodeKey","none");
    }

    public void setPhoneNumber(String number){
        editor.putString("phonenumber",number);
        editor.apply();
    }
    public String getPhoneNumber(){
        return sp.getString("phonenumber","");
    }
}
