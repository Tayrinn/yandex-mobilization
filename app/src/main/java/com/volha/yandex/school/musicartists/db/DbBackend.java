package com.volha.yandex.school.musicartists.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;

/**
 * Created by Volha on 07.08.2016.
 */

public class DbBackend implements DBContract {

    private static final String ARTIST_QUERY = "SELECT *, " +
            "group_concat(" + GenresTable.NAME + ") as " + ArtistTable.GENRES
            + " FROM " + ARTIST + " as a"
            + " JOIN " + COVER + " as c"
            + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID
            + " JOIN " + ARTISTS_GENRES + " as ag"
            + " ON a." + ArtistTable.ID + " = ag." + ArtistsGenresTable.ARTIST_ID
            + " JOIN " + GENRES + " as g"
            + " ON ag." + ArtistsGenresTable.GENRE_ID + " = g." + GenresTable.ID;

    public long insertArtist(SQLiteDatabase db, Artist artist) {
        db.beginTransaction();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(ArtistTable.ID, artist.getId());
            values.put(ArtistTable.COVER_ID, insertCover(db, artist.getCover()));
            values.put(ArtistTable.NAME, artist.getName());
            values.put(ArtistTable.ALBUMS, artist.getAlbums());
            values.put(ArtistTable.TRACKS, artist.getTracks());
            values.put(ArtistTable.DESCRIPTION, artist.getDescription());
            values.put(ArtistTable.LINK, artist.getLink());
            if (artist.getGenres() != null) {
                for (String genre : artist.getGenres()) {
                    long genreId = insertGenre(db, genre);
                    insertArtistGenre(db, artist.getId(), genreId);
                }
            }
            id = db.insert(ARTIST, null, values);
            Log.d("insert", "id=" + id);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public long insertArtist(SQLiteDatabase db, ContentValues values) {
        Artist artist = DBUtils.getArtistFromValues(values);
        return insertArtist(db, artist);
    }

    public int deleteArtist(SQLiteDatabase db, String selection, String[] selectionArgs) {
        return db.delete(ARTIST, selection, selectionArgs);
    }

    public int updateArtist(SQLiteDatabase db, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(ARTIST, values, selection, selectionArgs);
    }

    private long insertCover(SQLiteDatabase db, Cover cover) {
        ContentValues values = new ContentValues();
        values.put(CoverTable.BIG, cover.getBig());
        values.put(CoverTable.SMALL, cover.getSmall());
        return db.insert(COVER, null, values);
    }

    private long insertGenre(SQLiteDatabase db, String genre) {
        ContentValues values = new ContentValues();
        values.put(GenresTable.NAME, genre);
        long id = db.insertWithOnConflict(GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            id = getGenreId(db, genre);
        }
        return id;
    }

    private long insertArtistGenre(SQLiteDatabase db, int artistId, long genreId) {
        ContentValues values = new ContentValues();
        values.put(ArtistsGenresTable.ARTIST_ID, artistId);
        values.put(ArtistsGenresTable.GENRE_ID, genreId);
        return db.insert(ARTISTS_GENRES, null, values);
    }

    private long getGenreId(SQLiteDatabase db, String genre) {
        String query = "SELECT " + GenresTable.ID + " FROM " + GENRES
                + " WHERE " + GenresTable.NAME + " like ?";
        Cursor cursor = db.rawQuery(query, new String[]{genre});
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
    }

    public int clearAll(SQLiteDatabase db) {
        int cnt = db.delete(ARTIST, null, null);
        db.delete(COVER, null, null);
        db.delete(ARTISTS_GENRES, null, null);
        db.delete(GENRES, null, null);
        return cnt;
    }

    public Cursor getArtistsAndCovers(SQLiteDatabase db) {
        String query = ARTIST_QUERY + " GROUP BY a." + ArtistTable.ID;
        return db.rawQuery(query, null);
    }

    public Cursor getArtist(SQLiteDatabase db, String id) {
        String query = ARTIST_QUERY + " WHERE a." + ArtistTable.ID + " = ?";
        return db.rawQuery(query, new String[]{id});
    }

    @VisibleForTesting
    public int getCountAndClose(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT count(*) as size FROM " + tableName, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        return size;
    }
}
