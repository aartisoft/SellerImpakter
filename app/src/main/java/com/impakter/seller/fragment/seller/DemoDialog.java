package com.impakter.seller.fragment.seller;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.impakter.seller.R;
import com.impakter.seller.adapter.ContactAdapter;
import com.impakter.seller.adapter.ListAppAdapter;
import com.impakter.seller.events.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class DemoDialog extends Dialog implements View.OnClickListener{
    private RecyclerView rcvContact, rcvApp;
    private ContactAdapter contactAdapter;
    private List<String> listContacts = new ArrayList<>();
    private List<String> listPackages = new ArrayList<>();
    private ImageView ivClose;
    private EditText edtSearch;
    private BottomSheetBehavior sheetBehavior;
    private ListAppAdapter listAppAdapter;
    private int productId;
    private Activity activity;
    public DemoDialog(@NonNull Activity context, int productId) {
        super(context, R.style.DialogTheme);
//        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT);
//        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
        this.productId = productId;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_share);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
        edtSearch = findViewById(R.id.edt_search);

        rcvContact = findViewById(R.id.rcv_contact);
        rcvContact.setHasFixedSize(true);
        rcvContact.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        rcvApp = findViewById(R.id.rcv_app);
        rcvApp.setHasFixedSize(true);
        rcvApp.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));

    }

    private void initData() {
        contactAdapter = new ContactAdapter(activity, listContacts);
        rcvContact.setAdapter(contactAdapter);

        getListApp();
    }

    private void initControl() {
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivClose.setOnClickListener(this);
    }

    public void getListApp() {
        final List<String> packages = new ArrayList<String>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        final List<ResolveInfo> resInfosNew = new ArrayList<ResolveInfo>();
        final List<ResolveInfo> resInfos = activity.getPackageManager().queryIntentActivities(shareIntent, 0);
        resInfosNew.addAll(resInfos);
        if (!resInfos.isEmpty()) {
            int count = 0;
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
//                if (packageName.contains("com.facebook.katana")) {
//                    resInfosNew.remove(count);
//                } else
                packages.add(packageName);
                count++;
            }
        }
        listAppAdapter = new ListAppAdapter(activity, packages);
        rcvApp.setAdapter(listAppAdapter);

        listAppAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                invokeApplication(packages.get(position), resInfosNew.get(position));
            }
        });

    }

    private void invokeApplication(String packageName, ResolveInfo resolveInfo) {
        // if(packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.kakao.story")) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi guys, I found amazing thing to share. Send your love and care in form of gifts to your loved ones from anywhere in world. Log on to giftjaipur.com or download app" + "https://goo.gl/YslIVT" + "and use coupon code app50 to get Rs 50 off on your first purchase.");
        intent.putExtra(Intent.EXTRA_SUBJECT, "GiftJaipur 50 Rs Coupon...");
        intent.setPackage(packageName);

        getContext().startActivity(intent);
        // }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                this.dismiss();
                break;
        }
    }
}
