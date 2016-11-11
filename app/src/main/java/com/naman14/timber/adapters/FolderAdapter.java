package com.naman14.timber.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.naman14.timber.MusicPlayer;
import com.naman14.timber.R;
import com.naman14.timber.dataloaders.FolderLoader;
import com.naman14.timber.utils.NavigationUtils;
import com.naman14.timber.utils.PreferencesUtility;
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
    private final Drawable[] mIcons;
    private boolean mBusy = false;

    public FolderAdapter(Activity context, File root) {
        mContext = context;
        mIcons = new Drawable[] {
                ContextCompat.getDrawable(context, R.drawable.ic_folder_open_black_24dp),
                ContextCompat.getDrawable(context, R.drawable.ic_folder_parent_dark),
                ContextCompat.getDrawable(context, R.drawable.ic_file_music_dark),
                ContextCompat.getDrawable(context, R.drawable.ic_timer_wait)
        };
        updateDataSet(root);
    }

    public void applyTheme(boolean dark) {
        ColorFilter cf = new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        for (Drawable d : mIcons) {
            if (dark) {
                d.setColorFilter(cf);
            } else {
                d.clearColorFilter();
            }
        }
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
        if (localItem.isDirectory()) {
            itemHolder.albumArt.setImageDrawable("..".equals(localItem.getName()) ? mIcons[1] : mIcons[0]);
        } else {
            itemHolder.albumArt.setImageDrawable(mIcons[2]);
        }

        if (TimberUtils.isLollipop())
            itemHolder.albumArt.setTransitionName("transition_album_art" + i);
    }

    @Override
    public int getItemCount() {
        return mFileSet.size();
    }

    @Deprecated
    public void updateDataSet(File newRoot) {
        if (mBusy) {
            return;
        }
        if ("..".equals(newRoot.getName())) {
            goUp();
            return;
        }
        mRoot = newRoot;
        mFileSet = FolderLoader.getMediaFiles(newRoot, true);
    }

    @Deprecated
    public boolean goUp() {
        if (mRoot == null || mBusy) {
            return false;
        }
        File parent = mRoot.getParentFile();
        if (parent != null && parent.canRead()) {
            updateDataSet(parent);
            return true;
        } else {
            return false;
        }
    }

    public boolean goUpAsync() {
        if (mRoot == null || mBusy) {
            return false;
        }
        File parent = mRoot.getParentFile();
        if (parent != null && parent.canRead()) {
            updateDataSetAsync(parent);
            return true;
        } else {
            return false;
        }
    }

    public void updateDataSetAsync(File newRoot) {
        if (mBusy) {
            return;
        }
        if ("..".equals(newRoot.getName())) {
            goUpAsync();
            return;
        }
        mRoot = newRoot;
        new NavigateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRoot);
    }

    private class NavigateTask extends AsyncTask<File,Void,List<File>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBusy = true;
        }

        @Override
        protected List<File> doInBackground(File... params) {
            return FolderLoader.getMediaFiles(params[0], true);
        }

        @Override
        protected void onPostExecute(List<File> files) {
            super.onPostExecute(files);
            mFileSet = files;
            notifyDataSetChanged();
            mBusy = false;
            PreferencesUtility.getInstance(mContext).storeLastFolder(mRoot.getPath());
        }
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView title;
        protected ImageView albumArt;

        public ItemHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.folder_title);
            this.albumArt = (ImageView) view.findViewById(R.id.album_art);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mBusy) {
                return;
            }
            final File f = mFileSet.get(getAdapterPosition());
            if (f.isDirectory()) {
                albumArt.setImageDrawable(mIcons[3]);
                updateDataSetAsync(f);
            } else if (f.isFile()) {Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.clearQueue();
                        MusicPlayer.openFile(f.getPath());
                        MusicPlayer.playOrPause();
                        NavigationUtils.navigateToNowplaying(mContext, true);
                    }
                }, 350);
            }
        }

    }


}