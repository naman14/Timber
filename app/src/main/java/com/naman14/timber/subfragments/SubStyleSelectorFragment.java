package com.naman14.timber.subfragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.utils.Constants;

/**
 * Created by naman on 08/08/15.
 */
public class SubStyleSelectorFragment extends Fragment {

    SharedPreferences.Editor editor;

    private static final String ARG_PAGE_NUMBER = "pageNumber";
    private static final String WHAT = "what";

    public static SubStyleSelectorFragment newInstance(int pageNumber,String what) {
        SubStyleSelectorFragment fragment = new SubStyleSelectorFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PAGE_NUMBER, pageNumber);
        bundle.putString(WHAT,what);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_style_selector_pager, container, false);

        TextView styleName=(TextView) rootView.findViewById(R.id.style_name);
        styleName.setText("Style  "+getArguments().getInt(ARG_PAGE_NUMBER));

        ImageView styleImage=(ImageView) rootView.findViewById(R.id.style_image);
        styleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreferences();
            }
        });

        return rootView;
    }

    private void setPreferences(){

        if (getArguments().getString(WHAT).equals(Constants.SETTINGS_STYLE_SELECTOR_NOWPLAYING)){
            editor = getActivity().getSharedPreferences(Constants.FRAGMENT_ID, Context.MODE_PRIVATE).edit();
            editor.putString(Constants.NOWPLAYING_FRAGMENT_ID, getStyleForPageNumber());
            editor.commit();
        }
    }

    private String getStyleForPageNumber(){
        switch (getArguments().getInt(ARG_PAGE_NUMBER)){
            case 0: return Constants.TIMBER1;
            case 1: return Constants.TIMBER2;
            case 2: return Constants.TIMBER3;
            case 3: return Constants.TIMBER4;
            default:return Constants.TIMBER1;
        }
    }

}
