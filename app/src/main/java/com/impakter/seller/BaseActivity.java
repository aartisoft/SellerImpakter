package com.impakter.seller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.impakter.seller.activity.LoginActivity;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.widget.dialog.ProgressDialog;


public class BaseActivity extends AppCompatActivity {

    //    private TextViewOpenSansSemibold lblTitle;
    protected BaseActivity self;
    protected ProgressDialog progressDialog;
    public static String TAG;
    protected PreferencesManager preferencesManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        super.onCreate(savedInstanceState);
        self = this;
        TAG = self.getClass().getSimpleName();
        preferencesManager = PreferencesManager.getInstance(self);
    }

//    private void initUI() {
//        lblTitle = (TextViewOpenSansSemibold) findViewById(R.id.lblTitle);
//        findViewById(R.id.btnMenu).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//    }

//	protected void initAdModLayout() {
//		AdView adView = new AdView(this);
//		adView.setAdSize(AdSize.BANNER);
//		adView.setAdUnitId(WebserviceConfig.admob);
//		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutAds);
//		if (layout != null) {
//
//			layout.addView(adView);
//			AdRequest adRequest = new AdRequest.Builder().build();
//			adView.loadAd(adRequest);
//		}
//	}

//    protected void setHeaderTitle(int idString) {
//        lblTitle.setText(idString);
//    }
//
//    protected void setHeaderTitle(String s) {
//        lblTitle.setText(s);
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    public void startActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
    }

    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void showToast(int idString) {
        Toast.makeText(this, idString, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String string) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

    public void gotoActivity(Context context, Class<?> cla) {
        Intent intent = new Intent(context, cla);
        startActivity(intent);
    }

    public void gotoActivityWithClearTop(Context context, Class<?> cla) {
        Intent intent = new Intent(context, cla);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void gotoActivity(Context context, Class<?> cla, Bundle bundle) {
        Intent intent = new Intent(context, cla);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    protected void showDialog(int messageId, int positiveLabelId,
                              DialogInterface.OnClickListener positiveOnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(messageId));
        builder.setPositiveButton(getString(positiveLabelId), positiveOnClick);
        AlertDialog alert = builder.create();
        alert.show();
    }

    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    public void showToastMessage(String strId) {
        Toast.makeText(self, strId, Toast.LENGTH_SHORT).show();
    }

    protected boolean isLoggedIn() {
        if (preferencesManager.getUserLogin() == null) {
            return false;
        } else {
            if (preferencesManager.getUserLogin().getId() != 0) {
                return true;
            } else
                return false;
        }
    }
    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
    protected void showConfirmLoginDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(self);
        builder.setTitle(getResources().getString(R.string.alert));
        builder.setMessage(getResources().getString(R.string.please_login));
        builder.setCancelable(false);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoActivity(self, LoginActivity.class);
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

    @Override
    protected void onPause() {
//        PreferencesManager.getInstance(getBaseContext()).putBooleanValue("APP_IS_SHOW", false);
        super.onPause();
    }

    @Override
    protected void onResume() {
//        PreferencesManager.getInstance(getBaseContext()).putBooleanValue("APP_IS_SHOW", true);
        /* CLEAR PUSH NOTIFICATION */
        super.onResume();
    }
}
