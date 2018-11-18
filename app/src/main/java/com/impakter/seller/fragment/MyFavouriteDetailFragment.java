package com.impakter.seller.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.MainActivity;
import com.impakter.seller.adapter.CollectionAdapter;
import com.impakter.seller.adapter.CollectionDetailAdapter;
import com.impakter.seller.adapter.LatestAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnShareClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CollectionRespond;
import com.impakter.seller.object.ProOfCollectionRespond;
import com.impakter.seller.object.ProductByCategoryRespond;
import com.impakter.seller.object.ProductObj;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavouriteDetailFragment extends BaseFragment implements View.OnClickListener, OnShareClickListener {
    private View rootView;
    private RecyclerView rcvCollectionDetail;
    private List<ProOfCollectionRespond.Data> listCollectionsDetail = new ArrayList<>();
    private CollectionDetailAdapter collectionDetailAdapter;

    private int userId;
    private TextView tvNoData, tvTitle;
    private ImageView ivBack, ivEdit;
    private boolean isFirstLoad = false;
    private ProgressBar progressBar;
    private int collectionId;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String collectionName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_favorite_detail, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        tvTitle = rootView.findViewById(R.id.tv_title);
        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        ivBack = rootView.findViewById(R.id.iv_back);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        ivEdit = rootView.findViewById(R.id.iv_edit);
        progressBar = rootView.findViewById(R.id.progress_bar);

        rcvCollectionDetail = rootView.findViewById(R.id.rcv_collection_detail);
        rcvCollectionDetail.setHasFixedSize(true);
        rcvCollectionDetail.setLayoutManager(new GridLayoutManager(self, 2));

    }

    private void initData() {
        collectionDetailAdapter = new CollectionDetailAdapter(self, listCollectionsDetail, false);
        rcvCollectionDetail.setAdapter(collectionDetailAdapter);

        Bundle bundle = getArguments();
        if (bundle != null) {
            collectionId = bundle.getInt(Constants.COLLECTION_ID);
            collectionName = bundle.getString(Constants.COLLECTION_NAME);
        }
        tvTitle.setText(collectionName);
        getProductOfCollection();
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProductOfCollection();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        collectionDetailAdapter.setOnShareClickListener(this);
    }

    private void getProductOfCollection() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getProductOfCollection(collectionId).enqueue(new Callback<ProOfCollectionRespond>() {
            @Override
            public void onResponse(Call<ProOfCollectionRespond> call, Response<ProOfCollectionRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listCollectionsDetail.clear();
                        listCollectionsDetail.addAll(response.body().getData());
                        collectionDetailAdapter.notifyDataSetChanged();
//                        showToast(response.body().getMessage());
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<ProOfCollectionRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void checkNoData() {
        if (listCollectionsDetail.size() == 0) {
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
                isFirstLoad = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
//                ((MainActivity) self).showToolbar();
                getFragmentManager().popBackStack();
                break;
            case R.id.iv_edit:
                EditCollectionFragment editCollectionFragment = new EditCollectionFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.COLLECTION_ID, collectionId);
                bundle.putString(Constants.COLLECTION_NAME, collectionName);
                editCollectionFragment.setArguments(bundle);
                ((MainActivity) self).showFragment(editCollectionFragment, true);
                break;
        }
    }
    @Override
    public void onShareClick(View view, int position) {
        BottomSheetShareFragment bottomSheetShareFragment = new BottomSheetShareFragment();
        bottomSheetShareFragment.show(getFragmentManager(), null);

//        startActivity(new Intent(self, BuyProductDialog.class));
    }
}
