package io.agora.api.example.common.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import androidx.annotation.RequiresApi;

public class PopWindow implements PopupWindow.OnDismissListener{
    private static final String TAG = "PopWindow";
    private static final float DEFAULT_ALPHA = 0.7f;
    private Context context;
    private int width;
    private int height;
    private boolean isFocusable = true;
    private boolean isOutside = true;
    private int resLayoutId = -1;
    private View contentView;
    private PopupWindow popupWindow;
    private int animationStyle = -1;

    private boolean clippEnable = true;//default is true
    private boolean ignoreCheekPress = false;
    private int inputMode = -1;
    private PopupWindow.OnDismissListener onDismissListener;
    private int softInputMode = -1;
    private boolean touchable = true;//default is ture
    private View.OnTouchListener onTouchListener;

    private Window window;//当前Activity 的窗口
    /**
     * 弹出PopWindow 背景是否变暗，默认不会变暗。
     */
    private boolean isBackgroundDark = false;

    private float backgroundDrakValue = 0;// 背景变暗的值，0 - 1
    /**
     * 设置是否允许点击 PopupWindow之外的地方，关闭PopupWindow
     */
    private boolean enableOutsideTouchDisMiss = true;// 默认点击pop之外的地方可以关闭

    private PopWindow(Context context){
        this.context = context;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public PopWindow showAsDropDown(View anchor, int xOff, int yOff){
        if(popupWindow !=null){
            popupWindow.showAsDropDown(anchor,xOff,yOff);
        }
        return this;
    }

    public PopWindow showAsDropDown(View anchor){
        if(popupWindow !=null){
            popupWindow.showAsDropDown(anchor);
        }
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public PopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity){
        if(popupWindow !=null){
            popupWindow.showAsDropDown(anchor,xOff,yOff,gravity);
        }
        return this;
    }



    public PopWindow showAtLocation(View parent, int gravity, int x, int y){
        if(popupWindow !=null){
            popupWindow.showAtLocation(parent,gravity,x,y);
        }
        return this;
    }


    private void apply(PopupWindow popupWindow){
        popupWindow.setClippingEnabled(clippEnable);
        if(ignoreCheekPress){
            popupWindow.setIgnoreCheekPress();
        }
        if(inputMode !=-1){
            popupWindow.setInputMethodMode(inputMode);
        }
        if(softInputMode !=-1){
            popupWindow.setSoftInputMode(softInputMode);
        }
        if(onDismissListener !=null){
            popupWindow.setOnDismissListener(onDismissListener);
        }
        if(onTouchListener !=null){
            popupWindow.setTouchInterceptor(onTouchListener);
        }
        popupWindow.setTouchable(touchable);



    }

    private PopupWindow build(){

        if(contentView == null){
            contentView = LayoutInflater.from(context).inflate(resLayoutId,null);
        }


        Activity activity = (Activity) contentView.getContext();
        if(activity!=null && isBackgroundDark){
            //如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
            final  float alpha = (backgroundDrakValue > 0 && backgroundDrakValue < 1) ? backgroundDrakValue : DEFAULT_ALPHA;
            window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.alpha = alpha;
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setAttributes(params);
        }


        if(width != 0 && height !=0 ){
            popupWindow = new PopupWindow(contentView, width, height);
        }else{
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if(animationStyle !=-1){
            popupWindow.setAnimationStyle(animationStyle);
        }

        apply(popupWindow);//设置一些属性

        if(width == 0 || height == 0){
            popupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            width = popupWindow.getContentView().getMeasuredWidth();
            height = popupWindow.getContentView().getMeasuredHeight();
        }

        // 添加dissmiss 监听
        popupWindow.setOnDismissListener(this);

        if(!enableOutsideTouchDisMiss){
            //注意这三个属性必须同时设置，不然不能disMiss，以下三行代码在Android 4.4 上是可以，然后在Android 6.0以上，下面的三行代码就不起作用了，就得用下面的方法
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setBackgroundDrawable(null);
            //注意下面这三个是contentView 不是PopupWindow
            popupWindow.getContentView().setFocusable(true);
            popupWindow.getContentView().setFocusableInTouchMode(true);
            popupWindow.getContentView().setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();

                    return true;
                }
                return false;
            });
            //在Android 6.0以上 ，只能通过拦截事件来解决
            popupWindow.setTouchInterceptor((v, event) -> {

                final int x = (int) event.getX();
                final int y = (int) event.getY();

                if ((event.getAction() == MotionEvent.ACTION_DOWN)
                    && ((x < 0) || (x >= width) || (y < 0) || (y >= height))) {
                    Log.e(TAG,"out side ");
                    Log.e(TAG,"width:"+ popupWindow.getWidth()+"height:"+ popupWindow.getHeight()+" x:"+x+" y  :"+y);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    Log.e(TAG,"out side ...");
                    return true;
                }
                return false;
            });
        }else{
            popupWindow.setFocusable(isFocusable);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(isOutside);
        }
        // update
        popupWindow.update();

        return popupWindow;
    }

    @Override
    public void onDismiss() {
        dissmiss();
    }


    public void dissmiss(){

        if(onDismissListener !=null){
            onDismissListener.onDismiss();
        }

        if(window !=null){
            WindowManager.LayoutParams params = window.getAttributes();
            params.alpha = 1.0f;
            window.setAttributes(params);
        }
        if(popupWindow !=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public static class PopupWindowBuilder{
        private PopWindow popWindow;

        public PopupWindowBuilder(Context context){
            popWindow = new PopWindow(context);
        }
        public PopupWindowBuilder size(int width,int height){
            popWindow.width = width;
            popWindow.height = height;
            return this;
        }


        public PopupWindowBuilder setFocusable(boolean focusable){
            popWindow.isFocusable = focusable;
            return this;
        }



        public PopupWindowBuilder setView(int resLayoutId){
            popWindow.resLayoutId = resLayoutId;
            popWindow.contentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view){
            popWindow.contentView = view;
            popWindow.resLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable){
            popWindow.isOutside = outsideTouchable;
            return this;
        }

        public PopupWindowBuilder setAnimationStyle(int animationStyle){
            popWindow.animationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable){
            popWindow.clippEnable =enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress){
            popWindow.ignoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode){
            popWindow.inputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener){
            popWindow.onDismissListener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode){
            popWindow.softInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable){
            popWindow.touchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter){
            popWindow.onTouchListener = touchIntercepter;
            return this;
        }

        public PopupWindowBuilder enableBackgroundDark(boolean isDark){
            popWindow.isBackgroundDark = isDark;
            return this;
        }

        public PopupWindowBuilder setBgDarkAlpha(float darkValue){
            popWindow.backgroundDrakValue = darkValue;
            return this;
        }

        public PopupWindowBuilder enableOutsideTouchableDissmiss(boolean disMiss){
            popWindow.enableOutsideTouchDisMiss = disMiss;
            return this;
        }


        public PopWindow create(){
            popWindow.build();
            return popWindow;
        }

    }

}
