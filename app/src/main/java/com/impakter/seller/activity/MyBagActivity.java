package com.impakter.seller.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.MyBagAdapter;

public class MyBagActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private RecyclerView rcvProduct;
    private MyBagAdapter myBagAdapter;
    private LinearLayout btnBuyNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bag);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        myBagAdapter = new MyBagAdapter(self);
        rcvProduct.setAdapter(myBagAdapter);
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_buy_now:
                gotoActivity(self,ContinueAsGuestActivity.class);
                break;
        }
    }
}
