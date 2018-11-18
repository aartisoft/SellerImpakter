package com.impakter.seller.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifBitmapProvider;
import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.activity.OrderConfirmationActivity;
import com.impakter.seller.config.Constants;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.OrderDetailRespond;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderReviewFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ImageView ivBack, ivProduct;
    private TextView tvProductName, tvBrand, tvDescription, tvOption;
    private MaterialRatingBar ratingBar;
    private EditText edtContent;
    private Button btnCancel, btnSubmit;
    private OrderDetailRespond.ListItems product;
    private int orderId;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_review, container, false);
        initViews();
        initData();
        initControl();
        return rootView;

    }

    private void initViews() {
        ivBack = rootView.findViewById(R.id.iv_back);
        ivProduct = rootView.findViewById(R.id.iv_product);

        tvProductName = rootView.findViewById(R.id.tv_product_name);
        tvBrand = rootView.findViewById(R.id.tv_brand);
        tvDescription = rootView.findViewById(R.id.tv_description);
        tvOption = rootView.findViewById(R.id.tv_option);

        ratingBar = rootView.findViewById(R.id.rating_bar);
        edtContent = rootView.findViewById(R.id.edt_content);

        btnSubmit = rootView.findViewById(R.id.btn_submit);
        btnCancel = rootView.findViewById(R.id.btn_cancel);
    }

    private void initData() {
        userId = preferenceManager.getUserLogin().getId();
        Bundle bundle = getArguments();
        String brandName = "";
        if (bundle != null) {
            product = bundle.getParcelable(Constants.PRODUCT);
            brandName = bundle.getString(Constants.BRAND_NAME);
            orderId = bundle.getInt(Constants.ORDER_ID);
        }
        tvProductName.setText(product.getName());
        tvBrand.setText(brandName);
        tvDescription.setText(product.getCode());
        tvOption.setText(product.getOptions());
        ratingBar.setRating(product.getRate());
        Glide.with(self).load(product.getImage()).into(ivProduct);

    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    private void reviewOrder(String content) {
        showDialog();
        ConnectServer.getResponseAPI().reviewOrder(orderId, product.getId(), userId, content).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());
                        startActivity(OrderConfirmationActivity.class);

                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<BaseMessageRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_cancel:
                getFragmentManager().popBackStack();
                break;
            case R.id.btn_submit:
                reviewOrder(edtContent.getText().toString().trim());
                break;
        }
    }
}
