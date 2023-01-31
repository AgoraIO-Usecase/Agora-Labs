package io.agora.api.example;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.agora.api.example.main.MainActivity;
import io.agora.api.example.utils.ThreadUtils;

public class SplashActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ((TextView)findViewById(R.id.version)).setText(getVersionName());
        ThreadUtils.postRunOnUIDelayed(() -> {
            Intent it=new Intent(SplashActivity.this, MainActivity.class);
            startActivity(it);
            finish();
        },2000);
    }

    private String getVersionName(){
        try {
            return getString(R.string.version,getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
