package com.bapan.localproducts.models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.bapan.localproducts.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

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
    
    public ProfileViewModel(Application application) {
        super(application);
    }
}
