package com.bapan.localproducts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bapan.localproducts.Fragments.MyDemandsFragment;
import com.bapan.localproducts.Fragments.MySuppliesFragment;
import com.bapan.localproducts.Fragments.MarketFragment;
import com.bapan.localproducts.Fragments.ProductFragment;
import com.bapan.localproducts.models.ProfileResponse;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPrepData sharedPrepData;
    private TextView market,mysupplies,mydemands,additem,textviewProfile;
    public DrawerLayout drawerLayout;
    private ViewModel viewModel;
    private MyUtils myUtils;
    public int currentOption;
    public ProfileResponse profile;
    private CircleImageView imageProfile;
    private FloatingActionButton floatingActionButton_add;
    

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); //  Disable dark-mode
        sharedPrepData = new SharedPrepData(this);
        if(sharedPrepData.getId().length() != 18){
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("type",true);
            startActivity(intent);
            finish();
        }
        //initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        additem = findViewById(R.id.textviewAddItem);
        market = findViewById(R.id.textviewMarket);
        market.setOnClickListener(this::onClick);
        mysupplies = findViewById(R.id.textviewMySupplies);
        mysupplies.setOnClickListener(this::onClick);
        mydemands = findViewById(R.id.textviewMyDemands);
        mydemands.setOnClickListener(this::onClick);
        imageProfile = findViewById(R.id.circularimageviewProfileImageActivityMain);
        textviewProfile = findViewById(R.id.textviewProfile);
        floatingActionButton_add = findViewById(R.id.actionButtonAdd);
        //initialize variables
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        mySuppliesFragment = new MySuppliesFragment();
        myDemandsFragment = new MyDemandsFragment();
        myUtils = new MyUtils(this,market);
        marketFragment = new MarketFragment();
        productFragment = new ProductFragment();
        //listeners
        ((LinearLayout)findViewById(R.id.linearLayoutprofileWindow)).setOnClickListener(null);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                viewModel.getOption().setValue(currentOption);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        textviewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("type",false);
                startActivity(intent);
            }
        });
        
        View.OnClickListener clickListener_add_item = view -> {     // For add item
            Intent intent = new Intent(MainActivity.this,EditorItemActivity.class);
            intent.putExtra("timestamp","null");
            startActivity(intent);
        };
        additem.setOnClickListener(clickListener_add_item);
        floatingActionButton_add.setOnClickListener(clickListener_add_item);
        
        //common
        viewModel.getOption().setValue(R.id.textviewMarket);
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(Gravity.START);
            }
        },1000);*/
        
        //set profile data
        if(sharedPrepData.getId().equals("null")){
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            finish();
        }else {
            viewModel.getProfileResponse(sharedPrepData.getId()).observe(this, profileResponse -> {
                profile = profileResponse;
                Toast.makeText(this, ""+profileResponse.getName(), Toast.LENGTH_SHORT).show();
                if((profile.getPin() == null) || (profile.getPlace() == null) || (profile.getName() == null) || (profile.getPlacecode() == null)){
                    sharedPrepData.setID("null");
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    finish();
                }else {
                    ((TextView) findViewById(R.id.textviewName)).setText(profileResponse.getName());
                    ((TextView) findViewById(R.id.textviewPlace)).setText(profileResponse.getPlace()+", "+profileResponse.getPin());
                    if(profileResponse.getImage() != null){
                        Glide.with(MainActivity.this)
                                .load(MyUtils.BASE_URL+profileResponse.getImage())
                                .into(imageProfile);
                    }
                    market.performClick();
                }
            });
        }
            
        //connectivity
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        refresh();
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                Toast.makeText(MainActivity.this, "connection lost", Toast.LENGTH_SHORT).show();
                }
        };
        
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }
        
        //getting option
        viewModel.getOption().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer id) {
            ////for test mode
            if(profile != null || profile == null){
                switch (id){
                    case R.id.textviewMarket:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, marketFragment).commit();
                        break;
                    case R.id.textviewMySupplies:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, mySuppliesFragment).commit();
                        break;
                    case R.id.textviewMyDemands:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, myDemandsFragment).commit();
                        break;
                }
            }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
    }

    @Override
    public void onClick(View v) {
    if(MyUtils.isInternetConnection(this)){
        myUtils.changeButtonColor((TextView) v,getResources().getColor(R.color.app_low),getResources().getColor(R.color.primary_dark));
        currentOption = v.getId();
        drawerLayout.closeDrawers();
    }else {
        Toast.makeText(this, "No Connection.", Toast.LENGTH_SHORT).show();
    }
    }
    public void openDrawer(){
        drawerLayout.openDrawer(GravityCompat.START);
    }
    private void refresh(){
        //getting profile response
        viewModel.loadProfileResponse(sharedPrepData.getId());
    }
    public void addProductFragment(String image){
    Bundle bundle = new Bundle();
    bundle.putString("image",image);
    productFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentContainerView,productFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    //Fragments
    MySuppliesFragment mySuppliesFragment;
    MyDemandsFragment myDemandsFragment;
    MarketFragment marketFragment;
    ProductFragment productFragment;
}