package com.bapan.localproducts.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bapan.localproducts.EditorItemActivity;
import com.bapan.localproducts.MainActivity;
import com.bapan.localproducts.MyUtils;
import com.bapan.localproducts.R;
import com.bapan.localproducts.SharedPrepData;
import com.bapan.localproducts.ViewModel;
import com.bapan.localproducts.adapters.MyproductsAdapter;
import com.bapan.localproducts.models.Product;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyDemandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDemandsFragment extends Fragment implements MyproductsAdapter.MyProductClick {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyDemandsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyDemands.
     */
    // TODO: Rename and change types and number of parameters
    public static MyDemandsFragment newInstance(String param1, String param2) {
        MyDemandsFragment fragment = new MyDemandsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_my_demands, container, false);

        //initialize views
        menu = view.findViewById(R.id.linearlayoutMenuMyDemands);
        recyclerView = view.findViewById(R.id.recyclerviewMyDemands);
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshLayoutMyDemands);
        noconnectionlayout = view.findViewById(R.id.imageviewNoConnection2);
        //initialize variables
        mainActivity = (MainActivity)getActivity();
        sharedPrepData = new SharedPrepData(getContext());
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        mLayoutManager = new LinearLayoutManager(getContext());
        //initialize listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openDrawer();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            lastFirstVisiblePosition = 0;
                load();
            }
        });
        //common
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        state = mLayoutManager.onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load(){
        if(MyUtils.isInternetConnection(getContext())) {
            viewModel.getMyDemands(sharedPrepData.getId()).observe(getActivity(), new Observer<List<Product>>() {
                @Override
                public void onChanged(List<Product> myProducts) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(new MyproductsAdapter(myProducts, getContext(), MyDemandsFragment.this));
                    myproductslist = myProducts;
                    mLayoutManager.onRestoreInstanceState(state);

                }
            });
        }else {
            Toast.makeText(mainActivity, "No Connection.", Toast.LENGTH_SHORT).show();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        },1500);
    }
    LinearLayout menu;
    MainActivity mainActivity;
    RecyclerView recyclerView;
    ViewModel viewModel;
    SharedPrepData sharedPrepData;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView noconnectionlayout;
    int lastFirstVisiblePosition = 0;
    private LinearLayoutManager mLayoutManager;
    Parcelable state;
    List<Product> myproductslist;

    @Override
    public void onClickMyProduct(Product clicked_product) {
        if(MyUtils.isInternetConnection(mainActivity)){
            String[] data = new String[]{
                    clicked_product.getImage(),
                    clicked_product.getTitle(),
                    clicked_product.getPrice(),
                    clicked_product.getDetails(),
                    clicked_product.getDemand(),
                    clicked_product.getPublish()
            };
            Intent intent = new Intent(mainActivity, EditorItemActivity.class);
            intent.putExtra("data",data);
            intent.putExtra("timestamp",clicked_product.getTime());
            mainActivity.startActivity(intent);
        }else {
            Toast.makeText(mainActivity, "No Connection.", Toast.LENGTH_SHORT).show();
        }
    }
}