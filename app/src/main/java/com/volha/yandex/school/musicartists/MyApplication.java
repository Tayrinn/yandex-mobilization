package com.volha.yandex.school.musicartists;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.realm.RealmConfiguration;

/**
 * Created by Volha on 17.04.2016.
 */
public class MyApplication extends Application {

    public static MyApplication from(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    private final DisplayImageOptions imageOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    private ImageLoader imageLoader;
    private RealmConfiguration realmConfig;

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
        imageLoader = ImageLoader.getInstance();
        realmConfig = new RealmConfiguration.Builder(this).build();
    }

    public DisplayImageOptions getImageOptions() {
        return imageOptions;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public RealmConfiguration getRealmConfig() {
        return realmConfig;
    }
}
