package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.fragment.ProductByCategoryFragment;
import com.impakter.seller.object.HomeCategoryRespond;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

public class IssueDetailViewPagerAdapter extends PagerAdapter {
    private Activity context;
    private List<ReceivedIssueDetailRespond.Data> listProduct;
    private LayoutInflater inflater;
    private int status;
    private OnChangeStatusClickListener onChangeStatusClickListener;

    public IssueDetailViewPagerAdapter(Activity context, List<ReceivedIssueDetailRespond.Data> listProduct, int status) {
        this.context = context;
        this.listProduct = listProduct;
        this.status = status;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = inflater.inflate(R.layout.item_list_issue_detail, container, false);

        TextView tvOrderCode, tvProductName, tvBrand, tvColor, tvQuantity, tvTotalCost, tvShipping, tvSize, tvSubTotalCost;
        TextView tvOtherInformation, tvStatus;
        ImageView ivProduct;
        LinearLayout btnChangeStatus;

        tvOrderCode = view.findViewById(R.id.tv_order_code);
        tvProductName = view.findViewById(R.id.tv_product_name);
        tvBrand = view.findViewById(R.id.tv_brand);
        tvColor = view.findViewById(R.id.tv_color);
        tvQuantity = view.findViewById(R.id.tv_quantity);
        tvTotalCost = view.findViewById(R.id.tv_total_cost);
        tvShipping = view.findViewById(R.id.tv_shipping);
        tvSize = view.findViewById(R.id.tv_size);
        tvSubTotalCost = view.findViewById(R.id.tv_sub_total_cost);
        tvOtherInformation = view.findViewById(R.id.tv_other_information);
        tvStatus = view.findViewById(R.id.tv_status);

        ivProduct = view.findViewById(R.id.iv_product);
        btnChangeStatus = view.findViewById(R.id.btn_change_status);

        ReceivedIssueDetailRespond.Data data = listProduct.get(position);

        tvOrderCode.setText("#" + data.getOrderItemId());
        tvProductName.setText(data.getProductName());
        tvBrand.setText(data.getSellerName());
        if (data.getOptions().size() > 0)
            tvSize.setText(data.getOptions().get(0));
        tvQuantity.setText(context.getResources().getString(R.string.qt) + "  " + data.getQuantity());
        if (data.getOptions().size() > 1)
            tvColor.setText(data.getOptions().get(1));
        tvTotalCost.setText(context.getResources().getString(R.string.lbl_currency) + data.getTotalPrice());
        tvSubTotalCost.setText(context.getResources().getString(R.string.lbl_currency) + data.getPrice());
        tvShipping.setText(context.getResources().getString(R.string.lbl_currency) + data.getShipping());

        setStatus(tvStatus, status);
        Glide.with(context).load(data.getImage()).into(ivProduct);
        
        tvOtherInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnChangeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeStatusClickListener.onChangeStatus(v, position);
            }
        });

        container.addView(view);

        return view;
    }
    private void setStatus(TextView tvStatus, int status) {
        switch (status) {
            case Constants.NEW:
                tvStatus.setText(context.getResources().getString(R.string.lbl_new));
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
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
