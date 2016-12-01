package com.naman14.timber.musicplayer;

import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import static com.naman14.timber.musicplayer.MusicService.MAX_HISTORY_SIZE;

public final class Shuffler {

    private final LinkedList<Integer> mHistoryOfNumbers = new LinkedList<Integer>();

    private final TreeSet<Integer> mPreviousNumbers = new TreeSet<Integer>();

    private final Random mRandom = new Random();

    private int mPrevious;


    public Shuffler() {
        super();
    }


    public int nextInt(final int interval) {
        int next;
        do {
            next = mRandom.nextInt(interval);
        } while (next == mPrevious && interval > 1
                && !mPreviousNumbers.contains(Integer.valueOf(next)));
        mPrevious = next;
        mHistoryOfNumbers.add(mPrevious);
        mPreviousNumbers.add(mPrevious);
        cleanUpHistory();
        return next;
    }


    private void cleanUpHistory() {
        if (!mHistoryOfNumbers.isEmpty() && mHistoryOfNumbers.size() >= MAX_HISTORY_SIZE) {
            for (int i = 0; i < Math.max(1, MAX_HISTORY_SIZE / 2); i++) {
                mPreviousNumbers.remove(mHistoryOfNumbers.removeFirst());
            }
        }
    }
}