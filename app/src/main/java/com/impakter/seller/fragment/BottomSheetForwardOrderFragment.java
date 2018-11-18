package com.impakter.seller.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.R;
import com.impakter.seller.activity.MyBagActivity;
import com.impakter.seller.adapter.ListAppAdapter;
import com.impakter.seller.adapter.seller.ListEmailAdapter;
import com.impakter.seller.adapter.seller.OrderForwardAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.DateTimeUtility;
import com.impakter.seller.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetForwardOrderFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private View rootView;
    private RecyclerView rcvProduct;
    private List<ReceivedOrderDetailRespond.Data> listProducts = new ArrayList<>();
    private ImageView ivClose;
    private BottomSheetBehavior sheetBehavior;
    private OrderForwardAdapter orderForwardAdapter;

    private TextView tvOrderCode, tvOrderDate, tvOtherInformation;
    private ImageView ivRightArrow;
    private EditText edtEmail, edtContent;
    private Button btnSendOrder;
    private LinearLayout layoutShowContact, layoutMain;

    private RecyclerView rcvEmail;
    private ListEmailAdapter listEmailAdapter;
    private ProgressDialog progressDialog;
    private int orderId;

    public BottomSheetForwardOrderFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);
                sheetBehavior = BottomSheetBehavior.from(bottomSheet);
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                getActivity().sendBroadcast(intent);
            }
        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bottom_sheet_forwar_order, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        initData();
        initControl();
        return rootView;
    }

    private void initViews() {
        ivClose = rootView.findViewById(R.id.iv_close);
        ivRightArrow = rootView.findViewById(R.id.iv_expandle);

        tvOrderCode = rootView.findViewById(R.id.tv_order_code);
        tvOrderDate = rootView.findViewById(R.id.tv_order_date);
        tvOtherInformation = rootView.findViewById(R.id.tv_other_information);

        edtEmail = rootView.findViewById(R.id.edt_email);
        edtContent = rootView.findViewById(R.id.edt_content);

        btnSendOrder = rootView.findViewById(R.id.btn_send_order);
        layoutShowContact = rootView.findViewById(R.id.layout_show_contact);
        layoutMain = rootView.findViewById(R.id.layout_main);

        rcvProduct = rootView.findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        rcvEmail = rootView.findViewById(R.id.rcv_contact);
        rcvEmail.setHasFixedSize(true);
        rcvEmail.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getInt(Constants.ORDER_ID);
            long orderDate = bundle.getLong(Constants.ORDER_DATE);
            listProducts = bundle.getParcelableArrayList(Constants.LIST_ORDER);

            tvOrderCode.setText("#" + orderId);
            tvOrderDate.setText(DateTimeUtility.convertTimeStampToDate(orderDate + "", "dd/MM/yy"));
        }
        orderForwardAdapter = new OrderForwardAdapter(getActivity(), listProducts);
        rcvProduct.setAdapter(orderForwardAdapter);

        listEmailAdapter = new ListEmailAdapter(getActivity());
        rcvEmail.setAdapter(listEmailAdapter);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        btnSendOrder.setOnClickListener(this);
        layoutShowContact.setOnClickListener(this);
    }

    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_finish_order, null);

        final Activity activity = getActivity();
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setContentView(view);

        LinearLayout btnGoToOrder = view.findViewById(R.id.btn_go_to_order);
        LinearLayout btnGoToHome = view.findViewById(R.id.btn_go_to_home);
        btnGoToOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Constants.GO_TO_ORDERS);
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        btnGoToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Constants.GO_TO_HOME);
                activity.sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    protected void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected void closeDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }

    private void forwardOrder(String email, String message) {
        showDialog();
        ConnectServer.getResponseAPI().forwardOrder(orderId, email, message).enqueue(new Callback<ForwardOrderRespond>() {
            @Override
            public void onResponse(Call<ForwardOrderRespond> call, Response<ForwardOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        showBottomSheetDialog();
                        dismiss();
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
                this.dismiss();
                Intent intent = new Intent();
                intent.setAction(Constants.HIDE_BLUR);
                getActivity().sendBroadcast(intent);
                break;
            case R.id.iv_expandle:

                break;
            case R.id.btn_send_order:
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
        }
    }
}
