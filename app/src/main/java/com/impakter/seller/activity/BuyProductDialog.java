package com.impakter.seller.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.R;
import com.impakter.seller.adapter.ContactAdapter;
import com.impakter.seller.adapter.ImageViewPagerAdapter;
import com.impakter.seller.adapter.ListAppAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.ProductDetailRespond;
import com.impakter.seller.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyProductDialog extends AlertDialog implements View.OnClickListener {
    private final String TAG = BuyProductDialog.class.getSimpleName();

    private ImageView ivProduct;
    private TextView tvProductName, tvBrand, tvDescription, tvPrice;
    private ProgressDialog progressDialog;


    private LinearLayout btnAddToBag, btnBuyNow;
    private Spinner spSize, spQuantity;
    private MaterialRatingBar ratingBar;

    private int quantity;
    private List<String> listSize = new ArrayList<>();
    private List<String> listColor = new ArrayList<>();
    private List<Integer> listQuantity = new ArrayList<>();
    private String description;
    private String introduction;
    private int productId;

    public BuyProductDialog(@NonNull Context context, int productId) {
        super(context);
        this.productId = productId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_product);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivProduct = findViewById(R.id.iv_product);

        tvProductName = findViewById(R.id.tv_product_name);
        tvProductName.setSelected(true);
        tvBrand = findViewById(R.id.tv_brand);
        tvBrand.setPaintFlags(tvBrand.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        spQuantity = findViewById(R.id.sp_quantity);
        spSize = findViewById(R.id.sp_size);

        btnAddToBag = findViewById(R.id.btn_add_to_bag);
        btnBuyNow = findViewById(R.id.btn_buy_now);

    }

    private void initData() {
        getProductDetail(productId);
    }

    private void initControl() {
        btnAddToBag.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
    }

    private void getProductDetail(int id) {
        showDialog();
        ConnectServer.getResponseAPI().getProductDetail(id).enqueue(new Callback<ProductDetailRespond>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetailRespond> call, @NonNull Response<ProductDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        // List images may you also like
                        introduction = response.body().getData().getIntroduction();
                        description = response.body().getData().getDescription();

                        tvProductName.setText(response.body().getData().getTitle());
                        tvDescription.setText(description);
                        tvBrand.setText(response.body().getData().getBrandName());
                        tvPrice.setText(getContext().getResources().getString(R.string.lbl_currency) + response.body().getData().getPricePrimary());
                        ratingBar.setRating(response.body().getData().getAverageRating());

                        quantity = response.body().getData().getQuantity();
                        listQuantity.clear();
                        for (int i = 1; i <= quantity; i++) {
                            listQuantity.add(i);
                        }
                        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listQuantity);
                        spQuantity.setAdapter(quantityAdapter);

                        listSize.clear();
                        listSize.addAll(response.body().getData().getOptionAttribute().getSize());
                        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listSize);
                        spSize.setAdapter(sizeAdapter);

                        listColor.clear();
                        listColor.addAll(response.body().getData().getOptionAttribute().getColor());


                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ProductDetailRespond> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                closeDialog();
            }
        });
    }

    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add_to_bag:

                break;
            case R.id.btn_buy_now:

                break;
        }
    }
}
