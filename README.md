# AppUpdateDialog
App应用更新Library
AppUpdateDialog for Android ；使用DialogFragment 实现自动更新，动态权限申请

效果预览
-------  
* 支持Dialog，Notification 下载进度展示

* 支持自定义更新通知弹窗界面，继承BaseUpdateDialogFragment，并使用Dialog样式

#### 扩展性
继承BaseUpdateDialogFragment, 即可自定义更新提示布局，数据通过Intent()，被BaseUpdateDialogFragment 接收 

#### 使用步骤
*1.MyVersion extend BaseVersion
*2.调用更新弹窗

#### 调用更新弹窗提示
```java
SimpleUpdateFragment updateFragment = new SimpleUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseUpdateDialogFragment.INTENT_KEY, Serializable extend BaseVersion);
                updateFragment.setArguments(bundle);
                FragmentManager transition = getFragmentManager();
                updateFragment.show(transition, "tag");
```


#### 版本信息实体基类，自己的实体需要继承该类，并根据需要return 数据

```java
/**
 * ProjectName：DialogDemo
 * Created by 李岩 on 2018/5/11 0011
 */
public abstract class BaseVersion implements Serializable{
    private static final long serialVersionUID = -1381524807039147958L;
    /**
     * 通知栏样式下载
     */
    public final static int NOTIFYCATION_STYLE = 10010;
    /**
     * 弹框样式下载
     */
    public final static int DEFAULT_STYLE = 10011;
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
     * @return default DEFAULT_STYLE
     */
    public int getViewStyle(){
        return view_style;
    }
    /**
     * 设置下载样式 One of {@link #NOTIFYCATION_STYLE}, or {@link #DEFAULT_STYLE}
     * @return default DEFAULT_STYLE
     */
    public void setViewStyle(int style){
        view_style = style;
    }

    /**
     * 当 Notification 样式时，设置显示图标
     * @return default R.drawable.ic_launcher
     */
    public int getNotifyIcon() {
        return R.mipmap.ic_launcher;
    }
}

```

#### 8.0安装应用配置
8.0 应用安装需要权限：<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
推荐博客：https://blog.csdn.net/growing_tree/article/details/71190741

```xml
<service android:name=".NotifyDownloadService"/> <-- 通知栏下载服务 -->
        
        <-- 8.0读取本地文件配置 -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.fileProvider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"/>
        </provider>

```

#### xml/file_paths.xml 文件内容
```xml 
 <-- 8.0读取本地文件配置 -->
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="my_download" path="sskj.lee.dialogdemo/download/"/>
</paths>
```



License
-------

    Copyright 2018 tifezh

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
