package com.impakter.seller.fragment;

import android.content.Intent;
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

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.BuyProductDialog;
import com.impakter.seller.adapter.CategorySpinnerAdapter;
import com.impakter.seller.adapter.NewLatestAdapter;
import com.impakter.seller.adapter.TimeSpinnerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnBuyClickListener;
import com.impakter.seller.events.OnFavoriteClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.events.OnShareClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CategoryObj;
import com.impakter.seller.object.HomeLatestRespond;
import com.impakter.seller.object.MenuCategoryRespond;
import com.impakter.seller.object.TimeObj;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LatestFragment extends BaseFragment implements OnBuyClickListener, OnFavoriteClickListener, OnShareClickListener {
    private View rootView;
    private Spinner spCategory, spTime;
    private RecyclerView rcvLatest;
    //    private LatestAdapter latestAdapter;
    private NewLatestAdapter latestAdapter;
    private List<HomeLatestRespond.Data> listProducts = new ArrayList<>();
    private List<CategoryObj> listCategories = new ArrayList<>();
    private ArrayList<TimeObj> listTypeTime = new ArrayList<>();
    private CategorySpinnerAdapter categorySpinnerAdapter;
    private TimeSpinnerAdapter timeSpinnerAdapter;
    private String categoryId;
    private int typeTime;
    private int page = 1;
    private int totalPage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;
    private boolean isFirst = true;
    private ProgressBar progressBar;
    private boolean isFirstLoad = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_latest, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        spCategory = rootView.findViewById(R.id.sp_category);
        spTime = rootView.findViewById(R.id.sp_time);

        rcvLatest = rootView.findViewById(R.id.rcv_latest);
        rcvLatest.setHasFixedSize(true);
        rcvLatest.setLayoutManager(new GridLayoutManager(self, 2));

        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        progressBar = rootView.findViewById(R.id.progress_bar);
    }

    private void initData() {
        initTypeTimeData();

        latestAdapter = new NewLatestAdapter(rcvLatest, self, listProducts);
        rcvLatest.setAdapter(latestAdapter);

        latestAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listProducts.add(null);
                    latestAdapter.notifyItemInserted(listProducts.size() - 1);
                    getMoreHomeLatest();
                } else {
//                    showToast("No More Data");
                    page = 1;
                    latestAdapter.setLoaded();
                    latestAdapter.notifyDataSetChanged();
                }

            }
        });

        categorySpinnerAdapter = new CategorySpinnerAdapter(self, listCategories);
        spCategory.setAdapter(categorySpinnerAdapter);

        timeSpinnerAdapter = new TimeSpinnerAdapter(self, listTypeTime);
        spTime.setAdapter(timeSpinnerAdapter);

        if (getUserVisibleHint() && !isFirstLoad) {
            getMenuCategory();
            getHomeLatest();
        }
    }

    private void initTypeTimeData() {
        listTypeTime.clear();
        listTypeTime.add(new TimeObj(0, getResources().getString(R.string.last_24h)));
        listTypeTime.add(new TimeObj(1, getResources().getString(R.string.last_7_days)));
        listTypeTime.add(new TimeObj(2, getResources().getString(R.string.last_30_days)));
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                latestAdapter.setLoaded();
                latestAdapter.notifyDataSetChanged();
                getHomeLatest();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryId = listCategories.get(position).getCatId() + "";
                if (categoryId.equals("-1"))
                    categoryId = "";
                page = 1;
                latestAdapter.setLoaded();
                latestAdapter.notifyDataSetChanged();
                if (!isFirst)
                    getHomeLatest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TimeObj timeObj = (TimeObj) parent.getItemAtPosition(position);
                typeTime = timeObj.getId();
                page = 1;
                latestAdapter.setLoaded();
                latestAdapter.notifyDataSetChanged();
                if (!isFirst)
                    getHomeLatest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        latestAdapter.setOnBuyClickListener(this);
        latestAdapter.setOnFavoriteClickListener(this);
        latestAdapter.setOnShareClickListener(this);
    }

    private void getMenuCategory() {
//        showDialog();
        ConnectServer.getResponseAPI().getMenuCategory().enqueue(new Callback<MenuCategoryRespond>() {
            @Override
            public void onResponse(Call<MenuCategoryRespond> call, Response<MenuCategoryRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listCategories.clear();
                        listCategories.add(new CategoryObj("ALL", -1));
                        listCategories.addAll(response.body().getData());
                        categorySpinnerAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                } else {
                    showToast(response.body().getMessage());
                }
//                closeDialog();
            }

            @Override
            public void onFailure(Call<MenuCategoryRespond> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                showToast(t.getMessage());
//                closeDialog();
            }
        });
    }

    private void getHomeLatest() {
//        showDialog();
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getHomeLatest(categoryId, typeTime, page).enqueue(new Callback<HomeLatestRespond>() {
            @Override
            public void onResponse(Call<HomeLatestRespond> call, Response<HomeLatestRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPage();

                        listProducts.clear();
                        listProducts.addAll(response.body().getData());
                        latestAdapter.notifyDataSetChanged();
                    }
                } else {
                    showToast(response.body().getMessage());
                }
                isFirst = false;
                checkNoData();
                progressBar.setVisibility(View.GONE);
//                closeDialog();
            }

            @Override
            public void onFailure(Call<HomeLatestRespond> call, Throwable t) {
                isFirst = false;
                checkNoData();
                progressBar.setVisibility(View.GONE);
//                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    private void getMoreHomeLatest() {
        ConnectServer.getResponseAPI().getHomeLatest(categoryId, typeTime, page).enqueue(new Callback<HomeLatestRespond>() {
            @Override
            public void onResponse(Call<HomeLatestRespond> call, Response<HomeLatestRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        //   remove progress item
                        listProducts.remove(listProducts.size() - 1);

                        listProducts.addAll(response.body().getData());
                        latestAdapter.setLoaded();
                        latestAdapter.notifyDataSetChanged();
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<HomeLatestRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                listProducts.remove(listProducts.size() - 1);
                latestAdapter.notifyItemRemoved(listProducts.size() - 1);
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
                getMenuCategory();
                getHomeLatest();
                isFirstLoad = true;
            }
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
        BuyProductDialog buyProductDialog = new BuyProductDialog(self,listProducts.get(position).getId());
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

//        startActivity(new Intent(self, BuyProductDialog.class));
    }
}
