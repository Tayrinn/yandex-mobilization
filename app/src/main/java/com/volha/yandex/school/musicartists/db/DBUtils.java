package com.volha.yandex.school.musicartists.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;

/**
 * Created by Volha on 10.08.2016.
 */

public class DBUtils implements DBContract {

    public static Artist getArtistFromCursor(Cursor cursor) {
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

    public static ContentValues getValuesFromArtist(Artist artist) {
        ContentValues values = new ContentValues();
        values.put(ArtistTable.NAME, artist.getName());
        values.put(ArtistTable.DESCRIPTION, artist.getDescription());
        values.put(ArtistTable.LINK, artist.getLink());
        values.put(ArtistTable.ALBUMS, artist.getAlbums());
        values.put(ArtistTable.TRACKS, artist.getTracks());
        values.put(ArtistTable.ID, artist.getId());
        values.put(ArtistTable.GENRES, artist.getGenresString());
        values.put(CoverTable.BIG, artist.getCover().getBig());
        values.put(CoverTable.SMALL, artist.getCover().getSmall());
        return values;
    }

    public static Artist getArtistFromValues(ContentValues values) {
        Artist artist = new Artist();
        artist.setName(values.getAsString(ArtistTable.NAME));
        artist.setDescription(values.getAsString(ArtistTable.DESCRIPTION));
        artist.setLink(values.getAsString(ArtistTable.LINK));
        artist.setAlbums(values.getAsInteger(ArtistTable.ALBUMS));
        artist.setTracks(values.getAsInteger(ArtistTable.TRACKS));
        artist.setId(values.getAsInteger(ArtistTable.ID));
        artist.setGenresString(values.getAsString(ArtistTable.GENRES));
        Cover cover = new Cover();
        cover.setBig(values.getAsString(CoverTable.BIG));
        cover.setSmall(values.getAsString(CoverTable.SMALL));
        artist.setCover(cover);
        return artist;
    }
}
