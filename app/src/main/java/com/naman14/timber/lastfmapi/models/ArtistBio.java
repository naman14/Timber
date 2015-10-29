/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.naman14.timber.lastfmapi.models;

import com.google.gson.annotations.SerializedName;

public class ArtistBio {

    private static final String PUBLISHED = "published";
    private static final String SUMMARY = "summary";
    private static final String CONTENT = "content";
    private static final String YEARFORMED = "yearformed";

    @SerializedName(PUBLISHED)
    public String mPublished;

    @SerializedName(SUMMARY)
    public String mSummary;

    @SerializedName(CONTENT)
    public String mContent;

    @SerializedName(YEARFORMED)
    public String mYearFormed;

}
