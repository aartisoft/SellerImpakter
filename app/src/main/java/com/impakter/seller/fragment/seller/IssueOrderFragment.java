package com.impakter.seller.fragment.seller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.IssueOrderDetailActivity;
import com.impakter.seller.activity.MessageDetailActivity;
import com.impakter.seller.adapter.CommonAdapter;
import com.impakter.seller.adapter.seller.IssueOrderAdapter;
import com.impakter.seller.adapter.seller.RecentSearchAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.RecentSearchObj;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnForwardClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.CommonObj;
import com.impakter.seller.object.seller.CreateRoomRespond;
import com.impakter.seller.object.seller.ReceivedIssueRespond;
import com.impakter.seller.utils.AppUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueOrderFragment extends BaseFragment implements OnChangeStatusClickListener, OnForwardClickListener, OnItemClickListener {
    private final int RESOLVED = 5;
    private final int REVIEWING = 2;

    private View rootView;
    private Spinner spSortBy;

    private RecyclerView rcvOrders;
    private List<ReceivedIssueRespond.Data> listOrders = new ArrayList<>();
    private List<ReceivedIssueRespond.Data> listSearchResult = new ArrayList<>();
    private IssueOrderAdapter issueOrderAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvNoData;

    private int sellerId;
    private String filterStatus = "";
    private String sortBy = "";

    private int page = 1;
    private int totalPage;

    private int searchPage = 1;
    private int totalSearchPage;

    private boolean isFirst = true;
    private boolean isFirstLoad = false;

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
        rootView = inflater.inflate(R.layout.fragment_issue_order, container, false);
        initViews();
        DbContext.init(self);
        dbContext = DbContext.getInstance();
        setUpToolbar();
        initData();
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

        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        progressBar = rootView.findViewById(R.id.progress_bar);

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

        issueOrderAdapter = new IssueOrderAdapter(rcvOrders, self, listOrders);
        rcvOrders.setAdapter(issueOrderAdapter);

        processLoadMore();

        if (getUserVisibleHint())
            getListReceivedOrders();
    }

    private void processLoadMore() {
        issueOrderAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                if (!isSearch) {
                    page++;
                    if (page <= totalPage) {
                        listOrders.add(null);
                        issueOrderAdapter.notifyItemInserted(listOrders.size() - 1);
                        getMoreReceivedOrders();
                    }
                } else {
                    searchPage++;
                    if (searchPage <= totalSearchPage) {
                        listSearchResult.add(null);
                        issueOrderAdapter.notifyItemInserted(listSearchResult.size() - 1);
                        searchMore();
                    }
                }


            }
        });
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
                issueOrderAdapter.setLoaded();
                issueOrderAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
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

                page = 1;
                issueOrderAdapter.setLoaded();
                issueOrderAdapter.notifyDataSetChanged();

                if (!isFirst)
                    getListReceivedOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        issueOrderAdapter.setOnChangeStatusClickListener(this);
        issueOrderAdapter.setOnForwardClickListener(this);
        issueOrderAdapter.setOnItemClickListener(this);

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
                    issueOrderAdapter = new IssueOrderAdapter(rcvOrders, self, listOrders);
                    rcvOrders.setAdapter(issueOrderAdapter);
                    processLoadMore();
                    issueOrderAdapter.setOnItemClickListener(new OnItemClickListener() {
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
        }

        showDialog();
        ConnectServer.getResponseAPI().searchIssueOrder(sellerId, keyword, sortBy, 1).enqueue(new Callback<ReceivedIssueRespond>() {
            @Override
            public void onResponse(Call<ReceivedIssueRespond> call, Response<ReceivedIssueRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalSearchPage = response.body().getAllPages();

                        listSearchResult.clear();
                        listSearchResult.addAll(response.body().getData());
                        issueOrderAdapter = new IssueOrderAdapter(rcvOrders, self, listSearchResult);
                        rcvOrders.setAdapter(issueOrderAdapter);

                        processLoadMore();

                        issueOrderAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(self, IssueOrderDetailActivity.class);
                                intent.putExtra(Constants.ORDER_ID, listSearchResult.get(position).getOrderReturnId());
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
            public void onFailure(Call<ReceivedIssueRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void searchMore() {
        ConnectServer.getResponseAPI().searchIssueOrder(sellerId, keyword, sortBy, searchPage).enqueue(new Callback<ReceivedIssueRespond>() {
            @Override
            public void onResponse(Call<ReceivedIssueRespond> call, Response<ReceivedIssueRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listSearchResult.remove(listOrders.size() - 1);

                        listSearchResult.addAll(response.body().getData());
                        issueOrderAdapter.setLoaded();
                        issueOrderAdapter.notifyDataSetChanged();
                    } else {
                        listSearchResult.remove(listSearchResult.size() - 1);
                        issueOrderAdapter.setLoaded();
                        issueOrderAdapter.notifyItemRemoved(listSearchResult.size() - 1);
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedIssueRespond> call, Throwable t) {
                showToast(t.getMessage());
                listSearchResult.remove(listSearchResult.size() - 1);
                issueOrderAdapter.setLoaded();
                issueOrderAdapter.notifyItemRemoved(listSearchResult.size() - 1);
            }
        });
    }

    private void getListReceivedOrders() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getReceivedIssue(sellerId, 1).enqueue(new Callback<ReceivedIssueRespond>() {
            @Override
            public void onResponse(Call<ReceivedIssueRespond> call, Response<ReceivedIssueRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listOrders.clear();
                        listOrders.addAll(response.body().getData());
                        issueOrderAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                isFirst = false;
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedIssueRespond> call, Throwable t) {
                isFirst = false;
                showToast(t.getMessage());
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }
        });
    }

    private void getMoreReceivedOrders() {
        ConnectServer.getResponseAPI().getReceivedIssue(sellerId, page).enqueue(new Callback<ReceivedIssueRespond>() {
            @Override
            public void onResponse(Call<ReceivedIssueRespond> call, Response<ReceivedIssueRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {

                        listOrders.remove(listOrders.size() - 1);
                        listOrders.addAll(response.body().getData());
                        issueOrderAdapter.setLoaded();
                        issueOrderAdapter.notifyDataSetChanged();

                    } else {
                        showToast(response.body().getMessage());
                        listOrders.remove(listOrders.size() - 1);
                        issueOrderAdapter.setLoaded();
                        issueOrderAdapter.notifyItemRemoved(listOrders.size() - 1);
                    }
                }
                isFirst = false;
                checkNoData();
            }

            @Override
            public void onFailure(Call<ReceivedIssueRespond> call, Throwable t) {
                isFirst = false;
                showToast(t.getMessage());
                checkNoData();
                listOrders.remove(listOrders.size() - 1);
                issueOrderAdapter.setLoaded();
                issueOrderAdapter.notifyItemRemoved(listOrders.size() - 1);
            }
        });
    }

    private void changeIssueStatus(final TextView tvStatus, int orderReturnId, final int status, final int position) {
        showDialog();
        ConnectServer.getResponseAPI().changeIssueStatus(sellerId, orderReturnId, status).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());
                        if (status == RESOLVED) { //5: resolved
                            listOrders.get(position).setStatus(RESOLVED);
                            issueOrderAdapter.notifyItemChanged(position);
                            tvStatus.setText(getResources().getString(R.string.resolved));
                        } else if (status == REVIEWING) {
                            listOrders.get(position).setStatus(REVIEWING);
                            issueOrderAdapter.notifyItemChanged(position);
                            tvStatus.setText(getResources().getString(R.string.reviewing));
                        }
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

    private void showChangeIssueStatusDialog(final TextView tvStatus, final int orderId, final int status, final int position) {
        View view = self.getLayoutInflater().inflate(R.layout.dialog_change_issue_order_status, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setView(view);

        LinearLayout layoutInProgress = view.findViewById(R.id.layout_in_progress);
        LinearLayout layoutSolved = view.findViewById(R.id.layout_solved);
        LinearLayout layoutReviewing = view.findViewById(R.id.layout_reviewing);

        layoutInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                issueStatus = 1;
//                changeIssueStatus(orderId, issueStatus);
//                dialog.dismiss();
                Toast.makeText(self, getResources().getString(R.string.has_been_processed), Toast.LENGTH_SHORT).show();
            }
        });
        layoutSolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status != RESOLVED) {
                    changeIssueStatus(tvStatus, orderId, RESOLVED, position);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_resolved));
                }

            }
        });
        layoutReviewing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == REVIEWING) {
                    showToast(getResources().getString(R.string.has_been_being_reviewed));
                } else if (status != RESOLVED) {
                    changeIssueStatus(tvStatus, orderId, REVIEWING, position);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_resolved));
                }

            }
        });
        dialog = builder.create();
//        Bitmap map = BlurUtils.takeScreenShot(self);
//
//        Bitmap fast = BlurUtils.fastBlur(map, 10);
//        final Drawable draw = new BitmapDrawable(getResources(), fast);
//        dialog.getWindow().setBackgroundDrawable(draw);
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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

        int issueStatus = listOrders.get(position).getStatus();
        showChangeIssueStatusDialog((TextView) view, listOrders.get(position).getOrderReturnId(), issueStatus, position);
    }

    @Override
    public void onForward(View view, int position) {
//        Intent intent = new Intent(self, IssueOrderDetailActivity.class);
//        intent.putExtra(Constants.ORDER_ID, listOrders.get(position).getOrderReturnId());
//        self.startActivity(intent);
//        getActivity().overridePendingTransition(R.anim.slide_in_left,
//                R.anim.slide_out_left);
        createConversation(listOrders.get(position).getUserId());
    }

    @Override
    public void onItemClick(View view, int position) {
        processItemClick(position);
    }

    private void processItemClick(int position) {
        Intent intent = new Intent(self, IssueOrderDetailActivity.class);
        intent.putExtra(Constants.ORDER_ID, listOrders.get(position).getOrderReturnId());
        intent.putExtra(Constants.ORDER_DATE, listOrders.get(position).getOrderDate());
        self.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

    private void createConversation(final int otherUserId) {
        showDialog();
        ConnectServer.getResponseAPI().createConversation(sellerId, otherUserId).enqueue(new Callback<CreateRoomRespond>() {
            @Override
            public void onResponse(Call<CreateRoomRespond> call, Response<CreateRoomRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.CONVERSATION_ID, response.body().getConversationId());
                        bundle.putInt(Constants.RECEIVER_ID, otherUserId);
                        gotoActivity(self, MessageDetailActivity.class, bundle);
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<CreateRoomRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }
}
