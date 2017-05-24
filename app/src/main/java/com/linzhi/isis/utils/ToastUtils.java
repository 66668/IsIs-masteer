package com.linzhi.isis.utils;

        import android.content.Context;
        import android.widget.Toast;

/**
 * Created by sjy on 2017/5/22.
 */

public class ToastUtils {

    public static void ShortToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}
