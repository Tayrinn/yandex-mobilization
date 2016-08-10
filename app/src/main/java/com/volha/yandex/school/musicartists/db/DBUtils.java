package com.volha.yandex.school.musicartists.db;

import android.database.Cursor;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;

/**
 * Created by Volha on 10.08.2016.
 */

public class DBUtils {

    public static Artist getArtistFromCursor(Cursor cursor) {
        Artist artist = new Artist();
        artist.setName(cursor.getString(cursor.getColumnIndex(DBContract.ArtistTable.NAME)));
        artist.setDescription(cursor.getString(cursor.getColumnIndex(DBContract.ArtistTable.DESCRIPTION)));
        artist.setLink(cursor.getString(cursor.getColumnIndex(DBContract.ArtistTable.LINK)));
        artist.setAlbums(cursor.getInt(cursor.getColumnIndex(DBContract.ArtistTable.ALBUMS)));
        artist.setTracks(cursor.getInt(cursor.getColumnIndex(DBContract.ArtistTable.TRACKS)));
        artist.setId(cursor.getInt(cursor.getColumnIndex(DBContract.ArtistTable.ID)));
        artist.setGenresString(cursor.getString(cursor.getColumnIndex(DBContract.ArtistTable.GENRES)));
        Cover cover = new Cover();
        cover.setBig(cursor.getString(cursor.getColumnIndex(DBContract.CoverTable.BIG)));
        cover.setSmall(cursor.getString(cursor.getColumnIndex(DBContract.CoverTable.SMALL)));
        artist.setCover(cover);
        return artist;
    }
}
