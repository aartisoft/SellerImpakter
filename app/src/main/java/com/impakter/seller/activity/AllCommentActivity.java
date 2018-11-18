package com.impakter.seller.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.MoreImageAdapter;
import com.impakter.seller.adapter.seller.CommentAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CommentRespond;
import com.impakter.seller.object.ProductDetailRespond;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;
import com.joooonho.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllCommentActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;

    private TextView tvProductName, tvBrand, tvNumberComment, tvNumberLike;
    private MaterialRatingBar ratingBar;

    private RecyclerView rcvComment;
    private CommentAdapter commentAdapter;
    private List<ListCommentRespond.Data> listComments = new ArrayList<>();
    private int userId;
    private int page = 1;
    private String content = "";
    private int totalPage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private SelectableRoundedImageView ivProduct;
    private ProductDetailRespond.Data product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_comment);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivProduct = findViewById(R.id.iv_product);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swf_layout);

        tvProductName = findViewById(R.id.tv_product_name);
        tvBrand = findViewById(R.id.tv_brand);
        tvNumberComment = findViewById(R.id.tv_number_comment);
        tvNumberLike = findViewById(R.id.tv_number_like);
        ratingBar = findViewById(R.id.rating_bar);

        rcvComment = findViewById(R.id.rcv_comment);
        rcvComment.setHasFixedSize(true);
        rcvComment.setLayoutManager(new LinearLayoutManager(self));
    }

    private void initData() {
        if (isLoggedIn())
            userId = preferencesManager.getUserLogin().getId();

        Intent intent = getIntent();
        if (intent != null) {
            product = intent.getParcelableExtra(Constants.PRODUCT);
            setProductData();
        }

        commentAdapter = new CommentAdapter(rcvComment, self, listComments, product.getId());
        rcvComment.setAdapter(commentAdapter);

        commentAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                page++;
                if (page <= totalPage) {
                    listComments.add(null);
                    commentAdapter.notifyItemInserted(listComments.size() - 1);
                    getMoreComment();
                }

            }
        });
        getListComment();

    }

    private void setProductData() {
        tvProductName.setText(product.getTitle());
        tvBrand.setText(product.getBrandName());
        ratingBar.setRating(product.getAverageRating());
        tvNumberLike.setText(product.getTotalFavorite() + " " + getResources().getString(R.string.like));
        Glide.with(self).load(product.getImages().get(0)).into(ivProduct);
    }

    private void initControl() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                commentAdapter.setLoaded();
                commentAdapter.notifyDataSetChanged();
                getListComment();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        commentAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                int commentId = listComments.get(position).getId();
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.COMMENT_ID, commentId);
//                gotoActivity(self,bundle);

            }
        });
        commentAdapter.setOnViewAllClickListener(new CommentAdapter.OnViewAllClickListener() {
            @Override
            public void onViewAllClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(Constants.COMMENT_ITEM, listComments.get(position));
                bundle.putInt(Constants.PRODUCT_ID, product.getId());
                gotoActivity(self, AllReplyActivity.class, bundle);
            }
        });
        ivBack.setOnClickListener(this);
    }


    private void getListComment() {
        showDialog();
        ConnectServer.getResponseAPI().getListComment(product.getId(), 1).enqueue(new Callback<ListCommentRespond>() {
            @Override
            public void onResponse(Call<ListCommentRespond> call, Response<ListCommentRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS) || response.body().getStatus().equals("SUCESS")) {
                        totalPage = response.body().getAllPage();
                        tvNumberComment.setText(response.body().getTotalComment() + " " + getResources().getString(R.string.comments));

                        listComments.clear();
                        listComments.addAll(response.body().getData());
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ListCommentRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                closeDialog();
            }
        });
    }

    private void getMoreComment() {
        ConnectServer.getResponseAPI().getListComment(product.getId(), page).enqueue(new Callback<ListCommentRespond>() {
            @Override
            public void onResponse(Call<ListCommentRespond> call, Response<ListCommentRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS) || response.body().getStatus().equals("SUCESS")) {
                        listComments.remove(listComments.size() - 1);

                        listComments.addAll(response.body().getData());
                        commentAdapter.setLoaded();
                        commentAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        listComments.remove(listComments.size() - 1);
                        commentAdapter.setLoaded();
                        commentAdapter.notifyItemRemoved(listComments.size() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<ListCommentRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                listComments.remove(listComments.size() - 1);
                commentAdapter.setLoaded();
                commentAdapter.notifyItemRemoved(listComments.size() - 1);
            }
        });
    }

    private void showPopupDialog() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
