package com.impakter.seller.fragment.seller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.activity.DetailActivity;
import com.impakter.seller.activity.MessageDetailActivity;
import com.impakter.seller.activity.OtherProfileActivity;
import com.impakter.seller.activity.ReceivedOrderDetail;
import com.impakter.seller.adapter.ActivityAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dialog.CommentDialog;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.NotificationRespond;
import com.impakter.seller.object.ProductDetailRespond;
import com.impakter.seller.object.seller.CreateRoomRespond;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityFragment extends BaseFragment {
    private View rootView;
    private RecyclerView rcvActivity;
    private ActivityAdapter activityAdapter;
    private List<NotificationRespond.Data> listActivities = new ArrayList<>();
    private TextView tvNoData;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int sellerId;
    private int page = 1;
    private int totalPage;
    private boolean isFirstLoad = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_activity, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        progressBar = rootView.findViewById(R.id.progress_bar);
        tvNoData = rootView.findViewById(R.id.tv_no_data);
        swipeRefreshLayout = rootView.findViewById(R.id.swf_layout);

        rcvActivity = rootView.findViewById(R.id.rcv_activity);
        rcvActivity.setHasFixedSize(true);
        rcvActivity.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        sellerId = preferenceManager.getUserLogin().getId();

        activityAdapter = new ActivityAdapter(rcvActivity, self, listActivities);
        rcvActivity.setAdapter(activityAdapter);

        activityAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listActivities.add(null);
                    activityAdapter.notifyItemInserted(listActivities.size() - 1);
                    getMoreNotifications();
                }

            }
        });
        if (getUserVisibleHint()) {
            getNotifications();
        }
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                activityAdapter.setLoaded();
                activityAdapter.notifyDataSetChanged();
                getNotifications();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        activityAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NotificationRespond.Data data = listActivities.get(position);
                int type = listActivities.get(position).getType();
                switchScreen(data);
            }
        });
    }

    private void switchScreen(NotificationRespond.Data data) {
        switch (data.getType()) {
            case Constants.TYPE_LIKE_PRODUCT:
            case Constants.TYPE_FOLLOW:
                int userId = data.getParams().getId();
                Intent intentShowProfile = new Intent(self, OtherProfileActivity.class);
                intentShowProfile.putExtra(Constants.OTHER_USER_ID, userId);
                gotoActivity(intentShowProfile);
                break;
            case Constants.TYPE_MESSAGE:
                int receiverId = data.getParams().getId();
                createConversation(receiverId);
                break;
            case Constants.TYPE_RATE_PRODUCT:
                int productId = data.getParams().getId();
                Intent intentProductDetail = new Intent(self, DetailActivity.class);
                intentProductDetail.putExtra(Constants.PRODUCT_ID, productId);
                gotoActivity(intentProductDetail);
                break;
            case Constants.TYPE_COMMENT_PRODUCT:
                int proId = data.getParams().getId();
                getProductDetail(proId, data);
                break;
            case Constants.TYPE_RECEIVE_ORDER:
                int orderId = data.getParams().getId();
                Intent intent = new Intent(self, ReceivedOrderDetail.class);
                intent.putExtra(Constants.ORDER_ID, orderId);
                gotoActivity(intent);
                break;
        }
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

    private void showCommentScreen(ProductDetailRespond.Data product, NotificationRespond.Data notification) {

        CommentDialog commentDialog = new CommentDialog(self, product, notification);
        commentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        commentDialog.show();

        commentDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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

    private void getProductDetail(final int id, final NotificationRespond.Data notification) {
        showDialog();
        ConnectServer.getResponseAPI().getProductDetail(id).enqueue(new Callback<ProductDetailRespond>() {
            @Override
            public void onResponse(@NonNull Call<ProductDetailRespond> call, @NonNull Response<ProductDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        ProductDetailRespond.Data product = response.body().getData();
                        showCommentScreen(product, notification);
                    } else {
                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        closeDialog();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(@NonNull Call<ProductDetailRespond> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                closeDialog();
            }
        });
    }

//    private void getListComment(int productId, final ProductDetailRespond.Data product, final String content) {
////        showDialog();
//        ConnectServer.getResponseAPI().getListComment(productId, 1).enqueue(new Callback<ListCommentRespond>() {
//            @Override
//            public void onResponse(Call<ListCommentRespond> call, Response<ListCommentRespond> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().equals(Constants.SUCCESS) || response.body().getStatus().equals("SUCESS")) {
//                        if (response.body().getData().size() != 0) {
//                            ListCommentRespond.Data comment = response.body().getData().get(0);
//                            int totalComment = response.body().getTotalComment();
//                            showCommentScreen(product, comment, totalComment, content);
//
//                        } else {
//                            Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//                closeDialog();
//            }
//
//            @Override
//            public void onFailure(Call<ListCommentRespond> call, Throwable t) {
//                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
//                closeDialog();
//            }
//        });
//    }

    private void getNotifications() {
        progressBar.setVisibility(View.VISIBLE);
        ConnectServer.getResponseAPI().getNotification(sellerId, 1).enqueue(new Callback<NotificationRespond>() {
            @Override
            public void onResponse(Call<NotificationRespond> call, Response<NotificationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        totalPage = response.body().getAllPages();

                        listActivities.clear();
                        listActivities.addAll(response.body().getData());

                        activityAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<NotificationRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getMoreNotifications() {
        ConnectServer.getResponseAPI().getNotification(sellerId, page).enqueue(new Callback<NotificationRespond>() {
            @Override
            public void onResponse(Call<NotificationRespond> call, Response<NotificationRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        listActivities.remove(listActivities.size() - 1);
                        listActivities.addAll(response.body().getData());
                        activityAdapter.setLoaded();
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        listActivities.remove(listActivities.size() - 1);
                        activityAdapter.setLoaded();
                        activityAdapter.notifyItemRemoved(listActivities.size() - 1);
                    }
                }
                checkNoData();

            }

            @Override
            public void onFailure(Call<NotificationRespond> call, Throwable t) {
                showToast(t.getMessage());
                checkNoData();
                listActivities.remove(listActivities.size() - 1);
                activityAdapter.setLoaded();
                activityAdapter.notifyItemRemoved(listActivities.size() - 1);
            }
        });
    }

    private void checkNoData() {
        if (listActivities.size() == 0) {
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
                getNotifications();
                isFirstLoad = true;
            }
        }
    }
}
