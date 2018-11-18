package com.impakter.seller.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ContactAdapter;
import com.impakter.seller.adapter.seller.ForwardIssueAdapter;
import com.impakter.seller.adapter.seller.OrderForwardAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.ContactObj;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.fragment.seller.BottomSheetForwardOrderFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.DateTimeUtility;
import com.impakter.seller.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForwardOrderDialog extends Dialog implements View.OnClickListener {
    private RecyclerView rcvProduct;
    private List<ReceivedOrderDetailRespond.Data> listProducts = new ArrayList<>();
    private ImageView ivClose;
    private OrderForwardAdapter orderForwardAdapter;

    private TextView tvOrderCode, tvOrderDate, tvOtherInformation;
    private ImageView ivExpanable;
    private EditText edtEmail, edtContent;
    private Button btnSendOrder;
    private CheckBox ckbSaveContact;
    private LinearLayout layoutShowContact, layoutMain;

    private RecyclerView rcvEmail;
    private ArrayList<String> listContacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private DbContext dbContext;

    private Activity activity;
    private long orderDate;
    private int orderId;
    private OnForwardOrderListener onForwardOrderListener;
    private ProgressDialog progressDialog;

    public ForwardOrderDialog(@NonNull Activity context, int orderId, long orderDate, List<ReceivedOrderDetailRespond.Data> listProduct) {
        super(context, R.style.DialogTheme);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.listProducts = listProduct;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        onForwardOrderListener = (OnForwardOrderListener) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_forwar_order);
        initViews();
        DbContext.init(activity);
        dbContext = DbContext.getInstance();
        initData();
        initControl();
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
        ivExpanable = findViewById(R.id.iv_expandable);

        tvOrderCode = findViewById(R.id.tv_order_code);
        tvOrderDate = findViewById(R.id.tv_order_date);
        tvOtherInformation = findViewById(R.id.tv_other_information);

        edtEmail = findViewById(R.id.edt_email);
        edtContent = findViewById(R.id.edt_content);
        ckbSaveContact = findViewById(R.id.ckb_save_contact);

        btnSendOrder = findViewById(R.id.btn_send_order);
        layoutShowContact = findViewById(R.id.layout_show_contact);
        layoutMain = findViewById(R.id.layout_main);

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

        rcvEmail = findViewById(R.id.rcv_contact);
        rcvEmail.setHasFixedSize(true);
        rcvEmail.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void initData() {

        tvOrderCode.setText("#" + orderId);
        tvOrderDate.setText(DateTimeUtility.convertTimeStampToDate(orderDate + "", "dd/MM/yy"));

        orderForwardAdapter = new OrderForwardAdapter(activity, listProducts);
        rcvProduct.setAdapter(orderForwardAdapter);

        listContacts.clear();
        listContacts.addAll(ConvertObject.convertContactObjToString(dbContext.getContacts()));
        contactAdapter = new ContactAdapter(activity, listContacts);
        rcvEmail.setAdapter(contactAdapter);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        btnSendOrder.setOnClickListener(this);
        layoutShowContact.setOnClickListener(this);
        tvOtherInformation.setOnClickListener(this);

        ckbSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ckbSaveContact.isChecked()) {
                    if (edtEmail.getText().toString().trim().length() != 0) {
                        ContactObj contactObj = new ContactObj();
                        contactObj.setEmail(edtEmail.getText().toString().trim());
                        dbContext.addContactToDB(contactObj);
                    }
                }
            }
        });

        contactAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String email = listContacts.get(position);
                edtEmail.setText(email);
                layoutMain.setVisibility(View.VISIBLE);
                rcvEmail.setVisibility(View.GONE);
                ivExpanable.setImageResource(R.drawable.ic_expandle_more);
            }
        });
        contactAdapter.setOnDeleteClickListener(new OnDeleteClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                String email = listContacts.get(position);
                if (dbContext.deleteContact(email)) {
                    listContacts.remove(position);
                    contactAdapter.notifyItemRemoved(position);
                }
            }
        });
    }

    private void forwardOrder(final String email, String message) {
        showDialog();
        ConnectServer.getResponseAPI().forwardOrder(orderId, email, message).enqueue(new Callback<ForwardOrderRespond>() {
            @Override
            public void onResponse(Call<ForwardOrderRespond> call, Response<ForwardOrderRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent();
//                        intent.setAction(Constants.FINISH_FORWARD_ORDER);
//                        self.sendBroadcast(intent);
                        dismiss();
                        onForwardOrderListener.onForwardSuccess(email);
                    } else {
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ForwardOrderRespond> call, Throwable t) {
                closeDialog();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.btn_send_order:
                String email = edtEmail.getText().toString().trim();
                String message = edtContent.getText().toString().trim();
                forwardOrder(email, message);
//                onForwardOrderListener.onForwardSuccess(email);
                break;
            case R.id.layout_show_contact:
                if (layoutMain.getVisibility() == View.VISIBLE) {
                    layoutMain.setVisibility(View.INVISIBLE);
                    rcvEmail.setVisibility(View.VISIBLE);
                    ivExpanable.setImageResource(R.drawable.ic_expandle_less);
                } else {
                    layoutMain.setVisibility(View.VISIBLE);
                    rcvEmail.setVisibility(View.GONE);
                    ivExpanable.setImageResource(R.drawable.ic_expandle_more);
                }
                break;
            case R.id.tv_other_information:
                Toast.makeText(activity, "We will update this function when we have the description or design about this function!", Toast.LENGTH_SHORT).show();
                break;
        }
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

    public interface OnForwardOrderListener {
        void onForwardSuccess(String email);
    }

}
