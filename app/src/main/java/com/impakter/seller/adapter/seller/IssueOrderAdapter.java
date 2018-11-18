package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.events.OnForwardClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.object.seller.ReceivedIssueRespond;
import com.impakter.seller.object.seller.ReceivedOrderRespond;
import com.impakter.seller.utils.DateTimeUtility;

import java.util.List;

public class IssueOrderAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private Activity context;
    private List<ReceivedIssueRespond.Data> listOrders;
    private LayoutInflater inflater;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private OnChangeStatusClickListener onChangeStatusClickListener;
    private OnForwardClickListener onForwardClickListener;
    private OnItemClickListener onItemClickListener;

    public IssueOrderAdapter(RecyclerView recyclerView, Activity context, List<ReceivedIssueRespond.Data> listOrders) {
        this.context = context;
        this.listOrders = listOrders;
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
        return listOrders.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.item_list_issue_order, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReceivedIssueRespond.Data data = listOrders.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tvOrderDate.setText(DateTimeUtility.convertTimeStampToDate(data.getOrderDate() + "", "dd/MM/yy"));
            ((ViewHolder) holder).tvOrderCode.setText("#" + data.getOrderReturnId());
            ((ViewHolder) holder).tvContent.setText(data.getNote());

            setIssueStatus(((ViewHolder) holder).tvStatus, data.getStatus());

            if (data.getImages().size() != 0) {
                Glide.with(context).load(data.getImages().get(0)).into(((ViewHolder) holder).ivProduct);
            }
        }

    }

    private void setIssueStatus(TextView tvStatus, int status) {
        switch (status) {
            case 1: // new/in progress
                tvStatus.setText(context.getResources().getString(R.string.inprogress));
                break;
            case 2: //reviewing
                tvStatus.setText(context.getString(R.string.reviewing));
                break;
            case 3:
            case 4:
            case 5:
                tvStatus.setText(context.getResources().getString(R.string.resolved));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listOrders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvOrderCode, tvOrderDate, tvContent, tvStatus;
        private LinearLayout btnChangeStatus, btnMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);

            tvOrderCode = itemView.findViewById(R.id.tv_order_code);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvStatus = itemView.findViewById(R.id.tv_status);

            btnChangeStatus = itemView.findViewById(R.id.btn_change_status);
            btnMessage = itemView.findViewById(R.id.btn_message);

            btnChangeStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onChangeStatusClickListener.onChangeStatus(tvStatus, getAdapterPosition());
                }
            });
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onForwardClickListener.onForward(v, getAdapterPosition());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    public void setOnChangeStatusClickListener(OnChangeStatusClickListener onChangeStatusClickListener) {
        this.onChangeStatusClickListener = onChangeStatusClickListener;
    }

    public void setOnForwardClickListener(OnForwardClickListener onForwardClickListener) {
        this.onForwardClickListener = onForwardClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
