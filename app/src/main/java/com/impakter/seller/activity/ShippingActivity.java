package com.impakter.seller.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;

public class ShippingActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        btnContinue = findViewById(R.id.btn_continue);
    }

    private void initData() {

    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                gotoActivity(self, PaymentActivity.class);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
