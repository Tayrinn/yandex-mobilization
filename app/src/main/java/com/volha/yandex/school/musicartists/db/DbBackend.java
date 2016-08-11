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

    private SQLiteDatabase db;

    public DbBackend(Context context) {
        ArtistsOpenHelper helper = new ArtistsOpenHelper(context);
        db = helper.getWritableDatabase();
        db.enableWriteAheadLogging();
    }

    private static final String ARTIST_QUERY = "SELECT *, " +
            "group_concat(" + GenresTable.NAME + ") as " + ArtistTable.GENRES
            + " FROM " + ARTIST + " as a"
            + " JOIN " + COVER + " as c"
            + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID
            + " JOIN " + ARTISTS_GENRES + " as ag"
            + " ON a." + ArtistTable.ID + " = ag." + ArtistsGenresTable.ARTIST_ID
            + " JOIN " + GENRES + " as g"
            + " ON ag." + ArtistsGenresTable.GENRE_ID + " = g." + GenresTable.ID;

    public long insertArtist(Artist artist) {
        db.beginTransaction();
        long id = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(ArtistTable.ID, artist.getId());
            values.put(ArtistTable.COVER_ID, insertCover(artist.getCover()));
            values.put(ArtistTable.NAME, artist.getName());
            values.put(ArtistTable.ALBUMS, artist.getAlbums());
            values.put(ArtistTable.TRACKS, artist.getTracks());
            values.put(ArtistTable.DESCRIPTION, artist.getDescription());
            values.put(ArtistTable.LINK, artist.getLink());
            if (artist.getGenres() != null) {
                for (String genre : artist.getGenres()) {
                    long genreId = insertGenre(genre);
                    insertArtistGenre(artist.getId(), genreId);
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

    public long insertArtist(ContentValues values) {
        Artist artist = DBUtils.getArtistFromValues(values);
        return insertArtist(artist);
    }

    public int  bulkInsertArtists(ContentValues[] values) {
        db.beginTransaction();
        int count = 0;
        try {
            for (ContentValues value : values) {
                if (insertArtist(value) > 0)
                    count++;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return count;
    }

    public int deleteArtist(String selection, String[] selectionArgs) {
        return db.delete(ARTIST, selection, selectionArgs);
    }

    public int updateArtist(ContentValues values, String selection, String[] selectionArgs) {
        return db.update(ARTIST, values, selection, selectionArgs);
    }

    private long insertCover(Cover cover) {
        ContentValues values = new ContentValues();
        values.put(CoverTable.BIG, cover.getBig());
        values.put(CoverTable.SMALL, cover.getSmall());
        return db.insert(COVER, null, values);
    }

    private long insertGenre(String genre) {
        ContentValues values = new ContentValues();
        values.put(GenresTable.NAME, genre);
        long id = db.insertWithOnConflict(GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            id = getGenreId(genre);
        }
        return id;
    }

    private long insertArtistGenre(int artistId, long genreId) {
        ContentValues values = new ContentValues();
        values.put(ArtistsGenresTable.ARTIST_ID, artistId);
        values.put(ArtistsGenresTable.GENRE_ID, genreId);
        return db.insert(ARTISTS_GENRES, null, values);
    }

    private long getGenreId(String genre) {
        String query = "SELECT " + GenresTable.ID + " FROM " + GENRES
                + " WHERE " + GenresTable.NAME + " like ?";
        Cursor cursor = db.rawQuery(query, new String[]{genre});
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
    }

    public int clearAll() {
        int cnt = db.delete(ARTIST, null, null);
        db.delete(COVER, null, null);
        db.delete(ARTISTS_GENRES, null, null);
        db.delete(GENRES, null, null);
        return cnt;
    }

    public Cursor getArtistsAndCovers() {
        String query = ARTIST_QUERY + " GROUP BY a." + ArtistTable.ID;
        return db.rawQuery(query, null);
    }

    public Cursor getArtist(String id) {
        String query = ARTIST_QUERY + " WHERE a." + ArtistTable.ID + " = ?";
        return db.rawQuery(query, new String[]{id});
    }

    @VisibleForTesting
    public int getCountAndClose(String tableName) {
        Cursor cursor = db.rawQuery("SELECT count(*) as size FROM " + tableName, null);
        cursor.moveToFirst();
        int size = cursor.getInt(0);
        cursor.close();
        return size;
    }
}
