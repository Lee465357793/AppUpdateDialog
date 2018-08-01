package sskj.lee.appupdatedialog;

import android.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import sskj.lee.appupdatelibrary.BaseUpdateDialogFragment;
import sskj.lee.appupdatelibrary.BaseVersion;
import sskj.lee.appupdatelibrary.SimpleUpdateFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog:{
                SimpleUpdateFragment updateFragment = new SimpleUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseUpdateDialogFragment.INTENT_KEY, initData(BaseVersion.DEFAULT_STYLE));
                updateFragment.setArguments(bundle);
                FragmentManager transition = getFragmentManager();
                updateFragment.show(transition, "tag");
            }
                break;
            case R.id.notifycation:

                SimpleUpdateFragment updateFragment = new SimpleUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseUpdateDialogFragment.INTENT_KEY, initData(BaseVersion.NOTIFYCATION_STYLE));
                updateFragment.setArguments(bundle);
                FragmentManager transition = getFragmentManager();
                updateFragment.show(transition, "tag");
                break;
        }
    }

    private VersionInfo initData(int dialogStyle) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setContent("版本更新内容\n1.aaaaaaaaaa\n2.bbbbbbbbb");
        versionInfo.setTitle("版本更新");
        versionInfo.setMustup(false);
        versionInfo.setUrl("https://www.ff.songcaijubao.com/uploads/app/android/20180709/7b05c7c6948e09db2908d32f318a824d.apk");
        versionInfo.setViewStyle(dialogStyle);
        return versionInfo;
    }
}
