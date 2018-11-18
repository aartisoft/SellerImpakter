package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;

import java.util.List;

public class OrderForwardAdapter extends RecyclerView.Adapter<OrderForwardAdapter.ViewHolder> {
    private Activity context;
    private List<ReceivedOrderDetailRespond.Data> listOrders;
    private LayoutInflater inflater;

    public OrderForwardAdapter(Activity context, List<ReceivedOrderDetailRespond.Data> listOrders) {
        this.context = context;
        this.listOrders = listOrders;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_order_forward, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceivedOrderDetailRespond.Data data = listOrders.get(position);
        holder.tvProductName.setText(data.getProductName());
        Glide.with(context).load(data.getImage()).into(holder.ivProduct);
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
        }
    }

}
