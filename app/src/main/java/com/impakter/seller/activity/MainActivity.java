package com.impakter.seller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.MenuAdapter;
import com.impakter.seller.adapter.ViewPagerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dialog.FinishForwardOrderDialog;
import com.impakter.seller.dialog.ForwardOrderDialog;
import com.impakter.seller.fragment.HomeFragment;
import com.impakter.seller.fragment.seller.BottomSheetCommentFragment;
import com.impakter.seller.fragment.seller.IssueOrderFragment;
import com.impakter.seller.fragment.seller.MessageFragment;
import com.impakter.seller.fragment.seller.ProfileFragment;
import com.impakter.seller.fragment.seller.ReceivedOrderFragment;
import com.impakter.seller.fragment.UpdateProfileFragment;
import com.impakter.seller.object.UserObj;
import com.impakter.seller.utils.BlurUtils;
import com.impakter.seller.utils.RealPathUtil;

import java.util.ArrayList;

import jp.wasabeef.blurry.Blurry;

public class MainActivity extends BaseActivity implements View.OnClickListener, BottomSheetCommentFragment.OnCloseButtonClickListener,
        BottomSheetCommentFragment.OnViewAllCommentClickListener,
        ForwardOrderDialog.OnForwardOrderListener, FinishForwardOrderDialog.OnGoToHomeClickListener, FinishForwardOrderDialog.OnGoToOrderClickListener {
    private static final int REQUEST_IMAGE_GALLERY = 1234;
    public static final int REQUEST_IMAGE_CAPTURE = 9999;

    private static final int NOTIFICATIONS = 0;
    private static final int MESSAGES = 1;
    private static final int SETTINGS = 2;
    private static final int CUSTOMER_CARE = 3;
    private static final int LOG_OUT = 4;

    private ImageView tabProfile, tabReceivedOrder, tabIssue, tabMessage, tabMenu;
    private ImageView ivClose;
    private MenuAdapter menuAdapter;
    private DrawerLayout drawerLayout;
    private RecyclerView rcvMenu;
    private ArrayList<String> listMenu = new ArrayList<>();

    private ImageView ivSearch;
    private LinearLayout btnEdit;
    private LinearLayout toolbar;
    private OnRequestChangeAvatarListener onActivityResultListener;
    private boolean doubleBackToExitPressedOnce = false;

    private ViewPager viewPager;

    private ImageView imageOverLay;

    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;

    private Fragment commentFragment;
    private String urlImage;

    public LinearLayout getToolbar() {
        return toolbar;
    }

    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    private void setUpToolbar() {
        ImageView ivSearch = toolbar.findViewById(R.id.iv_search);
        ImageView ivFilter = toolbar.findViewById(R.id.iv_filter);
        LinearLayout btnEdit = toolbar.findViewById(R.id.btn_edit);
        TextView tvTitle = toolbar.findViewById(R.id.tv_title);
        btnEdit.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
        ivFilter.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initData();
        initControl();
        mListener();
        initFragment();
//        showHomeFragment();
//        showFragment(new HomeFragment(), false);
    }

    private void mListener() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.SHOW_BLUR)) {
                    imageOverLay.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BlurUtils.takeScreenShot(self);
                    Blurry.with(MainActivity.this).animate().from(bitmap).into(imageOverLay);
                }
                if (intent.getAction().equals(Constants.HIDE_BLUR)) {
                    imageOverLay.setVisibility(View.GONE);
                    imageOverLay.setImageBitmap(null);
                }
                if (intent.getAction().equals(Constants.SHOW_COMMENT)) {
                    showFragment();
                }
                if (intent.getAction().equals(Constants.SHOW_HOME)) {
                    viewPager.setCurrentItem(0);
                    formatBottomMenu();
                    tabProfile.setImageResource(R.drawable.ic_account_active);
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(Constants.SHOW_BLUR);
        filter.addAction(Constants.HIDE_BLUR);
        filter.addAction(Constants.SHOW_COMMENT);
        filter.addAction(Constants.SHOW_HOME);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initViews() {
        imageOverLay = findViewById(R.id.image);

        toolbar = findViewById(R.id.toolbar);

        tabProfile = findViewById(R.id.tab_profile);
        tabReceivedOrder = findViewById(R.id.tab_received_order);
        tabIssue = findViewById(R.id.tab_issue);
        tabMessage = findViewById(R.id.tab_message);
        tabMenu = findViewById(R.id.tab_menu);

        drawerLayout = findViewById(R.id.drawer_layout);

        rcvMenu = findViewById(R.id.rcv_menu);
        rcvMenu.setHasFixedSize(true);
        rcvMenu.setLayoutManager(new LinearLayoutManager(self));

        ivClose = findViewById(R.id.iv_close);
        ivSearch = findViewById(R.id.iv_search);
        btnEdit = findViewById(R.id.btn_edit);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(4);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    private void initFragment() {
        commentFragment = getSupportFragmentManager().findFragmentById(R.id.frag_comment);
        hideFragment();
//        commentFragment = new BottomSheetCommentFragment();
    }

    private void showFragment() {

//        Bundle bundle = new Bundle();
//        bundle.putInt(Constants.ORDER_ID, orderId);
//        bundle.putLong(Constants.ORDER_DATE, orderDate);
//        bundle.putParcelableArrayList(Constants.LIST_ORDER, (ArrayList<? extends Parcelable>) listProducts);
//        commentFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()/*.replace(R.id.fr_comment, commentFragment)*/.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom).show(commentFragment).commit();
    }

    private void hideFragment() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom).hide(commentFragment).commit();
        imageOverLay.setVisibility(View.GONE);
//        view.setVisibility(View.GONE);
        imageOverLay.setImageBitmap(null);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfileFragment(), getResources().getString(R.string.categories));
        adapter.addFrag(new ReceivedOrderFragment(), getResources().getString(R.string.received_orders));
        adapter.addFrag(new IssueOrderFragment(), getResources().getString(R.string.trending));
        adapter.addFrag(new MessageFragment(), getResources().getString(R.string.message));
        viewPager.setAdapter(adapter);
    }

    private void initData() {
        menuAdapter = new MenuAdapter(self, listMenu);
        rcvMenu.setAdapter(menuAdapter);

        menuAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TextView textView, int position) {
                switch (position) {
                    case NOTIFICATIONS:
                        setItemBackgroundMenu(position);
                        gotoActivity(self, NotificationsActivity.class);
                        break;
                    case MESSAGES:
                        setItemBackgroundMenu(position);
                        gotoActivity(self, MessageActivity.class);
                        break;
                    case SETTINGS:
                        setItemBackgroundMenu(position);
                        break;
                    case CUSTOMER_CARE:
                        setItemBackgroundMenu(position);
                        gotoActivity(self, CustomerCareActivity.class);
                        break;
                    case LOG_OUT:
                        logout();
                        setItemBackgroundMenu(position);
                        break;
                }
            }
        });
        initMenu();
    }

    private void setItemBackgroundMenu(int position) {
        menuAdapter.setPosition(position);
        menuAdapter.notifyDataSetChanged();
        drawerLayout.closeDrawers();
    }

    private void initMenu() {
        listMenu.add(getResources().getString(R.string.notifications));
        listMenu.add(getResources().getString(R.string.messages));
        listMenu.add(getResources().getString(R.string.setting));
        listMenu.add(getResources().getString(R.string.customer_care));
        if (isLoggedIn()) {
            listMenu.add(getResources().getString(R.string.logout));
        }
        menuAdapter.notifyDataSetChanged();
    }

    private void initControl() {
        tabProfile.setOnClickListener(this);
        tabReceivedOrder.setOnClickListener(this);
        tabIssue.setOnClickListener(this);
        tabMessage.setOnClickListener(this);
        tabMenu.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
    }

    private void showHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fr_content, new HomeFragment()).addToBackStack(null).commit();
    }

    public void showFragment(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(R.id.fr_content, fragment).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .replace(R.id.fr_content, fragment).commit();
        }
    }

    public void showFragmentWithAddMethod(Fragment fragment, boolean addToBackStack) {
        if (addToBackStack) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .add(R.id.fr_content, fragment).addToBackStack(null).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right)
                    .add(R.id.fr_content, fragment).commit();
        }
    }

    private void formatBottomMenu() {
        setUpToolbar();

        tabProfile.setImageResource(R.drawable.ic_account_inactive);
        tabReceivedOrder.setImageResource(R.drawable.ic_confirmed_inactive);
        tabIssue.setImageResource(R.drawable.ic_issue_inactive);
        tabMessage.setImageResource(R.drawable.ic_message_inactive);
        tabMenu.setImageResource(R.drawable.ic_menu_inactive);
    }

    private void logout() {
        clearUser();
        gotoActivityWithClearTop(self, LoginActivity.class);
        finish();
    }

    private void clearUser() {
        preferencesManager.setSaveAccount(false);
        UserObj userObj = new UserObj();
        preferencesManager.setUserLogin(userObj);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_profile:
                if (isLoggedIn()) {
                    formatBottomMenu();
                    tabProfile.setImageResource(R.drawable.ic_account_active);
                    viewPager.setCurrentItem(0);
                } else {
                    showConfirmDialog();
                }

                break;
            case R.id.tab_received_order:
                if (isLoggedIn()) {
                    formatBottomMenu();
                    tabReceivedOrder.setImageResource(R.drawable.ic_confirmed_active);
                    viewPager.setCurrentItem(1);
                } else {
                    showConfirmDialog();
                }

                break;
            case R.id.tab_issue:
                if (isLoggedIn()) {
                    formatBottomMenu();
                    tabIssue.setImageResource(R.drawable.ic_issue_active);
                    viewPager.setCurrentItem(2);
                } else {
                    showConfirmDialog();
                }

                break;
            case R.id.tab_message:
                if (isLoggedIn()) {
                    formatBottomMenu();
                    viewPager.setCurrentItem(3);
                    tabMessage.setImageResource(R.drawable.ic_message_active);

                } else {
                    showConfirmDialog();
                }

                break;
            case R.id.tab_menu:
                formatBottomMenu();
                tabMenu.setImageResource(R.drawable.ic_menu_active);
                drawerLayout.openDrawer(Gravity.END);
                break;
            case R.id.iv_close:
                formatBottomMenu();
                drawerLayout.closeDrawers();
                break;
            case R.id.iv_search:

                break;

            case R.id.btn_edit:
                showFragment(new UpdateProfileFragment(), true);
                break;
        }
    }

    @Override
    public void onViewAllCommentClick() {
        gotoActivity(self, AllCommentActivity.class);
    }

    @Override
    public void onCloseButtonClick() {
        hideFragment();
    }

    @Override
    public void onGoToHomeClick() {
        viewPager.setCurrentItem(0);
        formatBottomMenu();
        tabProfile.setImageResource(R.drawable.ic_account_active);
        finishForwardOrderDialog.dismiss();
    }

    @Override
    public void onGoToOrderClick() {
        finishForwardOrderDialog.dismiss();
    }

    FinishForwardOrderDialog finishForwardOrderDialog;

    @Override
    public void onForwardSuccess(String email) {

        finishForwardOrderDialog = new FinishForwardOrderDialog(self, email);
        finishForwardOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finishForwardOrderDialog.show();

        imageOverLay.setVisibility(View.VISIBLE);
        Bitmap bitmap = BlurUtils.takeScreenShot(MainActivity.this);
        Blurry.with(MainActivity.this).animate().from(bitmap).into(imageOverLay);

        finishForwardOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imageOverLay.setVisibility(View.GONE);
                imageOverLay.setImageBitmap(null);
            }
        });
    }

    public interface OnRequestChangeAvatarListener {
        void onSuccess(int requestCode, int resultCode, Intent data);
    }

    public void setOnActivityResult(OnRequestChangeAvatarListener OnRequestChangeAvatarListener) {
        this.onActivityResultListener = OnRequestChangeAvatarListener;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constants.REQUEST_CODE_GO_TO_PRODUCT_DETAIL_ACTIVITY) {
//            if (resultCode == RESULT_OK) {
//                String userId = data.getStringExtra(Constants.USER_ID_COMMENT);
//                OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.USER_ID_COMMENT, userId);
//                otherProfileFragment.setArguments(bundle);
//                formatBottomMenu();
//                tabIssue.setImageResource(R.drawable.ic_account_active);
////                showToolbar();
//                showFragment(otherProfileFragment, false);
//            }
//        } else {
//            onActivityResultListener.onSuccess(requestCode, resultCode, data);
//        }
//    }

    public boolean isLoggedIn() {
        if (preferencesManager.getUserLogin() == null) {
            return false;
        } else {
            if (preferencesManager.getUserLogin().getId() != 0) {
                return true;
            } else
                return false;
        }
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            urlImage = RealPathUtil.getRealPath(self, data.getData());
            Bundle bundle = new Bundle();
            bundle.putString(Constants.IMAGE_URL, urlImage);
            gotoActivity(self, SendMessageImageActivity.class, bundle);
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            urlImage = RealPathUtil.getRealPath(self, data.getData());
            Bundle bundle = new Bundle();
            bundle.putString(Constants.IMAGE_URL, urlImage);
            gotoActivity(self, SendMessageImageActivity.class, bundle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
