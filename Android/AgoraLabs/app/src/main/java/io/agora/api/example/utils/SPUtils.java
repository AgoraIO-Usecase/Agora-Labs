package io.agora.api.example.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SPUtils {

    private static Map<String, SPUtils> sSPMap = new HashMap<>();
    private SharedPreferences sp;

    public static SPUtils getInstance(Context context) {
        return getInstance(context, "");
    }

    public static SPUtils getInstance(Context context, String spName) {
        if (isSpace(spName)) spName = "spUtils";
        SPUtils sp = sSPMap.get(spName);
        if (sp == null) {
            sp = new SPUtils(context, spName);
            sSPMap.put(spName, sp);
        }
        return sp;
    }

    private SPUtils(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
    }

    public void put(@NonNull String key, @NonNull String value) {
        SharedPreferences.Editor editor = sp.edit().putString(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public String getString(@NonNull String key) {
        return getString(key, "");
    }

    public String getString(@NonNull String key, @NonNull String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void put(@NonNull String key, int value) {
        SharedPreferences.Editor editor = sp.edit().putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }


    public int getInt(@NonNull String key) {
        return getInt(key, -1);
    }


    public int getInt(@NonNull String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void put(@NonNull String key, long value) {
        SharedPreferences.Editor editor = sp.edit().putLong(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public long getLong(@NonNull String key) {
        return getLong(key, -1L);
    }

    public long getLong(@NonNull String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    public void put(@NonNull String key, float value) {
        SharedPreferences.Editor editor = sp.edit().putFloat(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public float getFloat(@NonNull String key) {
        return getFloat(key, -1f);
    }

    public float getFloat(@NonNull String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void put(@NonNull String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit().putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public boolean getBoolean(@NonNull String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(@NonNull String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }


    public void put(@NonNull String key, @NonNull Set<String> values) {
        SharedPreferences.Editor editor = sp.edit().putStringSet(key, values);
        SharedPreferencesCompat.apply(editor);
    }

    public Set<String> getStringSet(@NonNull String key) {
        return getStringSet(key, Collections.emptySet());
    }

    public Set<String> getStringSet(@NonNull String key, @NonNull Set<String> defaultValue) {
        return sp.getStringSet(key, defaultValue);
    }

    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    public void remove(@NonNull String key) {
        SharedPreferences.Editor editor = sp.edit().remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public boolean contains(@NonNull String key) {
        return sp.contains(key);
    }

    public void clear() {
        SharedPreferences.Editor editor = sp.edit().clear();
        SharedPreferencesCompat.apply(editor);
    }

    private static boolean isSpace(String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }


        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }


}
