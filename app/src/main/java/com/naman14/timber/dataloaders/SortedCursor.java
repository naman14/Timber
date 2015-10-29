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
package com.naman14.timber.dataloaders;

import android.database.AbstractCursor;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * This cursor basically wraps a song cursor and is given a list of the order of the ids of the
 * contents of the cursor. It wraps the Cursor and simulates the internal cursor being sorted
 * by moving the point to the appropriate spot
 */
public class SortedCursor extends AbstractCursor {
    // cursor to wrap
    private final Cursor mCursor;
    // the map of external indices to internal indices
    private ArrayList<Integer> mOrderedPositions;
    // this contains the ids that weren't found in the underlying cursor
    private ArrayList<Long> mMissingIds;
    // this contains the mapped cursor positions and afterwards the extra ids that weren't found
    private HashMap<Long, Integer> mMapCursorPositions;
    // extra we want to store with the cursor
    private ArrayList<Object> mExtraData;

    /**
     * @param cursor     to wrap
     * @param order      the list of unique ids in sorted order to display
     * @param columnName the column name of the id to look up in the internal cursor
     */
    public SortedCursor(final Cursor cursor, final long[] order, final String columnName,
                        final List<? extends Object> extraData) {
        if (cursor == null) {
            throw new IllegalArgumentException("Non-null cursor is needed");
        }

        mCursor = cursor;
        mMissingIds = buildCursorPositionMapping(order, columnName, extraData);
    }

    /**
     * This function populates mOrderedPositions with the cursor positions in the order based
     * on the order passed in
     *
     * @param order     the target order of the internal cursor
     * @param extraData Extra data we want to add to the cursor
     * @return returns the ids that aren't found in the underlying cursor
     */
    private ArrayList<Long> buildCursorPositionMapping(final long[] order,
                                                       final String columnName, final List<? extends Object> extraData) {
        ArrayList<Long> missingIds = new ArrayList<Long>();

        mOrderedPositions = new ArrayList<Integer>(mCursor.getCount());
        mExtraData = new ArrayList<Object>();

        mMapCursorPositions = new HashMap<Long, Integer>(mCursor.getCount());
        final int idPosition = mCursor.getColumnIndex(columnName);

        if (mCursor.moveToFirst()) {
            // first figure out where each of the ids are in the cursor
            do {
                mMapCursorPositions.put(mCursor.getLong(idPosition), mCursor.getPosition());
            } while (mCursor.moveToNext());

            // now create the ordered positions to map to the internal cursor given the
            // external sort order
            for (int i = 0; order != null && i < order.length; i++) {
                final long id = order[i];
                if (mMapCursorPositions.containsKey(id)) {
                    mOrderedPositions.add(mMapCursorPositions.get(id));
                    mMapCursorPositions.remove(id);
                    if (extraData != null) {
                        mExtraData.add(extraData.get(i));
                    }
                } else {
                    missingIds.add(id);
                }
            }

            mCursor.moveToFirst();
        }

        return missingIds;
    }

    /**
     * @return the list of ids that weren't found in the underlying cursor
     */
    public ArrayList<Long> getMissingIds() {
        return mMissingIds;
    }

    /**
     * @return the list of ids that were in the underlying cursor but not part of the ordered list
     */
    public Collection<Long> getExtraIds() {
        return mMapCursorPositions.keySet();
    }

    /**
     * @return the extra object data that was passed in to be attached to the current row
     */
    public Object getExtraData() {
        int position = getPosition();
        return position < mExtraData.size() ? mExtraData.get(position) : null;
    }

    @Override
    public void close() {
        mCursor.close();

        super.close();
    }

    @Override
    public int getCount() {
        return mOrderedPositions.size();
    }

    @Override
    public String[] getColumnNames() {
        return mCursor.getColumnNames();
    }

    @Override
    public String getString(int column) {
        return mCursor.getString(column);
    }

    @Override
    public short getShort(int column) {
        return mCursor.getShort(column);
    }

    @Override
    public int getInt(int column) {
        return mCursor.getInt(column);
    }

    @Override
    public long getLong(int column) {
        return mCursor.getLong(column);
    }

    @Override
    public float getFloat(int column) {
        return mCursor.getFloat(column);
    }

    @Override
    public double getDouble(int column) {
        return mCursor.getDouble(column);
    }

    @Override
    public boolean isNull(int column) {
        return mCursor.isNull(column);
    }

    @Override
    public boolean onMove(int oldPosition, int newPosition) {
        if (newPosition >= 0 && newPosition < getCount()) {
            mCursor.moveToPosition(mOrderedPositions.get(newPosition));
            return true;
        }

        return false;
    }
}
