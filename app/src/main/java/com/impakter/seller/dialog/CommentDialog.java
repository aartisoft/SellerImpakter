package com.impakter.seller.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.activity.AllCommentActivity;
import com.impakter.seller.activity.DetailActivity;
import com.impakter.seller.adapter.ImageViewPagerAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.NotificationRespond;
import com.impakter.seller.object.ProductDetailRespond;
import com.impakter.seller.object.UserObj;
import com.impakter.seller.object.seller.CommentItemRespond;
import com.impakter.seller.object.seller.ListCommentRespond;
import com.impakter.seller.object.seller.ReplyCommentRespond;
import com.impakter.seller.widget.dialog.ProgressDialog;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = CommentDialog.class.getSimpleName();
    private ImageView ivClose, ivProduct;

    private TextView tvProductName, tvBrand, tvNumberComment, tvNumberLike, tvViewAllComment;
    private TextView tvContent, tvCustomerName;

    private MaterialRatingBar ratingBarProduct, ratingBarComment;

    private EditText edtContent;
    private ImageView ivSend;
    private CircleImageView ivAvatar, ivCustomer;

    private Activity activity;
    private ProgressDialog progressDialog;

    private ProductDetailRespond.Data product;
    private NotificationRespond.Data notification;
    private int commentId;
    private UserObj userObj;

    public CommentDialog(@NonNull Activity context, ProductDetailRespond.Data product, NotificationRespond.Data notification) {
        super(context, R.style.DialogTheme);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
        this.product = product;
        this.notification = notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_comment_fragment);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ratingBarComment = findViewById(R.id.comment_rating_bar);
        ratingBarProduct = findViewById(R.id.rating_bar);
        ivClose = findViewById(R.id.iv_close);
        ivSend = findViewById(R.id.iv_send);
        ivProduct = findViewById(R.id.iv_product);
        ivAvatar = findViewById(R.id.iv_avatar);
        ivCustomer = findViewById(R.id.iv_customer);

        tvProductName = findViewById(R.id.tv_product_name);
        tvBrand = findViewById(R.id.tv_brand);
        tvNumberComment = findViewById(R.id.tv_number_comment);
        tvNumberLike = findViewById(R.id.tv_number_like);
        tvViewAllComment = findViewById(R.id.tv_view_all_comment);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvContent = findViewById(R.id.tv_content);

        edtContent = findViewById(R.id.edt_content);
    }


    private void initData() {
//        getProductDetail(productId);
        userObj = PreferencesManager.getInstance(activity).getUserLogin();
        Glide.with(activity).load(userObj.getAvatar()).into(ivAvatar);

        setProductData();
        setCommentData();

    }

    private void setProductData() {
        tvProductName.setText(product.getTitle());
        tvBrand.setText(product.getBrandName());
        ratingBarProduct.setRating(product.getAverageRating());
        tvNumberComment.setText(product.getTotalComment() + " " + activity.getResources().getString(R.string.comments));
        tvNumberLike.setText(product.getTotalFavorite() + " " + activity.getResources().getString(R.string.like));
        Glide.with(activity).load(product.getImages().get(0)).into(ivProduct);
    }

    private void setCommentData() {
        commentId = notification.getParams().getCommentId();

        tvCustomerName.setText(notification.getParams().getCommentUserName());
        tvContent.setText(notification.getParams().getContent());
        Glide.with(activity).load(notification.getParams().getImage()).into(ivCustomer);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        tvViewAllComment.setOnClickListener(this);
        ivSend.setOnClickListener(this);
    }

    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void replyTo(final int commentId, String content) {
        showDialog();
        if (userObj != null) {
            int userId = userObj.getId();
            ConnectServer.getResponseAPI().replyTo(userId, product.getId(), commentId, content).enqueue(new Callback<CommentItemRespond>() {
                @Override
                public void onResponse(Call<CommentItemRespond> call, Response<CommentItemRespond> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus().equals(Constants.SUCCESS)) {
//                            Intent intent = new Intent(activity, AllCommentActivity.class);
//                            intent.putExtra(Constants.PRODUCT, product);
//                            activity.startActivity(intent);
//                            edtContent.setText("");
                            dismiss();
                        } else {
                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    closeDialog();
                }

                @Override
                public void onFailure(Call<CommentItemRespond> call, Throwable t) {
                    closeDialog();
                    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.tv_view_all_comment:
                Intent intent = new Intent(activity, AllCommentActivity.class);
                intent.putExtra(Constants.PRODUCT, product);
                activity.startActivity(intent);
                dismiss();
                break;
            case R.id.iv_send:
                if (edtContent.getText().toString().trim().length() != 0) {
                    replyTo(commentId, edtContent.getText().toString().trim());
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.input_content), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}
