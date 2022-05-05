package com.bapan.localproducts.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bapan.localproducts.MainActivity;
import com.bapan.localproducts.MyUtils;
import com.bapan.localproducts.R;
import com.bapan.localproducts.RetrofitClient;
import com.bapan.localproducts.models.PublisherResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product, container, false);
        //views
        back = view.findViewById(R.id.framelayoutBack);
        frameLayout = view.findViewById(R.id.framelayoutProductFragment);
        linearLayoutProductImage = view.findViewById(R.id.linearLayoutProduct);
        circleimageviewUserProfile = view.findViewById(R.id.circleimageviewProfile2);
        textviewName = view.findViewById(R.id.textviewUserName);
        //initialize variables
        mainActivity = (MainActivity)getActivity(); 
        //listeners
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            close();
            }
        });
        frameLayout.setOnClickListener(null);
        //common
        changeConfig(true);
        Glide.with(getContext()).load(MyUtils.BASE_URL+getArguments().getString("image")).into(new CustomTarget<Drawable>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                linearLayoutProductImage.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        fetchPublisher();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        changeConfig(false);
    }

    public void changeColor(int resourseColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mainActivity.getWindow().setStatusBarColor(resourseColor);
        }
    }
    public void close(){
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        mainActivity.getSupportFragmentManager().popBackStack();
    }
    public void changeConfig(boolean type){
        if(type){
            mainActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            changeColor(Color.TRANSPARENT);
            mainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }else {
            changeColor(mainActivity.getResources().getColor(R.color.app_dark));
            mainActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mainActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
    public void fetchPublisher(){
        Call<PublisherResponse> call = RetrofitClient.getInstance().getApi().getPublisher("1");
        call.enqueue(new Callback<PublisherResponse>() {
            @Override
            public void onResponse(Call<PublisherResponse> call, Response<PublisherResponse> response) {
                Glide.with(mainActivity)
                .load(MyUtils.BASE_URL+response.body().getImage())
                .into(circleimageviewUserProfile);
                textviewName.setText(response.body().getName());
            }

            @Override
            public void onFailure(Call<PublisherResponse> call, Throwable t) {
            }
        });
    }
    View view;
    FrameLayout back;
    FrameLayout frameLayout;
    MainActivity mainActivity;
    LinearLayout linearLayoutProductImage;
    CircleImageView circleimageviewUserProfile;
    TextView textviewName;
}