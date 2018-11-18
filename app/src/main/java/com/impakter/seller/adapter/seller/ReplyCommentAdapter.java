package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyCommentAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<ReplyCommentRespond.Data> listSubComments = new ArrayList<>();
    private LayoutInflater inflater;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;


    public ReplyCommentAdapter(RecyclerView recyclerView, Activity context, List<ReplyCommentRespond.Data> listComments) {
        this.context = context;
        this.listSubComments = listComments;
        inflater = LayoutInflater.from(context);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView,
                                   int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    // Do something
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    loading = true;
                }
            }
        });

    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return listSubComments.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.item_list_sub_comment, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReplyCommentRespond.Data data = listSubComments.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tvName.setText(data.getFullName());
            ((ViewHolder) holder).tvContent.setText(data.getContent());
            ((ViewHolder) holder).tvTime.setText(DateUtils.getRelativeTimeSpanString(data.getDate() * 1000L, System.currentTimeMillis(),
                    0, DateUtils.FORMAT_ABBREV_ALL));

            Glide.with(context).load(data.getAvatar()).into(((ViewHolder) holder).ivAvatar);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listSubComments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvatar;
        private TextView tvName;
        private TextView tvContent, tvTime;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }
}
