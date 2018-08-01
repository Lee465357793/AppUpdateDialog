package sskj.lee.appupdatelibrary;

import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

/**
 * ProjectName：DialogDemo
 * DESC: (类描述)
 * Created by 李岩 on 2018/6/6 0006
 * updateName:(修改人名称)
 * updateTime:(修改时间)
 * updateDesc:(修改内容)
 */
public class SimpleUpdateFragment extends BaseUpdateDialogFragment implements View.OnClickListener {

    private TextView mDialogConfirm;
    private ImageView mDialogCancel;
    private NumberProgressBar mDialogPregressbar;
    private LinearLayout mDialogCloseLayout;

    @Override
    public int getLayoutId() {
        return R.layout.activity_simple;
    }

    @Override
    protected void initView(View view, BaseVersion versionData) {
        TextView dialogTitle = view.findViewById(R.id.update_dialog_title);
        mDialogCloseLayout = view.findViewById(R.id.update_dialog_close_layout);
        TextView dialogContent = view.findViewById(R.id.update_dialog_info);
        mDialogPregressbar = view.findViewById(R.id.update_dialog_progressbar);
        mDialogCancel = view.findViewById(R.id.update_diglog_close);
        mDialogConfirm = view.findViewById(R.id.update_dialog_confirm);
        dialogTitle.setText(versionData.getTitle());
        dialogContent.setText(versionData.getContent());
        mDialogCancel.setOnClickListener(this);
        mDialogConfirm.setOnClickListener(this);
    }

    /**
     * 当开始下载
     */
    @Override
    protected void onDownLoadStart() {
        mDialogConfirm.setVisibility(View.GONE);
        mDialogCloseLayout.setVisibility(View.GONE);
        mDialogPregressbar.setVisibility(View.VISIBLE);
    }

    /**
     * 下载中，可更新dialog 进度
     * @param progress
     */
    @Override
    protected void onDownLoadUpdate(int progress) {
        mDialogPregressbar.setProgress(progress);
    }

    /**
     * 下载结束，可自定义操作
     * @param file
     */
    @Override
    protected void onDownLoadFinish(File file) {
        super.onDownLoadFinish(file);//自定义操作请删除该行
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == R.id.update_diglog_close){
            if (mVersionData.isMustUp()){
                showMustUpDialog();
            }else {
                dismiss();
            }
        }else if (vid == R.id.update_dialog_confirm){
            NotificationManagerCompat manager = NotificationManagerCompat.from(getActivity().getApplicationContext());
            boolean isOpened = manager.areNotificationsEnabled();
            if (isOpened){
                startDownload();
            }else {
                showNotifyPermissionDialog();
            }
        }
    }
}
