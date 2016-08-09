package com.volha.yandex.school.musicartists;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;
import com.volha.yandex.school.musicartists.db.ArtistsOpenHelper;
import com.volha.yandex.school.musicartists.db.DBContract;
import com.volha.yandex.school.musicartists.db.DbBackend;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Created by Volha on 07.08.2016.
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class DbBackendTest implements DBContract {

    @Test
    public void insertArtist(){
        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();
        Artist artist = createArtist();
        dbBackend.insertArtist(db, artist);

        Assert.assertEquals(1, dbBackend.getCountAndClose(db, ARTIST));
        Assert.assertEquals(1, dbBackend.getCountAndClose(db, COVER));
        Assert.assertEquals(2, dbBackend.getCountAndClose(db, GENRES));
        Assert.assertEquals(2, dbBackend.getCountAndClose(db, ARTISTS_GENRES));
    }

    @Test
    public void clearAll() {

        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();
        Artist artist = createArtist();
        dbBackend.insertArtist(db, artist);

        Assert.assertEquals(1, dbBackend.getCountAndClose(db, ARTIST));
        Assert.assertEquals(1, dbBackend.getCountAndClose(db, COVER));
        Assert.assertEquals(2, dbBackend.getCountAndClose(db, GENRES));
        Assert.assertEquals(2, dbBackend.getCountAndClose(db, ARTISTS_GENRES));

        dbBackend.clearAll(db);

        Assert.assertEquals(0, dbBackend.getCountAndClose(db, ARTIST));
        Assert.assertEquals(0, dbBackend.getCountAndClose(db, COVER));
        Assert.assertEquals(0, dbBackend.getCountAndClose(db, GENRES));
        Assert.assertEquals(0, dbBackend.getCountAndClose(db, ARTISTS_GENRES));

    }

    @Test
    public void getArtistAndCoversIsEmpty() {

        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();

        Cursor artistAndCoverCursor = dbBackend.getArtistsAndCovers(db);
        Assert.assertEquals(0, artistAndCoverCursor.getCount());

        artistAndCoverCursor.close();
    }

    @Test
    public void getArtistAndCover() {
        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();
        Artist artist = createArtist();
        dbBackend.insertArtist(db, artist);

        Cursor cursor = dbBackend.getArtistsAndCovers(db);
        cursor.moveToFirst();

        Assert.assertEquals(1, cursor.getInt(cursor.getColumnIndex(ArtistTable.ID)));
        Assert.assertEquals("Test", cursor.getString(cursor.getColumnIndex(ArtistTable.NAME)));
        Assert.assertEquals("small_image", cursor.getString(cursor.getColumnIndex(CoverTable.SMALL)));

        cursor.close();
    }

    @Test
    public void getArtist() {
        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();
        Artist artist = createArtist();
        dbBackend.insertArtist(db, artist);

        Cursor cursor = dbBackend.getArtist(db, "1");
        cursor.moveToFirst();

        Assert.assertEquals(1, cursor.getInt(cursor.getColumnIndex(ArtistTable.ID)));
        Assert.assertEquals("Test", cursor.getString(cursor.getColumnIndex(ArtistTable.NAME)));
        Assert.assertEquals("small_image", cursor.getString(cursor.getColumnIndex(CoverTable.SMALL)));

        cursor.close();
    }

    @Test
    public void getArtistFromCursor() {
        ArtistsOpenHelper helper = new ArtistsOpenHelper(RuntimeEnvironment.application);
        SQLiteDatabase db = helper.getWritableDatabase();

        DbBackend dbBackend = new DbBackend();
        Artist artist = createArtist();
        dbBackend.insertArtist(db, artist);

        Cursor cursor = dbBackend.getArtist(db, "1");
        cursor.moveToFirst();

        Artist artistFromCursor = dbBackend.getArtistFromCursor(cursor);

        Assert.assertEquals(artist, artistFromCursor);

        cursor.close();
    }

    private Artist createArtist() {
        Artist artist = new Artist();
        artist.setId(1);
        artist.setName("Test");
        artist.setGenresString("pop,rock");
        artist.setDescription("testtest");
        artist.setAlbums(1);
        artist.setTracks(10);
        Cover cover = new Cover();
        cover.setSmall("small_image");
        cover.setBig("big_image");
        artist.setCover(cover);
        return artist;
    }
}
