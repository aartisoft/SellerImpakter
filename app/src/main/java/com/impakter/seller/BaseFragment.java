package com.impakter.seller;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.impakter.seller.activity.LoginActivity;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.widget.dialog.ProgressDialog;
import com.impakter.seller.widget.textview.TextViewOpenSansSemibold;

import java.util.List;


public class BaseFragment extends Fragment {
    protected String TAG = this.getClass().getSimpleName();
    private TextViewOpenSansSemibold lblTitle;
    protected Activity self;
    private ProgressDialog progressDialog;
    public PreferencesManager preferenceManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        self = getActivity();
        this.preferenceManager = PreferencesManager.getInstance(self);
    }

    protected void initUI(View view) {
//        lblTitle = (TextViewOpenSansSemibold) view.findViewById(R.id.lblTitle);
    }

    protected void initMenuButton(View view) {
//        try {
//            ((ImageView) view.findViewById(R.id.btnMenu)).setImageResource(R.drawable.ic_menu_active);
//            view.findViewById(R.id.btnMenu).setOnClickListener(
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            ((MainActivity) getActivity()).menu.showMenu();
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    protected void initBackButton(View view) {
//        ((ImageView) view.findViewById(R.id.btnMenu)).setImageResource(R.drawable.btn_back);
//        view.findViewById(R.id.btnMenu).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getMainActivity().onBackPressed();
//                    }
//                });
    }

    protected void initMapButton(View view) {
//        view.findViewById(R.id.icMapHome);
//        view.findViewById(R.id.icMapHome).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(getActivity(), ActivityMap.class);
//                        startActivity(i);
//                        self.overridePendingTransition(R.anim.slide_in_left,
//                                R.anim.slide_out_left);
//                    }
//                });
    }

    protected boolean isLoggedIn() {
        if (preferenceManager.getUserLogin() == null) {
            return false;
        } else {
            if (preferenceManager.getUserLogin().getId() != 0) {
                return true;
            } else
                return false;
        }
    }

    protected void showConfirmLoginDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(self);
        builder.setTitle(getResources().getString(R.string.alert));
        builder.setMessage(getResources().getString(R.string.please_login));
        builder.setCancelable(false);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(LoginActivity.class);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    protected void setHeaderTitle(int idString) {
        lblTitle.setText(idString);
    }

    protected void setHeaderTitle(String s) {
        lblTitle.setText(s);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

    protected void startActivity(Class<?> cla) {
        startActivity(new Intent(getActivity(), cla));
    }

    public void gotoActivity(Context context, Class<?> cla, Bundle bundle) {
        Intent intent = new Intent(context, cla);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void gotoActivity(Intent intent) {
        startActivity(intent);
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected void showToast(String message) {
        Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int message) {
        Toast.makeText(getMainActivity(), message, Toast.LENGTH_SHORT).show();
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(self);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
