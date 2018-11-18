package com.impakter.seller.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.IssueDetailAdapter;
import com.impakter.seller.adapter.seller.IssueDetailViewPagerAdapter;
import com.impakter.seller.adapter.seller.MessageDetailAdapter;
import com.impakter.seller.config.Constants;
import com.impakter.seller.dialog.FinishForwardIssueDialog;
import com.impakter.seller.dialog.FinishForwardOrderDialog;
import com.impakter.seller.dialog.ForwardIssueDialog;
import com.impakter.seller.events.OnChangeStatusClickListener;
import com.impakter.seller.fragment.seller.BottomSheetForwardIssueFragment;
import com.impakter.seller.fragment.seller.BottomSheetForwardOrderFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.BaseMessageRespond;
import com.impakter.seller.object.seller.CreateRoomRespond;
import com.impakter.seller.object.seller.MessageObj;
import com.impakter.seller.object.seller.MessageRespond;
import com.impakter.seller.object.seller.ReceivedIssueDetailRespond;
import com.impakter.seller.utils.BlurUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueOrderDetailActivity extends BaseActivity implements View.OnClickListener, ForwardIssueDialog.OnForwardIssueListener,
        FinishForwardIssueDialog.OnGoToHomeClickListener, FinishForwardIssueDialog.OnGoToResolutionCenterClickListener {
    private final int RESOLVED = 5;
    private final int REVIEWING = 2;

    private ImageView ivBack;
    private CircleImageView ivCustomer;

    private TextView tvCustomerName, tvIssue;

    private LinearLayout layoutOneMessage;
    private RecyclerView rcvMessage;
    private List<MessageObj> listMessages = new ArrayList<>();
    private MessageDetailAdapter messageAdapter;

    private RecyclerView rcvProduct;
    private List<ReceivedIssueDetailRespond.Data> listProduct = new ArrayList<>();
    private IssueDetailAdapter issueDetailAdapter;
    private int userId; // Id of seller
    private int receiverId; // Id of person who sent issue

//    private ViewPager viewPager;
//    private IssueDetailViewPagerAdapter issueDetailViewPagerAdapter;

    private Button btnAnswer, btnForwardIssue;

    private View viewOverlay;
    private ImageView ivOverlay;
    private BottomSheetForwardIssueFragment forwardIssueFragment;

    private int issueId;
    private int status;
    private long orderDate;

    private String customerName, customerAvatar, issue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_order_detail);
        initViews();
        iniData();
        initControl();

    }

    private void initViews() {
        viewOverlay = findViewById(R.id.view_overlay);

        ivBack = findViewById(R.id.iv_back);
        ivOverlay = findViewById(R.id.iv_over_lay);
        ivCustomer = findViewById(R.id.iv_customer);

        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvIssue = findViewById(R.id.tv_issue);

        layoutOneMessage = findViewById(R.id.layout_one_message);

        btnAnswer = findViewById(R.id.btn_answer);
        btnForwardIssue = findViewById(R.id.btn_forward_issue);

        rcvMessage = findViewById(R.id.rcv_message);
        rcvMessage.setHasFixedSize(true);
        rcvMessage.setLayoutManager(new LinearLayoutManager(self));

        rcvProduct = findViewById(R.id.rcv_product);
        rcvProduct.setHasFixedSize(true);
        rcvProduct.setLayoutManager(new LinearLayoutManager(self, LinearLayoutManager.HORIZONTAL, false));
//        viewPager = findViewById(R.id.viewpager);

    }

    private void iniData() {
        if (isLoggedIn()) {
            userId = preferencesManager.getUserLogin().getId();
        }
        Intent intent = getIntent();
        if (intent != null) {
            issueId = intent.getIntExtra(Constants.ORDER_ID, -1);
            orderDate = intent.getLongExtra(Constants.ORDER_DATE, -1);
        }
        getIssueDetail();
    }

    private void initControl() {
        btnForwardIssue.setOnClickListener(this);
        btnAnswer.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void setData(ReceivedIssueDetailRespond.Data data) {

    }

    private void getListMessage() {
        showDialog();
        ConnectServer.getResponseAPI().createConversation(userId, receiverId).enqueue(new Callback<CreateRoomRespond>() {
            @Override
            public void onResponse(Call<CreateRoomRespond> call, Response<CreateRoomRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        int conversationId = response.body().getConversationId();
                        ConnectServer.getResponseAPI().getListMessages(userId, conversationId, 1).enqueue(new Callback<MessageRespond>() {
                            @Override
                            public void onResponse(Call<MessageRespond> call, Response<MessageRespond> response) {
                                if (response.isSuccessful()) {
                                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
//                                        totalPage = response.body().getAllPages();

                                        listMessages.clear();
                                        listMessages.addAll(response.body().getData());
                                        Collections.reverse(listMessages);
                                        messageAdapter.notifyDataSetChanged();
                                        rcvMessage.scrollToPosition(listMessages.size() - 1);

                                        if (listMessages.size() > 1) {
                                            layoutOneMessage.setVisibility(View.GONE);
                                            rcvMessage.setVisibility(View.VISIBLE);
                                        } else {
                                            layoutOneMessage.setVisibility(View.VISIBLE);
                                            rcvMessage.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                closeDialog();
                            }

                            @Override
                            public void onFailure(Call<MessageRespond> call, Throwable t) {
                                showToast(t.getMessage());
                                closeDialog();
                            }
                        });
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CreateRoomRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    private void getIssueDetail() {
        showDialog();
        ConnectServer.getResponseAPI().getIssueDetail(issueId).enqueue(new Callback<ReceivedIssueDetailRespond>() {
            @Override
            public void onResponse(Call<ReceivedIssueDetailRespond> call, Response<ReceivedIssueDetailRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        //information of person who sent issue to seller
                        customerName = response.body().getUserName();
                        customerAvatar = response.body().getUserAvatar();
                        issue = response.body().getNote();

                        receiverId = response.body().getUserId();
                        Glide.with(self).load(customerAvatar).into(ivCustomer);
                        tvIssue.setText(issue);
                        tvCustomerName.setText(customerName);

                        status = response.body().getStatusIssue();
                        listProduct.clear();
                        listProduct.addAll(response.body().getData());

                        issueDetailAdapter = new IssueDetailAdapter(self, listProduct, status);
                        rcvProduct.setAdapter(issueDetailAdapter);

                        issueDetailAdapter.setOnChangeStatusClickListener(new OnChangeStatusClickListener() {
                            @Override
                            public void onChangeStatus(View view, int position) {
                                showChangeIssueStatusDialog((TextView) view, issueId, status, position);
                            }
                        });

//                        issueDetailViewPagerAdapter = new IssueDetailViewPagerAdapter(self,listProduct,status);
//                        viewPager.setAdapter(issueDetailViewPagerAdapter);
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<ReceivedIssueDetailRespond> call, Throwable t) {
                showToast(t.getMessage());
                closeDialog();
            }
        });
    }

    private void changeIssueStatus(final TextView tvStatus, int orderReturnId, final int issueStatus, final int position) {
        showDialog();
        ConnectServer.getResponseAPI().changeIssueStatus(userId, orderReturnId, status).enqueue(new Callback<BaseMessageRespond>() {
            @Override
            public void onResponse(Call<BaseMessageRespond> call, Response<BaseMessageRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        showToast(response.body().getMessage());
                        if (issueStatus == RESOLVED) { //5: resolved
                            issueDetailAdapter.setStatus(RESOLVED);
                            status = RESOLVED;
                            tvStatus.setText(getResources().getString(R.string.resolved));
                        } else if (issueStatus == REVIEWING) { //2: REVIEWING
                            issueDetailAdapter.setStatus(REVIEWING);
                            status = REVIEWING;
                            tvStatus.setText(getResources().getString(R.string.reviewing));
                        }
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<BaseMessageRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    private Dialog dialog;

    private void showChangeIssueStatusDialog(final TextView tvStatus, final int orderId, final int status, final int position) {
        View view = self.getLayoutInflater().inflate(R.layout.dialog_change_issue_order_status, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setView(view);

        LinearLayout layoutInProgress = view.findViewById(R.id.layout_in_progress);
        LinearLayout layoutSolved = view.findViewById(R.id.layout_solved);
        LinearLayout layoutReviewing = view.findViewById(R.id.layout_reviewing);

        layoutInProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(self, getResources().getString(R.string.has_been_processed), Toast.LENGTH_SHORT).show();
            }
        });
        layoutSolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status != RESOLVED) {
                    changeIssueStatus(tvStatus, orderId, RESOLVED, position);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_resolved));
                }

            }
        });
        layoutReviewing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == REVIEWING) {
                    showToast(getResources().getString(R.string.has_been_being_reviewed));
                } else if (status != RESOLVED) {
                    changeIssueStatus(tvStatus, orderId, REVIEWING, position);
                    dialog.dismiss();
                } else {
                    showToast(getResources().getString(R.string.has_been_resolved));
                }

            }
        });
        dialog = builder.create();
        dialog.show();

        ivOverlay.setVisibility(View.VISIBLE);
        Bitmap map = BlurUtils.takeScreenShot(self);
        Blurry.with(IssueOrderDetailActivity.this).from(map).into(ivOverlay);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ivOverlay.setVisibility(View.GONE);
                ivOverlay.setImageBitmap(null);
            }
        });
    }

    private void createConversation(final int otherUserId) {
        showDialog();
        ConnectServer.getResponseAPI().createConversation(userId, otherUserId).enqueue(new Callback<CreateRoomRespond>() {
            @Override
            public void onResponse(Call<CreateRoomRespond> call, Response<CreateRoomRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.CONVERSATION_ID, response.body().getConversationId());
                        bundle.putInt(Constants.RECEIVER_ID, otherUserId);
                        gotoActivity(self, MessageDetailActivity.class, bundle);
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
                closeDialog();
            }

            @Override
            public void onFailure(Call<CreateRoomRespond> call, Throwable t) {
                closeDialog();
                showToast(t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_answer:
                createConversation(receiverId);
                break;
            case R.id.btn_forward_issue:
//                viewOverlay.setVisibility(View.VISIBLE);
//                viewOverlay.setAnimation(AnimationUtils.loadAnimation(self, R.anim.fadein));
                ForwardIssueDialog forwardIssueDialog = new ForwardIssueDialog(self, issueId, orderDate, listProduct);
                forwardIssueDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forwardIssueDialog.show();

                forwardIssueDialog.setCustomerInfo(customerName, customerAvatar, issue);

                ivOverlay.setVisibility(View.VISIBLE);
                Bitmap map = BlurUtils.takeScreenShot(self);
                Blurry.with(IssueOrderDetailActivity.this).from(map).into(ivOverlay);

                forwardIssueDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ivOverlay.setVisibility(View.GONE);
                        ivOverlay.setImageBitmap(null);
                    }
                });
                break;
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onForwardSuccess(String email) {
        FinishForwardIssueDialog finishForwardIssueDialog = new FinishForwardIssueDialog(self, email);
        finishForwardIssueDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        finishForwardIssueDialog.show();

        ivOverlay.setVisibility(View.VISIBLE);
        Bitmap map = BlurUtils.takeScreenShot(self);
        Blurry.with(this).from(map).into(ivOverlay);

        finishForwardIssueDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ivOverlay.setVisibility(View.GONE);
                ivOverlay.setImageBitmap(null);
            }
        });
    }

    @Override
    public void onGoToHomeClick() {
        Intent intent = new Intent();
        intent.setAction(Constants.SHOW_HOME);
        sendBroadcast(intent);
        finish();
    }

    @Override
    public void onGoToResolutionCenterClick() {
        finish();
    }
}
