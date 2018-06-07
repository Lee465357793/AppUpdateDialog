package sskj.lee.appupdatedialog;

import sskj.lee.appupdatelibrary.BaseVersion;

/**
 * ProjectName：DialogDemo
 * DESC: (类描述)
 * Created by 李岩 on 2018/5/15 0015
 * updateName:(修改人名称)
 * updateTime:(修改时间)
 * updateDesc:(修改内容)
 */
public class VersionInfo extends BaseVersion {
    private String mDescription;
    private String mVersion;
    private String mUrl;

    @Override
    public String getTitle() {
        return mDescription;
    }

    @Override
    public String getContent() {
        return mDescription;
    }

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getVersionCode() {
        return null;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public void setVersion(String version) {
        mVersion = version;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public void setViewStyle(int viewStyle) {
        view_style = viewStyle;
    }

    @Override
    public int getNotifyIcon() {
        return R.mipmap.ic_launcher;
    }

    @Override
    public int getViewStyle() {
        return view_style;
    }
}
