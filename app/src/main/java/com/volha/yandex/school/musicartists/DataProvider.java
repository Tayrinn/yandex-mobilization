package com.volha.yandex.school.musicartists;

import android.content.Context;

import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;
import com.volha.yandex.school.musicartists.data.RealmString;
import com.volha.yandex.school.musicartists.retrofit.ApiServices;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Volha on 11.08.2016.
 */

public class DataProvider {

    public Observable<List<Artist>> getArtists(final Context context) {
        ApiServices apiServices = new ApiServices();
        final Realm realm = Realm.getInstance(MyApplication.from(context).getRealmConfig());
        Observable<List<Artist>> fromInternet = apiServices
                .getArtists()
                .subscribeOn(Schedulers.io())
                .doOnNext(artists -> {
                    realm.beginTransaction();
                    realm.clear(Artist.class); // delete all manually
                    realm.clear(Cover.class); // because realm don't have
                    realm.clear(RealmString.class); // cascade delete
                    realm.copyToRealm(artists);
                    realm.commitTransaction();
                });
        Observable<List<Artist>> fromDB = realm
                .where(Artist.class)
                        .findAll()
                        .asObservable()
                .map(this::getArrayListFromRealmResult);
        return Observable.merge(fromDB, fromInternet);
    }

    public Observable<Artist> getArtist(Context context, int artistId) {
        Realm realm = Realm.getInstance(MyApplication.from(context).getRealmConfig());
        return realm.where(Artist.class).equalTo("id", artistId).findFirst().asObservable();
    }

    private List<Artist> getArrayListFromRealmResult(RealmResults<Artist> artists) {
        List<Artist> result = new ArrayList<>(artists.size());
        for (Artist artist : artists) {
            result.add(artist);
        }
        return result;
    }


}
