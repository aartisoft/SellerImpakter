package com.impakter.seller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.adapter.ListPeopleAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.ActionFollowRespond;
import com.impakter.seller.object.ContactRespond;
import com.impakter.seller.widget.textview.TextViewHeeboRegular;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack, ivSearch;
    private TextViewHeeboRegular tvTitle;
    private RecyclerView rcvPeople;
    private List<ContactRespond.Data> listPepople = new ArrayList<>();
    private ListPeopleAdapter listPeopleAdapter;
    private int userId ;
    private int otherUserId;
    private boolean followStatus;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_follow);
        initViews();
        initData();
        initControl();

    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swf_layout);

        ivBack = findViewById(R.id.iv_back);
        ivSearch = findViewById(R.id.iv_search);

        tvTitle = findViewById(R.id.tv_title);

        rcvPeople = findViewById(R.id.rcv_people);
        rcvPeople.setHasFixedSize(true);
        rcvPeople.setLayoutManager(new LinearLayoutManager(self));

    }

    private void initData() {
        userId = preferencesManager.getUserLogin().getId();
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(Constants.TITLE);
//            otherUserId = intent.getIntExtra(Constants.SELLER_ID, -1);
            tvTitle.setText(title);
        }
        listPeopleAdapter = new ListPeopleAdapter(self, listPepople);
        rcvPeople.setAdapter(listPeopleAdapter);

        getListFollower();
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        ivSearch.setOnClickListener(this);
        listPeopleAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isLoggedIn()) {
                    if (listPepople.get(position).getFollowStatus()) {
                        follow((TextView) view, Constants.UN_FOLLOW, listPepople.get(position).getId(), position);
                    } else {
                        follow((TextView) view, Constants.FOLLOW, listPepople.get(position).getId(), position);
                    }
                } else {
                    showConfirmLoginDialog();
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getListFollower();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void follow(final TextView btnFollow, final int action, int sellerId, final int position) {
        showDialog();
        ConnectServer.getResponseAPI().follow(userId, sellerId, action).enqueue(new Callback<ActionFollowRespond>() {
            @Override
            public void onResponse(Call<ActionFollowRespond> call, Response<ActionFollowRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());
                        if (action == Constants.FOLLOW) {
                            btnFollow.setText(getResources().getString(R.string.following));
                            listPepople.get(position).setFollowStatus(true);
                        } else {
                            btnFollow.setText(getResources().getString(R.string.follow));
                            listPepople.get(position).setFollowStatus(false);
                        }
                        listPeopleAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ActionFollowRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void getListFollower() {
        showDialog();
        ConnectServer.getResponseAPI().getListFollower(userId).enqueue(new Callback<ContactRespond>() {
            @Override
            public void onResponse(Call<ContactRespond> call, Response<ContactRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listPepople.clear();
                        listPepople.addAll(response.body().getData());
                        listPeopleAdapter.notifyDataSetChanged();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ContactRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.iv_search:

                break;
        }
    }
}
