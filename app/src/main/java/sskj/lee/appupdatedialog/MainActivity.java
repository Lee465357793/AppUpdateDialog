package sskj.lee.appupdatedialog;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SimpleUpdateFragment updateFragment = new SimpleUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseUpdateDialogFragment.INTENT_KEY, initData(BaseVersion.DEFAULT_STYLE));
                updateFragment.setArguments(bundle);
                if (getSupportFragmentManager().findFragmentByTag("update") == null) {
                    if (!isFinishing()) {
                        fragmentTransaction.add(updateFragment, "update");
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                }
            }
                break;
            case R.id.notifycation:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SimpleUpdateFragment updateFragment = new SimpleUpdateFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable(BaseUpdateDialogFragment.INTENT_KEY, initData(BaseVersion.DEFAULT_STYLE));
                updateFragment.setArguments(bundle);
                if (getSupportFragmentManager().findFragmentByTag("update") == null) {
                    if (!isFinishing()) {
                        fragmentTransaction.add(updateFragment, "update");
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                }
                break;
        }
    }

    private VersionInfo initData(int dialogStyle) {
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setContent("版本更新内容\n1.aaaaaaaaaa\n2.bbbbbbbbb");
        versionInfo.setTitle("版本更新");
        versionInfo.setMustup(false);
        versionInfo.setUrl("http://acj6.0098118.com/pc6_soure/2020-8-23/feb2c5383048eebTFeSHXkPP9uQr5M.apk");
        versionInfo.setViewStyle(dialogStyle);
        return versionInfo;
    }
}
