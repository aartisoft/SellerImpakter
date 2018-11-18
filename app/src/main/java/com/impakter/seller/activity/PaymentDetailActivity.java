package com.impakter.seller.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.CreditCardAdapter;

public class PaymentDetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private RecyclerView rcvPaymentDetail;
    private CreditCardAdapter creditCardAdapter;
    private TextView tvEdit, btnAddNewPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvEdit = findViewById(R.id.tv_edit);
        btnAddNewPayment = findViewById(R.id.btn_add_new_payment);

        rcvPaymentDetail = findViewById(R.id.rcv_payment);
        rcvPaymentDetail.setHasFixedSize(true);
        rcvPaymentDetail.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        creditCardAdapter = new CreditCardAdapter(self, false);
        rcvPaymentDetail.setAdapter(creditCardAdapter);
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        btnAddNewPayment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_edit:
                gotoActivity(self, EditCreditCardActivity.class);
                break;

            case R.id.btn_add_new_payment:
                gotoActivity(self, AddNewPaymentActivity.class);
                break;
        }
    }
}
