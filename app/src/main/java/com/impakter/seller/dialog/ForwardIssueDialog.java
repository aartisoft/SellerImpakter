package com.impakter.seller.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.bumptech.glide.Glide;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ContactAdapter;
import com.impakter.seller.adapter.seller.ForwardIssueAdapter;
import com.impakter.seller.adapter.seller.ListEmailAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.config.PreferencesManager;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.ContactObj;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.widget.dialog.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForwardIssueDialog extends Dialog implements View.OnClickListener {
    private ImageView ivProduct, ivClose;
    private CircleImageView ivCustomer;

    //    private TextView tvOrderCode, tvOrderDate, tvOtherInformation, tvQuantity, tvProductName;
    private TextView tvCustomerName;
    private TextView tvIssue;

    private ImageView ivExpandable;
    private EditText edtEmail, edtContent;
    private Button btnSendIssue;
    private LinearLayout layoutShowContact, layoutMain;

    private RecyclerView rcvEmail;
    private ArrayList<String> listContacts = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private DbContext dbContext;
    private CheckBox ckbSaveContact;

    private RecyclerView rcvProduct;
    private List<ReceivedIssueDetailRespond.Data> listProduct = new ArrayList<>();
    private ForwardIssueAdapter forwardIssueAdapter;

    private Activity activity;
    private ProgressDialog progressDialog;

    private int sellerId;
    private int orderId;
    private long orderDate;
    private OnForwardIssueListener onForwardIssueListener;
    private PreferencesManager preferencesManager;
    private String customerName;
    private String customerAvatar;
    private String issue;

    public ForwardIssueDialog(@NonNull Activity context, int orderId, long orderDate, List<ReceivedIssueDetailRespond.Data> listProduct) {
        super(context, R.style.DialogTheme);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
        this.orderDate = orderDate;
        this.orderId = orderId;
        this.listProduct = listProduct;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public void setCustomerInfo(String customerName, String customerAvatar, String issue) {
        this.customerName = customerName;
        this.customerAvatar = customerAvatar;
        this.issue = issue;

        tvCustomerName.setText(customerName);
        tvIssue.setText(issue);
        Glide.with(activity).load(customerAvatar).into(ivCustomer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bottom_sheet_forward_issue);
        preferencesManager = PreferencesManager.getInstance(activity);
        initViews();
        DbContext.init(activity);
        dbContext = DbContext.getInstance();
        initData();
        initControl();
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
        ivCustomer = findViewById(R.id.iv_customer);
        ivProduct = findViewById(R.id.iv_product);
        ivExpandable = findViewById(R.id.iv_expandable);

        tvIssue = findViewById(R.id.tv_issue);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        ckbSaveContact = findViewById(R.id.ckb_save_contact);

        edtEmail = findViewById(R.id.edt_email);
        edtContent = findViewById(R.id.edt_content);

        btnSendIssue = findViewById(R.id.btn_send_issue);
        layoutShowContact = findViewById(R.id.layout_show_contact);
        layoutMain = findViewById(R.id.layout_main);

        rcvEmail = findViewById(R.id.rcv_contact);
        rcvEmail.setHasFixedSize(true);
        rcvEmail.setLayoutManager(new LinearLayoutManager(activity));

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

    }

    private void initData() {
        if (isLoggedIn())
            sellerId = preferencesManager.getUserLogin().getId();

        listContacts.clear();
        listContacts.addAll(ConvertObject.convertContactObjToString(dbContext.getContacts()));

        contactAdapter = new ContactAdapter(activity, listContacts);
        rcvEmail.setAdapter(contactAdapter);

        forwardIssueAdapter = new ForwardIssueAdapter(activity, listProduct, orderDate);
        rcvProduct.setAdapter(forwardIssueAdapter);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        btnSendIssue.setOnClickListener(this);
        layoutShowContact.setOnClickListener(this);

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

    protected boolean isLoggedIn() {
        if (preferencesManager.getUserLogin() == null) {
            return false;
        } else {
            if (preferencesManager.getUserLogin().getId() != 0) {
                return true;
            } else
                return false;
        }
    }

    private void forwardIssue(final String email, String description) {
        showDialog();
        ConnectServer.getResponseAPI().forwardIssue(15, orderId, email, description).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        onForwardIssueListener.onForwardSuccess(response.body().getEmail());
//                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<BaseMessageRespond> call, Throwable t) {
                closeDialog();
                Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_expandle:

                break;
            case R.id.btn_send_issue:
                String email = edtEmail.getText().toString().trim();
                String description = edtContent.getText().toString().trim();
                forwardIssue(email, description);
                break;
            case R.id.layout_show_contact:
                if (layoutMain.getVisibility() == View.VISIBLE) {
                    layoutMain.setVisibility(View.INVISIBLE);
                    rcvEmail.setVisibility(View.VISIBLE);
                    ivExpandable.setImageResource(R.drawable.ic_expandle_less);
                } else {
                    layoutMain.setVisibility(View.VISIBLE);
                    rcvEmail.setVisibility(View.GONE);
                    ivExpandable.setImageResource(R.drawable.ic_expandle_more);
                }
                break;
        }
    }

    public interface OnForwardIssueListener {
        void onForwardSuccess(String email);
    }
}
