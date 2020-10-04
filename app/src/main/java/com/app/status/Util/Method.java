package com.app.status.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.status.Interface.OnClick;
import com.app.status.R;

import java.io.File;

public class Method {

    public Activity activity;
    private OnClick onClick;

    private SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "status";
    public String pref_link = "link";
    private String is_first = "is_first";

    public Method(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public Method(Activity activity, OnClick onClick) {
        this.activity = activity;
        this.onClick = onClick;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    //rtl
    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public boolean isAppWA() {
        String packageName = "com.whatsapp";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }

    public boolean isAppWB() {
        String packageName = "com.whatsapp.w4b";
        Intent mIntent = activity.getPackageManager().getLaunchIntentForPackage(packageName);
        return mIntent != null;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(is_first, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(is_first, true);
    }

    public String url_type() {
        switch (pref.getString(pref_link, null)) {
            case "w":
                return "w";
            case "wb":
                return "wb";
            case "wball":
                return "wball";
            default:
                return "w";
        }
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void setStatusBarGradiant() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void share(String link, String type) {

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        if (type.equals("image")) {
            shareIntent.setType("image/*");
        } else {
            shareIntent.setType("video/*");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.play_more_app));
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(link)));
        activity.startActivity(Intent.createChooser(shareIntent, "Share to"));

    }

    public void click(final int position, final String type) {
        onClick.position(position, type);
    }

}
