package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.events.OnCheckChangeListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.object.seller.ProductSellerRespond;
import com.suke.widget.SwitchButton;

import java.util.List;

public class ProductSellerAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<ProductSellerRespond.Data> listProducts;
    private LayoutInflater inflater;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private OnCheckChangeListener onCheckChangeListener;

    public ProductSellerAdapter(RecyclerView recyclerView, Activity context, List<ProductSellerRespond.Data> listProducts) {
        this.context = context;
        this.listProducts = listProducts;
        inflater = LayoutInflater.from(context);

        final GridLayoutManager linearLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
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
        return listProducts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.item_list_product_seller, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new ProgressViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ProductSellerRespond.Data data = listProducts.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tvProductName.setText(data.getName());
            boolean isChecked = data.getStatus() == 1 ? true : false;
            ((ViewHolder) holder).switchStatus.setChecked(isChecked);
            Glide.with(context).load(data.getImage()).into(((ViewHolder) holder).ivProduct);
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvProductName;
        private ToggleButton switchStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            switchStatus = itemView.findViewById(R.id.switch_status);
            switchStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCheckChangeListener.onCheckChange(v, getAdapterPosition(), switchStatus.isChecked());
                }
            });
//            switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    onCheckChangeListener.onCheckChange(buttonView, getAdapterPosition(), isChecked);
//                }
//            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    public void setOnCheckChangeListener(OnCheckChangeListener onCheckChangeListener) {
        this.onCheckChangeListener = onCheckChangeListener;
    }
}
