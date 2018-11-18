package com.impakter.seller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;

public class PaymentActivity extends BaseActivity {
    private ImageView ivBack;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
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
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoActivity(self, ConfirmActivity.class);
            }
        });
    }
}
