package com.impakter.seller.fragment.seller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.ReceivedOrderDetail;
import com.impakter.seller.adapter.CommonAdapter;
import com.impakter.seller.adapter.seller.ReceivedOrderAdapter;
import com.impakter.seller.adapter.seller.RecentSearchAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.RecentSearchObj;
import com.impakter.seller.dialog.FinishForwardIssueDialog;
import com.impakter.seller.dialog.FinishForwardOrderDialog;
import com.impakter.seller.dialog.ForwardOrderDialog;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnForwardClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.CommonObj;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.object.seller.ReceivedOrderRespond;
import com.impakter.seller.utils.AppUtil;
import com.impakter.seller.utils.BlurUtils;
import com.impakter.seller.widget.CustomSpinner;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceivedOrderFragment extends BaseFragment implements OnChangeStatusClickListener, OnForwardClickListener, OnItemClickListener{
    private final int NEW = 1;
    private final int IN_PROGRESS = 2;
    private final int TO_SHIP = 3;
    private final int SHIPPED = 4;

    private View rootView;
    private CustomSpinner spStatus, spSortBy;

    private RecyclerView rcvOrders;
    private List<ReceivedOrderRespond.Data> listOrders = new ArrayList<>();
    private List<ReceivedOrderRespond.Data> listSearchResult = new ArrayList<>();

    private ReceivedOrderAdapter receivedOrderAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private int sellerId;
    private String filterStatus = "";
    private String sortBy = "";
    private int orderStatus;

    private boolean isFirst = true;
    private boolean isFirstLoad = false;

    private ImageView ivOverlay;

    private int page = 1;
    private int totalPage;

    private int searchPage = 1;
    private int totalSearchPage;

    private ImageView ivBack;
    private EditText edtKeyword;
    private LinearLayout layoutSearch, layoutRecentSearch;
    private String keyword;
    private ImageView ivSearch, ivClear;
    private LinearLayout toolbar;

    private RecyclerView rcvRecentSearch;
    private ArrayList<String> listKeywords = new ArrayList<>();
    private RecentSearchAdapter recentSearchAdapter;
    private DbContext dbContext;
    private boolean isSearch = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_received_order, container, false);
        initViews();
        DbContext.init(self);
        dbContext = DbContext.getInstance();
        setUpToolbar();
        initData();
        initSpinnerStatus();
        initSpinnerSortBy();
        initControl();
        return rootView;
    }

    private void setUpToolbar() {
//        LinearLayout toolbar = ((MainActivity) self).getToolbar();
        ivSearch = rootView.findViewById(R.id.iv_search);
        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        LinearLayout btnEdit = rootView.findViewById(R.id.btn_edit);
        btnEdit.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        toolbar = rootView.findViewById(R.id.toolbar);
        ivClear = rootView.findViewById(R.id.iv_clear);
        layoutSearch = rootView.findViewById(R.id.layout_search);
        layoutRecentSearch = rootView.findViewById(R.id.layout_recent_search);
        edtKeyword = rootView.findViewById(R.id.edt_keyword);
        ivBack = rootView.findViewById(R.id.iv_back);


        ivOverlay = rootView.findViewById(R.id.iv_over_lay);

        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        progressBar = rootView.findViewById(R.id.progress_bar);

        spStatus = rootView.findViewById(R.id.sp_status);
        spSortBy = rootView.findViewById(R.id.sp_sort_by);

        rcvOrders = rootView.findViewById(R.id.rcv_received_order);
        rcvOrders.setHasFixedSize(true);
        rcvOrders.setLayoutManager(new LinearLayoutManager(self));

        rcvRecentSearch = rootView.findViewById(R.id.rcv_recent_search);
        rcvRecentSearch.setHasFixedSize(true);
        rcvRecentSearch.setLayoutManager(new LinearLayoutManager(self));

    }

    private void initData() {
        if (isLoggedIn())
            sellerId = preferenceManager.getUserLogin().getId();

        recentSearchAdapter = new RecentSearchAdapter(self, listKeywords);
        rcvRecentSearch.setAdapter(recentSearchAdapter);

        receivedOrderAdapter = new ReceivedOrderAdapter(rcvOrders, self, listOrders);
        rcvOrders.setAdapter(receivedOrderAdapter);

        processLoadMore();

        if (getUserVisibleHint())
            getListReceivedOrders();
    }

    private void processLoadMore() {
        receivedOrderAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (!isSearch) {
                    page++;
                    if (page <= totalPage) {
                        listOrders.add(null);
                        receivedOrderAdapter.notifyItemInserted(listOrders.size() - 1);
                        getMoreListReceivedOrders();
                    }
                } else {
                    searchPage++;
                    if (searchPage <= totalSearchPage) {
                        listSearchResult.add(null);
                        receivedOrderAdapter.notifyItemInserted(listSearchResult.size() - 1);
                        searchMore();
                    }
                }


            }
        });
    }

    private void initSpinnerStatus() {
        ArrayList<CommonObj> listStatus = new ArrayList<>();
        listStatus.add(new CommonObj(-1, getResources().getString(R.string.all)));
        listStatus.add(new CommonObj(1, getResources().getString(R.string.confirmed)));
        listStatus.add(new CommonObj(2, getResources().getString(R.string.packed)));
        listStatus.add(new CommonObj(3, getResources().getString(R.string.shipped)));
        listStatus.add(new CommonObj(4, getResources().getString(R.string.delivered)));

        CommonAdapter statusAdapter = new CommonAdapter(self, listStatus);
        spStatus.setAdapter(statusAdapter);
    }

    private void initSpinnerSortBy() {
        ArrayList<CommonObj> listSortType = new ArrayList<>();
        listSortType.add(new CommonObj(-1, getResources().getString(R.string.none)));
        listSortType.add(new CommonObj(1, getResources().getString(R.string.no_preference)));
        listSortType.add(new CommonObj(2, getResources().getString(R.string.most_recent)));
        listSortType.add(new CommonObj(3, getResources().getString(R.string.most_dated)));

        CommonAdapter sortTypeAdapter = new CommonAdapter(self, listSortType);
        spSortBy.setAdapter(sortTypeAdapter);
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSearch) {
                    page = 1;
                    getListReceivedOrders();

                } else {
                    searchPage = 1;
                    search();
                }
                receivedOrderAdapter.setLoaded();
                receivedOrderAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        spStatus.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                Intent intent = new Intent();
                intent.setAction(Constants.SHOW_BLUR);
                self.sendBroadcast(intent);
            }

            @Override
            public void onSpinnerClosed() {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                self.sendBroadcast(intent);
            }
        });
        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonObj obj = (CommonObj) parent.getItemAtPosition(position);
                filterStatus = obj.getId() + "";
                if (filterStatus.equals("-1")) {
                    filterStatus = "";
                }
                page = 1;
                receivedOrderAdapter.setLoaded();
                receivedOrderAdapter.notifyDataSetChanged();

                if (!isFirst)
                    getListReceivedOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(self, "Nothing", Toast.LENGTH_SHORT).show();
            }
        });
        spSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CommonObj obj = (CommonObj) parent.getItemAtPosition(position);
                sortBy = obj.getId() + "";
                if (sortBy.equals("-1")) {
                    sortBy = "";
                }
                if (!isFirst)
                    getListReceivedOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spSortBy.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened() {
                Intent intent = new Intent();
                intent.setAction(Constants.SHOW_BLUR);
                self.sendBroadcast(intent);
            }

            @Override
            public void onSpinnerClosed() {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                self.sendBroadcast(intent);
            }
        });
        receivedOrderAdapter.setOnChangeStatusClickListener(this);
        receivedOrderAdapter.setOnForwardClickListener(this);
        receivedOrderAdapter.setOnItemClickListener(this);

        recentSearchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                keyword = listKeywords.get(position);
                edtKeyword.setText(keyword);
                edtKeyword.setSelection(keyword.length());
                search();
                layoutRecentSearch.setVisibility(View.GONE);
            }
        });
        recentSearchAdapter.setOnDeleteClickListener(new OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                String primaryKey = listKeywords.get(position);
                if (dbContext.deleteRecentSearcdItem(primaryKey)) {
                    listKeywords.remove(position);
                    recentSearchAdapter.notifyItemRemoved(position);
                }

            }
        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtKeyword.setText("");
            }
        });
        edtKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = edtKeyword.getText().toString().trim();
                    search();
                    layoutRecentSearch.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isSearch = true;

                listKeywords.clear();
                listKeywords.addAll(ConvertObject.convertRealmStringToString(dbContext.getRecentSearch()));
                Log.e(TAG, "size: " + listKeywords.size());
                recentSearchAdapter.notifyDataSetChanged();
                if (layoutSearch.getVisibility() == View.GONE) {
                    layoutSearch.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.GONE);
                    edtKeyword.requestFocus();
                    AppUtil.showKeyboard(self, edtKeyword);

                }
            }
        });
        edtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (layoutRecentSearch.getVisibility() == View.GONE)
                    layoutRecentSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    ivClear.setVisibility(View.VISIBLE);
                } else {
                    ivClear.setVisibility(View.INVISIBLE);
                }

                recentSearchAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtKeyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    layoutRecentSearch.setVisibility(View.VISIBLE);
                } else {
                    layoutRecentSearch.setVisibility(View.GONE);
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearch = false;

                if (layoutSearch.getVisibility() == View.VISIBLE) {
                    layoutSearch.setVisibility(View.GONE);
                    edtKeyword.setText("");
                    layoutRecentSearch.setVisibility(View.GONE);
                    toolbar.setVisibility(View.VISIBLE);
                    edtKeyword.clearFocus();
                    AppUtil.hideSoftKeyboard(self);
                    receivedOrderAdapter = new ReceivedOrderAdapter(rcvOrders, self, listOrders);
                    rcvOrders.setAdapter(receivedOrderAdapter);
                    processLoadMore();
                    receivedOrderAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            processItemClick(position);
                        }
                    });

                }
            }
        });
    }

    private void search() {
        if (edtKeyword.getText().toString().trim().length() != 0) {
            RecentSearchObj recentSearchObj = new RecentSearchObj();
            recentSearchObj.setKeyWord(edtKeyword.getText().toString().trim());
            dbContext.addToRecentSearch(recentSearchObj);
//                    listKeywords.add(edtKeyword.getText().toString().trim());
        }

        showDialog();
        ConnectServer.getResponseAPI().searchReceivedOrder(sellerId, keyword, filterStatus, 1).enqueue(new Callback<ReceivedOrderRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderRespond> call, Response<ReceivedOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalSearchPage = response.body().getAllPages();

                        listSearchResult.clear();
                        listSearchResult.addAll(response.body().getData());
                        receivedOrderAdapter = new ReceivedOrderAdapter(rcvOrders, self, listSearchResult);
                        rcvOrders.setAdapter(receivedOrderAdapter);

                        processLoadMore();

                        receivedOrderAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(self, ReceivedOrderDetail.class);
                                intent.putExtra(Constants.ORDER_ID, listSearchResult.get(position).getOrderId());
                                intent.putExtra(Constants.ORDER_DATE, listSearchResult.get(position).getOrderDate());
                                self.startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.slide_in_left,
                                        R.anim.slide_out_left);
                            }
                        });
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedOrderRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void searchMore() {
        ConnectServer.getResponseAPI().searchReceivedOrder(sellerId, keyword, filterStatus, searchPage).enqueue(new Callback<ReceivedOrderRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderRespond> call, Response<ReceivedOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listSearchResult.remove(listOrders.size() - 1);

                        listSearchResult.addAll(response.body().getData());
                        receivedOrderAdapter.setLoaded();
                        receivedOrderAdapter.notifyDataSetChanged();
                    } else {
                        listSearchResult.remove(listSearchResult.size() - 1);
                        receivedOrderAdapter.setLoaded();
                        receivedOrderAdapter.notifyItemRemoved(listSearchResult.size() - 1);
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedOrderRespond> call, Throwable t) {
                showToast(t.getMessage());
                listSearchResult.remove(listSearchResult.size() - 1);
                receivedOrderAdapter.setLoaded();
                receivedOrderAdapter.notifyItemRemoved(listSearchResult.size() - 1);
            }
        });
    }

    private void getListReceivedOrders() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getListReceivedOrder(sellerId, filterStatus, sortBy, 1).enqueue(new Callback<ReceivedOrderRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderRespond> call, Response<ReceivedOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listOrders.clear();
                        listOrders.addAll(response.body().getData());
                        receivedOrderAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                isFirst = false;
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedOrderRespond> call, Throwable t) {
                isFirst = false;
                showToast(t.getMessage());
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }
        });
    }

    private void getMoreListReceivedOrders() {
        ConnectServer.getResponseAPI().getListReceivedOrder(sellerId, filterStatus, sortBy, page).enqueue(new Callback<ReceivedOrderRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderRespond> call, Response<ReceivedOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listOrders.remove(listOrders.size() - 1);
                        listOrders.addAll(response.body().getData());
                        receivedOrderAdapter.setLoaded();
                        receivedOrderAdapter.notifyDataSetChanged();
                    } else {
                        listOrders.remove(listOrders.size() - 1);
                        receivedOrderAdapter.setLoaded();
                        receivedOrderAdapter.notifyItemRemoved(listOrders.size() - 1);
                        showToast(response.body().getMessage());
                    }
                }
                isFirst = false;
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedOrderRespond> call, Throwable t) {
                isFirst = false;
                showToast(t.getMessage());
                checkNoData();
                listOrders.remove(listOrders.size() - 1);
                receivedOrderAdapter.setLoaded();
                receivedOrderAdapter.notifyItemRemoved(listOrders.size() - 1);
            }
        });
    }

    private void changeOrderStatus(final TextView tvStatus, final int orderId, final int orderStatus, final int position) {
        showDialog();
        ConnectServer.getResponseAPI().changeStatusOrder(orderId, orderStatus).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        ReceivedOrderRespond.StatusAction statusAction = listOrders.get(position).getStatusAction().get(0);
                        if (orderStatus == IN_PROGRESS) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(false);
                            statusAction.setStatusShiped(true);
                            listOrders.get(position).setStatus(IN_PROGRESS);
                            receivedOrderAdapter.notifyItemChanged(position);
                            tvStatus.setText(getResources().getString(R.string.inprogress));
                        } else if (orderStatus == TO_SHIP) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(true);
                            statusAction.setStatusShiped(false);
                            listOrders.get(position).setStatus(TO_SHIP);
                            receivedOrderAdapter.notifyItemChanged(position);
                            tvStatus.setText(getResources().getString(R.string.to_ship));
                        } else if (orderStatus == SHIPPED) {
                            statusAction.setStatusProcessed(true);
                            statusAction.setStatusToShip(true);
                            statusAction.setStatusShiped(true);
                            listOrders.get(position).setStatus(SHIPPED);
                            receivedOrderAdapter.notifyItemChanged(position);
                            tvStatus.setText(getResources().getString(R.string.shipped));
                        }

                        showToast(response.body().getMessage());
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

    private void showChangeOrderStatusDialog(final TextView tvStatus, final int position) {
        View view = self.getLayoutInflater().inflate(R.layout.dialog_change_order_status, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setView(view);

        LinearLayout layoutInProgress = view.findViewById(R.id.layout_in_progress);
        LinearLayout layoutToShip = view.findViewById(R.id.layout_to_ship);
        LinearLayout layoutShipped = view.findViewById(R.id.layout_shipped);
        LinearLayout layoutDelivered = view.findViewById(R.id.layout_delivered);

        final ReceivedOrderRespond.Data data = listOrders.get(position);

        layoutInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.getStatusAction().get(0).getStatusProcessed()) {
                    orderStatus = IN_PROGRESS;
                    changeOrderStatus(tvStatus, data.getOrderId(), orderStatus, position);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_processed));
                }
            }
        });
        layoutToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getStatus() == NEW) {
                    showToast(getResources().getString(R.string.has_not_been_processed));
                } else if (data.getStatus() == IN_PROGRESS) {
                    orderStatus = TO_SHIP;
                    changeOrderStatus(tvStatus, data.getOrderId(), orderStatus, position);
                    dialog.dismiss();
                } else if (data.getStatus() == TO_SHIP) {
                    showToast(getResources().getString(R.string.has_been_being_shipped));
                } else if (data.getStatus() == SHIPPED) {
                    showToast(getResources().getString(R.string.has_been_shipped));
                }
            }
        });
        layoutShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getStatus() == NEW) {
                    showToast(getResources().getString(R.string.has_not_been_processed));
                } else if (data.getStatus() == IN_PROGRESS) {
                    showToast(getResources().getString(R.string.has_not_been_shipped));
                } else if (data.getStatus() == TO_SHIP) {
                    orderStatus = SHIPPED;
                    changeOrderStatus(tvStatus, data.getOrderId(), orderStatus, position);
                    dialog.dismiss();
                } else if (data.getStatus() == SHIPPED) {
                    showToast(getResources().getString(R.string.has_been_shipped));
                }
            }
        });
        layoutDelivered.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                orderStatus = 4;
                changeOrderStatus(tvStatus, data.getOrderId(), orderStatus, position);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()

        {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                self.sendBroadcast(intent);
            }
        });

    }

    private void checkNoData() {
        if (listOrders.size() == 0) {
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
                getListReceivedOrders();
                isFirstLoad = true;
            }
        }
    }

    @Override
    public void onChangeStatus(View view, int position) {
        Intent intent = new Intent();
        intent.setAction(Constants.SHOW_BLUR);
        self.sendBroadcast(intent);

        showChangeOrderStatusDialog((TextView) view, position);
    }

    @Override
    public void onForward(View view, int position) {
//        Intent intent = new Intent(self, ReceivedOrderDetail.class);
//        intent.putExtra(Constants.ORDER_ID, listOrders.get(position).getOrderId());
//        intent.putExtra(Constants.ORDER_DATE, listOrders.get(position).getOrderDate());
//        self.startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.slide_in_left,
//                R.anim.slide_out_left);
        int orderId = listOrders.get(position).getOrderId();
        long orderDate = listOrders.get(position).getOrderDate();

        getOrderDetail(orderId, orderDate);
    }

    private void getOrderDetail(final int orderId, final long orderDate) {
        showDialog();
        ConnectServer.getResponseAPI().getReceivedOrderDetail(orderId).enqueue(new Callback<ReceivedOrderDetailRespond>() {
            @Override
            public void onResponse(Call<ReceivedOrderDetailRespond> call, Response<ReceivedOrderDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        boolean isForwarded = response.body().isForward();
                        List<ReceivedOrderDetailRespond.Data> listProducts = new ArrayList<>();
                        listProducts.clear();
                        listProducts.addAll(response.body().getData());


                        if (!isForwarded) {
                            showForwardOrderDialog(orderId, orderDate, listProducts);
                        } else {
                            showToast(getResources().getString(R.string.has_been_forwarded));
                        }

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

    private void showForwardOrderDialog(final int orderId, final long orderDate, List<ReceivedOrderDetailRespond.Data> listProducts) {
        Intent intent = new Intent();
        intent.setAction(Constants.SHOW_BLUR);
        self.sendBroadcast(intent);

        ForwardOrderDialog forwardOrderDialog = new ForwardOrderDialog(self, orderId, orderDate, listProducts);
        forwardOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        forwardOrderDialog.show();

        forwardOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                self.sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        processItemClick(position);
    }

    private void processItemClick(int position) {
        Intent intent = new Intent(self, ReceivedOrderDetail.class);
        intent.putExtra(Constants.ORDER_ID, listOrders.get(position).getOrderId());
        intent.putExtra(Constants.ORDER_DATE, listOrders.get(position).getOrderDate());
        self.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

//    FinishForwardOrderDialog finishForwardOrderDialog;
//
//    @Override
//    public void onForwardSuccess(String email) {
//        finishForwardOrderDialog = new FinishForwardOrderDialog(self, email);
//        finishForwardOrderDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        finishForwardOrderDialog.show();
//
//        Intent intent = new Intent();
//        intent.setAction(Constants.SHOW_BLUR);
//        self.sendBroadcast(intent);
//
//        finishForwardOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                Intent intent = new Intent();
//                intent.setAction(Constants.HIDE_BLUR);
//                self.sendBroadcast(intent);
//            }
//        });
//    }
//
//    @Override
//    public void onGoToHomeClick() {
//        Intent intent = new Intent();
//        intent.setAction(Constants.SHOW_HOME);
//        self.sendBroadcast(intent);
//        finishForwardOrderDialog.dismiss();
//    }
//
//    @Override
//    public void onGoToOrderClick() {
//        finishForwardOrderDialog.dismiss();
//    }
}
