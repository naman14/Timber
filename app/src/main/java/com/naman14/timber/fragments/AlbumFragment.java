package com.naman14.timber.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naman14.timber.R;
import com.naman14.timber.adapters.AlbumAdapter;
import com.naman14.timber.dataloaders.AlbumLoader;

/**
 * Created by naman on 07/07/15.
 */
public class AlbumFragment extends Fragment {

    private AlbumAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_recyclerview, container, false);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mAdapter = new AlbumAdapter(getActivity(), AlbumLoader.getAllAlbums(getActivity()));
        recyclerView.setAdapter(mAdapter);

//        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
//            @Override
//            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
//                int bitmapWidth = Math.round(screenBounds.width());
//                int bitmapHeight = Math.round(screenBounds.height());
//                Bitmap bitmap = null;
//                if (bitmapWidth > 0 && bitmapHeight > 0) {
//                    Matrix matrix = new Matrix();
//                    matrix.set(viewToGlobalMatrix);
//                    matrix.postTranslate(-screenBounds.left, -screenBounds.top);
//                    bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    canvas.concat(matrix);
//                    sharedElement.draw(canvas);
//                }
//                return bitmap;
//            }
//        });

        return recyclerView;
    }

}

