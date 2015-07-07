package com.naman14.timber.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import com.naman14.timber.utils.TimberUtils;

/**
 * This is used by the music playback service to track the music tracks it is playing
 * It has extra meta data to determine where the track came from so that we can show the appropriate
 * song playing indicator
 */
public class MusicPlaybackTrack implements Parcelable {

    public long mId;

    public long mSourceId;

    public TimberUtils.IdType mSourceType;

    public int mSourcePosition;


    public static final Creator<MusicPlaybackTrack> CREATOR = new Creator<MusicPlaybackTrack>() {
        @Override
        public MusicPlaybackTrack createFromParcel(Parcel source) {
            return new MusicPlaybackTrack(source);
        }

        @Override
        public MusicPlaybackTrack[] newArray(int size) {
            return new MusicPlaybackTrack[size];
        }
    };

    public MusicPlaybackTrack(long id, long sourceId, TimberUtils.IdType type, int sourcePosition) {
        mId = id;
        mSourceId = sourceId;
        mSourceType = type;
        mSourcePosition = sourcePosition;
    }

    public MusicPlaybackTrack(Parcel in) {
        mId = in.readLong();
        mSourceId = in.readLong();
        mSourceType = TimberUtils.IdType.getTypeById(in.readInt());
        mSourcePosition = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeLong(mSourceId);
        dest.writeInt(mSourceType.mId);
        dest.writeInt(mSourcePosition);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MusicPlaybackTrack) {
            MusicPlaybackTrack other = (MusicPlaybackTrack)o;
            if (other != null) {
                if (mId == other.mId
                        && mSourceId == other.mSourceId
                        && mSourceType == other.mSourceType
                        && mSourcePosition == other.mSourcePosition) {
                    return true;
                }

                return false;
            }
        }

        return super.equals(o);
    }
}
