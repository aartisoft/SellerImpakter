package com.impakter.seller.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.ImageViewPagerAdapter;
import com.impakter.seller.adapter.ViewPagerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.fragment.seller.AboutFragment;
import com.impakter.seller.fragment.BottomSheetFavouriteFragment;
import com.impakter.seller.fragment.BottomSheetShareFragment;
import com.impakter.seller.fragment.CommentFragment;
import com.impakter.seller.fragment.seller.DetailFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.ProductDetailRespond;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.rd.PageIndicatorView;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivShare, ivFavourite, ivBack, ivShopping, ivOption;
    private Spinner spSize, spColor;
    private ViewPager viewPagerImage, viewPagerProduct;
    private SmartTabLayout tabLayout;
    private ImageViewPagerAdapter imageViewPagerAdapter;
    private List<String> listImages = new ArrayList<>();
    private List<ProductDetailRespond.MoreFromThisBrand> listMoreImages = new ArrayList<>();

    private TextView tvProductName, tvBrand, tvPrice, tvQuantity;
    private MaterialRatingBar ratingBar;

    private PageIndicatorView pageIndicatorView;

    private int id, sellerId;
    private int quantity;
    private List<String> listSize = new ArrayList<>();
    private List<String> listColor = new ArrayList<>();
    private String description;
    private String introduction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        initViews();
        getDataFromIntent();
        initData();
        initControl();
    }

    private void initViews() {
        ivShare = findViewById(R.id.iv_share);
        ivFavourite = findViewById(R.id.iv_favourite);
        ivBack = findViewById(R.id.iv_back);
        ivShopping = findViewById(R.id.iv_shopping);
        ivOption = findViewById(R.id.iv_option);

        viewPagerImage = findViewById(R.id.viewpager_image);
        viewPagerProduct = findViewById(R.id.viewpager_product);
//        viewPagerProduct.setOffscreenPageLimit(3);

        tabLayout = findViewById(R.id.tab_detail);

        pageIndicatorView = findViewById(R.id.pageIndicatorView);

        tvProductName = findViewById(R.id.tv_product_name);
        tvProductName.setSelected(true);
        tvBrand = findViewById(R.id.tv_brand);
        tvBrand.setPaintFlags(tvBrand.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvPrice = findViewById(R.id.tv_price);
        tvQuantity = findViewById(R.id.tv_quantity);

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        spSize = findViewById(R.id.sp_size);
        spColor = findViewById(R.id.sp_color);

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        id = intent.getIntExtra(Constants.PRODUCT_ID, -1);
    }

    private void initData() {
        getProductDetail(id);

    }

    private void initControl() {
        tvBrand.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivShopping.setOnClickListener(this);
        ivOption.setOnClickListener(this);
        ivFavourite.setOnClickListener(this);
        ivShare.setOnClickListener(this);
    }

    private void getProductDetail(int id) {
        showDialog();
        ConnectServer.getResponseAPI().getProductDetail(id).enqueue(new Callback<ProductDetailRespond>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetailRespond> call, @NonNull Response<ProductDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        sellerId = response.body().getData().getSellerId();
                        // List images may you also like
                        listMoreImages.clear();
                        listMoreImages.addAll(response.body().getData().getMoreFromThisBrand());
                        introduction = response.body().getData().getIntroduction();
                        description = response.body().getData().getDescription();
                        setupViewPager(viewPagerProduct);

                        tvProductName.setText(response.body().getData().getTitle());
                        tvBrand.setText(response.body().getData().getBrandName());
                        tvPrice.setText(getResources().getString(R.string.lbl_currency) + response.body().getData().getPricePrimary());
                        ratingBar.setRating(response.body().getData().getAverageRating());

                        listImages.clear();
                        listImages.addAll(response.body().getData().getImages());
                        imageViewPagerAdapter = new ImageViewPagerAdapter(DetailActivity.this, listImages);
                        viewPagerImage.setAdapter(imageViewPagerAdapter);

                        quantity = response.body().getData().getQuantity();
                        tvQuantity.setText(quantity + "");

                        listSize.clear();
                        listSize.addAll(response.body().getData().getOptionAttribute().getSize());
                        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(self, android.R.layout.simple_list_item_1, listSize);
                        spSize.setAdapter(sizeAdapter);

                        listColor.clear();
                        listColor.addAll(response.body().getData().getOptionAttribute().getColor());
                        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(self, android.R.layout.simple_list_item_1, listColor);
                        spColor.setAdapter(colorAdapter);


                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ProductDetailRespond> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void getData() {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        DetailFragment detailFragment = new DetailFragment();
        CommentFragment commentFragment = new CommentFragment();
        AboutFragment aboutFragment = new AboutFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.DESCRIPTION, description);
        bundle.putInt(Constants.PRODUCT_ID, id);
        bundle.putParcelableArrayList(Constants.ARR_IMAGE, (ArrayList<? extends Parcelable>) listMoreImages);
        bundle.putString(Constants.INTRODUCTION, introduction);
        bundle.putInt(Constants.SELLER_ID, sellerId);

        detailFragment.setArguments(bundle);
        commentFragment.setArguments(bundle);
        aboutFragment.setArguments(bundle);

        adapter.addFrag(detailFragment, getResources().getString(R.string.detail));
//        adapter.addFrag(commentFragment, getResources().getString(R.string.comments));
//        adapter.addFrag(aboutFragment, getResources().getString(R.string.about));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setViewPager(viewPager);
    }

    public void showBottomSheetDialog(int productId) {
        BottomSheetFavouriteFragment bottomSheetFragment = new BottomSheetFavouriteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PRODUCT_ID, productId);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_brand:
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.SELLER_ID, sellerId);
                gotoActivity(self, BrandDetailActivity.class, bundle);
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_shopping:
                BuyProductDialog buyProductDialog = new BuyProductDialog(self, id);
                buyProductDialog.show();
                break;
            case R.id.iv_option:

                break;
            case R.id.iv_share:
                BottomSheetShareFragment bottomSheetShareFragment = new BottomSheetShareFragment();
                bottomSheetShareFragment.show(getSupportFragmentManager(), null);
                break;
            case R.id.iv_favourite:
                showBottomSheetDialog(id);
                break;

        }
    }
}
