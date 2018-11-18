package com.impakter.seller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.config.Constants;

public class Demo extends BaseFragment {
    private View rootView;
    private ImageView ivClose;
    private LinearLayout layoutParent;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_sheet_finish_order, container, false);
        ivClose = rootView.findViewById(R.id.iv_close);
        layoutParent = rootView.findViewById(R.id.root_view);
        layoutParent.setOnClickListener(null);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                self.sendBroadcast(intent);
            }
        });
        return rootView;
    }
}
