package com.jhjj9158.niupaivideo.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.bean.UserDetailBean;
import com.jhjj9158.niupaivideo.bean.UserPostBean;
import com.jhjj9158.niupaivideo.dialog.DialogPicSelector;
import com.jhjj9158.niupaivideo.dialog.DialogProgress;
import com.jhjj9158.niupaivideo.utils.CommonUtil;
import com.jhjj9158.niupaivideo.utils.Contact;
import com.jhjj9158.niupaivideo.utils.FileUtils;
import com.jhjj9158.niupaivideo.utils.ActivityManagerUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifyActivity extends BaseActivity {

    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.modify_headimg)
    CircleImageView modifyHeadimg;
    @BindView(R.id.modify_name)
    EditText modifyName;
    @BindView(R.id.modify_signature)
    EditText modifySignature;
    @BindView(R.id.modify_save)
    TextView modifySave;
    @BindView(R.id.modify_gender)
    TextView modifyGender;

    private UserDetailBean.ResultBean userInfo;
    String headImgPath;
    private DialogProgress progress;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String json = msg.obj.toString();
            switch (msg.what) {
                case 1:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getInt("code") == 100) {
                            CommonUtil.showTextToast(ModifyActivity.this, "修改成功");
                            Picasso.with(ModifyActivity.this).load(new File(headImgPath)).placeholder(R.drawable.me_user_admin).into
                                    (modifyHeadimg);
                        } else {
                            CommonUtil.showTextToast(ModifyActivity
                                    .this, jsonObject.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getInt("code") == 100) {
                            CommonUtil.showTextToast(ModifyActivity.this, "修改成功");
                            finish();
                        } else {
                            CommonUtil.showTextToast(ModifyActivity
                                    .this, jsonObject.getString("msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hintTitle();
        ActivityManagerUtil.getActivityManager().pushActivity2Stack(this);

        toolbarTitle.setText("修改资料");
        userInfo = getIntent().getParcelableExtra("userInfo");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.argb(99, 00, 00, 00));
        }

        initView();


    }

    @Override
    protected View getChildView() {
        return View.inflate(this, R.layout.activity_modify, null);
    }

    private String name;
    private String signature;

    private void initView() {
        String headImage = new String(Base64.decode(userInfo.getHeadphoto().getBytes(),
                Base64.DEFAULT));
        name = new String(Base64.decode(userInfo.getNickName().getBytes(),
                Base64.DEFAULT));
        signature = new String(Base64.decode(userInfo.getSignature().getBytes(),
                Base64.DEFAULT));
        if (!headImage.contains("http")) {
            headImage = "http://" + headImage;
        }
        Picasso.with(this).load(headImage).placeholder(R.drawable.me_user_admin).into(modifyHeadimg);
        modifyName.setText(name);
        modifyName.setSelection(name.length());
        modifySignature.setText(signature);
        modifySignature.setSelection(signature.length());
        modifySignature.setSingleLine(false);
        if (userInfo.getGender() == 1) {
            modifyGender.setText("男");
        } else if (userInfo.getGender() == 0) {
            modifyGender.setText("女");
        } else {
            modifyGender.setText("未知");
        }
    }

    @OnClick({R.id.toolbar_back, R.id.modify_save, R.id.modify_headimg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.modify_save:
                String et_name = modifyName.getText().toString();
                String et_signature = modifySignature.getText().toString();
                if (TextUtils.isEmpty(et_name)) {
                    CommonUtil.showTextToast(this, "名称不能为空");
                    return;
                }
                if (!et_name.equals(name)) {
                    saveInfo(1, et_name);
                } else if (!et_signature.equals(signature)) {
                    saveInfo(2, et_signature);
                } else {
                    finish();
                }
                break;
            case R.id.modify_headimg:
                if (!CommonUtil.checkPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                        .READ_EXTERNAL_STORAGE})) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest
                            .permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                            .READ_EXTERNAL_STORAGE}, Contact.CHECK_PERMISSION);
                } else {
//                    DialogPicSelector dialogPicSelector = new DialogPicSelector(this);
//                    InitiView.initiBottomDialog(dialogPicSelector);
//                    dialogPicSelector.show();
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, Contact.IMAGE_PICKER);
                    break;
                }
        }
    }

    private void saveInfo(int chooseSelect, String modifyString) {
        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("UpdateUserInfor");
        userPostBean.setUseridx(userInfo.getUidx());
        userPostBean.setName(modifyString);
        userPostBean.setChooseSelect(chooseSelect);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 2;
                handler.sendMessage(message);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case Contact.REQUEST_TAKE_PHOTO:
                        File temp = new File(Environment.getExternalStorageDirectory() + "/" +
                                DialogPicSelector.imageDir);
                        Uri uri = FileProvider.getUriForFile(this, getApplicationContext()
                                .getPackageName() + ".provider", temp);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DialogPicSelector.photoZoom(this, uri);
                        } else {
                            DialogPicSelector.photoZoom(this, Uri.fromFile(temp));
                        }
                        break;
                    case Contact.REQUEST_PHOTO_ZOOM:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            DialogPicSelector.photoZoom(this, data.getData());
                        } else {
                            String realPath = FileUtils.getPath(this, data.getData());
                            Uri newUri = Uri.parse("file:///" + realPath);
                            DialogPicSelector.photoZoom(this, newUri);
                        }
                        break;
                    case Contact.REQUEST_PHOTO_RESULT:
                        if (data != null) {
                            Uri resultUri = data.getData();
                            if (resultUri == null) {
                                resultUri = Uri.parse(data.getAction());
                            }
                            headImgPath = FileUtils.getRealFilePath(this, resultUri);
                            setHeadImag(headImgPath);
                        }
                        break;
                }
                break;
            case ImagePicker.RESULT_CODE_ITEMS:
                if (data != null && requestCode == Contact.IMAGE_PICKER) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    headImgPath = images.get(0).path;
                    setHeadImag(headImgPath);
                } else {
                    Toast.makeText(this, "没有数据", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Contact.CHECK_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(this, ImageGridActivity.class);
                    startActivityForResult(intent, Contact.IMAGE_PICKER);
                } else {
                    new AlertDialog.Builder(this).setMessage("请允许牛拍获取您的相机、相册权限，以确保您能更换新的头像！")
                            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
        }
    }

    private void setHeadImag(String headImgPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(headImgPath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        byte[] encode = Base64.encode(bytes, Base64.DEFAULT);
        String encodeString = new String(encode);

        String type = options.outMimeType;
        if (TextUtils.isEmpty(type)) {
            type = "未能识别的图片";
        } else {
            type = type.substring(6, type.length());
        }


        UserPostBean userPostBean = new UserPostBean();
        userPostBean.setOpcode("UpdateUserImage");
        userPostBean.setUseridx(userInfo.getUidx());
        userPostBean.setType(type);

        Gson gson = new Gson();
        String jsonUser = gson.toJson(userPostBean);

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("user", CommonUtil.EncryptAsDoNet(jsonUser, Contact.KEY))
                    .add("base64", encodeString)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(Contact.USER_INFO)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = response.body().string();
                message.what = 1;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd("ModifyActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart("ModifyActivity");
    }

}
