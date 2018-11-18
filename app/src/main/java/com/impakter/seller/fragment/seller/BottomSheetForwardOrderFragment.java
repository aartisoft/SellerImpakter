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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ContactAdapter;
import com.impakter.seller.adapter.seller.OrderForwardAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dblocal.Convertobject.ConvertObject;
import com.impakter.seller.dblocal.configrealm.DbContext;
import com.impakter.seller.dblocal.realmobject.ContactObj;
import com.impakter.seller.events.OnDeleteClickListener;
import com.impakter.seller.events.OnItemClickListener;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.seller.ForwardOrderRespond;
import com.impakter.seller.object.seller.ReceivedOrderDetailRespond;
import com.impakter.seller.utils.DateTimeUtility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetForwardOrderFragment extends BaseFragment implements View.OnClickListener {
    private View rootView;
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

    private int orderId;
    private OnForwardOrderListener onForwardOrderListener;
    private OnCloseButtonClickListener onCloseButtonClickListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnForwardOrderListener) {
            onForwardOrderListener = (OnForwardOrderListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnForwardOrderListener");
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
        rootView = inflater.inflate(R.layout.bottom_sheet_forwar_order, container, false);
        initViews();
        DbContext.init(self);
        dbContext = DbContext.getInstance();
        initData();
        initControl();

        return rootView;
    }

    private void initViews() {
        ivClose = rootView.findViewById(R.id.iv_close);
        ivExpanable = rootView.findViewById(R.id.iv_expandable);

        tvOrderCode = rootView.findViewById(R.id.tv_order_code);
        tvOrderDate = rootView.findViewById(R.id.tv_order_date);
        tvOtherInformation = rootView.findViewById(R.id.tv_other_information);

        edtEmail = rootView.findViewById(R.id.edt_email);
        edtContent = rootView.findViewById(R.id.edt_content);
        ckbSaveContact = rootView.findViewById(R.id.ckb_save_contact);

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

        listContacts.clear();
        listContacts.addAll(ConvertObject.convertContactObjToString(dbContext.getContacts()));
        contactAdapter = new ContactAdapter(self, listContacts);
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
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        onForwardOrderListener.onForwardSuccess(response.body().getEmail());
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
                Toast.makeText(self, "We will update this function when we have the description or design about this function!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface OnForwardOrderListener {
        void onForwardSuccess(String email);
    }

    public interface OnCloseButtonClickListener {
        void onCloseButtonClick();
    }
}
