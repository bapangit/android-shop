package com.bapan.localproducts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bapan.localproducts.models.FetchProductResponse;
import com.bapan.localproducts.models.CommonResponse;
import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditorItemActivity extends AppCompatActivity {
    ImageView productimage;
    private static final int PICK_IMAGE = 1;
    TextInputEditText title,price,details;
    Bitmap bitmap;
    String timestamp;
    String encodedproductimage = "false";
    TextView save,delete,pagetitle;
    CheckBox demand,publish;
    private String id;
    private LinearLayout exit;
    private ProgressBar progressbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_item);
        //initialize views
        productimage = findViewById(R.id.imageViewProductImage);
        title = findViewById(R.id.edittexttitle);
        price = findViewById(R.id.edittextprice);
        details = findViewById(R.id.edittextdetails);
        save = findViewById(R.id.textviewsave);
        demand = findViewById(R.id.checkboxdemand);
        publish = findViewById(R.id.checkboxpublish);
        delete = findViewById(R.id.textviewDelete);
        pagetitle = findViewById(R.id.textViewPageTitle);
        exit = findViewById(R.id.linearlayoutExit);
        progressbar = findViewById(R.id.progressbarupload);
        //initialize listeners
        productimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(EditorItemActivity.this)
                        .crop(3f,2f)
                        .compress(256)
                        .maxResultSize(720, 480)
                        .start(PICK_IMAGE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timestamp.length() == 10){
                    update(id,timestamp,encodedproductimage,title.getText().toString(),price.getText().toString(),details.getText().toString(),demand.isChecked()?"true":"false",publish.isChecked()?"true":"false");
                }else{
                    save(
                    id,
                    getIntent().getStringExtra("placecode"),
                    title.getText().toString(),
                    price.getText().toString(),
                    details.getText().toString(),
                    demand.isChecked(),
                    publish.isChecked()
                    );
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<CommonResponse> call = RetrofitClient.getInstance().getApi().deleteProduct(id,timestamp);
                call.enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        Toast.makeText(EditorItemActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        if(response.body().getError().equals("0")){
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        Toast.makeText(EditorItemActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //common
        id = new SharedPrepData(this).getId();
        timestamp = getIntent().getStringExtra("timestamp");
        if(timestamp.length() == 10){
            fetchProduct();
            delete.setVisibility(View.VISIBLE);
            pagetitle.setText("Update Product");
        }else {
            pagetitle.setText("Create Product");
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
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    //convert to string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                    byte[] imageInbyte = byteArrayOutputStream.toByteArray();
                    encodedproductimage = Base64.encodeToString(imageInbyte,Base64.DEFAULT);
                    //set in imageview
                    productimage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void save(String id,String placecode,String title,String price,String details,boolean demand,boolean publish) {
    if(price.equals("")){
        price = "0";
    }
        String error = MyUtils.validateProductData(encodedproductimage,title,price,details);
        if(error.equals("false")){
            progressbar.setVisibility(View.VISIBLE);
            Call<CommonResponse> call = RetrofitClient.getInstance().getApi().createProduct(
            id,
            encodedproductimage,
            title,
            Integer.parseInt(price)+"",
            details,
            demand?"true":"false",
            publish?"true":"false"
            );
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    Toast.makeText(EditorItemActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if(response.body().getError().equals("0")){
                        finish();
                    }
                    if(!response.isSuccessful()){
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    Toast.makeText(EditorItemActivity.this, "failed"+t.getMessage(), Toast.LENGTH_LONG).show();
                    progressbar.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            Toast.makeText(this,error, Toast.LENGTH_SHORT).show();
        }
    }
    private void update(String id,String timestamp,String encodedproductimage,String title,String price,String details,String demandtype,String publish){
    String errormessage = MyUtils.validateUpdateData();
        if(errormessage.equals("false")){
            progressbar.setVisibility(View.VISIBLE);
            Call<CommonResponse> call = RetrofitClient.getInstance().getApi().updateProduct(id,timestamp,encodedproductimage,title,price,details,demandtype,publish);
            call.enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    Toast.makeText(EditorItemActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                    if(!response.isSuccessful()){
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    if (t instanceof IOException) {
                        Toast.makeText(EditorItemActivity.this, "No Connection.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditorItemActivity.this, "Something Went Wrong.", Toast.LENGTH_SHORT).show();
                    }
                    progressbar.setVisibility(View.INVISIBLE);
                }
            });
        }else {
            Toast.makeText(this,errormessage, Toast.LENGTH_SHORT).show();
        }
    }
    private void fetchProduct(){
    String[] data = getIntent().getStringArrayExtra("data");
        Glide.with(EditorItemActivity.this)
                .load(MyUtils.BASE_URL+data[0])
                .into(productimage);
        title.setText(data[1]);
        price.setText(data[2]);
        details.setText(data[3]);
        demand.setChecked(!data[4].equals("0"));
        publish.setChecked(!data[5].equals("0"));
    }

    public void cancel(View view) {
        finish();
    }
}