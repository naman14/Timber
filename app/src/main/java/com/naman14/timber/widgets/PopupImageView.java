package com.naman14.timber.widgets;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import com.afollestad.appthemeengine.util.TintHelper;

/**
 * Created by naman on 29/10/16.
 */
public class PopupImageView extends AppCompatImageView {

    public PopupImageView(Context context) {
        super(context);
        tint();
    }

    public PopupImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        tint();
    }

    public PopupImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        tint();
    }

    private void tint() {
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("dark_theme", false)) {
            TintHelper.setTint(this, Color.parseColor("#eeeeee"));
        } else  TintHelper.setTint(this, Color.parseColor("#434343"));
    }

}
