package com.impakter.seller.fragment.seller;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.CategorySpinnerAdapter;
import com.impakter.seller.adapter.CommonAdapter;
import com.impakter.seller.adapter.seller.ProductSellerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnCheckChangeListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.CartSellerRespond;
import com.impakter.seller.object.CategoryObj;
import com.impakter.seller.object.CommonObj;
import com.impakter.seller.object.SellerProfileRespond;
import com.impakter.seller.object.seller.ProductSellerRespond;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductFragment extends BaseFragment {
    private View rootView;
    private Spinner spCategory, spStatus;
    private RecyclerView rcvProduct;
    private ArrayList<ProductSellerRespond.Data> listProducts = new ArrayList<>();
    private ProductSellerAdapter productSellerAdapter;
    private List<CategoryObj> listCategories = new ArrayList<>();
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String categoryId = "";
    private String status = "";
    private int page = 1;
    private int sellerId;
    private int totalPage;

    private TextView tvNoData;
    private boolean isFirst = true;
    private boolean isFirstLoad;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_product, container, false);
        initViews();
        initData();
        initSpinnerStatus();
        initControl();
        return rootView;
    }

    private void initViews() {
        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        spCategory = rootView.findViewById(R.id.sp_category);
        spStatus = rootView.findViewById(R.id.sp_status);
        progressBar = rootView.findViewById(R.id.progress_bar);

        tvNoData = rootView.findViewById(R.id.tv_no_data);

        rcvProduct = rootView.findViewById(R.id.rcv_trending);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new GridLayoutManager(self, 3));
    }

    private void initSpinnerStatus() {
        ArrayList<CommonObj> listStatus = new ArrayList<>();
        listStatus.add(new CommonObj(-1, getResources().getString(R.string.none)));
        listStatus.add(new CommonObj(0, getResources().getString(R.string.disabled)));
        listStatus.add(new CommonObj(1, getResources().getString(R.string.activated)));

        CommonAdapter sortTypeAdapter = new CommonAdapter(self, listStatus);
        spStatus.setAdapter(sortTypeAdapter);
    }

    private void initData() {
        sellerId = preferenceManager.getUserLogin().getId();

        productSellerAdapter = new ProductSellerAdapter(rcvProduct, self, listProducts);
        rcvProduct.setAdapter(productSellerAdapter);
//
        productSellerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listProducts.add(null);
                    productSellerAdapter.notifyItemInserted(listProducts.size() - 1);
                    getMoreProductSeller();
                } else {
                    page = 1;
//                    Toast.makeText(self, "No More Data", Toast.LENGTH_SHORT).show();
                }

            }
        });

        categorySpinnerAdapter = new CategorySpinnerAdapter(self, listCategories);
        spCategory.setAdapter(categorySpinnerAdapter);
//
        if (getUserVisibleHint()) {
            getListCartOfSeller();
            getProductSeller();
        }
    }


    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                productSellerAdapter.setLoaded();
                getProductSeller();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        productSellerAdapter.setOnCheckChangeListener(new OnCheckChangeListener() {
            @Override
            public void onCheckChange(View view, int position, boolean isChecked) {
                if (isChecked) {
                    changeProductStatus(listProducts.get(position).getId(), Constants.ACTIVATED, position);
                } else {
                    changeProductStatus(listProducts.get(position).getId(), Constants.DISABLED, position);
                }
            }

        });
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = listCategories.get(position).getCatId() + "";
                if (categoryId.equals("-1"))
                    categoryId = "";
                page = 1;
                productSellerAdapter.setLoaded();
                if (!isFirst)
                    getProductSeller();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonObj commonObj = (CommonObj) parent.getItemAtPosition(position);
                status = commonObj.getId() + "";
                if (status.equals("-1"))
                    status = "";
                page = 1;
                productSellerAdapter.setLoaded();
                if (!isFirst)
                    getProductSeller();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void changeProductStatus(int productId, final int status, final int position) {
        showDialog();
        ConnectServer.getResponseAPI().changeProductStatus(productId, status).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        if (status == Constants.ACTIVATED) {
                            listProducts.get(position).setStatus(Constants.ACTIVATED);
                            showToast(getResources().getString(R.string.activated));
                        } else {
                            listProducts.get(position).setStatus(Constants.DISABLED);
                            showToast(getResources().getString(R.string.disabled));
                        }
                        productSellerAdapter.notifyItemChanged(position);
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

    private void getListCartOfSeller() {
//        showDialog();
//        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getListCartOfSeller(sellerId).enqueue(new Callback<CartSellerRespond>() {
            @Override
            public void onResponse(Call<CartSellerRespond> call, Response<CartSellerRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listCategories.clear();
                        listCategories.add(new CategoryObj("ALL", -1));
                        listCategories.addAll(response.body().getListCategory());
                        categorySpinnerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
                checkNoData();
//                progressBar.setVisibility(View.GONE);
//                closeDialog();
            }

            @Override
            public void onFailure(Call<CartSellerRespond> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                checkNoData();
//                closeDialog();
            }
        });
    }

    private void getProductSeller() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getListProducts(sellerId, categoryId, status, 1).enqueue(new Callback<ProductSellerRespond>() {
            @Override
            public void onResponse(Call<ProductSellerRespond> call, Response<ProductSellerRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listProducts.clear();
                        listProducts.addAll(response.body().getData());
                        productSellerAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                isFirst = false;
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }

            @Override
            public void onFailure(Call<ProductSellerRespond> call, Throwable t) {
                isFirst = false;
                showToast(t.getMessage());
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }
        });
    }

    private void getMoreProductSeller() {
        ConnectServer.getResponseAPI().getListProducts(sellerId, categoryId, status, page).enqueue(new Callback<ProductSellerRespond>() {
            @Override
            public void onResponse(Call<ProductSellerRespond> call, Response<ProductSellerRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listProducts.remove(listProducts.size() - 1);
                        listProducts.addAll(response.body().getData());
                        productSellerAdapter.setLoaded();
                        productSellerAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        listProducts.remove(listProducts.size() - 1);
                        productSellerAdapter.setLoaded();
                        productSellerAdapter.notifyItemRemoved(listCategories.size() - 1);
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<ProductSellerRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                checkNoData();
                listProducts.remove(listProducts.size() - 1);
                productSellerAdapter.setLoaded();
                productSellerAdapter.notifyItemRemoved(listCategories.size() - 1);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible()) {
            if (isVisibleToUser && !isFirstLoad) {
                getListCartOfSeller();
                getProductSeller();
                isFirstLoad = true;
            }
        }
    }
}
