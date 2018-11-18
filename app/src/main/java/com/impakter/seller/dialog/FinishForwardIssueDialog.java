package com.impakter.seller.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.impakter.seller.R;

public class FinishForwardIssueDialog extends Dialog implements View.OnClickListener {

    private ImageView ivClose;
    private TextView tvEmail;
    private LinearLayout btnGoToOrder, btnGoToHome;
    private Activity activity;
    private OnGoToHomeClickListener onGoToHomeClickListener;
    private OnGoToResolutionCenterClickListener onGoToResolutionCenterClickListener;
    private String email;

    public FinishForwardIssueDialog(@NonNull Activity context, String email) {
        super(context, R.style.DialogTheme);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
        this.email = email;

        onGoToHomeClickListener = (OnGoToHomeClickListener) context;
        onGoToResolutionCenterClickListener = (OnGoToResolutionCenterClickListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_finish_forward_order);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
        tvEmail = findViewById(R.id.tv_email);

        btnGoToHome = findViewById(R.id.btn_go_to_home);
        btnGoToOrder = findViewById(R.id.btn_go_to_order);
    }

    private void initData() {
        tvEmail.setText(email);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        btnGoToOrder.setOnClickListener(this);
        btnGoToHome.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.btn_go_to_home:
                onGoToHomeClickListener.onGoToHomeClick();
                dismiss();
                break;
            case R.id.btn_go_to_order:
                onGoToResolutionCenterClickListener.onGoToResolutionCenterClick();
                dismiss();
                break;
        }
    }

    public interface OnGoToHomeClickListener {
        void onGoToHomeClick();
    }

    public interface OnGoToResolutionCenterClickListener {
        void onGoToResolutionCenterClick();
    }
}
