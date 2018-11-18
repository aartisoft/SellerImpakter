package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.activity.ProductDetailActivity;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.utils.AppUtil;
import com.impakter.seller.utils.DateTimeUtility;

import java.util.List;

public class ForwardIssueAdapter extends RecyclerView.Adapter<ForwardIssueAdapter.ViewHolder> {
    private Activity context;
    private List<ReceivedIssueDetailRespond.Data> listProduct;
    private LayoutInflater inflater;
    private long orderDate;

    public ForwardIssueAdapter(Activity context, List<ReceivedIssueDetailRespond.Data> listProduct, long orderDate) {
        this.context = context;
        this.listProduct = listProduct;
        this.orderDate = orderDate;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_forward_issue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReceivedIssueDetailRespond.Data data = listProduct.get(position);
        int w = context.getResources().getDisplayMetrics().widthPixels;
        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT
        );
        if (listProduct.size() > 1) {
            if (position == listProduct.size() - 1) {
                params.setMargins(AppUtil.convertDpToPixel(context, 14), 0, AppUtil.convertDpToPixel(context, 14), 0);
                holder.cardView.setLayoutParams(params);
            } else {
                params.setMargins(AppUtil.convertDpToPixel(context, 14), 0, AppUtil.convertDpToPixel(context, 0), 0);
                holder.cardView.setLayoutParams(params);
            }
            holder.cardView.getLayoutParams().width = (int) (w * 5 / 6F);
        } else {
            params.setMargins(AppUtil.convertDpToPixel(context, 14), 0, AppUtil.convertDpToPixel(context, 14), 0);
            holder.cardView.setLayoutParams(params);
        }
        holder.tvOrderDate.setText(DateTimeUtility.convertTimeStampToDate(orderDate + "", "dd/MM/yy"));
        holder.tvOrderCode.setText("#" + data.getOrderItemId());
        holder.tvProductName.setText(data.getProductName());
        holder.tvQuantity.setText(context.getResources().getString(R.string.qt) + "  " + data.getQuantity());

        Glide.with(context).load(data.getImage()).into(holder.ivProduct);
    }


    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvOrderCode, tvOrderDate, tvOtherInformation, tvQuantity, tvProductName;
        private ImageView ivProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);

            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOtherInformation = itemView.findViewById(R.id.tv_other_information);

            ivProduct = itemView.findViewById(R.id.iv_product);

            tvOtherInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReceivedIssueDetailRespond.Data data = listProduct.get(getAdapterPosition());
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
                    intentProductDetail.putExtra(Constants.PRICE, data.getTotalPrice());
                    context.startActivity(intentProductDetail);
                    context.overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                }
            });
        }
    }

}
