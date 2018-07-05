package sskj.lee.appupdatelibrary;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

public class NotifyDownloadService extends Service {
    /**
     * 版本信息
     */
    private BaseVersion mBaseVersion;
    /**
     * 是否取消下载
     */
    public boolean mCancelUpdata = false;
    private int NOTIFY_DOWNLOAD_ID = 1025;
    private NotificationManager mNotificationManager;
    private DownloadTask mDownloadTask;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;

    @Nullable
    @Override  
    public IBinder onBind(Intent intent) {
        return null;
    }
  
    @Override  
    public void onCreate() {  
        super.onCreate();
        mDownloadTask = new DownloadTask();
        mNotificationManager  = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

  
    @Override  
    public void onStart(Intent intent, int startId) {  
        super.onStart(intent, startId);
        if (intent != null){

            mBaseVersion = (BaseVersion) intent.getSerializableExtra(BaseUpdateDialogFragment.INTENT_KEY);
            if (mBaseVersion != null | intent != null){
                showNotification();
            }
        }
    }
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);  
    }  
  
    @Override  
    public void onDestroy() {
        super.onDestroy();
        mNotificationManager.cancelAll();
        mDownloadTask.cancel(true);
    }

    /**
     * 创建通知下载
     */

    private void showNotification() {
        //适配安卓8.0的消息渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_1", "channel_name_1",NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_1");
            mNotification = mBuilder
                    .setSmallIcon(mBaseVersion.getNotifyIcon())
                    .setOngoing(true)
                    .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                    .setContentTitle("正在下载")
                    .setProgress(100, 0, false).build();
        }else {
            mBuilder = new NotificationCompat.Builder(getApplicationContext());
            mNotification = mBuilder
                    .setSmallIcon(mBaseVersion.getNotifyIcon())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), mBaseVersion.getNotifyIcon()))
                    .setOngoing(true)
                    .setContentTitle("正在下载")
                    .setContentIntent(getDefalutIntent(Notification.FLAG_ONGOING_EVENT))
                    .setProgress(100, 0, false).build();
        }
        mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        startForeground(NOTIFY_DOWNLOAD_ID, mNotification);
        mDownloadTask.execute(mBaseVersion.getUrl());
    }

    private PendingIntent getDefalutIntent(int flags) {
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    class DownloadTask extends AsyncTask<String, Integer, File> {

        @Override
        protected File doInBackground(String... strings) {
            // 判断SD卡是否存在，并且是否具有读写权限
            File apkFile= null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                InputStream is= null;
                FileOutputStream fos= null;
                HttpURLConnection conn= null;
                try {
                    String savePath = String.format(Contacts.DOWNLOAD_PATH, Environment.getExternalStorageDirectory(), getPackageName());
                    URL url = new URL(mBaseVersion.getUrl());
                    conn = (HttpURLConnection) url.openConnection();
                    //处理下载读取长度为-1 问题
                    conn.setRequestProperty("Accept-Encoding", "identity");
                    conn.connect();

                    long length = conn.getContentLength();
                    File file = new File(savePath);
                    if (!file.exists()){
                        file.mkdirs();
                    }
                    apkFile = new File(savePath, TextUtils.isEmpty(mBaseVersion.getVersionName()) ? String.valueOf(System.currentTimeMillis() + ".apk") : mBaseVersion.getVersionName()+ ".apk");
                    if (apkFile.exists()){
                        apkFile.delete();
                    }
                    is = conn.getInputStream();//创建输入流
                    fos = new FileOutputStream(apkFile);
                    long count= 0;
                    byte[] buf = new byte[1024];
                    int lastProgress = -1;
                    do {
                        int readNum = is.read(buf);
                        if (readNum <= 0){//下载完成
                            mCancelUpdata = true;
                            break;
                        }
                        count += readNum;
                        fos.write(buf, 0, readNum);// 写入文件

                        final int progress = (int) (count * 100 / length);//计算当前进度， 更新进度

                        if (progress != lastProgress){
                            lastProgress = progress;
                            mBuilder.setContentTitle("正在下载" + progress + "%");
                            mBuilder.setProgress(100, progress, false);
                            mNotification = mBuilder.build();
                            mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                            mNotificationManager.notify(NOTIFY_DOWNLOAD_ID, mNotification);
                            if (progress == 100){
                                publishProgress((int)(count * 100 / length));//计算当前进度， 更新进度
                            }
                        }

                    }while (!mCancelUpdata);
                    fos.close();
                    is.close();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (fos != null) fos.close();
                        if (is != null) is.close();
                    } catch (IOException ignored) {
                    }
                    if (conn != null) conn.disconnect();
                }
            }else {
                Toast.makeText(getApplicationContext(), Contacts.DIALOG_SDCARD_NULL, Toast.LENGTH_SHORT).show();
            }
            return apkFile;
        }

        /**
         * 更新进度
         * @param values
         */
        @Override
        protected void onProgressUpdate(final Integer... values) {
            mNotificationManager.cancel(NOTIFY_DOWNLOAD_ID);
        }

        /**
         * 下载完成
         * @param file
         */
        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if (file != null && file.exists()){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileProvider", file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                startActivity(intent);
            }
            mDownloadTask.cancel(true);
            stopSelf();
        }
    }
}  