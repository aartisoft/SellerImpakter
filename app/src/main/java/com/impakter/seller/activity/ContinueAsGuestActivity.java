package com.impakter.seller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;

public class ContinueAsGuestActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private LinearLayout btnCreateAccount, btnSignIn, btnCheckOutAsAGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_as_guest);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);

        btnCreateAccount = findViewById(R.id.btn_create_account);
        btnSignIn = findViewById(R.id.btn_sign_in);
        btnCheckOutAsAGuest = findViewById(R.id.btn_check_out_as_a_guest);
    }

    private void initData() {

    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnCreateAccount.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnCheckOutAsAGuest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_create_account:
                gotoActivity(self, SignUpActivity.class);
                break;
            case R.id.btn_sign_in:
                gotoActivity(self, LoginActivity.class);
                break;
            case R.id.btn_check_out_as_a_guest:
                gotoActivity(self,ShippingActivity.class);
                break;
        }
    }
}
