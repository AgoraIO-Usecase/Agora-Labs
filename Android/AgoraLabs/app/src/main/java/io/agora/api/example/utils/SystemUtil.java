package io.agora.api.example.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import java.util.Locale;

public class SystemUtil {
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isZh() {
        String language = getLanguageEnv();

        return language != null && (language.trim().equals("zh-CN") || language.trim().equals("zh-TW"));
    }

    private static String getLanguageEnv() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        String country = locale.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }

    public static void vibrator(Context context){
        Vibrator vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
    }

}
