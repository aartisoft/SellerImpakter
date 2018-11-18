package com.impakter.seller.activity;

import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.OtherAddressAdapter;
import com.impakter.seller.events.OnItemClickListener;

public class AddressDetailActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private TextView tvAddress, tvCity, tvPhoneNumber;
    private RecyclerView rcvOtherAddress;
    private OtherAddressAdapter otherAddressAdapter;
    private Button btnAddNewAddress;
    private LinearLayout layoutPrimaryAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_detail);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        btnAddNewAddress = findViewById(R.id.btn_add_new_address);
        layoutPrimaryAddress = findViewById(R.id.layout_primary_address);

        tvAddress = findViewById(R.id.tv_address);
        tvCity = findViewById(R.id.tv_city_contry);
        tvPhoneNumber = findViewById(R.id.tv_phone_number);

        rcvOtherAddress = findViewById(R.id.rcv_other_address);
        rcvOtherAddress.setHasFixedSize(true);
        rcvOtherAddress.setLayoutManager(new LinearLayoutManager(self));
        ViewCompat.setNestedScrollingEnabled(rcvOtherAddress, false);

    }

    private void initData() {
        otherAddressAdapter = new OtherAddressAdapter(self);
        rcvOtherAddress.setAdapter(otherAddressAdapter);
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnAddNewAddress.setOnClickListener(this);
        layoutPrimaryAddress.setOnClickListener(this);

        otherAddressAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                gotoActivity(self, EditAddressActivity.class);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_add_new_address:
                gotoActivity(self, AddNewAddressActivity.class);
                break;
            case R.id.layout_primary_address:
                gotoActivity(self, EditAddressActivity.class);
                break;
        }
    }
}
