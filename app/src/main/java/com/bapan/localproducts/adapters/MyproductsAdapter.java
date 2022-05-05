package com.bapan.localproducts.adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bapan.localproducts.EditorItemActivity;
import com.bapan.localproducts.MyUtils;
import com.bapan.localproducts.R;
import com.bapan.localproducts.models.Product;
import com.bumptech.glide.Glide;

import java.util.List;


public class MyproductsAdapter extends RecyclerView.Adapter<MyproductsAdapter.ViewHolder> {

    private List<Product> myproductslist;
    private Context context;
    private MyProductClick myProductClick;

    public MyproductsAdapter(List<Product> myproductslist, Context context,MyProductClick myProductClick) {
        this.myproductslist = myproductslist;
        this.context = context;
        this.myProductClick = myProductClick;
    }

    @NonNull
    @Override
    public MyproductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.my_product, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem,myProductClick);
        
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyproductsAdapter.ViewHolder holder, int position) {
        holder.title.setText(myproductslist.get(position).getTitle());
        Glide.with(context)
        .load(MyUtils.BASE_URL+myproductslist.get(position).getImage())
        .into(holder.productImage);
        holder.price.setText("\u20B9 "+myproductslist.get(position).getPrice());
        holder.time.setText(MyUtils.getTimeDuration(Integer.valueOf(myproductslist.get(position).getTimeduration()))+" ago");
        holder.place.setText(myproductslist.get(position).getPlace());
        if(myproductslist.get(position).getPublish().equals("1")){
            holder.published.setVisibility(View.VISIBLE);
        }else {
            holder.published.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return myproductslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title,price,time,place;
        public ImageView productImage;
        public View published;
        private MyProductClick myProductClick;
        public ViewHolder(@NonNull View itemView,MyProductClick myProductClick) {
            super(itemView);
            this.myProductClick = myProductClick;
            itemView.setOnClickListener(this::onClick);
            title = itemView.findViewById(R.id.textViewTitle);
            productImage = itemView.findViewById(R.id.imageViewProductImage);
            price = itemView.findViewById(R.id.textviewPrice);
            time = itemView.findViewById(R.id.textviewTime);
            place = itemView.findViewById(R.id.textViewProductPlace);
            published = itemView.findViewById(R.id.viewPublished);
        }

        @Override
        public void onClick(View v) {
            myProductClick.onClickMyProduct(myproductslist.get(getAdapterPosition()));
        }
    }
    public interface MyProductClick{
        void onClickMyProduct(Product clicked_product);
    }
}
