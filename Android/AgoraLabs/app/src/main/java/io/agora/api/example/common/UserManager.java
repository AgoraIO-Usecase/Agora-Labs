package io.agora.api.example.common;

import android.text.TextUtils;
import android.util.Log;
import io.agora.api.example.App;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.model.User;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.GsonUtil;
import io.agora.api.example.utils.SPUtils;

public class UserManager {
    private volatile static UserManager instance;
    private User mUser;

    private UserManager() {
    }

    public User getUser() {
        if (mUser != null) {
            if (TextUtils.isEmpty(ApiManager.token)) {
                ApiManager.token = mUser.token;
            }
            return mUser;
        }
        readingUserInfoFromPrefs();
        return mUser;
    }

    public void saveUserInfo(User user) {
        mUser = user;
        writeUserInfoToPrefs(false);
       // EventBus.getDefault().post(new UserInfoChangeEvent());
    }

    public void logout() {
        writeUserInfoToPrefs(true);
    }

    private void writeUserInfoToPrefs(boolean isLogOut) {
        if (isLogOut) {
            mUser = null;
            SPUtils.getInstance(App.getInstance()).put(Constant.CURRENT_USER, "");
        } else {
            SPUtils.getInstance(App.getInstance()).put(Constant.CURRENT_USER, AESUtils.encrypt(getUserInfoJson()));
        }
    }


    private void readingUserInfoFromPrefs() {
        String userInfo = SPUtils.getInstance(App.getInstance()).getString(Constant.CURRENT_USER, "");
        if (!TextUtils.isEmpty(userInfo)) {
            try {
                mUser = GsonUtil.getInstance().fromJson(AESUtils.decrypt(userInfo), User.class);
                if (TextUtils.isEmpty(ApiManager.token)) {
                    ApiManager.token = mUser.token;
                }
            }catch (Exception e) {
                e.printStackTrace();
                SPUtils.getInstance(App.getInstance()).put(Constant.CURRENT_USER, "");
            }
        }
    }

    private String getUserInfoJson() {
        return GsonUtil.getInstance().toJson(mUser);
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null)
                    instance = new UserManager();
            }
        }
        return instance;
    }

    public void addCameraCount(){
        int count=SPUtils.getInstance(App.getInstance()).getInt(Constant.CAMERA_COUNT,0);
        count++;
        SPUtils.getInstance(App.getInstance()).put(Constant.CAMERA_COUNT,count);
    }

    public int getCameraCount(){
        return SPUtils.getInstance(App.getInstance()).getInt(Constant.CAMERA_COUNT,0);
    }

    public boolean isLogin() {
        if (mUser == null) {
            readingUserInfoFromPrefs();
            return mUser != null && !TextUtils.isEmpty(mUser.userNo);
        } else {
            if (TextUtils.isEmpty(ApiManager.token)) {
                ApiManager.token = mUser.token;
            }
        }
        return true;
    }

}
