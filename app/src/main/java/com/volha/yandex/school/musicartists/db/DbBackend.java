package com.volha.yandex.school.musicartists.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;

/**
 * Created by Volha on 07.08.2016.
 */

public class DbBackend implements DBContract {

    public long insertArtist(SQLiteDatabase db, Artist artist) {
        ContentValues values = new ContentValues();
        values.put(ArtistTable.ID, artist.getId());
        values.put(ArtistTable.COVER_ID, insertCover(db, artist.getCover()));
        values.put(ArtistTable.NAME, artist.getName());
        values.put(ArtistTable.ALBUMS, artist.getAlbums());
        values.put(ArtistTable.TRACKS, artist.getTracks());
        values.put(ArtistTable.DESCRIPTION, artist.getDescription());
        values.put(ArtistTable.GENRES, artist.getGenresString());
        values.put(ArtistTable.LINK, artist.getLink());
        return db.insert(ARTIST, null, values);
    }

    private long insertCover(SQLiteDatabase db, Cover cover) {
        ContentValues values = new ContentValues();
        values.put(CoverTable.BIG, cover.getBig());
        values.put(CoverTable.SMALL, cover.getSmall());
        return db.insert(COVER, null, values);
    }

    public void clearAll(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + ARTIST);
        db.execSQL("DELETE FROM " + COVER);
    }

    public Cursor getArtistsAndCovers(SQLiteDatabase db) {
        String query = "SELECT * FROM " + ARTIST + " as a"
                + " LEFT OUTER JOIN " + COVER + " as c"
                + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID;
        return db.rawQuery(query, null);
    }

    public Cursor getArtist(SQLiteDatabase db, int id) {
        String query = "SELECT * FROM " + ARTIST + " as a"
                + " LEFT OUTER JOIN " + COVER + " as c"
                + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID
                + " WHERE a." + ArtistTable.ID + " = ?";
        return db.rawQuery(query, new String[] {String.valueOf(id)});
    }

    public Artist getArtistFromCursor(Cursor cursor) {
        Artist artist = new Artist();
        artist.setName(cursor.getString(cursor.getColumnIndex(ArtistTable.NAME)));
        artist.setDescription(cursor.getString(cursor.getColumnIndex(ArtistTable.DESCRIPTION)));
        artist.setLink(cursor.getString(cursor.getColumnIndex(ArtistTable.LINK)));
        artist.setAlbums(cursor.getInt(cursor.getColumnIndex(ArtistTable.ALBUMS)));
        artist.setTracks(cursor.getInt(cursor.getColumnIndex(ArtistTable.TRACKS)));
        artist.setId(cursor.getInt(cursor.getColumnIndex(ArtistTable.ID)));
        artist.setGenresString(cursor.getString(cursor.getColumnIndex(ArtistTable.TRACKS)));
        Cover cover = new Cover();
        cover.setBig(cursor.getString(cursor.getColumnIndex(CoverTable.BIG)));
        cover.setSmall(cursor.getString(cursor.getColumnIndex(CoverTable.SMALL)));
        artist.setCover(cover);
        return artist;
    }
}
