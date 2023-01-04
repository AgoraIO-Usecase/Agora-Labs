package io.agora.api.example;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.agora.api.example.main.MainActivity;
import io.agora.api.example.utils.ThreadUtils;

public class SplashActivity extends AppCompatActivity {

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ThreadUtils.postRunOnUIDelayed(new Runnable() {
            @Override public void run() {
                Intent it=new Intent(SplashActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        },1500);
    }
}
