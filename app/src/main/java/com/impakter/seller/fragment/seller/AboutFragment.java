package com.impakter.seller.fragment.seller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.BrandDetailActivity;
import com.impakter.seller.activity.MyBagActivity;
import com.impakter.seller.adapter.MoreImageAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.object.ProductDetailRespond;

import java.util.ArrayList;

public class AboutFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private TextView tvAbout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        tvAbout = rootView.findViewById(R.id.tv_about);
    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            String introduction = bundle.getString(Constants.INTRODUCTION);
            tvAbout.setText(introduction);
        }
    }

    private void initControl() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
