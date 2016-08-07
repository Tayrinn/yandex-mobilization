package com.volha.yandex.school.musicartists.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Volha on 07.08.2016.
 */

public class ArtistsOpenHelper extends SQLiteOpenHelper implements DBContract{

    private static final int DB_VERSION = 1;

    public ArtistsOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ARTIST + "(" +
                ArtistTable.ID + " INTEGER PRIMARY KEY, " +
                ArtistTable.NAME + " TEXT NOT NULL, " +
                ArtistTable.GENRES + " TEXT, " +
                ArtistTable.TRACKS + " INTEGER, " +
                ArtistTable.ALBUMS + " INTEGER, " +
                ArtistTable.LINK + " TEXT, " +
                ArtistTable.DESCRIPTION + " TEXT, " +
                ArtistTable.COVER_ID + " INTEGER" +
                ")");

        db.execSQL(
                "CREATE TABLE " + COVER + "(" +
                        CoverTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CoverTable.BIG + " TEXT, " +
                        CoverTable.SMALL + " TEXT " +
                        ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
