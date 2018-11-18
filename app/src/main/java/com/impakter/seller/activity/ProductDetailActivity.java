package com.impakter.seller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.config.Constants;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class ProductDetailActivity extends BaseActivity {
    private ImageView ivBack, ivProduct;
    private TextView tvProductName, tvPrice, tvBrand, tvColor, tvSize, tvQuantity;
    private TextView tvDetail;
    private MaterialRatingBar ratingBar;

    private String productImage, productName, brand, color, size, detail;
    private int quantity;
    private float price, rate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivProduct = findViewById(R.id.iv_product);

        tvProductName = findViewById(R.id.tv_product_name);
        tvPrice = findViewById(R.id.tv_price);
        tvBrand = findViewById(R.id.tv_brand);
        tvColor = findViewById(R.id.tv_color);
        tvSize = findViewById(R.id.tv_size);
        tvQuantity = findViewById(R.id.tv_quantity);
        tvDetail = findViewById(R.id.tv_detail);

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            productImage = intent.getStringExtra(Constants.IMAGE_URL);
            productName = intent.getStringExtra(Constants.PRODUCT_NAME);
            brand = intent.getStringExtra(Constants.BRAND_NAME);
            color = intent.getStringExtra(Constants.COLOR);
            size = intent.getStringExtra(Constants.SIZE);
            detail = intent.getStringExtra(Constants.DESCRIPTION);
            quantity = intent.getIntExtra(Constants.QUANTITY, 0);
            rate = intent.getFloatExtra(Constants.RATE, 0);
            price = intent.getFloatExtra(Constants.PRICE, 0);

        }
        tvProductName.setText(productName);
        tvBrand.setText(brand);
        tvSize.setText(size.substring(size.indexOf(":") + 1));
        tvColor.setText(color);
        tvQuantity.setText(quantity + "");
        tvPrice.setText(getResources().getString(R.string.lbl_currency) + price);
        tvDetail.setText(detail);
        ratingBar.setRating(rate);

        Glide.with(self).load(productImage).into(ivProduct);
    }

    private void initControl() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
