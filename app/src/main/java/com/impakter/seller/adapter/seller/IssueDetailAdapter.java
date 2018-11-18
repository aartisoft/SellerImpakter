package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.activity.ProductDetailActivity;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.AppUtil;

import java.util.List;

public class IssueDetailAdapter extends RecyclerView.Adapter<IssueDetailAdapter.ViewHolder> {
    private Activity context;
    private List<ReceivedIssueDetailRespond.Data> listProduct;
    private LayoutInflater inflater;
    private int status;
    private OnChangeStatusClickListener onChangeStatusClickListener;

    public IssueDetailAdapter(Activity context, List<ReceivedIssueDetailRespond.Data> listProduct, int status) {
        this.context = context;
        this.listProduct = listProduct;
        this.status = status;
        inflater = LayoutInflater.from(context);
    }

    public void setStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_issue_detail, parent, false);
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
                params.setMargins(AppUtil.convertDpToPixel(context, 10), 0, AppUtil.convertDpToPixel(context, 10), 0);
                holder.cardView.setLayoutParams(params);
            } else {
                params.setMargins(AppUtil.convertDpToPixel(context, 10), 0, AppUtil.convertDpToPixel(context, 0), 0);
                holder.cardView.setLayoutParams(params);
            }
            holder.cardView.getLayoutParams().width = (int) (w * 5 / 6F);
        } else {
            params.setMargins(AppUtil.convertDpToPixel(context, 10), 0, AppUtil.convertDpToPixel(context, 10), 0);
            holder.cardView.setLayoutParams(params);
        }

        holder.tvOrderCode.setText("#" + data.getOrderItemId());
        holder.tvProductName.setText(data.getProductName());
        holder.tvBrand.setText(data.getSellerName());
        if (data.getOptions().size() > 0)
            holder.tvSize.setText(data.getOptions().get(0));
        holder.tvQuantity.setText(context.getResources().getString(R.string.qt) + "  " + data.getQuantity());
        if (data.getOptions().size() > 1)
            holder.tvColor.setText(data.getOptions().get(1));
        holder.tvTotalCost.setText(context.getResources().getString(R.string.lbl_currency) + data.getTotalPrice());
        holder.tvSubTotalCost.setText(context.getResources().getString(R.string.lbl_currency) + data.getPrice());
        holder.tvShipping.setText(context.getResources().getString(R.string.lbl_currency) + data.getShipping());

        holder.tvStatus.setSelected(true);
        setStatus(holder.tvStatus, status);
        Glide.with(context).load(data.getImage()).into(holder.ivProduct);
    }

    private void setStatus(TextView tvStatus, int status) {
        switch (status) {
            case Constants.NEW:
//                tvStatus.setText(context.getResources().getString(R.string.lbl_new));
                tvStatus.setText(context.getResources().getString(R.string.inprogress));
                break;
            case Constants.REVIEWING:
                tvStatus.setText(context.getResources().getString(R.string.reviewing));
                break;
            case Constants.FAVOR_FOR_BUYER:
                tvStatus.setText(context.getResources().getString(R.string.favor_for_buyer));
                break;
            case Constants.FAVOR_FOR_SELLER:
                tvStatus.setText(context.getResources().getString(R.string.favor_for_seller));
                break;
            case Constants.ACCEPT_RETURN:
                tvStatus.setText(context.getResources().getString(R.string.accept_return));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvOrderCode, tvProductName, tvBrand, tvColor, tvQuantity, tvTotalCost, tvShipping, tvSize, tvSubTotalCost;
        private TextView tvOtherInformation, tvStatus;
        private ImageView ivProduct;
        private LinearLayout btnChangeStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);

            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvBrand = itemView.findViewById(R.id.tv_brand);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvTotalCost = itemView.findViewById(R.id.tv_total_cost);
            tvShipping = itemView.findViewById(R.id.tv_shipping);
            tvSize = itemView.findViewById(R.id.tv_size);
            tvSubTotalCost = itemView.findViewById(R.id.tv_sub_total_cost);
            tvOtherInformation = itemView.findViewById(R.id.tv_other_information);
            tvStatus = itemView.findViewById(R.id.tv_status);

            ivProduct = itemView.findViewById(R.id.iv_product);
            btnChangeStatus = itemView.findViewById(R.id.btn_change_status);

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
            btnChangeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeStatusClickListener.onChangeStatus(tvStatus, getAdapterPosition());
                }
            });
        }
    }

    public void setOnChangeStatusClickListener(OnChangeStatusClickListener onChangeStatusClickListener) {
        this.onChangeStatusClickListener = onChangeStatusClickListener;
    }
}
