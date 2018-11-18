package com.impakter.seller.fragment.seller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;

public class IssueOrderDetailFragment extends BaseFragment{
    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_issue_order_detail,container,false);
        return rootView;
    }
}
