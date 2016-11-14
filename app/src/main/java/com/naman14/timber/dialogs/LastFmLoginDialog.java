package com.naman14.timber.dialogs;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.fragments.PlaylistFragment;
import com.naman14.timber.fragments.SettingsFragment;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.UserListener;
import com.naman14.timber.lastfmapi.models.UserLoginQuery;

/**
 * Created by christoph on 17.07.16.
 */
public class LastFmLoginDialog extends DialogFragment {
    public static final String FRAGMENT_NAME = "LastFMLogin";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity()).positiveText("Login").negativeText("Cancel").customView(R.layout.dialog_lastfm_login,false).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String username = ((EditText)dialog.findViewById(R.id.lastfm_username)).getText().toString();
                String password = ((EditText)dialog.findViewById(R.id.lastfm_password)).getText().toString();
                if(username.length()==0||password.length()==0)return;
                LastFmClient.getInstance(getActivity()).getUserLoginInfo(new UserLoginQuery(username, password), new UserListener() {

                    @Override
                    public void userSuccess() {
                        if (getTargetFragment() instanceof SettingsFragment) {
                            ((SettingsFragment) getTargetFragment()).updateLastFM();
                        }
                    }

                    @Override
                    public void userInfoFailed() {
                        Toast.makeText(getTargetFragment().getActivity(),"Failed to Login",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).build();
    }
}
