package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.opengl.GLU;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.config.Constants;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.events.OnLoadMoreListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.CommentRespond;
import com.impakter.seller.object.UserObj;
import com.impakter.seller.object.seller.CommentItemRespond;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;
import com.impakter.seller.utils.DateTimeUtility;
import com.impakter.seller.widget.dialog.ProgressDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentAdapter extends RecyclerView.Adapter {
    private Activity context;
    private List<ListCommentRespond.Data> listComments;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private OnViewAllClickListener onViewAllClickListener;
    private ProgressDialog progressDialog;
    private int productId;
    private List<ListCommentRespond.Reply> listSubComment;
    private String content;

    public CommentAdapter(RecyclerView recyclerView, Activity context, List<ListCommentRespond.Data> listComments, int productId) {
        this.context = context;
        this.listComments = listComments;
        this.productId = productId;
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
        return listComments.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View view = inflater.inflate(R.layout.item_list_comment, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_loading, parent, false);
            return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final ListCommentRespond.Data comment = listComments.get(position);
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).tvName.setText(comment.getFullName());
            ((ViewHolder) holder).tvContent.setText(comment.getContent());
            ((ViewHolder) holder).tvTime.setText(DateUtils.getRelativeTimeSpanString(comment.getDate() * 1000L, System.currentTimeMillis(),
                    0, DateUtils.FORMAT_ABBREV_ALL));

            Glide.with(context).load(comment.getAvatar()).into(((ViewHolder) holder).ivAvatar);

            ((ViewHolder) holder).rcvSubComment.setNestedScrollingEnabled(false);
            List<ListCommentRespond.Reply> listSubComment = comment.getReply();
            Collections.sort(listSubComment, new Comparator<ListCommentRespond.Reply>() {
                @Override
                public int compare(ListCommentRespond.Reply o1, ListCommentRespond.Reply o2) {
                    return o1.getReplyDate() - o2.getReplyDate();
                }
            });
            final SubCommentAdapter subCommentAdapter = new SubCommentAdapter(context, listSubComment);
            ((ViewHolder) holder).rcvSubComment.setAdapter(subCommentAdapter);

            if (comment.getMoreReply()) {
                ((ViewHolder) holder).tvViewAll.setVisibility(View.VISIBLE);
            } else {
                ((ViewHolder) holder).tvViewAll.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).ivSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyTo(comment.getId(), ((ViewHolder) holder).edtContent.getText().toString(), comment.getReply(), subCommentAdapter);
                    ((ViewHolder) holder).edtContent.setText("");
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return listComments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivAvatar, ivMyAvatar;
        private TextView tvName;
        private TextView tvContent, tvTime, tvViewAll;
        private RecyclerView rcvSubComment;
        private EditText edtContent;
        private ImageView ivSend;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivMyAvatar = itemView.findViewById(R.id.iv_my_avatar);
            ivSend = itemView.findViewById(R.id.iv_send);

            edtContent = itemView.findViewById(R.id.edt_content);

            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvViewAll = itemView.findViewById(R.id.tv_view_all_comment);


            rcvSubComment = itemView.findViewById(R.id.rcv_sub_comment);
            rcvSubComment.setHasFixedSize(true);
            ViewCompat.setNestedScrollingEnabled(rcvSubComment, false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            rcvSubComment.setLayoutManager(linearLayoutManager);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(itemView, getAdapterPosition());
                }
            });
            tvViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewAllClickListener.onViewAllClick(v, getAdapterPosition());
                }
            });
            edtContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        ivMyAvatar.setVisibility(View.VISIBLE);
                    Glide.with(context).load(PreferencesManager.getInstance(context).getUserLogin().getAvatar()).into(ivMyAvatar);
                }
            });
            edtContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() != 0) {
                        ivSend.setVisibility(View.VISIBLE);
                    } else {
                        ivSend.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void replyTo(final int commentId, String content, final List<ListCommentRespond.Reply> listReply, final SubCommentAdapter subCommentAdapter) {
        showDialog();
        UserObj userObj = PreferencesManager.getInstance(context).getUserLogin();
        if (userObj != null) {
            int userId = userObj.getId();
            ConnectServer.getResponseAPI().replyTo(userId, productId, commentId, content).enqueue(new Callback<CommentItemRespond>() {
                @Override
                public void onResponse(Call<CommentItemRespond> call, Response<CommentItemRespond> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals(Constants.SUCCESS)) {
                            CommentItemRespond.Data data = response.body().getData().get(0);
                            ListCommentRespond.Reply reply = new ListCommentRespond.Reply();
                            reply.setReplyId(data.getReplyToId());
                            reply.setReplyAvatar(data.getUserAvatar());
                            reply.setReplyContent(data.getContent());
                            reply.setReplyDate(data.getDateCreated());
                            reply.setReplyFullName(data.getUserName());
                            reply.setReplyUserId(data.getUserId());
                            listReply.add(listReply.size(), reply);
                            for (ListCommentRespond.Reply reply1 : listReply) {
                                Log.e("toan", "item: " + reply1.getReplyContent());
                            }
                            subCommentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    closeDialog();
                }

                @Override
                public void onFailure(Call<CommentItemRespond> call, Throwable t) {
                    closeDialog();
                    Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnViewAllClickListener(OnViewAllClickListener onViewAllClickListener) {
        this.onViewAllClickListener = onViewAllClickListener;
    }

    public interface OnViewAllClickListener {
        void onViewAllClick(View view, int position);
    }
}
