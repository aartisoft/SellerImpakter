package com.impakter.seller.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.impakter.seller.R;
import com.impakter.seller.adapter.seller.ListEmailAdapter;
import com.impakter.seller.widget.dialog.ProgressDialog;

public class SendMessageDialog extends Dialog implements View.OnClickListener {

    private static final int REQUEST_IMAGE_GALLERY = 1234;
    public static final int REQUEST_IMAGE_CAPTURE = 9999;
    private ImageView ivClose, ivCamera;
    private ImageView ivExpandle;
    private EditText edtEmail, edtContent;
    private Button btnSendIssue;
    private LinearLayout layoutShowContact, layoutMain;

    private RecyclerView rcvEmail;
    private ListEmailAdapter listEmailAdapter;

    private Activity activity;
    private ProgressDialog progressDialog;

    public SendMessageDialog(@NonNull Activity context) {
        super(context, R.style.DialogTheme);
        getWindow().getAttributes().windowAnimations = R.style.dialog_animation_info;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_send_message);
        initViews();
        initData();
        initControl();
    }

    private void initViews() {
        ivClose = findViewById(R.id.iv_close);
        ivExpandle = findViewById(R.id.iv_expandle);
        ivCamera = findViewById(R.id.iv_camera);

        edtEmail = findViewById(R.id.edt_email);
        edtContent = findViewById(R.id.edt_content);

        btnSendIssue = findViewById(R.id.btn_send_issue);
        layoutShowContact = findViewById(R.id.layout_show_contact);
        layoutMain = findViewById(R.id.layout_main);

        rcvEmail = findViewById(R.id.rcv_contact);
        rcvEmail.setHasFixedSize(true);
        rcvEmail.setLayoutManager(new LinearLayoutManager(activity));

    }

    private void initData() {
        listEmailAdapter = new ListEmailAdapter(activity);
        rcvEmail.setAdapter(listEmailAdapter);
    }

    private void initControl() {
        ivClose.setOnClickListener(this);
        ivCamera.setOnClickListener(this);
        btnSendIssue.setOnClickListener(this);
        layoutShowContact.setOnClickListener(this);
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

    public void chooseImage() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.app_name)
                .setMessage(R.string.select_photo_from)
                .setPositiveButton(R.string.gallery,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // one can be replaced with any action code
                                Intent pickPhoto = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                activity.startActivityForResult(pickPhoto,
                                        REQUEST_IMAGE_GALLERY);
                            }
                        })
                .setNegativeButton(R.string.camera,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // zero can be replaced with any action code
                                Intent takePictureIntent = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent
                                        .resolveActivity(activity.getPackageManager()) != null) {
                                    activity.startActivityForResult(takePictureIntent,
                                            REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        }).create().show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_expandle:

                break;
            case R.id.iv_camera:
                chooseImage();
                break;
            case R.id.btn_send_issue:
                String email = edtEmail.getText().toString().trim();
                String content = edtContent.getText().toString().trim();
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
