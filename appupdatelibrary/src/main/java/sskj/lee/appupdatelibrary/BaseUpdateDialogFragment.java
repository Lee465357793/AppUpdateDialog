package sskj.lee.appupdatelibrary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

/**
 * ProjectName：DialogDemo
 * DESC: (类描述)
 * Created by 李岩 on 2018/6/4 0004
 * updateName:(修改人名称)
 * updateTime:(修改时间)
 * updateDesc:(修改内容)
 */
public abstract class BaseUpdateDialogFragment extends DialogFragment {

    private Activity mActivity;
    public BaseVersion mVersionData;
    private int PERMISSION_REQUEST_WRITE_SD = 1024;
    public static String INTENT_KEY = "result_key";
    private DownloadTask mDownloadTask;
    public static final String DOWNLOAD_PATH = "%1$s/%2$s/download";
    public static final String DIALOG_SDCARD_NULL = "未检测到SD卡";
    private int retryCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Design_BottomSheetDialog_NoActionBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View inflate  = LayoutInflater.from(mActivity).inflate(getLayoutId(), null);
        return inflate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVersionData = (BaseVersion) getArguments().getSerializable(INTENT_KEY);
        initView(view, mVersionData);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadTask != null) {
            mDownloadTask.cancel(true);
        }
    }

    public abstract int getLayoutId();

    /**
     * 初始化UI
     *
     * @param view
     * @param versionData
     */
    protected abstract void initView(View view, BaseVersion versionData);

    /**
     * 当下载开始前（MainThred）
     */
    protected abstract void onDownLoadStart();

    /**
     * 当下载中（MainThred）
     *
     * @param progress
     */
    protected abstract void onDownLoadUpdate(int progress);

    /**
     * 当下在完成
     *
     * @param file
     */
    protected void onDownLoadFinish(File file) {
        if (file != null && file.exists()) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mActivity, "sskj.lee.appupdatelibrary.appUpdateFileProvider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            if(isAdded()) startActivity(intent);
        }else {
            Toast.makeText(mActivity, "下载出错，请在[个人中心]检测下载新版本", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 校验权限
     */
    void checkPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_SD);
            Toast.makeText(mActivity, "请在应用设置中开启 [文件读写] 权限", Toast.LENGTH_SHORT).show();
        } else {
            if(TextUtils.isEmpty(mVersionData.getUrl())){
                Toast.makeText(mActivity, "下载地址为空", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            if(!mVersionData.getUrl().contains("apk")){
                Toast.makeText(mActivity, "下载地址异常", Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            if (mVersionData.isMustUp()) {
                openDownloadTask();
            } else if (mVersionData.getViewStyle() == BaseVersion.NOTIFYCATION_STYLE) {
                mActivity.startService(new Intent(mActivity, NotifyDownloadService.class).putExtra(INTENT_KEY, mVersionData));
                dismiss();//关掉更新提示dialog
            } else {
                openDownloadTask();
            }
        }
    }

    /**
     * 开启下载
     */
    protected void openDownloadTask() {
        mDownloadTask = new DownloadTask();
        mDownloadTask.execute(mVersionData.getUrl());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_WRITE_SD && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//开通权限
            startDownload();
        }
    }

    protected void startDownload() {
        checkPermission();
    }

    /**
     * 强制更新提示
     */
    public void showMustUpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        AlertDialog dialog = builder.setTitle("温馨提示")
                .setMessage("本次更新是我们的一大步，放弃更新将会退出应用哦~!")
                .setNegativeButton("关闭应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mActivity.finish();
                    }
                })
                .setPositiveButton("继续更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkPermission();
                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

    /**
     * 通知权限
     */
    public void showNotifyPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        AlertDialog dialog = builder.setTitle("温馨提示")
                .setMessage("下载更新需要允许应用通知,是否打开应用通知")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", mActivity.getPackageName(), null));
                        startActivity(intent);
                    }
                }).show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLUE);
    }

    public class DownloadTask extends AsyncTask<String, Integer, File> {

        private boolean mCancelUpdata = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retryCount++;
            onDownLoadStart();
        }

        @Override
        protected File doInBackground(String... strings) {
            // 判断SD卡是否存在，并且是否具有读写权限
            File apkFile = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                InputStream is = null;
                FileOutputStream fos = null;
                HttpURLConnection conn = null;
                try {
                    String savePath = String.format(DOWNLOAD_PATH, Environment.getExternalStorageDirectory(), mActivity.getPackageName());
                    URL url = new URL(mVersionData.getUrl());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();
                    long length = conn.getContentLength();
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    apkFile = new File(savePath, TextUtils.isEmpty(mVersionData.getVersionName()) ? String.valueOf(System.currentTimeMillis() + ".apk") : mVersionData.getVersionName() + ".apk");
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }
                    is = conn.getInputStream();//创建输入流
                    fos = new FileOutputStream(apkFile);
                    long count = 0;
                    byte[] buf = new byte[1024];
                    do {
                        int readNum = is.read(buf);
                        if (readNum <= 0) {//下载完成
                            mCancelUpdata = true;
                            break;
                        }
                        count += readNum;
                        fos.write(buf, 0, readNum);// 写入文件
                        publishProgress((int) (count * 100 / length));//计算当前进度， 更新进度
                    } while (!mCancelUpdata);
                    fos.close();
                    is.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    mDownloadTask.cancel(true);
                    mDownloadTask = null;
                    dismissAllowingStateLoss();
                    onDownLoadFinish(null);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    mDownloadTask.cancel(true);
                    mDownloadTask = null;
                    if(retryCount < 3) {
                        openDownloadTask();
                    }else {
                        dismissAllowingStateLoss();
                        onDownLoadFinish(null);
                    }
                } finally {
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException ignored) {
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            } else {
                Toast.makeText(mActivity, DIALOG_SDCARD_NULL, Toast.LENGTH_SHORT).show();
            }
            return apkFile;
        }

        /**
         * 更新进度
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            onDownLoadUpdate(values[0]);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        /**
         * 下载完成
         *
         * @param file
         */
        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            onDownLoadFinish(file);
            mDownloadTask.cancel(true);
            dismissAllowingStateLoss();//关闭下载中弹窗
        }
    }
}
