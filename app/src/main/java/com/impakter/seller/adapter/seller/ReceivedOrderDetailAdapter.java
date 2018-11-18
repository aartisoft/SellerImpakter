package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.activity.DetailActivity;
import com.impakter.seller.activity.ProductDetailActivity;
import com.impakter.seller.config.Constants;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;

import java.util.List;

public class ReceivedOrderDetailAdapter extends RecyclerView.Adapter<ReceivedOrderDetailAdapter.ViewHolder> {
    private Activity context;
    private List<ReceivedOrderDetailRespond.Data> listOrders;
    private LayoutInflater inflater;

    public ReceivedOrderDetailAdapter(Activity context, List<ReceivedOrderDetailRespond.Data> listOrders) {
        this.context = context;
        this.listOrders = listOrders;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_received_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceivedOrderDetailRespond.Data data = listOrders.get(position);
        holder.tvOrderCode.setText("#" + data.getOrderItemId());
        holder.tvProductName.setText(data.getProductName());
        holder.tvColor.setText(data.getOptions().get(1));
        holder.tvQuantity.setText("Qt   " + data.getQuantity());
        holder.tvPrice.setText(context.getResources().getString(R.string.lbl_currency) + data.getTotalItemPrice());

        Glide.with(context).load(data.getImage()).into(holder.ivProduct);
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvOrderCode, tvQuantity, tvColor, tvProductName, tvPrice, tvOtherInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);

            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOtherInfo = itemView.findViewById(R.id.tv_other_information);

            tvOtherInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceivedOrderDetailRespond.Data data = listOrders.get(getAdapterPosition());
                    Intent intentProductDetail = new Intent(context, ProductDetailActivity.class);
                    intentProductDetail.putExtra(Constants.IMAGE_URL, data.getImage());
                    intentProductDetail.putExtra(Constants.PRODUCT_NAME, data.getProductName());
                    intentProductDetail.putExtra(Constants.QUANTITY, data.getQuantity());
                    String color = data.getOptions().get(1);
                    intentProductDetail.putExtra(Constants.COLOR, color.substring(color.indexOf(":") + 1));
                    intentProductDetail.putExtra(Constants.SIZE, data.getOptions().get(0));
                    intentProductDetail.putExtra(Constants.RATE, data.getRating());
                    intentProductDetail.putExtra(Constants.DESCRIPTION, data.getDescription());
                    intentProductDetail.putExtra(Constants.BRAND_NAME, data.getSellerName());
                    intentProductDetail.putExtra(Constants.PRICE, data.getTotalItemPrice());
                    context.startActivity(intentProductDetail);
                    context.overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                }
            });
        }
    }

    private void showOtherInformationDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        View view = inflater.inflate(R.layout.dialog_other_product_information, null);
    }

}
