package sskj.lee.appupdatelibrary;

import java.io.Serializable;

/**
 * ProjectName：DialogDemo
 * DESC: (类描述)
 * Created by 李岩 on 2018/5/11 0011
 * updateName:(修改人名称)
 * updateTime:(修改时间)
 * updateDesc:(修改内容)
 */
public abstract class BaseVersion implements Serializable{
    private static final long serialVersionUID = -1381524807039147958L;
    /**
     * 通知栏样式下载
     */
    public final static int NOTIFYCATION_STYLE = 10010;
    /**
     * 默认样式下载(更新框内下载)
     */
    public final static int DEFAULT_STYLE = 10012;
    /**
     * 下载中提示 样式
     */
    public int view_style = NOTIFYCATION_STYLE;

    /**
     * 标题
     * @return
     */
    public abstract String getTitle();

    /**
     * 更新通知内容
     * @return
     */
    public abstract String getContent();

    /**
     * 下载链接
     * @return
     */
    public abstract String getUrl();


    /**
     * 版本名， 作为下载的文件名使用
     * @return
     */
    public String getVersionName(){
     return "default";
    }

    /**
     * 版本编号  code
     * @return
     */
    public abstract String getVersionCode();

    /**
     * 下载样式 One of {@link #NOTIFYCATION_STYLE}, or {@link #DEFAULT_STYLE}
     * @return default DIALOG_STYLE
     */
    public int getViewStyle(){
        return view_style;
    }
    /**
     * 设置下载样式 One of {@link #NOTIFYCATION_STYLE}, or {@link #DEFAULT_STYLE}
     * @return default DIALOG_STYLE
     */
    public void setViewStyle(int style){
        view_style = style;
    }

    /**
     * 当 Notification 样式时，设置显示图标
     * @return default R.drawable.ic_launcher
     */
    public abstract int getNotifyIcon();

    /**
     * 是否强制更新
     * @return
     */
    public abstract boolean isMustUp();
}
