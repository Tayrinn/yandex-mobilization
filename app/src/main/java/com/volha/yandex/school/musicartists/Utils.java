package com.volha.yandex.school.musicartists;

import android.content.Context;

/**
 * Created by Volha on 10.08.2016.
 */

public class Utils {

    public static String getSharedArtistName(Context context, int artistId) {
        return artistId + context.getString(R.string.album_cover_transition_name);
    }
}
