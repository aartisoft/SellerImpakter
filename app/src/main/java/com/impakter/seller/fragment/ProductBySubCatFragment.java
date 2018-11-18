package com.impakter.seller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.BuyProductDialog;
import com.impakter.seller.activity.ShareDialog;
import com.impakter.seller.adapter.LatestAdapter;
import com.impakter.seller.adapter.ProductAdapter;
import com.impakter.seller.adapter.ProductBySubCatAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnBuyClickListener;
import com.impakter.seller.events.OnFavoriteClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.events.OnShareClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.HomeLatestRespond;
import com.impakter.seller.object.ProductByCategoryRespond;
import com.impakter.seller.object.ProductObj;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductBySubCatFragment extends BaseFragment implements OnBuyClickListener, OnFavoriteClickListener, OnShareClickListener {
    private View rootView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rcvProductByType;
    private ProductBySubCatAdapter productBySubCatAdapter;
    private List<ProductByCategoryRespond.Data> listProducts = new ArrayList<>();

    private int categoryId, subCategoryId, typeTime;
    private int page = 1;
    private int totalPage;
    private ProgressBar progressBar;
    private boolean isFirstLoad = false;
    private TextView tvNoData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product_by_type, container, false);
        initViews();
        getDataFromIntent();
        initData();
        initControl();
        return rootView;
    }

    private void getDataFromIntent() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getInt(Constants.CATEGORY_ID);
            subCategoryId = bundle.getInt(Constants.SUBCATEGORY_ID);
        }
    }

    private void initViews() {
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        progressBar = rootView.findViewById(R.id.progress_bar);

        rcvProductByType = rootView.findViewById(R.id.rcv_product_by_type);
        rcvProductByType.setHasFixedSize(true);
        rcvProductByType.setLayoutManager(new GridLayoutManager(self, 2));
    }

    private void initData() {
        productBySubCatAdapter = new ProductBySubCatAdapter(rcvProductByType, self, listProducts);
        rcvProductByType.setAdapter(productBySubCatAdapter);
        productBySubCatAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listProducts.add(null);
                    productBySubCatAdapter.notifyItemInserted(listProducts.size() - 1);
                    getMoreProductByCat();
                } else {
                    page = 1;
//                    showToast("No More Data");
                }

            }
        });

        if (getUserVisibleHint())
            getProductByCat();

    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                productBySubCatAdapter.setLoaded();
                productBySubCatAdapter.notifyDataSetChanged();
                getProductByCat();
            }
        });

        productBySubCatAdapter.setOnBuyClickListener(this);
        productBySubCatAdapter.setOnFavoriteClickListener(this);
        productBySubCatAdapter.setOnShareClickListener(this);
    }

    private void getProductByCat() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getProductByCategory(categoryId, subCategoryId, 1).enqueue(new Callback<ProductByCategoryRespond>() {
            @Override
            public void onResponse(Call<ProductByCategoryRespond> call, Response<ProductByCategoryRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();
                        listProducts.clear();
                        listProducts.addAll(response.body().getData());
                        productBySubCatAdapter.notifyDataSetChanged();
//                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }

            @Override
            public void onFailure(Call<ProductByCategoryRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }
        });
    }

    private void getMoreProductByCat() {
        ConnectServer.getResponseAPI().getProductByCategory(categoryId, subCategoryId, page).enqueue(new Callback<ProductByCategoryRespond>() {
            @Override
            public void onResponse(Call<ProductByCategoryRespond> call, Response<ProductByCategoryRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listProducts.remove(listProducts.size() - 1);
                        listProducts.addAll(response.body().getData());
                        productBySubCatAdapter.setLoaded();
                        productBySubCatAdapter.notifyDataSetChanged();
//                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProductByCategoryRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                listProducts.remove(listProducts.size() - 1);
                productBySubCatAdapter.notifyItemRemoved(listProducts.size() - 1);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible()) {
            if (isVisibleToUser && !isFirstLoad) {
                getProductByCat();
                isFirstLoad = true;
            }
        }
    }

    private void checkNoData() {
        if (listProducts.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }
    }

    public void showBottomSheetDialog(int productId) {
        BottomSheetFavouriteFragment bottomSheetFragment = new BottomSheetFavouriteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.PRODUCT_ID, productId);
        bottomSheetFragment.setArguments(bundle);
        bottomSheetFragment.show(getFragmentManager(), null);
    }

    @Override
    public void onBuyClick(View view, int position) {
        BuyProductDialog buyProductDialog = new BuyProductDialog(self, listProducts.get(position).getId());
        buyProductDialog.show();
    }

    @Override
    public void onFavoriteClick(View view, int position) {
        showBottomSheetDialog(listProducts.get(position).getId());
    }

    @Override
    public void onShareClick(View view, int position) {
        BottomSheetShareFragment bottomSheetShareFragment = new BottomSheetShareFragment();
        bottomSheetShareFragment.show(getFragmentManager(), null);
//        ShareDialog shareDialog = new ShareDialog(self, listProducts.get(position).getId());
//        shareDialog.show();
//        startActivity(new Intent(self, BuyProductDialog.class));
    }
}
