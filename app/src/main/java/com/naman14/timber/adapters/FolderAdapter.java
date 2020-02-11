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
import com.naman14.timber.dataloaders.SongLoader;
import com.naman14.timber.models.Song;
import com.naman14.timber.utils.PreferencesUtility;
import com.naman14.timber.utils.TimberUtils;
import com.naman14.timber.widgets.BubbleTextGetter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nv95 on 10.11.16.
 */

public class FolderAdapter extends BaseSongAdapter<FolderAdapter.ItemHolder> implements BubbleTextGetter {

    @NonNull
    private List<File> mFileSet;
    private List<Song> mSongs;
    private File mRoot;
    private Activity mContext;
    private final Drawable[] mIcons;
    private boolean mBusy = false;


    public FolderAdapter(Activity context, File root) {
        mContext = context;
        mIcons = new Drawable[]{
                ContextCompat.getDrawable(context, R.drawable.ic_folder_open_black_24dp),
                ContextCompat.getDrawable(context, R.drawable.ic_folder_parent_dark),
                ContextCompat.getDrawable(context, R.drawable.ic_file_music_dark),
                ContextCompat.getDrawable(context, R.drawable.ic_timer_wait)
        };
        mSongs = new ArrayList<>();
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
        Song song = mSongs.get(i);
        itemHolder.title.setText(localItem.getName());
        if (localItem.isDirectory()) {
            itemHolder.albumArt.setImageDrawable("..".equals(localItem.getName()) ? mIcons[1] : mIcons[0]);
        } else {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(song.albumId).toString(),
                    itemHolder.albumArt,
                    new DisplayImageOptions.Builder().
                            cacheInMemory(true).showImageOnFail(mIcons[2])
                            .resetViewBeforeLoading(true).build());
        }
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
        getSongsForFiles(mFileSet);
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
            return updateDataSetAsync(parent);
        } else {
            return false;
        }
    }

    public boolean updateDataSetAsync(File newRoot) {
        if (mBusy) {
            return false;
        }
        if ("..".equals(newRoot.getName())) {
            goUpAsync();
            return false;
        }
        mRoot = newRoot;
        new NavigateTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRoot);
        return true;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        if (mBusy || mFileSet.size() == 0)
            return "";
        try {
            File f = mFileSet.get(pos);
            if (f.isDirectory()) {
                return String.valueOf(f.getName().charAt(0));
            } else {
                return Character.toString(f.getName().charAt(0));
            }
        } catch (Exception e) {
            return "";
        }
    }

    private void getSongsForFiles(List<File> files) {
        mSongs.clear();
        for (File file : files) {
            mSongs.add(SongLoader.getSongFromPath(file.getAbsolutePath(), mContext));
        }
    }


    private class NavigateTask extends AsyncTask<File, Void, List<File>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mBusy = true;
        }

        @Override
        protected List<File> doInBackground(File... params) {
            List<File> files = FolderLoader.getMediaFiles(params[0], true);
            getSongsForFiles(files);
            return files;
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

            if (f.isDirectory() && updateDataSetAsync(f)) {
                albumArt.setImageDrawable(mIcons[3]);
            } else if (f.isFile()) {

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int current = -1;
                        long songId = SongLoader.getSongFromPath(mFileSet.get(getAdapterPosition()).getAbsolutePath(),mContext).id;
                        int count = 0;
                        for (Song song : mSongs) {
                            if (song.id != -1) {
                                count++;
                            }
                        }
                        long[] ret = new long[count];
                        int j = 0;
                        for (int i = 0; i < getItemCount(); i++) {
                            if (mSongs.get(i).id != -1) {
                                ret[j] = mSongs.get(i).id;
                                if (mSongs.get(i).id == songId) {
                                    current = j;
                                }
                                j++;
                            }
                        }
                        playAll(mContext, ret, current, -1, TimberUtils.IdType.NA,
                                false, mSongs.get(getAdapterPosition()), false);
                    }
                }, 100);


            }
        }

    }


}