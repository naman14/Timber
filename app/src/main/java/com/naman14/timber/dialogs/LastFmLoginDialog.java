package com.naman14.timber.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.naman14.timber.R;
import com.naman14.timber.fragments.SettingsFragment;
import com.naman14.timber.lastfmapi.LastFmClient;
import com.naman14.timber.lastfmapi.callbacks.UserListener;
import com.naman14.timber.lastfmapi.models.UserLoginQuery;
import com.naman14.timber.utils.PreferencesUtility;

/**
 * Created by christoph on 17.07.16.
 */
public class LastFmLoginDialog extends DialogFragment {
    public static final String FRAGMENT_NAME = "LastFMLogin";

    private String username;
    private String password;
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {


        return new MaterialDialog.Builder(getActivity()).
                positiveText("Login").
                negativeText(getString(R.string.cancel)).
                title(getString(R.string.lastfm_login)).
                customView(R.layout.dialog_lastfm_login, false).
                onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if(savedInstanceState != null ){
                            username = (savedInstanceState.containsKey("NAME_LOGIN"))? savedInstanceState.getString("NAME_LOGIN"): ((EditText) dialog.findViewById(R.id.lastfm_username)).getText().toString();

                            password = (savedInstanceState.containsKey("PASSWORD_LOGIN"))? savedInstanceState.getString("PASSWORD_LOGIN"): ((EditText) dialog.findViewById(R.id.lastfm_password)).getText().toString();
                        }
                        else{
                            username = ((EditText) dialog.findViewById(R.id.lastfm_username)).getText().toString();
                            password = ((EditText) dialog.findViewById(R.id.lastfm_password)).getText().toString();
                        }

                        if (username.length() == 0 || password.length() == 0) return;
                        final Toast toast = Toast.makeText(getActivity(), getString(R.string.lastfm_login_failture), Toast.LENGTH_SHORT);
                        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Logging in..");
                        progressDialog.show();

                        LastFmClient.getInstance(getActivity()).getUserLoginInfo(new UserLoginQuery(username, password), new UserListener() {

                            @Override
                            public void userSuccess() {
                                progressDialog.dismiss();
                                if (getParentFragment() instanceof SettingsFragment) {
                                    ((SettingsFragment) getParentFragment()).updateLastFM();
                                }
                            }

                            @Override
                            public void userInfoFailed() {
                                progressDialog.dismiss();
                                toast.show();
                            }
                        });
                    }
                }).build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("NAME_LOGIN", username);
        outState.putString("PASSWORD_LOGIN", password);
    }
}
