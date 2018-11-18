package com.impakter.seller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.ViewPagerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.fragment.seller.ProductFragment;
import com.impakter.seller.fragment.ReviewFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.ActionFollowRespond;
import com.impakter.seller.object.SellerProfileRespond;
import com.impakter.seller.widget.textview.TextViewHeeboRegular;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandDetailActivity extends BaseActivity implements View.OnClickListener {
    private SmartTabLayout tabLayout;
    private ViewPager viewPager;

    private ImageView ivCover, ivBack;
    private CircleImageView ivAvatar;
    private TextViewHeeboRegular btnFollow, btnMessage;

    private TextView tvBrandName, tvAddress, tvNumberFollow;
    private int sellerId;
    private String categoryId;
    private int page = 1;
    private String userId = "";
    private boolean followStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_detail);
        if (isLoggedIn()) {
            userId = preferencesManager.getUserLogin().getId() + "";
        } else {
            userId = "";
        }

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            sellerId = bundle.getInt(Constants.SELLER_ID);
        }

        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewpager);
//        viewPager.setOffscreenPageLimit(3);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setViewPager(viewPager);

        ivCover = findViewById(R.id.iv_cover);
        ivAvatar = findViewById(R.id.iv_avatar);
        ivBack = findViewById(R.id.iv_back);

        btnFollow = findViewById(R.id.btn_follow);
        btnMessage = findViewById(R.id.btn_message);

        tvBrandName = findViewById(R.id.tv_brand_name);
        tvNumberFollow = findViewById(R.id.tv_number_follow);
        tvAddress = findViewById(R.id.tv_address);
    }

    private void initData() {
        getSellerProfile();

    }

    private void initControl() {
        btnFollow.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.SELLER_ID, sellerId);

        ProductFragment productFragment = new ProductFragment();
        productFragment.setArguments(bundle);


        adapter.addFrag(productFragment, getResources().getString(R.string.product));
        adapter.addFrag(new ReviewFragment(), getResources().getString(R.string.review));
        adapter.addFrag(new ReviewFragment(), getResources().getString(R.string.about));
        viewPager.setAdapter(adapter);
    }


    private void getSellerProfile() {
        showDialog();
        ConnectServer.getResponseAPI().getSellerProfile(sellerId, userId, categoryId, page).enqueue(new Callback<SellerProfileRespond>() {
            @Override
            public void onResponse(Call<SellerProfileRespond> call, Response<SellerProfileRespond> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        followStatus = response.body().getSellerInfo().isFollowStatus();

                        if (isLoggedIn()) {
                            if (followStatus) {
                                btnFollow.setText(getResources().getString(R.string.following));
                            } else {
                                btnFollow.setText(getResources().getString(R.string.follow));
                            }
                        } else {
                            btnFollow.setText(getResources().getString(R.string.follow));
                        }

                        tvBrandName.setText(response.body().getSellerInfo().getSellerName());
                        tvAddress.setText(response.body().getSellerInfo().getCountryName());
                        tvNumberFollow.setText(response.body().getSellerInfo().getTotalUserFollow() + " " + getResources().getString(R.string.following));

                        Glide.with(self).load(response.body().getSellerInfo().getSellerAvatar()).into(ivAvatar);
                        Glide.with(self).load(response.body().getSellerInfo().getSellerCover()).into(ivCover);


                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<SellerProfileRespond> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                closeDialog();
            }
        });
    }

    private void follow(final int action) {
        showDialog();
        ConnectServer.getResponseAPI().follow(Integer.parseInt(userId), sellerId, action).enqueue(new Callback<ActionFollowRespond>() {
            @Override
            public void onResponse(Call<ActionFollowRespond> call, Response<ActionFollowRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());
                        followStatus = response.body().getFollowStatus();
                        if (action == Constants.FOLLOW) {
                            btnFollow.setText(getResources().getString(R.string.following));
                        } else {
                            btnFollow.setText(getResources().getString(R.string.follow));
                        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_follow:
                if (isLoggedIn()) {
                    if (followStatus) {
                        follow(Constants.UN_FOLLOW);
                    } else {
                        follow(Constants.FOLLOW);
                    }
                } else {
                    showConfirmLoginDialog();
                }
                break;
        }
    }
}
