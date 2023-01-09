package io.agora.api.example.utils;

import android.content.Context;
import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;

import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

public class ConstraintLayoutUtils {

    public static void setHalfScreenLayout(View view,boolean topToParent){
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(UIUtil.dip2px(view.getContext(),0),UIUtil.dip2px(view.getContext(),0));
        params.leftToLeft=PARENT_ID;
        if(topToParent) {
            params.topToTop = PARENT_ID;
        }else{
            params.bottomToBottom=PARENT_ID;
        }
        params.rightToRight=PARENT_ID;
        params.matchConstraintPercentHeight=0.5f;
        view.setLayoutParams(params);
    }

    public static void setMatchParentLayout(View view){
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.leftToLeft=PARENT_ID;
        params.topToTop=PARENT_ID;
        params.rightToRight=PARENT_ID;
        view.setLayoutParams(params);
    }

    public static void setTopCenterLayout(View view,int topMargin){
        ConstraintLayout.LayoutParams params =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.topToTop = PARENT_ID;
        params.leftToLeft = PARENT_ID;
        params.rightToRight = PARENT_ID;
        params.topMargin=topMargin;
        view.setLayoutParams(params);
    }

    public static void setTopRight(View view,int topMargin){
        ConstraintLayout.LayoutParams params =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.topToTop = PARENT_ID;
        params.rightToRight = PARENT_ID;
        params.topMargin=topMargin;
        view.setLayoutParams(params);
    }

    public static void setBottomCenter(View view){
        ConstraintLayout.LayoutParams params =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = PARENT_ID;
        params.leftToLeft = PARENT_ID;
        params.rightToRight = PARENT_ID;
        params.bottomMargin=UIUtil.dip2px(view.getContext(),10);
        view.setLayoutParams(params);
    }

    public static void setBottomRight(View view){
        ConstraintLayout.LayoutParams params =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.bottomToBottom = PARENT_ID;
        params.rightToRight = PARENT_ID;
        params.bottomMargin=UIUtil.dip2px(view.getContext(),10);
        view.setLayoutParams(params);
    }


}
