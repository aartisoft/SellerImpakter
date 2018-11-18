package com.impakter.seller.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;

public class AddNewPaymentActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_payment);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
    }

    private void initData() {
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
