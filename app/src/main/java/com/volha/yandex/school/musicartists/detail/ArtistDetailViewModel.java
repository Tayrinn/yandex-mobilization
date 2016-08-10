package com.volha.yandex.school.musicartists.detail;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

import com.volha.yandex.school.musicartists.data.Artist;

/**
 * Created by Volha on 17.04.2016.
 */
public class ArtistDetailViewModel extends BaseObservable {

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> genres = new ObservableField<>();
    public ObservableField<String> link = new ObservableField<>();
    public ObservableField<String> description = new ObservableField<>();

    public ObservableInt albums = new ObservableInt();
    public ObservableInt tracks = new ObservableInt();

    private OnBrowserClickListener listener;

    public void setArtist(Artist artist) {

        name.set(artist.getName());
        genres.set(artist.getGenresString());
        link.set(artist.getLink());
        description.set(artist.getDescription());
        albums.set(artist.getAlbums());
        tracks.set(artist.getTracks());
    }

    public void onBrowseClick(View view) {
        listener.onBrowseClick(link.get());
    }

    public void setListener(OnBrowserClickListener listener) {
        this.listener = listener;
    }
}
