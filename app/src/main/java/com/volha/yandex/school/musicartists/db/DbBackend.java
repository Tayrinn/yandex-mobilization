package com.volha.yandex.school.musicartists.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

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
        values.put(ArtistTable.LINK, artist.getLink());
        for (String genre : artist.getGenres()) {
            long genreId = insertGenre(db, genre);
            insertArtistGenre(db, artist.getId(), genreId);
        }
        return db.insert(ARTIST, null, values);
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
        Cursor cursor = db.rawQuery(query, new String[] {genre});
        cursor.moveToFirst();
        long id = cursor.getLong(0);
        cursor.close();
        return id;
    }

    public void clearAll(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + ARTIST);
        db.execSQL("DELETE FROM " + COVER);
        db.execSQL("DELETE FROM " + ARTISTS_GENRES);
        db.execSQL("DELETE FROM " + GENRES);
    }

    public Cursor getArtistsAndCovers(SQLiteDatabase db) {
        String query = "SELECT *, " +
                "group_concat(" + GenresTable.NAME + ") as " + ArtistTable.GENRES
                + " FROM " + ARTIST + " as a"
                + " JOIN " + COVER + " as c"
                + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID
                + " JOIN " + ARTISTS_GENRES + " as ag"
                + " ON a." + ArtistTable.ID + " = ag." + ArtistsGenresTable.ARTIST_ID
                + " JOIN " + GENRES + " as g"
                + " ON ag." + ArtistsGenresTable.GENRE_ID + " = g." + GenresTable.ID
                + " GROUP BY a." + ArtistTable.ID;
        return db.rawQuery(query, null);
    }

    public Cursor getArtist(SQLiteDatabase db, String id) {
        String query = "SELECT *, " +
                "group_concat(" + GenresTable.NAME + ") as " + ArtistTable.GENRES
                + " FROM " + ARTIST + " as a"
                + " LEFT OUTER JOIN " + COVER + " as c"
                + " ON a." + ArtistTable.COVER_ID + " = c." + CoverTable.ID
                + " LEFT OUTER JOIN " + ARTISTS_GENRES + " as ag"
                + " ON a." + ArtistTable.ID + " = ag." + ArtistsGenresTable.ARTIST_ID
                + " LEFT OUTER JOIN " + GENRES + " as g"
                + " ON ag." + ArtistsGenresTable.GENRE_ID + " = g." + GenresTable.ID
                + " WHERE a." + ArtistTable.ID + " = ?"
                + " GROUP BY a." + ArtistTable.ID ;

        return db.rawQuery(query, new String[] {id});
    }

    public Artist getArtistFromCursor(Cursor cursor) {
        Artist artist = new Artist();
        artist.setName(cursor.getString(cursor.getColumnIndex(ArtistTable.NAME)));
        artist.setDescription(cursor.getString(cursor.getColumnIndex(ArtistTable.DESCRIPTION)));
        artist.setLink(cursor.getString(cursor.getColumnIndex(ArtistTable.LINK)));
        artist.setAlbums(cursor.getInt(cursor.getColumnIndex(ArtistTable.ALBUMS)));
        artist.setTracks(cursor.getInt(cursor.getColumnIndex(ArtistTable.TRACKS)));
        artist.setId(cursor.getInt(cursor.getColumnIndex(ArtistTable.ID)));
        artist.setGenresString(cursor.getString(cursor.getColumnIndex(ArtistTable.GENRES)));
        Cover cover = new Cover();
        cover.setBig(cursor.getString(cursor.getColumnIndex(CoverTable.BIG)));
        cover.setSmall(cursor.getString(cursor.getColumnIndex(CoverTable.SMALL)));
        artist.setCover(cover);
        return artist;
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
