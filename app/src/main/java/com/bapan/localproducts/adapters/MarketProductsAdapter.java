package com.bapan.localproducts.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bapan.localproducts.Fragments.ProductFragment;
import com.bapan.localproducts.MainActivity;
import com.bapan.localproducts.MyUtils;
import com.bapan.localproducts.R;
import com.bapan.localproducts.models.Product;
import com.bumptech.glide.Glide;

import java.util.List;

public class MarketProductsAdapter extends RecyclerView.Adapter<MarketProductsAdapter.ViewHolder> {
    List<Product> products;
    MainActivity mainActivity;
    public MarketProductsAdapter(List<Product> products, MainActivity mainActivity) {
        this.products = products;
        this.mainActivity = mainActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public MarketProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.market_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MarketProductsAdapter.ViewHolder holder, int position) {
        holder.title.setText(products.get(position).getTitle());
        Glide.with(mainActivity)
        .load(MyUtils.BASE_URL+products.get(position).getImage())
        .into(holder.imageViewProductItem);
        if(position%3 == 0){
            holder.linearLayout.setGravity(Gravity.LEFT);
            holder.imageViewProductItem.setMinimumHeight(180);
            holder.imageViewProductItem.setMinimumWidth(270);
        }else {
            holder.linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        holder.price.setText("\u20B9 "+products.get(position).getPrice());
        if(products.get(position).getDemand().equals("0")){
            holder.viewTypeSign.setBackgroundDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_baseline_supply));
        }else {
            holder.viewTypeSign.setBackgroundDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_baseline_demand));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title,price;
        public ImageView imageViewProductItem;
        public LinearLayout linearLayout;
        public CardView cardviewMarketProductItem;
        public View viewTypeSign;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textviewTitleMarketproduct);
            imageViewProductItem = itemView.findViewById(R.id.imageviewMarketProduct);
            linearLayout = itemView.findViewById(R.id.linearlayoutMarketProduct);
            imageViewProductItem.setOnClickListener(this::onClick);
            imageViewProductItem.setClipToOutline(true);
            cardviewMarketProductItem = itemView.findViewById(R.id.cardviewMarketProductItem);
            price = itemView.findViewById(R.id.textviewPriceMarketProduct);
            viewTypeSign = itemView.findViewById(R.id.viewTypeSign);
            
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Toast.makeText(mainActivity, ""+products.get(position).getUsersl(), Toast.LENGTH_SHORT).show();
            mainActivity.addProductFragment(products.get(position).getImage());
        }
    }
}
