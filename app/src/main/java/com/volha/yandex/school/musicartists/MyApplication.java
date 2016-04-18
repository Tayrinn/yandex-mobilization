package com.volha.yandex.school.musicartists;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by Volha on 17.04.2016.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheSize( 20 * 1024 * 1024 ) // 20Mb
                .memoryCacheSize( 2 * 1024 * 1024 ) // 2Mb
                .threadPoolSize( 5 )
                .diskCacheFileCount( 100 )
                .build();
        ImageLoader.getInstance().init( config );
    }
}
