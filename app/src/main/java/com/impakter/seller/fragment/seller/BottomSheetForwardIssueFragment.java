package com.impakter.seller.fragment.seller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.impakter.seller.config.Constants;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.utils.DateTimeUtility;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetForwardIssueFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
    private ImageView ivProduct, ivClose;
    private CircleImageView ivCustomer;

    private TextView tvOrderCode, tvOrderDate, tvOtherInformation, tvQuantity, tvProductName;
    private TextView tvCustomerName;
    private TextView tvIssue;

    private ImageView ivExpandle;
    private EditText edtEmail, edtContent;
    private Button btnSendIssue;
    private LinearLayout layoutShowContact, layoutMain;

    private RecyclerView rcvEmail;
    private ListEmailAdapter listEmailAdapter;
    private int orderId;
    private OnForwardIssueListener forwardIssueListener;
    private OnCloseButtonClickListener onCloseButtonClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForwardIssueListener) {
            forwardIssueListener = (OnForwardIssueListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnForwardIssueListener");
        }
        if (context instanceof OnCloseButtonClickListener) {
            onCloseButtonClickListener = (OnCloseButtonClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCloseButtonClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bottom_sheet_forward_issue, container, false);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        ivClose = rootView.findViewById(R.id.iv_close);
        ivCustomer = rootView.findViewById(R.id.iv_customer);
        ivProduct = rootView.findViewById(R.id.iv_product);
        ivExpandle = rootView.findViewById(R.id.iv_expandle);

        tvOrderCode = rootView.findViewById(R.id.tv_order_code);
        tvOrderDate = rootView.findViewById(R.id.tv_order_date);
        tvProductName = rootView.findViewById(R.id.tv_product_name);
        tvQuantity = rootView.findViewById(R.id.tv_quantity);
        tvOtherInformation = rootView.findViewById(R.id.tv_other_information);
        tvIssue = rootView.findViewById(R.id.tv_issue);

        edtEmail = rootView.findViewById(R.id.edt_email);
        edtContent = rootView.findViewById(R.id.edt_content);

        btnSendIssue = rootView.findViewById(R.id.btn_send_issue);
        layoutShowContact = rootView.findViewById(R.id.layout_show_contact);
        layoutMain = rootView.findViewById(R.id.layout_main);

        rcvEmail = rootView.findViewById(R.id.rcv_contact);
        rcvEmail.setHasFixedSize(true);
        rcvEmail.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getInt(Constants.ORDER_ID);
            long orderDate = bundle.getLong(Constants.ORDER_DATE);
            tvOrderCode.setText("#" + orderId);
            tvOrderDate.setText(DateTimeUtility.convertTimeStampToDate(orderDate + "", "dd/MM/yy"));
        }

        listEmailAdapter = new ListEmailAdapter(getActivity());
        rcvEmail.setAdapter(listEmailAdapter);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        btnSendIssue.setOnClickListener(this);
        layoutShowContact.setOnClickListener(this);
        tvOtherInformation.setOnClickListener(this);
    }

    private void forwardOrder(String email, String message) {
        showDialog();
        ConnectServer.getResponseAPI().forwardOrder(orderId, email, message).enqueue(new Callback<ForwardOrderRespond>() {
            @Override
            public void onResponse(Call<ForwardOrderRespond> call, Response<ForwardOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.setAction(Constants.FINISH_FORWARD_ORDER);
//                        self.sendBroadcast(intent);
//                        onForwardOrderListener.onForwardSuccess();
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ForwardOrderRespond> call, Throwable t) {
                closeDialog();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                onCloseButtonClickListener.onCloseButtonClick();
                break;
            case R.id.iv_expandle:

                break;
            case R.id.btn_send_issue:
                String email = edtEmail.getText().toString().trim();
                String message = edtContent.getText().toString().trim();
                forwardOrder(email, message);
                break;
            case R.id.layout_show_contact:
                if (layoutMain.getVisibility() == View.VISIBLE) {
                    layoutMain.setVisibility(View.INVISIBLE);
                    rcvEmail.setVisibility(View.VISIBLE);
                } else {
                    layoutMain.setVisibility(View.VISIBLE);
                    rcvEmail.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_other_information:
                showToast("We will update this function when we have the description or design about this function!");
                break;
        }
    }

    public interface OnForwardIssueListener {
        void onForwardSuccess();
    }

    public interface OnCloseButtonClickListener {
        void onCloseButtonClick();
    }
}
