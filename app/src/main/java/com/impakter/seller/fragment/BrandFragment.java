package com.impakter.seller.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.BrandAdapter;
import com.impakter.seller.object.BrandObj;

import java.util.ArrayList;

public class BrandFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rcvBrand;
    private ArrayList<BrandObj> listBrands = new ArrayList<>();
    private BrandAdapter brandAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_brand, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        rcvBrand = rootView.findViewById(R.id.rcv_brand);
        rcvBrand.setHasFixedSize(true);
        rcvBrand.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        brandAdapter = new BrandAdapter(self);
        brandAdapter.fillSections();
        rcvBrand.setAdapter(brandAdapter);
    }

    private void initControl() {

    }
}
