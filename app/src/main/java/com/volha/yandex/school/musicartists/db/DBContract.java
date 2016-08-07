package com.volha.yandex.school.musicartists.db;

/**
 * Created by Volha on 07.08.2016.
 */

public interface DBContract {
    String DB_NAME = "main.sqlite";

    String ARTIST = "artist";
    interface ArtistTable {
        String ID = "_id";
        String NAME = "name";
        String GENRES = "genres";
        String TRACKS = "tracks";
        String ALBUMS = "albums";
        String LINK = "link";
        String DESCRIPTION = "description";
        String COVER_ID = "cover_id";
    }

    String COVER = "cover";
    interface CoverTable {
        String ID = "cover_id";
        String SMALL = "small";
        String BIG = "big";
    }
}
