package com.impakter.seller.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ReceivedOrderDetailAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dialog.FinishForwardOrderDialog;
import com.impakter.seller.fragment.seller.BottomSheetForwardOrderFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.BlurUtils;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivedOrderDetail extends BaseActivity implements View.OnClickListener,
        BottomSheetForwardOrderFragment.OnCloseButtonClickListener, BottomSheetForwardOrderFragment.OnForwardOrderListener,
        FinishForwardOrderDialog.OnGoToHomeClickListener, FinishForwardOrderDialog.OnGoToOrderClickListener {
    private ImageView ivBack;
    private LinearLayout btnChangeStatus, btnForward;

    private RecyclerView rcvProduct;
    private List<ReceivedOrderDetailRespond.Data> listProducts = new ArrayList<>();
    private ReceivedOrderDetailAdapter receivedOrderDetailAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData, tvStatus;

    private int orderId;
    private int orderStatus;
    private long orderDate;
    private boolean isForwarded;

    private ImageView imageView;
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;

    private BottomSheetForwardOrderFragment fragmentForwardOrder;
    private View view;

    private ReceivedOrderDetailRespond.StatusAction statusAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initViews();
        initData();
        initControl();
        mListener();
        initFragment();
    }

    private void initViews() {
        imageView = findViewById(R.id.image);
        view = findViewById(R.id.view);

        ivBack = findViewById(R.id.iv_back);
        swipeRefreshLayout = findViewById(R.id.swf_layout);
        progressBar = findViewById(R.id.progress_bar);
        tvNoData = findViewById(R.id.tv_no_data);
        tvStatus = findViewById(R.id.tv_status);

        btnChangeStatus = findViewById(R.id.btn_change_status);
        btnForward = findViewById(R.id.btn_forward_order);

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(self));

    }

    private void initFragment() {
//        fragmentForwardOrder = getSupportFragmentManager().findFragmentById(R.id.fragment_forward_order);
//        hideFragment();
        fragmentForwardOrder = new BottomSheetForwardOrderFragment();
    }

    private void showFragment() {

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ORDER_ID, orderId);
        bundle.putLong(Constants.ORDER_DATE, orderDate);
        bundle.putBoolean(Constants.IS_FORWARDED, isForwarded);
        bundle.putParcelableArrayList(Constants.LIST_ORDER, (ArrayList<? extends Parcelable>) listProducts);
        fragmentForwardOrder.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fr_content, fragmentForwardOrder).setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom).show(fragmentForwardOrder).commit();
    }

    private void hideFragment() {
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_to_bottom, R.anim.slide_out_to_bottom).hide(fragmentForwardOrder).commit();
        imageView.setVisibility(View.GONE);
        view.setVisibility(View.GONE);
        imageView.setImageBitmap(null);
    }

    private void mListener() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.HIDE_BLUR)) {
                    hideFragment();
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(Constants.HIDE_BLUR);
        filter.addAction(Constants.FINISH_FORWARD_ORDER);
        registerReceiver(broadcastReceiver, filter);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getIntExtra(Constants.ORDER_ID, -1);
            orderDate = intent.getLongExtra(Constants.ORDER_DATE, -1);
        }
        receivedOrderDetailAdapter = new ReceivedOrderDetailAdapter(self, listProducts);
        rcvProduct.setAdapter(receivedOrderDetailAdapter);

        getOrderDetail();

    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        btnChangeStatus.setOnClickListener(this);
        btnForward.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderDetail();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void setOrderStatus(int status) {
        switch (status) {
            case Constants.NEW:
                tvStatus.setText(getResources().getString(R.string.lbl_new));
                break;
            case Constants.IN_PROGRESS:
                tvStatus.setText(getResources().getString(R.string.inprogress));
                break;
            case Constants.TO_SHIP:
                tvStatus.setText(getResources().getString(R.string.to_ship));
                break;
            case Constants.SHIPPED:
                tvStatus.setText(getResources().getString(R.string.shipped));
                break;
            case Constants.DELIVERED:
                tvStatus.setText(getResources().getString(R.string.delivered));
                break;
        }
    }

    private void getOrderDetail() {
        showDialog();
        ConnectServer.getResponseAPI().getReceivedOrderDetail(orderId).enqueue(new Callback<ReceivedOrderDetailRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderDetailRespond> call, Response<ReceivedOrderDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        statusAction = response.body().getStatusAction().get(0);
                        isForwarded = response.body().isForward();

                        setOrderStatus(response.body().getStatusOrder());

                        listProducts.clear();
                        listProducts.addAll(response.body().getData());
                        receivedOrderDetailAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedOrderDetailRespond> call, Throwable t) {
                closeDialog();
                checkNoData();
                showToast(t.getMessage());
            }
        });
    }

    private void checkNoData() {
        if (listProducts.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }
    }

    private void changeOrderStatus(int orderId, final int orderStatus) {
        showDialog();
        ConnectServer.getResponseAPI().changeStatusOrder(orderId, orderStatus).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        if (orderStatus == Constants.IN_PROGRESS) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(false);
                            statusAction.setStatusShiped(true);
                            tvStatus.setText(getResources().getString(R.string.inprogress));
                        } else if (orderStatus == Constants.TO_SHIP) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(true);
                            statusAction.setStatusShiped(false);
                            tvStatus.setText(getResources().getString(R.string.to_ship));
                        } else if (orderStatus == Constants.SHIPPED) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(true);
                            statusAction.setStatusShiped(true);
                            tvStatus.setText(getResources().getString(R.string.shipped));
                        }
                        isForwarded = false;
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

    private Dialog dialog;

    private void showChangeOrderStatusDialog(final int orderId) {
        View view = self.getLayoutInflater().inflate(R.layout.dialog_change_order_status, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setView(view);

        LinearLayout layoutInProgress = view.findViewById(R.id.layout_in_progress);
        LinearLayout layoutToShip = view.findViewById(R.id.layout_to_ship);
        LinearLayout layoutShipped = view.findViewById(R.id.layout_shipped);
        LinearLayout layoutDelivered = view.findViewById(R.id.layout_delivered);

        layoutInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusAction.getStatusProcessed()) {
                    orderStatus = Constants.IN_PROGRESS;
                    changeOrderStatus(orderId, orderStatus);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_processed));
                }
            }
        });
        layoutToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusAction.getStatusToShip()) {
                    orderStatus = Constants.TO_SHIP;
                    changeOrderStatus(orderId, orderStatus);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_being_shipped));
                }

            }
        });
        layoutShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusAction.getStatusShiped()) {
                    orderStatus = Constants.SHIPPED;
                    changeOrderStatus(orderId, orderStatus);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_shipped));
                }

            }
        });
        layoutDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderStatus = 4;
                changeOrderStatus(orderId, orderStatus);
                dialog.dismiss();
            }
        });
        dialog = builder.create();

        Bitmap map = BlurUtils.takeScreenShot(self);
        Blurry.with(this).from(map).into(imageView);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imageView.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_change_status:
                imageView.setVisibility(View.VISIBLE);
                showChangeOrderStatusDialog(orderId);
                break;
            case R.id.btn_forward_order:
                if (!isForwarded) {
                    imageView.setVisibility(View.VISIBLE);
                    Bitmap map = BlurUtils.takeScreenShot(self);
                    Blurry.with(this).from(map).into(imageView);
                    view.setVisibility(View.VISIBLE);
                    view.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadein));
                    showFragment();
                } else {
                    showToast(getResources().getString(R.string.has_been_forwarded));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCloseButtonClick() {
        hideFragment();
    }

    @Override
    public void onForwardSuccess(String email) {
        isForwarded = true;
        hideFragment();
        FinishForwardOrderDialog finishForwardOrderDialog = new FinishForwardOrderDialog(self, email);
        finishForwardOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finishForwardOrderDialog.show();

        imageView.setVisibility(View.VISIBLE);
        Bitmap map = BlurUtils.takeScreenShot(self);
        Blurry.with(this).from(map).into(imageView);

        finishForwardOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                imageView.setVisibility(View.GONE);
                imageView.setImageBitmap(null);
            }
        });
    }

    @Override
    public void onGoToHomeClick() {
        Intent intent = new Intent();
        intent.setAction(Constants.SHOW_HOME);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onGoToOrderClick() {
        finish();
    }
}
