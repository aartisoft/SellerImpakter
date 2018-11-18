package com.impakter.seller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;

public class ConfirmActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void initData() {

    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                gotoActivity(self, OrderConfirmationActivity.class);
                break;
        }
    }
}
