package com.volha.yandex.school.musicartists.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Volha on 07.08.2016.
 */

public class ArtistsOpenHelper extends SQLiteOpenHelper implements DBContract{

    private static final int DB_VERSION = 33; // блин, я в прошлый раз опечаталась и нажала 32 :(

    public ArtistsOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ARTIST + "(" +
                ArtistTable.ID + " INTEGER PRIMARY KEY, " +
                ArtistTable.NAME + " TEXT NOT NULL, " +
                ArtistTable.TRACKS + " INTEGER, " +
                ArtistTable.ALBUMS + " INTEGER, " +
                ArtistTable.LINK + " TEXT, " +
                ArtistTable.DESCRIPTION + " TEXT, " +
                ArtistTable.COVER_ID + " INTEGER" +
                ")");

        db.execSQL("CREATE TABLE " + COVER + "(" +
                        CoverTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        CoverTable.BIG + " TEXT, " +
                        CoverTable.SMALL + " TEXT " +
                        ")");

        db.execSQL("CREATE TABLE " + GENRES + "(" +
                GenresTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                GenresTable.NAME + " TEXT UNIQUE NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + ARTISTS_GENRES + "(" +
                ArtistsGenresTable.ARTIST_ID + " INTEGER NOT NULL, " +
                ArtistsGenresTable.GENRE_ID + " INTEGER NOT NULL," +
                " PRIMARY KEY (" +
                    ArtistsGenresTable.ARTIST_ID + ", " +
                    ArtistsGenresTable.GENRE_ID + ")" +
                ")" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DB_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + ARTIST);
            db.execSQL("DROP TABLE IF EXISTS " + COVER);
            db.execSQL("DROP TABLE IF EXISTS " + ARTISTS_GENRES);
            db.execSQL("DROP TABLE IF EXISTS " + GENRES);
            onCreate(db);
        }
    }
}
