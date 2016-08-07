package com.volha.yandex.school.musicartists.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Volha on 07.08.2016.
 */

public class DBContentProvider extends ContentProvider implements DBContract{

    static final String AUTHORITY = "com.volha.yandex.school.musicartists.db.Artist";

    static final String ARTIST_PATH = "artist";

    public static final Uri ARTIST_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ARTIST_PATH);
    static final String ARTIST_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
            + AUTHORITY + "." + ARTIST_PATH;

    static final String ARTIST_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "." + ARTIST_PATH;

    static final int URI_ARTISTS = 1;
    static final int URI_ARTISTS_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, ARTIST_PATH, URI_ARTISTS);
        uriMatcher.addURI(AUTHORITY, ARTIST_PATH + "/#", URI_ARTISTS_ID);
    }


    ArtistsOpenHelper dbHelper;
    SQLiteDatabase db;
    DbBackend dbBackend;

    @Override
    public boolean onCreate() {
        dbHelper = new ArtistsOpenHelper(getContext());
        dbBackend = new DbBackend();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case URI_ARTISTS:
                break;
            case URI_ARTISTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ArtistTable.ID + " = " + id;
                } else {
                    selection = selection + " AND " + ArtistTable.ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(ARTIST, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), ARTIST_CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ARTISTS:
                return ARTIST_CONTENT_TYPE;
            case URI_ARTISTS_ID:
                return ARTIST_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) != URI_ARTISTS)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(ARTIST, null, values);
        Uri resultUri = ContentUris.withAppendedId(ARTIST_CONTENT_URI, rowID);
        // уведомляем ContentResolver, что данные по адресу resultUri изменились
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_ARTISTS:
                break;
            case URI_ARTISTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ArtistTable.ID + " = " + id;
                } else {
                    selection = selection + " AND " + ArtistTable.ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(ARTIST, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case URI_ARTISTS:
                break;
            case URI_ARTISTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = ArtistTable.ID + " = " + id;
                } else {
                    selection = selection + " AND " + ArtistTable.ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(ARTIST, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
