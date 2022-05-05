package com.bapan.localproducts;

import android.app.Application;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bapan.localproducts.models.Product;
import com.bapan.localproducts.models.MyProductsResponse;
import com.bapan.localproducts.models.ProfileResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewModel extends AndroidViewModel {
    private MutableLiveData<ProfileResponse> profile_response;
    public MutableLiveData<ProfileResponse> getProfileResponse(String id){
    loadProfileResponse(id);
    if(profile_response == null){
        profile_response = new MutableLiveData<ProfileResponse>();
    }
        return profile_response;
    }
    public void loadProfileResponse(String id){
        Call<ProfileResponse> call = RetrofitClient.getInstance().getApi().getProfile(id);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                profile_response.postValue(response.body());
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
            }
        });
    }


    private MutableLiveData<Integer> option;
    public MutableLiveData<Integer> getOption(){
        if(option == null){
            option = new MutableLiveData<Integer>();
        }
        return option;
    }
    
    private MutableLiveData<List<Product>> mySupplies;
    public MutableLiveData<List<Product>> getMySupplies(String id){
    loadMySupplies(id);
        if(mySupplies == null){
            mySupplies = new MutableLiveData<List<Product>>();
        }
        return mySupplies;
    }
    private void loadMySupplies(String id){
        Call<MyProductsResponse> call = RetrofitClient.getInstance().getApi().getMyProducts(id,"false");
        call.enqueue(new Callback<MyProductsResponse>() {
            @Override
            public void onResponse(Call<MyProductsResponse> call, Response<MyProductsResponse> response) {
                mySupplies.postValue(response.body().getMyproducts());
            }

            @Override
            public void onFailure(Call<MyProductsResponse> call, Throwable t) {
            }
        });
    }
    private MutableLiveData<List<Product>> myDemands;
    public MutableLiveData<List<Product>> getMyDemands(String id){
        loadMyDemands(id);
        if(myDemands == null){
            myDemands = new MutableLiveData<List<Product>>();
        }
        return myDemands;
    }
    private void loadMyDemands(String id){
        Call<MyProductsResponse> call = RetrofitClient.getInstance().getApi().getMyProducts(id,"true");
        call.enqueue(new Callback<MyProductsResponse>() {
            @Override
            public void onResponse(Call<MyProductsResponse> call, Response<MyProductsResponse> response) {
                myDemands.postValue(response.body().getMyproducts());
            }

            @Override
            public void onFailure(Call<MyProductsResponse> call, Throwable t) {
            }
        });
    }
    //Market Products ViewModel
    public List<Product> marketproducts = new ArrayList<>();
    private MutableLiveData<List<Product>> marketproductslive;
    private String timestamp = "0";
    private boolean isfetching = false;
    public MutableLiveData<List<Product>> getMarketProductsLive(){
        if(marketproductslive == null){
            marketproductslive = new MutableLiveData<List<Product>>();
        }
        return marketproductslive;
    }
    public void getmoreMarketProducts(String placecode){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadMarketProducts(placecode);
            }
        },1000);
    }
    public void getMarketProducts(String placecode){
        timestamp = "0";
        marketproducts.clear();
        loadMarketProducts(placecode);
    }
    private void loadMarketProducts(String placecode){
    if(!isfetching){
        if(marketproductslive == null){
            marketproductslive = new MutableLiveData<List<Product>>();
        }
        isfetching = true;
        Call<MyProductsResponse> call = RetrofitClient.getInstance().getApi().getProducts(placecode,timestamp);
        call.enqueue(new Callback<MyProductsResponse>() {
            @Override
            public void onResponse(Call<MyProductsResponse> call, Response<MyProductsResponse> response) {
                if(response.body().getMyproducts().size() > 0){
                    marketproducts.addAll(response.body().getMyproducts());
                    timestamp = marketproducts.get(marketproducts.size()-1).getTime();
                    marketproductslive.postValue(marketproducts);
                }
                isfetching = false;
            }

            @Override
            public void onFailure(Call<MyProductsResponse> call, Throwable t) {
            isfetching = false;
            }
        });
    }
    }

    public ViewModel(@NonNull Application application) {
        super(application);
    }
}
