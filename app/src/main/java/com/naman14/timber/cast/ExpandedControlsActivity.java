package com.naman14.timber.cast;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;
import com.naman14.timber.R;

public class ExpandedControlsActivity extends ExpandedControllerActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_expanded_controller, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

    }
}