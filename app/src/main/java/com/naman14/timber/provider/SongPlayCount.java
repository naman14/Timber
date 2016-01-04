/*
* Copyright (C) 2014 The CyanogenMod Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.naman14.timber.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.HashSet;
import java.util.Iterator;

/**
 * This database tracks the number of play counts for an individual song.  This is used to drive
 * the top played tracks as well as the playlist images
 */
public class SongPlayCount {
    // how many weeks worth of playback to track
    private static final int NUM_WEEKS = 52;
    private static SongPlayCount sInstance = null;
    // interpolator curve applied for measuring the curve
    private static Interpolator sInterpolator = new AccelerateInterpolator(1.5f);
    // how high to multiply the interpolation curve
    private static int INTERPOLATOR_HEIGHT = 50;
    // how high the base value is. The ratio of the Height to Base is what really matters
    private static int INTERPOLATOR_BASE = 25;
    private static int ONE_WEEK_IN_MS = 1000 * 60 * 60 * 24 * 7;
    private static String WHERE_ID_EQUALS = SongPlayCountColumns.ID + "=?";
    private MusicDB mMusicDatabase = null;
    // number of weeks since epoch time
    private int mNumberOfWeeksSinceEpoch;

    // used to track if we've walkd through the db and updated all the rows
    private boolean mDatabaseUpdated;

    /**
     * Constructor of <code>RecentStore</code>
     *
     * @param context The {@link android.content.Context} to use
     */
    public SongPlayCount(final Context context) {
        mMusicDatabase = MusicDB.getInstance(context);

        long msSinceEpoch = System.currentTimeMillis();
        mNumberOfWeeksSinceEpoch = (int) (msSinceEpoch / ONE_WEEK_IN_MS);

        mDatabaseUpdated = false;
    }

    /**
     * @param context The {@link android.content.Context} to use
     * @return A new instance of this class.
     */
    public static final synchronized SongPlayCount getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new SongPlayCount(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Calculates the score of the song given the play counts
     *
     * @param playCounts an array of the # of times a song has been played for each week
     *                   where playCounts[N] is the # of times it was played N weeks ago
     * @return the score
     */
    private static float calculateScore(final int[] playCounts) {
        if (playCounts == null) {
            return 0;
        }

        float score = 0;
        for (int i = 0; i < Math.min(playCounts.length, NUM_WEEKS); i++) {
            score += playCounts[i] * getScoreMultiplierForWeek(i);
        }

        return score;
    }

    /**
     * Gets the column name for each week #
     *
     * @param week number
     * @return the column name
     */
    private static String getColumnNameForWeek(final int week) {
        return SongPlayCountColumns.WEEK_PLAY_COUNT + String.valueOf(week);
    }

    /**
     * Gets the score multiplier for each week
     *
     * @param week number
     * @return the multiplier to apply
     */
    private static float getScoreMultiplierForWeek(final int week) {
        return sInterpolator.getInterpolation(1 - (week / (float) NUM_WEEKS)) * INTERPOLATOR_HEIGHT
                + INTERPOLATOR_BASE;
    }

    /**
     * For some performance gain, return a static value for the column index for a week
     * WARNIGN: This function assumes you have selected all columns for it to work
     *
     * @param week number
     * @return column index of that week
     */
    private static int getColumnIndexForWeek(final int week) {
        // ID, followed by the weeks columns
        return 1 + week;
    }

    public void onCreate(final SQLiteDatabase db) {
        // create the play count table
        // WARNING: If you change the order of these columns
        // please update getColumnIndexForWeek
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(SongPlayCountColumns.NAME);
        builder.append("(");
        builder.append(SongPlayCountColumns.ID);
        builder.append(" INT UNIQUE,");

        for (int i = 0; i < NUM_WEEKS; i++) {
            builder.append(getColumnNameForWeek(i));
            builder.append(" INT DEFAULT 0,");
        }

        builder.append(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX);
        builder.append(" INT NOT NULL,");

        builder.append(SongPlayCountColumns.PLAYCOUNTSCORE);
        builder.append(" REAL DEFAULT 0);");

        db.execSQL(builder.toString());
    }

    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        // No upgrade path needed yet
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If we ever have downgrade, drop the table to be safe
        db.execSQL("DROP TABLE IF EXISTS " + SongPlayCountColumns.NAME);
        onCreate(db);
    }

    /**
     * Increases the play count of a song by 1
     *
     * @param songId The song id to increase the play count
     */
    public void bumpSongCount(final long songId) {
        if (songId < 0) {
            return;
        }

        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        updateExistingRow(database, songId, true);
    }

    /**
     * This creates a new entry that indicates a song has been played once as well as its score
     *
     * @param database a writeable database
     * @param songId   the id of the track
     */
    private void createNewPlayedEntry(final SQLiteDatabase database, final long songId) {
        // no row exists, create a new one
        float newScore = getScoreMultiplierForWeek(0);
        int newPlayCount = 1;

        final ContentValues values = new ContentValues(3);
        values.put(SongPlayCountColumns.ID, songId);
        values.put(SongPlayCountColumns.PLAYCOUNTSCORE, newScore);
        values.put(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX, mNumberOfWeeksSinceEpoch);
        values.put(getColumnNameForWeek(0), newPlayCount);

        database.insert(SongPlayCountColumns.NAME, null, values);
    }

    /**
     * This function will take a song entry and update it to the latest week and increase the count
     * for the current week by 1 if necessary
     *
     * @param database  a writeable database
     * @param id        the id of the track to bump
     * @param bumpCount whether to bump the current's week play count by 1 and adjust the score
     */
    private void updateExistingRow(final SQLiteDatabase database, final long id, boolean bumpCount) {
        String stringId = String.valueOf(id);

        // begin the transaction
        database.beginTransaction();

        // get the cursor of this content inside the transaction
        final Cursor cursor = database.query(SongPlayCountColumns.NAME, null, WHERE_ID_EQUALS,
                new String[]{stringId}, null, null, null);

        // if we have a result
        if (cursor != null && cursor.moveToFirst()) {
            // figure how many weeks since we last updated
            int lastUpdatedIndex = cursor.getColumnIndex(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX);
            int lastUpdatedWeek = cursor.getInt(lastUpdatedIndex);
            int weekDiff = mNumberOfWeeksSinceEpoch - lastUpdatedWeek;

            // if it's more than the number of weeks we track, delete it and create a new entry
            if (Math.abs(weekDiff) >= NUM_WEEKS) {
                // this entry needs to be dropped since it is too outdated
                deleteEntry(database, stringId);
                if (bumpCount) {
                    createNewPlayedEntry(database, id);
                }
            } else if (weekDiff != 0) {
                // else, shift the weeks
                int[] playCounts = new int[NUM_WEEKS];

                if (weekDiff > 0) {
                    // time is shifted forwards
                    for (int i = 0; i < NUM_WEEKS - weekDiff; i++) {
                        playCounts[i + weekDiff] = cursor.getInt(getColumnIndexForWeek(i));
                    }
                } else if (weekDiff < 0) {
                    // time is shifted backwards (by user) - nor typical behavior but we
                    // will still handle it

                    // since weekDiff is -ve, NUM_WEEKS + weekDiff is the real # of weeks we have to
                    // transfer.  Then we transfer the old week i - weekDiff to week i
                    // for example if the user shifted back 2 weeks, ie -2, then for 0 to
                    // NUM_WEEKS + (-2) we set the new week i = old week i - (-2) or i+2
                    for (int i = 0; i < NUM_WEEKS + weekDiff; i++) {
                        playCounts[i] = cursor.getInt(getColumnIndexForWeek(i - weekDiff));
                    }
                }

                // bump the count
                if (bumpCount) {
                    playCounts[0]++;
                }

                float score = calculateScore(playCounts);

                // if the score is non-existant, then delete it
                if (score < .01f) {
                    deleteEntry(database, stringId);
                } else {
                    // create the content values
                    ContentValues values = new ContentValues(NUM_WEEKS + 2);
                    values.put(SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX, mNumberOfWeeksSinceEpoch);
                    values.put(SongPlayCountColumns.PLAYCOUNTSCORE, score);

                    for (int i = 0; i < NUM_WEEKS; i++) {
                        values.put(getColumnNameForWeek(i), playCounts[i]);
                    }

                    // update the entry
                    database.update(SongPlayCountColumns.NAME, values, WHERE_ID_EQUALS,
                            new String[]{stringId});
                }
            } else if (bumpCount) {
                // else no shifting, just update the scores
                ContentValues values = new ContentValues(2);

                // increase the score by a single score amount
                int scoreIndex = cursor.getColumnIndex(SongPlayCountColumns.PLAYCOUNTSCORE);
                float score = cursor.getFloat(scoreIndex) + getScoreMultiplierForWeek(0);
                values.put(SongPlayCountColumns.PLAYCOUNTSCORE, score);

                // increase the play count by 1
                values.put(getColumnNameForWeek(0), cursor.getInt(getColumnIndexForWeek(0)) + 1);

                // update the entry
                database.update(SongPlayCountColumns.NAME, values, WHERE_ID_EQUALS,
                        new String[]{stringId});
            }

            cursor.close();
        } else if (bumpCount) {
            // if we have no existing results, create a new one
            createNewPlayedEntry(database, id);
        }

        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void deleteAll() {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        database.delete(SongPlayCountColumns.NAME, null, null);
    }

    /**
     * Gets a cursor containing the top songs played.  Note this only returns songs that have been
     * played at least once in the past NUM_WEEKS
     *
     * @param numResults number of results to limit by.  If <= 0 it returns all results
     * @return the top tracks
     */
    public Cursor getTopPlayedResults(int numResults) {
        updateResults();

        final SQLiteDatabase database = mMusicDatabase.getReadableDatabase();
        return database.query(SongPlayCountColumns.NAME, new String[]{SongPlayCountColumns.ID},
                null, null, null, null, SongPlayCountColumns.PLAYCOUNTSCORE + " DESC",
                (numResults <= 0 ? null : String.valueOf(numResults)));
    }

    /**
     * Given a list of ids, it sorts the results based on the most played results
     *
     * @param ids list
     * @return sorted list - this may be smaller than the list passed in for performance reasons
     */
    public long[] getTopPlayedResultsForList(long[] ids) {
        final int MAX_NUMBER_SONGS_TO_ANALYZE = 250;

        if (ids == null || ids.length == 0) {
            return null;
        }

        HashSet<Long> uniqueIds = new HashSet<Long>(ids.length);

        // create the list of ids to select against
        StringBuilder selection = new StringBuilder();
        selection.append(SongPlayCountColumns.ID);
        selection.append(" IN (");

        // add the first element to handle the separator case for the first element
        uniqueIds.add(ids[0]);
        selection.append(ids[0]);

        for (int i = 1; i < ids.length; i++) {
            // if the new id doesn't exist
            if (uniqueIds.add(ids[i])) {
                // append a separator
                selection.append(",");

                // append the id
                selection.append(ids[i]);

                // for performance reasons, only look at a certain number of songs
                // in case their playlist is ridiculously large
                if (uniqueIds.size() >= MAX_NUMBER_SONGS_TO_ANALYZE) {
                    break;
                }
            }
        }

        // close out the selection
        selection.append(")");

        long[] sortedList = new long[uniqueIds.size()];

        // now query for the songs
        final SQLiteDatabase database = mMusicDatabase.getReadableDatabase();
        Cursor topSongsCursor = null;
        int idx = 0;

        try {
            topSongsCursor = database.query(SongPlayCountColumns.NAME,
                    new String[]{SongPlayCountColumns.ID}, selection.toString(), null, null,
                    null, SongPlayCountColumns.PLAYCOUNTSCORE + " DESC");

            if (topSongsCursor != null && topSongsCursor.moveToFirst()) {
                do {
                    // for each id found, add it to the list and remove it from the unique ids
                    long id = topSongsCursor.getLong(0);
                    sortedList[idx++] = id;
                    uniqueIds.remove(id);
                } while (topSongsCursor.moveToNext());
            }
        } finally {
            if (topSongsCursor != null) {
                topSongsCursor.close();
                topSongsCursor = null;
            }
        }

        // append the remaining items - these are songs that haven't been played recently
        Iterator<Long> iter = uniqueIds.iterator();
        while (iter.hasNext()) {
            sortedList[idx++] = iter.next();
        }

        return sortedList;
    }

    /**
     * This updates all the results for the getTopPlayedResults so that we can get an
     * accurate list of the top played results
     */
    private synchronized void updateResults() {
        if (mDatabaseUpdated) {
            return;
        }

        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();

        database.beginTransaction();

        int oldestWeekWeCareAbout = mNumberOfWeeksSinceEpoch - NUM_WEEKS + 1;
        // delete rows we don't care about anymore
        database.delete(SongPlayCountColumns.NAME, SongPlayCountColumns.LAST_UPDATED_WEEK_INDEX
                + " < " + oldestWeekWeCareAbout, null);

        // get the remaining rows
        Cursor cursor = database.query(SongPlayCountColumns.NAME,
                new String[]{SongPlayCountColumns.ID},
                null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // for each row, update it
            do {
                updateExistingRow(database, cursor.getLong(0), false);
            } while (cursor.moveToNext());

            cursor.close();
            cursor = null;
        }

        mDatabaseUpdated = true;
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    /**
     * @param songId The song Id to remove.
     */
    public void removeItem(final long songId) {
        final SQLiteDatabase database = mMusicDatabase.getWritableDatabase();
        deleteEntry(database, String.valueOf(songId));
    }

    /**
     * Deletes the entry
     *
     * @param database database to use
     * @param stringId id to delete
     */
    private void deleteEntry(final SQLiteDatabase database, final String stringId) {
        database.delete(SongPlayCountColumns.NAME, WHERE_ID_EQUALS, new String[]{stringId});
    }

    public interface SongPlayCountColumns {

        /* Table name */
        String NAME = "songplaycount";

        /* Song IDs column */
        String ID = "songid";

        /* Week Play Count */
        String WEEK_PLAY_COUNT = "week";

        /* Weeks since Epoch */
        String LAST_UPDATED_WEEK_INDEX = "weekindex";

        /* Play count */
        String PLAYCOUNTSCORE = "playcountscore";
    }
}
