package com.impakter.seller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.impakter.seller.BaseActivity;
import com.impakter.seller.BaseFragment;
import com.impakter.seller.R;
import com.impakter.seller.config.Constants;
import com.impakter.seller.fragment.ChangePasswordFragment;
import com.impakter.seller.network.ConnectServer;
import com.impakter.seller.object.UserObj;
import com.impakter.seller.object.UserRespond;
import com.impakter.seller.utils.AppUtil;
import com.impakter.seller.utils.RealPathUtil;
import com.impakter.seller.widget.textview.TextViewHeeboRegular;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_IMAGE_GALLERY = 1234;
    public static final int REQUEST_CODE_UPDATE_AVATAR = 8888;
    public static final int REQUEST_IMAGE_CAPTURE = 9999;

    private TextView tvSave, tvChangeProfilePhoto;
    private EditText edtFirstName, edtLastName, edtBiography, edtLocation, edtEmail;
    private TextViewHeeboRegular btnChangePassword, btnEditAddress, btnEditCreditCard;
    private ImageView ivBack, ivCover;
    private CircleImageView ivAvatar;
    private UserObj userObj;
    private RelativeLayout btnChangeCover;
    private String urlAvatar = "";
    private String avatarUri;
    private String urlCover = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_update_profile);
        initViews();
        initData();
        initControl();
        AppUtil.hideSoftKeyboard(self);
    }

    private void initViews() {

        btnChangeCover = findViewById(R.id.btn_change_cover);
        tvSave = findViewById(R.id.tv_save);
        tvChangeProfilePhoto = findViewById(R.id.tv_change_profile_photo);

        edtFirstName = findViewById(R.id.edt_first_name);
        edtLastName = findViewById(R.id.edt_last_name);
        edtBiography = findViewById(R.id.edt_biography);
        edtLocation = findViewById(R.id.edt_location);
        edtEmail = findViewById(R.id.edt_email);

        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        ivAvatar = findViewById(R.id.iv_avatar);

        btnChangePassword = findViewById(R.id.btn_change_password);
        btnEditAddress = findViewById(R.id.btn_edit_address);
        btnEditCreditCard = findViewById(R.id.btn_edit_credit_card);
    }

    private void initData() {
        if (isLoggedIn())
            userObj = preferencesManager.getUserLogin();
        setDataUser();

    }

    private void setDataUser() {
        edtFirstName.setText(userObj.getFirstName());
        edtLastName.setText(userObj.getLastName());
        edtBiography.setText(userObj.getIntroduction());
        edtLocation.setText(userObj.getAddress());
        edtEmail.setText(userObj.getEmail());

        Glide.with(self).load(userObj.getCover()).into(ivCover);
        Glide.with(self).load(userObj.getAvatar()).into(ivAvatar);
    }

    private void initControl() {
        ivBack.setOnClickListener(this);
        tvSave.setOnClickListener(this);
        tvChangeProfilePhoto.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);

        btnChangePassword.setOnClickListener(this);
        btnEditAddress.setOnClickListener(this);
        btnEditCreditCard.setOnClickListener(this);
        btnChangeCover.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
//                ((MainActivity) self).showToolbar();
                onBackPressed();
                break;
            case R.id.tv_save:
                String address = edtLocation.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                if (validate())
                    updateProfile(/*userObj.getId(), userObj.getFirstName(), userObj.getLastName(), email, address*/);
                break;
            case R.id.tv_change_profile_photo:
            case R.id.iv_avatar:
                Intent intent = new Intent(self, ChangeAvatarActivity.class);
                self.startActivityForResult(intent, REQUEST_CODE_UPDATE_AVATAR);
                break;
            case R.id.btn_change_password:
//                ((MainActivity) self).showFragmentWithAddMethod(new ChangePasswordFragment(), true);
                gotoActivity(self, ChangePasswordActivity.class);
                break;
            case R.id.btn_edit_address:

                break;

            case R.id.btn_edit_credit_card:

                break;
            case R.id.btn_change_cover:
                chooseCover();
                break;
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(RealPathUtil.getRealPath(self, fileUri));
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file
                );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private boolean validate() {
        if (edtFirstName.getText().toString().trim().length() == 0) {
            Toast.makeText(self, getResources().getString(R.string.please_input_first_name), Toast.LENGTH_SHORT).show();
            edtFirstName.requestFocus();
            return false;
        } else if (edtLastName.getText().toString().trim().length() == 0) {
            showToast(getResources().getString(R.string.please_input_your_last_name));
            edtLastName.requestFocus();
            return false;
        } else if (edtEmail.getText().toString().trim().length() == 0) {
            showToast(getResources().getString(R.string.please_input_email));
            edtEmail.requestFocus();
            return false;
        } else if (!isValidEmailAddress(edtEmail.getText().toString().trim())) {
            showToast(getResources().getString(R.string.wrong_email_type));
            edtEmail.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void updateProfile(/*int id, String firstName, String lastName, String email, String address*/) {
        showDialog();
        RequestBody id = createPartFromString(userObj.getId() + "");
        RequestBody firstName = createPartFromString(edtFirstName.getText().toString().trim());
        RequestBody lastName = createPartFromString(edtLastName.getText().toString().trim());
        RequestBody address = createPartFromString(edtLocation.getText().toString().trim());
        RequestBody email = createPartFromString(edtEmail.getText().toString().trim());

        MultipartBody.Part avatar = null;
        MultipartBody.Part cover = null;
        if (urlAvatar != null && urlAvatar.length() != 0) {
            avatar = prepareFilePart("avatar", Uri.fromFile(new File(urlAvatar)));
        }
        if (urlCover != null && urlCover.length() != 0) {
            cover = prepareFilePart("cover", Uri.fromFile(new File(urlCover)));
        }

        ConnectServer.getResponseAPI().updateProfile(id, firstName, lastName, email, address, avatar, cover).enqueue(new Callback<UserRespond>() {
            @Override
            public void onResponse(Call<UserRespond> call, Response<UserRespond> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equals(Constants.SUCCESS)) {
                        UserObj user = response.body().getUserObj();
                        handleUpdate(user);

                        Intent intent = new Intent();
                        intent.setAction(Constants.UPDATE_PROFILE);
                        self.sendBroadcast(intent);

                        Toast.makeText(self, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                closeDialog();
            }

            @Override
            public void onFailure(Call<UserRespond> call, Throwable t) {
                Toast.makeText(self, t.getMessage(), Toast.LENGTH_SHORT).show();
                closeDialog();
            }
        });
    }

    private void handleUpdate(UserObj user) {
        if (preferencesManager.getUserLogin() != null) {
            UserObj oldUser = preferencesManager.getUserLogin();
            oldUser.setFirstName(user.getFirstName());
            oldUser.setLastName(user.getLastName());
            oldUser.setEmail(user.getEmail());
            oldUser.setAvatar(user.getAvatar());
            oldUser.setCover(user.getCover());
            oldUser.setAddress(user.getAddress());

            preferencesManager.setUserLogin(oldUser);

            userObj = preferencesManager.getUserLogin();
            setDataUser();

        } else {
            preferencesManager.setUserLogin(user);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_AVATAR && resultCode == Activity.RESULT_OK) {
            urlAvatar = data.getStringExtra(Constants.AVATAR);
            avatarUri = data.getStringExtra(Constants.AVATAR_URI);
            Glide.with(self).load(new File(urlAvatar)).into(ivAvatar);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            urlCover = RealPathUtil.getRealPath(self, data.getData());
            Glide.with(self).load(new File(urlCover)).into(ivCover);
        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            urlCover = RealPathUtil.getRealPath(self, data.getData());
            Glide.with(self).load(new File(urlCover)).into(ivCover);
        }
    }

    public void chooseCover() {
        new AlertDialog.Builder(self)
                .setTitle(R.string.app_name)
                .setMessage(R.string.select_photo_from)
                .setPositiveButton(R.string.gallery,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                // one can be replaced with any action code
                                Intent pickPhoto = new Intent(
                                        Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto,
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
                                        .resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent,
                                            REQUEST_IMAGE_CAPTURE);
                                }
                            }
                        }).create().show();
    }

}
