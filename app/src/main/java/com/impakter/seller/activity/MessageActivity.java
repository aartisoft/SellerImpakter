package com.impakter.seller.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.impakter.seller.BaseActivity;
import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.CommonAdapter;
import com.impakter.seller.adapter.MessageAdapter;
import com.impakter.seller.adapter.seller.ConversationRespond;
import com.impakter.seller.adapter.seller.RecentSearchAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.RecentSearchObj;
import com.impakter.seller.dialog.SendMessageDialog;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CommonObj;
import com.impakter.seller.utils.AppUtil;
import com.impakter.seller.widget.CustomSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends BaseActivity implements View.OnClickListener, OnItemClickListener {
    private RecyclerView rcvMessage;
    private MessageAdapter messageAdapter;
    private List<ConversationRespond.Data> listChatRooms = new ArrayList<>();
    private List<ConversationRespond.Data> listSearchResult = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoData;
    private ProgressBar progressBar;
    private CustomSpinner spStatus;
    private FloatingActionButton fabAddNewMessage;

    private int userId;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);
        initViews();
        DbContext.init(self);
        dbContext = DbContext.getInstance();
        setUpToolbar();
        initData();
        initSpinnerMessageStatus();
        initControl();
    }

    private void setUpToolbar() {
//        LinearLayout toolbar = ((MainActivity) self).getToolbar();
        ivSearch = findViewById(R.id.iv_search);
        TextView tvTitle = findViewById(R.id.tv_title);
        LinearLayout btnEdit = findViewById(R.id.btn_edit);
        btnEdit.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ivClear = findViewById(R.id.iv_clear);
        layoutSearch = findViewById(R.id.layout_search);
        layoutRecentSearch = findViewById(R.id.layout_recent_search);
        edtKeyword = findViewById(R.id.edt_keyword);
        ivBack = findViewById(R.id.iv_back);

        tvNoData = findViewById(R.id.tv_no_data);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swf_layout);

        spStatus = findViewById(R.id.sp_status);
        fabAddNewMessage = findViewById(R.id.fab_add_new_message);

        rcvMessage = findViewById(R.id.rcv_message);
        rcvMessage.setHasFixedSize(true);
        rcvMessage.setLayoutManager(new LinearLayoutManager(self));

        rcvRecentSearch = findViewById(R.id.rcv_recent_search);
        rcvRecentSearch.setHasFixedSize(true);
        rcvRecentSearch.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initSpinnerMessageStatus() {
        ArrayList<CommonObj> listStatus = new ArrayList<>();
        listStatus.add(new CommonObj(-1, getResources().getString(R.string.all)));
        listStatus.add(new CommonObj(1, getResources().getString(R.string.sent)));
        listStatus.add(new CommonObj(2, getResources().getString(R.string.received)));
        listStatus.add(new CommonObj(3, getResources().getString(R.string.drafts)));

        CommonAdapter statusAdapter = new CommonAdapter(self, listStatus);
        spStatus.setAdapter(statusAdapter);
    }

    private void initData() {
        if (isLoggedIn())
            userId = preferencesManager.getUserLogin().getId();

        recentSearchAdapter = new RecentSearchAdapter(self, listKeywords);
        rcvRecentSearch.setAdapter(recentSearchAdapter);

        messageAdapter = new MessageAdapter(rcvMessage, self, listChatRooms);
        rcvMessage.setAdapter(messageAdapter);

        processLoadMore();

        getListChatRooms();
    }

    private void processLoadMore() {
        messageAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (!isSearch) {
                    page++;
                    if (page <= totalPage) {
                        listChatRooms.add(null);
                        messageAdapter.notifyItemInserted(listChatRooms.size() - 1);
                        getMoreListChatRooms();
                    }
                } else {
                    searchPage++;
                    if (searchPage <= totalSearchPage) {
                        listSearchResult.add(null);
                        messageAdapter.notifyItemInserted(listSearchResult.size() - 1);
                        searchMore();
                    }
                }


            }
        });
    }

    private void initControl() {
        fabAddNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessageDialog sendMessageDialog = new SendMessageDialog(self);
                sendMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                sendMessageDialog.show();

                sendMessageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Intent intent = new Intent();
                        intent.setAction(Constants.HIDE_BLUR);
                        self.sendBroadcast(intent);
                    }
                });
                Intent intent = new Intent();
                intent.setAction(Constants.SHOW_BLUR);
                self.sendBroadcast(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isSearch) {
                    page = 1;
                    getListChatRooms();
                } else {
                    searchPage = 1;
                    search();
                }
                messageAdapter.setLoaded();
                messageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        messageAdapter.setOnItemClickListener(this);

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
                    messageAdapter = new MessageAdapter(rcvMessage, self, listChatRooms);
                    rcvMessage.setAdapter(messageAdapter);

                    processLoadMore();

                    messageAdapter.setOnItemClickListener(new OnItemClickListener() {
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
        ConnectServer.getResponseAPI().searchContact(userId, keyword, 1).enqueue(new Callback<ConversationRespond>() {
            @Override
            public void onResponse(Call<ConversationRespond> call, Response<ConversationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalSearchPage = response.body().getAllPages();

                        listSearchResult.clear();
                        listSearchResult.addAll(response.body().getData());
                        Collections.sort(listSearchResult, new Comparator<ConversationRespond.Data>() {
                            @Override
                            public int compare(ConversationRespond.Data o1, ConversationRespond.Data o2) {
                                return (int) (o2.getLastTime() - o1.getLastTime());
                            }
                        });
                        messageAdapter = new MessageAdapter(rcvMessage, self, listSearchResult);
                        rcvMessage.setAdapter(messageAdapter);

                        processLoadMore();

                        messageAdapter.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                int receiverId;
                                if (listChatRooms.get(position).getSenderId() == userId) {
                                    receiverId = listChatRooms.get(position).getReceiverId();
                                } else {
                                    receiverId = listChatRooms.get(position).getSenderId();
                                }
                                Bundle bundle = new Bundle();
                                bundle.putInt(Constants.CONVERSATION_ID, listChatRooms.get(position).getConversationId());
                                bundle.putInt(Constants.RECEIVER_ID, receiverId);

                                Intent intent = new Intent(self, MessageDetailActivity.class);
                                intent.putExtras(bundle);

                                self.startActivity(intent);
                                self.overridePendingTransition(R.anim.slide_in_left,
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
            public void onFailure(Call<ConversationRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void searchMore() {
        ConnectServer.getResponseAPI().searchContact(userId, keyword, searchPage).enqueue(new Callback<ConversationRespond>() {
            @Override
            public void onResponse(Call<ConversationRespond> call, Response<ConversationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listSearchResult.remove(listSearchResult.size() - 1);

                        listSearchResult.addAll(response.body().getData());
                        Collections.sort(listSearchResult, new Comparator<ConversationRespond.Data>() {
                            @Override
                            public int compare(ConversationRespond.Data o1, ConversationRespond.Data o2) {
                                return (int) (o2.getLastTime() - o1.getLastTime());
                            }
                        });
                        messageAdapter.setLoaded();
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        listSearchResult.remove(listSearchResult.size() - 1);
                        messageAdapter.setLoaded();
                        messageAdapter.notifyItemRemoved(listSearchResult.size() - 1);
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<ConversationRespond> call, Throwable t) {
                showToast(t.getMessage());
                listSearchResult.remove(listSearchResult.size() - 1);
                messageAdapter.setLoaded();
                messageAdapter.notifyItemRemoved(listSearchResult.size() - 1);
            }
        });
    }

    private void getListChatRooms() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getListConversation(userId, 1).enqueue(new Callback<ConversationRespond>() {
            @Override
            public void onResponse(Call<ConversationRespond> call, Response<ConversationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listChatRooms.clear();
                        listChatRooms.addAll(response.body().getData());
                        Collections.sort(listChatRooms, new Comparator<ConversationRespond.Data>() {
                            @Override
                            public int compare(ConversationRespond.Data o1, ConversationRespond.Data o2) {
                                return (int) (o2.getLastTime() - o1.getLastTime());
                            }
                        });
                        messageAdapter.notifyDataSetChanged();
                    }
                }
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }

            @Override
            public void onFailure(Call<ConversationRespond> call, Throwable t) {
                showToast(t.getMessage());
                progressBar.setVisibility(View.GONE);
                checkNoData();
            }
        });
    }

    private void getMoreListChatRooms() {
        ConnectServer.getResponseAPI().getListConversation(userId, page).enqueue(new Callback<ConversationRespond>() {
            @Override
            public void onResponse(Call<ConversationRespond> call, Response<ConversationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listChatRooms.remove(listChatRooms.size() - 1);

                        listChatRooms.addAll(response.body().getData());
                        Collections.sort(listChatRooms, new Comparator<ConversationRespond.Data>() {
                            @Override
                            public int compare(ConversationRespond.Data o1, ConversationRespond.Data o2) {
                                return (int) (o2.getLastTime() - o1.getLastTime());
                            }
                        });
                        messageAdapter.setLoaded();
                        messageAdapter.notifyDataSetChanged();
                    } else {
                        listChatRooms.remove(listChatRooms.size() - 1);
                        messageAdapter.setLoaded();
                        messageAdapter.notifyItemRemoved(listChatRooms.size() - 1);
                    }
                }
                checkNoData();
            }

            @Override
            public void onFailure(Call<ConversationRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                listChatRooms.remove(listChatRooms.size() - 1);
                messageAdapter.setLoaded();
                messageAdapter.notifyItemRemoved(listChatRooms.size() - 1);
            }
        });
    }

    private void checkNoData() {
        if (listChatRooms.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        processItemClick(position);
    }

    private void processItemClick(int position) {
        int receiverId;
        if (listChatRooms.get(position).getSenderId() == userId) {
            receiverId = listChatRooms.get(position).getReceiverId();
        } else {
            receiverId = listChatRooms.get(position).getSenderId();
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CONVERSATION_ID, listChatRooms.get(position).getConversationId());
        bundle.putInt(Constants.RECEIVER_ID, receiverId);

        Intent intent = new Intent(self, MessageDetailActivity.class);
        intent.putExtras(bundle);

        self.startActivity(intent);
        self.overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }
}
