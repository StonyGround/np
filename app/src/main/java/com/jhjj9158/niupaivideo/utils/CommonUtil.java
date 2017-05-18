package com.jhjj9158.niupaivideo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.jhjj9158.niupaivideo.R;
import com.jhjj9158.niupaivideo.activity.SettingActivity;
import com.jhjj9158.niupaivideo.bean.IndexBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by pc on 17-4-5.
 */

public class CommonUtil {

    public static String DecryptDoNet(String message, String key)
            throws Exception {
        byte[] bytesrc = Base64.decode(message.getBytes(), Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    public static String EncryptAsDoNet(String message, String key)
            throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encryptbyte = cipher.doFinal(message.getBytes());
        return new String(Base64.encode(encryptbyte, Base64.DEFAULT));
    }


    private static Toast mToast;
    private static Handler mHandler = new Handler();
    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showTextToast(Context context, String msg) {
        mHandler.removeCallbacks(r);
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showTextToast(Context context, String msg, int duration) {
        mHandler.removeCallbacks(r);
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }


    private static String sID = null;

    private static final String INSTALLATION = "INSTALLATION";

    public synchronized static String getDeviceID(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), INSTALLATION);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }


    public static int getScreenWidth(Context context) {
        DisplayMetrics display = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(display);
        return display.widthPixels;
    }


    public static boolean checkPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Class.forName("android.content.Context");
                Method method = clazz.getMethod("checkSelfPermission", String.class);
                for (String permission : permissions) {
                    int rest = (Integer) method.invoke(context, permission);
                    if (rest == PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            for (String permission : permissions) {
                PackageManager pm = context.getPackageManager();
                if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void requestPermissions(Context context, String[] permissions) {
        if (!checkPermission(context, permissions)) {
            ActivityCompat.requestPermissions((Activity) context, permissions, Contact.CHECK_PERMISSION);
        }
    }

    public static int getMinVid(List<IndexBean.ResultBean> resultBeanList) {
        int min = resultBeanList.get(0).getVid();
        for (int i = 0; i < resultBeanList.size(); i++) {
            if (min > resultBeanList.get(i).getVid()) {
                min = resultBeanList.get(i).getVid();
            }
        }
        return min;
    }

}
