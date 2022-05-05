package com.bapan.localproducts.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bapan.localproducts.MainActivity;
import com.bapan.localproducts.R;
import com.bapan.localproducts.SharedPrepData;
import com.bapan.localproducts.ViewModel;
import com.bapan.localproducts.adapters.MarketProductsAdapter;
import com.bapan.localproducts.models.Product;

import java.util.List;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarketFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MarketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarketFragment newInstance(String param1, String param2) {
        MarketFragment fragment = new MarketFragment();
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
        View view = inflater.inflate(R.layout.fragment_market, container, false);
        //initialize views
        menu = view.findViewById(R.id.linearlayoutMenuMarket);
        frame_layout_progress = view.findViewById(R.id.framelayoutProgress);
        swipeRefreshLayout = view.findViewById(R.id.swiperefreshLayoutMarket);
        //initialize variables
        mainActivity = (MainActivity)getActivity();
        place_code = new SharedPrepData(getContext()).getPlacecode();
        recyclerView = view.findViewById(R.id.recyclerviewMarket);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        layoutManager = new GridLayoutManager(getContext(),2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position%3==0?2:1;
            }
        });
        marketProductsAdapter = new MarketProductsAdapter(viewModel.marketproducts,(MainActivity)getActivity());
        handler = new Handler();
        
        runnable = new Runnable() {
            @Override
            public void run() {
                frame_layout_progress.setVisibility(View.GONE);
                if(layoutManager.getItemCount()-1 == layoutManager.findLastCompletelyVisibleItemPosition()){
                }
            }
        };
        
        //initialize listeners
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openDrawer();
            }
        });
        
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                    if((layoutManager.getItemCount()-4 <= layoutManager.findLastCompletelyVisibleItemPosition())){
                        fetch(LOAD_MORE);
                    }
                    if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                            hideProgressbar();
                    }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetch(LOAD);
            }
        });
        //common
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(marketProductsAdapter);
        viewModel.getMarketProductsLive().observe(getActivity(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                marketProductsAdapter.notifyDataSetChanged();
                is_fetching = false;
                hideProgressbar();
                if(layoutManager.getItemCount()-4 <= layoutManager.findLastCompletelyVisibleItemPosition()){
                    fetch(LOAD_MORE);
                }
                if(products.size()==0){
                }else {
                }
            }
        });
        if(viewModel.marketproducts.size() == 0){
            if(!swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(true);
            }
            fetch(LOAD);
        }
        //Toast.makeText(mainActivity, ""+getArguments().getString("place"), Toast.LENGTH_SHORT).show();
        return view;
    }
    @Override
    public void onPause() {
        super.onPause();
        state = layoutManager.onSaveInstanceState();
    }
    @Override
    public void onResume() {
        super.onResume();
        layoutManager.onRestoreInstanceState(state);
    }
    void fetch(int type){
        switch (type){
            case LOAD:
            viewModel.getMarketProducts(place_code);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },4000);
            break;
            case LOAD_MORE:
            viewModel.getmoreMarketProducts(place_code);
                showProgressbar();
            break;
        }
    }
    void showProgressbar(){
    if((layoutManager.getItemCount()-1 == layoutManager.findLastCompletelyVisibleItemPosition()) && layoutManager.getItemCount() != 0){
        frame_layout_progress.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 5000);
    }
    }
    void hideProgressbar(){
        handler.removeCallbacks(runnable);
        frame_layout_progress.setVisibility(View.GONE);
    }

    LinearLayout menu;
    MainActivity mainActivity;
    String place_code;
    RecyclerView recyclerView;
    ViewModel viewModel;
    private GridLayoutManager layoutManager;
    boolean is_fetching = false;
    Parcelable state;
    MarketProductsAdapter marketProductsAdapter;
    private final int LOAD = 0,LOAD_MORE = 1;
    FrameLayout frame_layout_progress;
    Handler handler;
    Runnable runnable;
    SwipeRefreshLayout swipeRefreshLayout;
}