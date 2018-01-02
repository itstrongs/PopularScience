package com.itstrong.popularscience.fragment.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.ConstantHolder;
import com.itstrong.popularscience.utils.HttpUtils;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.SPHandler;
import com.itstrong.popularscience.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 账号设置界面
 * Created by itstrong on 2016/6/8.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    /* 头像文件 */
    private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";

    private ImageView imgUserHead;
    private TextView textUserName;
    private EditText editUserPassOld;
    private EditText editUserPassNew;
    private EditText editUserEmail;
    private Button btnSubmit;

    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;

    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 350;
    private static int output_Y = 350;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_setting, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        imgUserHead = (ImageView)mActivity.findViewById(R.id.img_setting_head);
        textUserName = (TextView)mActivity.findViewById(R.id.text_setting_user_name);
        editUserPassOld = (EditText)mActivity.findViewById(R.id.text_setting_user_pass_old);
        editUserPassNew = (EditText)mActivity.findViewById(R.id.text_setting_user_pass_new);
        editUserEmail = (EditText)mActivity.findViewById(R.id.text_setting_user_email);
        btnSubmit = (Button)mActivity.findViewById(R.id.btn_setting_submit);
    }

    @Override
    public void setListener() {
        imgUserHead.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void processLogic() {
        mActivity.setFragmentTitle("账号设置");
        textUserName.setText(SPHandler.getUserName(mActivity));
        setUserLocalHeadImage();

    }

    /**
     * 设置用户本地头像
     */
    private void setUserLocalHeadImage() {
        String path = ConstantHolder.PATH_CACHE_IMG + File.separator + "user_head_img.png";
        LogUtils.d("IMG_SD_PATH:" + path);
        File file = new File(path);
        if(file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            imgUserHead.setImageBitmap(toRoundBitmap(bitmap));
        } else {
            LogUtils.d("头像不存在");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_setting_head:
                setUserHeadImage();
                break;
            case R.id.btn_setting_submit:
                updateUserInfo();
                break;
        }
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        String oldPass = editUserPassOld.getText().toString();
        String newPass = editUserPassNew.getText().toString();
        if (oldPass.isEmpty()) {
            ToastUtils.showToast(mActivity, "原密码不能为空");
            return;
        } else if (newPass.isEmpty()) {
            ToastUtils.showToast(mActivity, "新密码不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", SPHandler.getUserId(mActivity));
        map.put("password", oldPass);
        map.put("newpassword", newPass);
        map.put("email", editUserEmail.getText().toString());
        map.put("userName", SPHandler.getUserName(mActivity));
        LogUtils.d(map.toString());
        HttpUtils.getServerDataForPost(mActivity, ConstantHolder.URL_USER_SETTING, map, new HttpUtils.HttpRequestCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject root = new JSONObject(response);
                    boolean flag = root.getBoolean("flag");
                    if (flag) {
                        ToastUtils.showToast(mActivity, "修改成功！");
                        mActivity.switchFragmentPage(mActivity.SWITCH_TAB_MY);
                    } else {
                        String msg = root.getString("msg");
                        ToastUtils.showToast(mActivity, "提交失败！" + msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 设置用户头像
     */
    private void setUserHeadImage() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*");
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == mActivity.RESULT_CANCELED) {
            Toast.makeText(mActivity, "取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case CODE_GALLERY_REQUEST:
                cropRawPhoto(intent.getData());
                break;
            case CODE_CAMERA_REQUEST:
                if (hasSdcard()) {
                    File tempFile = new File(
                            Environment.getExternalStorageDirectory(),
                            IMAGE_FILE_NAME);
                    cropRawPhoto(Uri.fromFile(tempFile));
                } else {
                    Toast.makeText(getActivity(), "没有SDCard!", Toast.LENGTH_LONG).show();
                }
                break;
            case CODE_RESULT_REQUEST:
                if (intent != null) {
                    setImageToHeadView(intent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    /**
     * 裁剪原始的图片
     */
    public void cropRawPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private void setImageToHeadView(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            saveHeadImgToLocal(photo);
            imgUserHead.setImageBitmap(toRoundBitmap(photo));
        }
    }

    /**
     * 保持头像到本地
     * @param photo
     */
    private void saveHeadImgToLocal(Bitmap photo) {
        File f = new File(ConstantHolder.PATH_CACHE_IMG + "user_head_img.png");
        File imgDir = new File(ConstantHolder.PATH_CACHE_IMG);
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        photo.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制圆形图片
     * @param bitmap
     * @return
     */
    public Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    private boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            // 有存储的SDCard
            return true;
        } else {
            return false;
        }
    }
}
