package io.agora.api.example.common.dialog;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import io.agora.api.example.R;
import io.agora.api.example.utils.ThreadUtils;

public class AutoDissmissDialog extends AppCompatDialog {
    private ImageView icon;
    private TextView content;

    public AutoDissmissDialog(@NonNull Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_auto_dismiss);
        icon=findViewById(R.id.icon);
        content=findViewById(R.id.content);
    }


    public void setResource(int imgRes,int resContent){
        icon.setImageResource(imgRes);
        content.setText(resContent);
    }

    @Override
    public void show(){
        super.show();
        ThreadUtils.postRunOnUIDelayed(new Runnable() {
            @Override public void run() {
                dismiss();
            }
        },1000);
    }

}
