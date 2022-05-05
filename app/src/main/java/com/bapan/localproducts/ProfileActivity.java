package com.bapan.localproducts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bapan.localproducts.models.LoginResponse;
import com.bapan.localproducts.models.OtpResponse;
import com.bapan.localproducts.models.OtpVerifyResponse;
import com.bapan.localproducts.models.PlaceNamesResponse;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    TextView sendOtpButton, messagebox,textView_profile_photo;
    EditText phone,otp,name,pin;
    Button login;
    String id="";
    String errormsg;
    String networkerrormsg = "Network connection error .";
    Spinner spinner;
    SharedPrepData sharedPrepData;
    CircleImageView circleimageviewProfile;
    private static final int PICK_IMAGE = 2;
    String encodedProfileImage = "null";
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Initialize Views
        phone = findViewById(R.id.editTextPhone);
        sendOtpButton = findViewById(R.id.sendOtpButton);
        otp = findViewById(R.id.editTextOtp);
        name = findViewById(R.id.editTextName);
        pin = findViewById(R.id.editTextPin);
        login = findViewById(R.id.buttonLogin);
        messagebox = findViewById(R.id.textViewWarning);
        textView_profile_photo = findViewById(R.id.textView3);
        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter(ProfileActivity.this,R.layout.spinner_item,new ArrayList<String>(Arrays.asList("Select Place -"))));
        circleimageviewProfile = findViewById(R.id.circleimageviewProfile);
        
//        Initialize other variables
        errormsg = "Failed .";
        
//        Listeners
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 10){
                    setSendOtpEnabled(true);
                }else {
                    setSendOtpEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() == 6){
                    otp.clearFocus();
//                    name.setEnabled(false);
//                    pin.setEnabled(false);
                    verifyOtp(phone.getText().toString(),otp.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(s.toString().length() == 6){
                        getNames(s.toString());
                    }else {
                        spinner.setAdapter(null);
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        login.setOnClickListener(this);
        circleimageviewProfile.setOnClickListener(this::onClick);
        textView_profile_photo.setOnClickListener(this::onClick);
        // Common
        phone.requestFocus();
        sharedPrepData = new SharedPrepData(this);
        if(!getIntent().getBooleanExtra("type",true)){
            phone.setText(sharedPrepData.getPhoneNumber());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendOtpButton :
               phone.clearFocus();
               otp.setText("");
               if(isInternetConnection()){
                   otp.requestFocus();
                   sendotp(phone.getText().toString());
               }else {
                   setMessage("2",networkerrormsg);
               }
            break;
            case R.id.buttonLogin:
                if(isInternetConnection()){
                    startLogin(id,name.getText().toString(),pin.getText().toString(),spinner.getSelectedItemPosition());
                    
                }else {
                    setMessage("2",networkerrormsg);
                }
            break;
            case (R.id.circleimageviewProfile):
            case (R.id.textView3):
                ImagePicker.with(this)
                        .cropSquare()
                        .compress(128)
                        .maxResultSize(480, 480)
                        .start(PICK_IMAGE);
            break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            Uri path = data.getData();
            if(path != null){
                try {
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    //convert to string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    byte[] imageInbyte = byteArrayOutputStream.toByteArray();
                    encodedProfileImage = Base64.encodeToString(imageInbyte,Base64.DEFAULT);
                    //set in imageview
                    circleimageviewProfile.setImageBitmap(bitmap);
                    textView_profile_photo.setText("Change Profile Photo");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*api calls*/
            //sendotp api call
    private void sendotp(String number) {
        setSendOtpEnabled(false);
        CountDownTimer cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
            sendOtpButton.setText("Wait  "+Math.round(millisUntilFinished/1000)+" s");
            }
            public void onFinish() {
            sendOtpButton.setText("Send OTP");
                setSendOtpEnabled(true);
                
            }
        };
        cTimer.start();
        /*setSendOtpEnabled(false);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSendOtpEnabled(true);
            }
        }, 15000);*/
        Call<OtpResponse> call = RetrofitClient.getInstance().getApi().sendOtp(number);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if(response.isSuccessful()){
                    setMessage(response.body().getError(),response.body().getMessage());
                }else {
                    setMessage("1",errormsg);
                }
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                /*if (t instanceof IOException) {
                    setMessage("2","No connection.");
                }
                else {
                    setMessage("1",errormsg);
                }*/
            }
        });
    }
            //verifyOtp api call
    private void verifyOtp(String number,String otpNum){
        Call<OtpVerifyResponse> call = RetrofitClient.getInstance().getApi().verifyOtp(number,otpNum);
        call.enqueue(new Callback<OtpVerifyResponse>() {
            @Override
            public void onResponse(Call<OtpVerifyResponse> call, Response<OtpVerifyResponse> response) {
                
                if(response.isSuccessful()){
                    setMessage(response.body().getError(),response.body().getMessage());
                    if(response.body().getError().equals("0")){
                        id = response.body().getId();
                        name.setText(response.body().getName());
                        pin.setText(response.body().getPin());
                        if(response.body().getImage() != null){
                            Glide.with(ProfileActivity.this)
                                    .load(MyUtils.BASE_URL+response.body().getImage())
                                    .into(circleimageviewProfile);
                        }
                        //phone.setEnabled(false);
                        //otp.setEnabled(false);
                        name.setEnabled(true);
                        pin.setEnabled(true);
                        pin.requestFocus();
                        //session started
                        sharedPrepData.setID(response.body().getId());
                        sharedPrepData.setPhoneNumber(number);
                    }
                }else {
                    setMessage("1",errormsg);
                }
            }

            @Override
            public void onFailure(Call<OtpVerifyResponse> call, Throwable t) {
                if (t instanceof IOException) {
                    setMessage("2","Network connection error .");
                    otp.setText("");
                }
                else {
                    setMessage("1",errormsg);
                }
            }
        });
    }
            //login api call
    private void startLogin(String id,String name,String pin,int spinnerposition){
    
                if(id.equals("")){
                    setMessage("1",errormsg);
                }else if(name.length() == 0){
                    setMessage("1","Please enter your name .");
                }else if(pin.length() != 6) {
                    setMessage("1", "Pin code length must be 6-digits long.");
                }else if(spinnerposition <= 0){
                    setMessage("1", "Please select your place .");
                }else {
                    login.setEnabled(false);
                    Call<LoginResponse> call = RetrofitClient.getInstance().getApi().login(id,name,pin,spinnerposition-1,encodedProfileImage);
                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            setMessage(response.body().getError(),response.body().getMessage());
                            if(response.isSuccessful()){
                                if(response.body().getError().equals("0")){
                                    sharedPrepData.setPlacecode(response.body().getPlacecode());
                                    startActivity(new Intent(ProfileActivity.this,MainActivity.class));
                                    if(getIntent().getBooleanExtra("type",false)){
                                        finish();
                                    }else {
                                        finishAffinity();
                                    }
                                }
                            }else {
                                login.setEnabled(true);
                                setMessage("1",errormsg);
                            }
                            
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            if (t instanceof IOException) {
                                setMessage("2","Network connection error .");
                            }
                            else {
                                setMessage("1",errormsg);
                            }
                            login.setEnabled(true);
                        }
                    });
                }
    }
    
    private void getNames(String pincode){
        Call<PlaceNamesResponse> call = RetrofitClient.getInstance().getApi().getPlaceNames(pincode);
        call.enqueue(new Callback<PlaceNamesResponse>() {
            @Override
            public void onResponse(Call<PlaceNamesResponse> call, Response<PlaceNamesResponse> response) {
            if(response.isSuccessful()){
                List<String> list = response.body().getNames();
                list.add(0,"Select Place -");
                ArrayAdapter aa = new ArrayAdapter(ProfileActivity.this,R.layout.spinner_item,list);
                //Setting the ArrayAdapter data on the Spinner  
                spinner.setAdapter(aa); 
            }else {
                setMessage("1",errormsg);
            }
            }

            @Override
            public void onFailure(Call<PlaceNamesResponse> call, Throwable t) {
                if (t instanceof IOException) {
                    setMessage("2","Something went wrong.");
                    pin.setText("");
                }
                else {
                    setMessage("1",errormsg);
                }
            }
        });
    }

    private void setSendOtpEnabled(Boolean enable) {
        if(enable){
            sendOtpButton.setTextColor(getResources().getColor(R.color.app_low));
            sendOtpButton.setBackground(getResources().getDrawable(R.drawable.rounded_bg_1));
            sendOtpButton.setOnClickListener(this);
        }else {
            sendOtpButton.setTextColor(getResources().getColor(R.color.dark_extra_low));
            sendOtpButton.setBackground(getResources().getDrawable(R.drawable.rounded_bg_1_disabled));
            sendOtpButton.setOnClickListener(null);
        }
    }
    private void setMessage(String errorcode,String message){
        if(errorcode.equals("0")){
            messagebox.setTextColor(getResources().getColor(R.color.app_green));
            
        }else {
            messagebox.setTextColor(Color.RED);
        }
        messagebox.setText(message);
    }
    public boolean isInternetConnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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