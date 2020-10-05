package com.naman14.timber.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.Util;

/**
 * Created by naman on 02/01/16.
 */
public class ATEUtils {

    public static void setStatusBarColor(Activity activity, String key, int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Window window = activity.getWindow();
                if (Config.coloredStatusBar(activity, key))
                    window.setStatusBarColor(getStatusBarColor(color));
                else window.setStatusBarColor(Color.BLACK);
                if (Config.coloredNavigationBar(activity, key))
                    window.setNavigationBarColor(color);
                else window.setNavigationBarColor(Color.BLACK);
                applyTaskDescription(activity, key, color);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                final View decorView = activity.getWindow().getDecorView();
                final int lightStatusMode = Config.lightStatusBarMode(activity, key);
                boolean lightStatusEnabled = false;
                switch (lightStatusMode) {
                    case Config.LIGHT_STATUS_BAR_OFF:
                    default:
                        break;
                    case Config.LIGHT_STATUS_BAR_ON:
                        lightStatusEnabled = true;
                        break;
                    case Config.LIGHT_STATUS_BAR_AUTO:
                        lightStatusEnabled = Util.isColorLight(color);
                        break;
                }

                final int systemUiVisibility = decorView.getSystemUiVisibility();
                if (lightStatusEnabled) {
                    decorView.setSystemUiVisibility(systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    decorView.setSystemUiVisibility(systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final int color2 = Config.coloredStatusBar(activity, key) ?
                        Color.TRANSPARENT : Color.BLACK;
                activity.getWindow().setStatusBarColor(color2);
            }
            if (Config.coloredStatusBar(activity, key))
                ((DrawerLayout) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0)).setStatusBarBackgroundColor(getStatusBarColor(color));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void applyTaskDescription(@NonNull Activity activity, @Nullable String key, int color) {
        // Sets color of entry in the system recents page
        try {
            ActivityManager.TaskDescription td = new ActivityManager.TaskDescription(
                    (String) activity.getTitle(),
                    ((BitmapDrawable) activity.getApplicationInfo().loadIcon(activity.getPackageManager())).getBitmap(),
                    color);
            activity.setTaskDescription(td);
        } catch (Exception ignored) {

        }
    }

    public static int getStatusBarColor(int primaryColor) {
        float[] arrayOfFloat = new float[3];
        Color.colorToHSV(primaryColor, arrayOfFloat);
        arrayOfFloat[2] *= 0.9F;
        return Color.HSVToColor(arrayOfFloat);
    }

    public static void setFabBackgroundTint(FloatingActionButton fab, int color) {
        ColorStateList fabColorStateList = new ColorStateList(
                new int[][]{
                        new int[]{}
                },
                new int[]{
                        color,
                }
        );
        fab.setBackgroundTintList(fabColorStateList);
    }
}
