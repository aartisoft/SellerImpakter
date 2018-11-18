package com.impakter.seller.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.R;
import com.impakter.seller.widget.textview.TextViewHeeboRegular;

import java.util.ArrayList;
import java.util.List;

public class MyBagAdapter extends RecyclerView.Adapter<MyBagAdapter.ViewHolder> {
    private Activity context;
    private List<String> listProducts = new ArrayList<>();
    private LayoutInflater inflater;

    public MyBagAdapter(Activity context, List<String> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
        inflater = LayoutInflater.from(context);

    }

    public MyBagAdapter(Activity context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        fakeData();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_my_bag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    private void fakeData() {
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");
        listProducts.add("a");

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName, tvBrand, tvDescription, tvSize, tvPrice, tvQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
        }
    }
}
