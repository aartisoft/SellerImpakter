package com.impakter.seller.fragment.seller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ListEmailAdapter;
import com.impakter.seller.adapter.seller.OrderForwardAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.DateTimeUtility;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetCommentFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ImageView ivClose, ivProduct;

    private TextView tvProductName, tvBrand, tvNumberComment, tvNumberLike, tvViewAllComment;
    private TextView tvContent, tvCustomerName;

    private MaterialRatingBar ratingBarProduct, ratingBarComment;

    private EditText edtContent;
    private ImageView ivSend;
    private CircleImageView ivAvatar, ivCustomer;

    private OnViewAllCommentClickListener onViewAllCommentClickListener;
    private OnCloseButtonClickListener onCloseButtonClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onViewAllCommentClickListener = (OnViewAllCommentClickListener) context;
        onCloseButtonClickListener = (OnCloseButtonClickListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet_comment_fragment, container, false);
        initViews();
        initData();
        initControl();

        return rootView;
    }

    private void initViews() {
        ratingBarComment = rootView.findViewById(R.id.comment_rating_bar);
        ratingBarProduct = rootView.findViewById(R.id.rating_bar);
        ivClose = rootView.findViewById(R.id.iv_close);
        ivSend = rootView.findViewById(R.id.iv_send);
        ivProduct = rootView.findViewById(R.id.iv_product);
        ivAvatar = rootView.findViewById(R.id.iv_avatar);
        ivCustomer = rootView.findViewById(R.id.iv_customer);

        tvProductName = rootView.findViewById(R.id.tv_product_name);
        tvBrand = rootView.findViewById(R.id.tv_brand);
        tvNumberComment = rootView.findViewById(R.id.tv_number_comment);
        tvNumberLike = rootView.findViewById(R.id.tv_number_like);
        tvViewAllComment = rootView.findViewById(R.id.tv_view_all_comment);
        tvCustomerName = rootView.findViewById(R.id.tv_customer_name);
        tvContent = rootView.findViewById(R.id.tv_content);

        edtContent = rootView.findViewById(R.id.edt_content);
    }


    private void initData() {

    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        tvViewAllComment.setOnClickListener(this);
        ivSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onCloseButtonClickListener.onCloseButtonClick();
                break;
            case R.id.tv_view_all_comment:
                onViewAllCommentClickListener.onViewAllCommentClick();
                break;
            case R.id.iv_send:

                break;
        }
    }

    public interface OnViewAllCommentClickListener {
        void onViewAllCommentClick();
    }

    public interface OnCloseButtonClickListener {
        void onCloseButtonClick();
    }
}
