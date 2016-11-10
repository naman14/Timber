package com.naman14.timber.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.R;
import com.naman14.timber.dataloaders.FolderLoader;
import com.naman14.timber.utils.TimberUtils;

import java.io.File;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ItemHolder> {

    @NonNull
    private List<File> mFileSet;
    private File mRoot;
    private Activity mContext;

    public FolderAdapter(Activity context, File root) {
        mContext = context;
        updateDataSet(root);
    }

    @Override
    public FolderAdapter.ItemHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_folder_list, viewGroup, false);
        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(final FolderAdapter.ItemHolder itemHolder, int i) {
        File localItem = mFileSet.get(i);

        itemHolder.title.setText(localItem.getName());
        itemHolder.summary.setText("");

        if (TimberUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);
    }

    @Override
    public int getItemCount() {
        return mFileSet.size();
    }

    public void updateDataSet(File newRoot) {
        mRoot = "..".equals(newRoot.getName()) ? mRoot.getParentFile() : newRoot;
        mFileSet = FolderLoader.getMediaFiles(newRoot, true);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title, summary;
        protected ImageView albumArt;
        protected View footer;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.folder_title);
            this.summary = (TextView) view.findViewById(R.id.folder_summary);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            this.footer = view.findViewById(R.id.footer);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            File f = mFileSet.get(getAdapterPosition());
            if (f.isDirectory()) {
                updateDataSet(f);
                notifyDataSetChanged();
            } else if (f.isFile()) {

            }
        }

    }


}