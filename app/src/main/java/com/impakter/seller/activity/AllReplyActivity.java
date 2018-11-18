package com.impakter.seller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ReplyCommentAdapter;
import com.impakter.seller.adapter.seller.SubCommentAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.UserObj;
import com.impakter.seller.object.seller.CommentItemRespond;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllReplyActivity extends BaseActivity {
    private ImageView ivBack, ivSend;
    private CircleImageView ivAvatar, ivMyAvatar;
    private TextView tvName;
    private TextView tvContent, tvTime;
    private EditText edtContent;
    private RecyclerView rcvSubComment;
    private List<ReplyCommentRespond.Data> listComments = new ArrayList<>();
    private ReplyCommentAdapter commentAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private ListCommentRespond.Data commentItem;
    private int page = 1;
    private int totalPage;
    private UserObj userObj;
    private int productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_reply);
        initViews();
        initData();
        initControl();

    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swf_layout);

        ivBack = findViewById(R.id.iv_back);
        ivSend = findViewById(R.id.iv_send);
        ivAvatar = findViewById(R.id.iv_avatar);
        ivMyAvatar = findViewById(R.id.iv_my_avatar);

        tvName = findViewById(R.id.tv_name);
        tvContent = findViewById(R.id.tv_content);
        tvTime = findViewById(R.id.tv_time);
        edtContent = findViewById(R.id.edt_content);

        rcvSubComment = findViewById(R.id.rcv_sub_comment);
        rcvSubComment.setHasFixedSize(true);
        rcvSubComment.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        if (isLoggedIn()) {
            userObj = preferencesManager.getUserLogin();
        }
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                commentItem = bundle.getParcelable(Constants.COMMENT_ITEM);
                productId = bundle.getInt(Constants.PRODUCT_ID);
            }
        }
        tvName.setText(commentItem.getFullName());
        tvContent.setText(commentItem.getContent());
        tvTime.setText(DateUtils.getRelativeTimeSpanString(commentItem.getDate() * 1000L, System.currentTimeMillis(),
                0, DateUtils.FORMAT_ABBREV_ALL));
        Glide.with(self).load(commentItem.getAvatar()).into(ivAvatar);
        Glide.with(self).load(userObj.getAvatar()).into(ivMyAvatar);


        commentAdapter = new ReplyCommentAdapter(rcvSubComment, self, listComments);
        rcvSubComment.setAdapter(commentAdapter);
        commentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listComments.add(null);
                    commentAdapter.notifyItemInserted(listComments.size() - 1);
                    getMoreReplyComment();
                }

            }
        });
        getListReplyComment();
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                commentAdapter.setLoaded();
                commentAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edtContent.getText().toString().trim();
                if (content.length() != 0) {
                    replyTo(commentItem.getId(), content);
                }
            }
        });
    }

    private void getListReplyComment() {
        showDialog();
        ConnectServer.getResponseAPI().getListReplyComment(commentItem.getId(), 1).enqueue(new Callback<ReplyCommentRespond>() {
            @Override
            public void onResponse(Call<ReplyCommentRespond> call, Response<ReplyCommentRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS) || response.body().getStatus().equals("SUCESS")) {

                        totalPage = response.body().getAllPage();

                        listComments.clear();
                        listComments.addAll(response.body().getData());
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReplyCommentRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    private void getMoreReplyComment() {
        showDialog();
        ConnectServer.getResponseAPI().getListReplyComment(commentItem.getId(), page).enqueue(new Callback<ReplyCommentRespond>() {
            @Override
            public void onResponse(Call<ReplyCommentRespond> call, Response<ReplyCommentRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS) || response.body().getStatus().equals("SUCESS")) {
                        listComments.remove(listComments.size() - 1);

                        listComments.addAll(response.body().getData());
                        commentAdapter.setLoaded();
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        listComments.remove(listComments.size() - 1);
                        commentAdapter.setLoaded();
                        commentAdapter.notifyItemRemoved(listComments.size() - 1);
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReplyCommentRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
                listComments.remove(listComments.size() - 1);
                commentAdapter.setLoaded();
                commentAdapter.notifyItemRemoved(listComments.size() - 1);
            }
        });
    }

    private void replyTo(final int commentId, String content) {
        showDialog();
        if (userObj != null) {
            int userId = userObj.getId();
            ConnectServer.getResponseAPI().replyTo(userId, productId, commentId, content).enqueue(new Callback<CommentItemRespond>() {
                @Override
                public void onResponse(Call<CommentItemRespond> call, Response<CommentItemRespond> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals(Constants.SUCCESS)) {
                            CommentItemRespond.Data data = response.body().getData().get(0);
                            ReplyCommentRespond.Data reply = new ReplyCommentRespond.Data();
                            reply.setId(data.getReplyToId());
                            reply.setAvatar(data.getUserAvatar());
                            reply.setContent(data.getContent());
                            reply.setDate(data.getDateCreated());
                            reply.setFullName(data.getUserName());
                            reply.setUserId(data.getUserId());

                            listComments.add(0, reply);
                            commentAdapter.notifyItemInserted(0);
                            rcvSubComment.scrollToPosition(0);

                            edtContent.setText("");
                        } else {
                            Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    closeDialog();
                }

                @Override
                public void onFailure(Call<CommentItemRespond> call, Throwable t) {
                    closeDialog();
                    Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
