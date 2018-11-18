package com.impakter.seller.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.config.Constants;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.UserObj;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private TextView tvSave;
    private ImageView ivBack;
    private UserObj userObj;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_password);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        tvSave = findViewById(R.id.tv_save);
        ivBack = findViewById(R.id.iv_back);

        edtCurrentPassword = findViewById(R.id.edt_current_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
    }

    private void initData() {
        userObj = preferencesManager.getUserLogin();
    }

    private void initControl() {
        tvSave.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                String currentPassword = edtCurrentPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                if (validate()) {
                    changePassword(currentPassword, newPassword);
                }
                break;
            case R.id.iv_back:
                onBackPressed();
                break;

        }
    }

    private boolean validate() {
        if (edtCurrentPassword.getText().toString().trim().length() == 0) {
            showToast(getResources().getString(R.string.input_current_password));
            edtCurrentPassword.requestFocus();
            return false;
        } else if (edtNewPassword.getText().toString().trim().length() == 0) {
            showToast(getResources().getString(R.string.input_new_password));
            edtNewPassword.requestFocus();
            return false;
        } else if (edtConfirmPassword.getText().toString().trim().length() == 0) {
            showToast(getResources().getString(R.string.input_confirm_password));
            edtConfirmPassword.requestFocus();
            return false;
        } else if (!edtNewPassword.getText().toString().trim().equals(edtConfirmPassword.getText().toString().trim())) {
            showToast(getResources().getString(R.string.confirm_password_mismatch));
            edtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void changePassword(String currentPassword, String newPassword) {
        showDialog();
        ConnectServer.getResponseAPI().changePassword(userObj.getId(), currentPassword, newPassword).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());

                        UserObj userObj = preferencesManager.getUserLogin();
                        userObj.setPassword(edtNewPassword.getText().toString().trim());
                        preferencesManager.setUserLogin(userObj);

                        getFragmentManager().popBackStack();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<BaseMessageRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

}
