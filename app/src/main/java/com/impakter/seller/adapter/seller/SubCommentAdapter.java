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
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.object.CommentRespond;
import com.impakter.seller.object.seller.ListCommentRespond;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubCommentAdapter extends RecyclerView.Adapter<SubCommentAdapter.ViewHolder> {
    private Activity context;
    private List<ListCommentRespond.Reply> listSubComments = new ArrayList<>();
    private LayoutInflater inflater;

    public SubCommentAdapter(Activity context, List<ListCommentRespond.Reply> listComments) {
        this.context = context;
        this.listSubComments = listComments;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list_sub_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListCommentRespond.Reply data = listSubComments.get(position);
        holder.tvName.setText(data.getReplyFullName());
        holder.tvContent.setText(data.getReplyContent());
        holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(data.getReplyDate() * 1000L, System.currentTimeMillis(),
                0, DateUtils.FORMAT_ABBREV_ALL));

        Glide.with(context).load(data.getReplyAvatar()).into(holder.ivAvatar);
    }

    @Override
    public int getItemCount() {
//        if (listSubComments.size() > 2)
//            return 2;
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

}
