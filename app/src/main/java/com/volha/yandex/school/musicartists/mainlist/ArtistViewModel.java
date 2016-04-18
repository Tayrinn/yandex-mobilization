package com.volha.yandex.school.musicartists.mainlist;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.view.View;

/**
 * Created by Volha on 17.04.2016.
 */
public class ArtistViewModel extends BaseObservable {

    public ObservableField<String> name   = new ObservableField<>();
    public ObservableField<String> genres = new ObservableField<>();

    public ObservableInt albums = new ObservableInt();
    public ObservableInt tracks = new ObservableInt();

    public ArtistViewHolder.OnItemClickListener listener;

    public void onItemClick(View view) {
        listener.onItemClick();
    }
}
