package com.impakter.seller.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.ActivityAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.NotificationRespond;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private RecyclerView rcvNotification;
    private ActivityAdapter activityAdapter;
    private List<NotificationRespond.Data> listNotifications = new ArrayList<>();
    private ProgressBar progressBar;
    private TextView tvNoData;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int userId;
    private int page = 1;
    private int totalPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvNoData = findViewById(R.id.tv_no_data);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swf_layout);

        rcvNotification = findViewById(R.id.rcv_notification);
        rcvNotification.setHasFixedSize(true);
        rcvNotification.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        userId = preferencesManager.getUserLogin().getId();

        activityAdapter = new ActivityAdapter(rcvNotification, self, listNotifications);
        rcvNotification.setAdapter(activityAdapter);

        activityAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listNotifications.add(null);
                    activityAdapter.notifyItemInserted(listNotifications.size() - 1);
                    getMoreNotifications();
                } else {
//                    showToast("No More Data");
                    page = 1;
                    activityAdapter.setLoaded();
                    activityAdapter.notifyDataSetChanged();
                }

            }
        });

        getNotifications();
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                activityAdapter.setLoaded();
                activityAdapter.notifyDataSetChanged();
                getNotifications();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ivBack.setOnClickListener(this);
    }

    private void getNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getNotification(12, page).enqueue(new Callback<NotificationRespond>() {
            @Override
            public void onResponse(Call<NotificationRespond> call, Response<NotificationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listNotifications.clear();
                        listNotifications.addAll(response.body().getData());

                        activityAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<NotificationRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getMoreNotifications() {
        ConnectServer.getResponseAPI().getNotification(12, page).enqueue(new Callback<NotificationRespond>() {
            @Override
            public void onResponse(Call<NotificationRespond> call, Response<NotificationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listNotifications.remove(listNotifications.size() - 1);
                        listNotifications.addAll(response.body().getData());
                        activityAdapter.setLoaded();
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        activityAdapter.setLoaded();
                        activityAdapter.notifyDataSetChanged();
                    }
                }
                checkNoData();

            }

            @Override
            public void onFailure(Call<NotificationRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                listNotifications.remove(listNotifications.size() - 1);
                activityAdapter.notifyItemRemoved(listNotifications.size() - 1);
            }
        });
    }

    private void checkNoData() {
        if (listNotifications.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
