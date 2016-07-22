package com.volha.yandex.school.musicartists.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.FragmentDetailsBinding;
import com.volha.yandex.school.musicartists.detail.ArtistDetailViewModel;
import com.volha.yandex.school.musicartists.detail.OnBrowserClickListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Volha on 15.07.2016.
 */
public class DetailFragment extends Fragment {

    public final static String TAG = "detail_fragment";
    public final static String TAG_ARTIST_ID = "tag_artist_id";

    public static DetailFragment newInstance( int artistId ) {
        DetailFragment fragment = new DetailFragment();
        Bundle argument = new Bundle();
        argument.putInt( TAG_ARTIST_ID, artistId );
        fragment.setArguments( argument );
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView( LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState ) {
        FragmentDetailsBinding binding = FragmentDetailsBinding.inflate( inflater );
        ArtistDetailViewModel model = new ArtistDetailViewModel();
        binding.setArtist( model );

        int artistId = getArguments().getInt( TAG_ARTIST_ID );

        RealmConfiguration realmConfig = new RealmConfiguration.Builder( getContext() ).build();
        Realm realm = Realm.getInstance( realmConfig );

        Artist artist = realm.where( Artist.class ).equalTo( "id", artistId ).findFirst();
        realm.close();

        model.setArtist( artist );
        model.setListener( onBrowserClickListener );

        Toolbar toolbar = binding.detailsToolbar;
        toolbar.setTitle( artist.getName() );
        ((MainActivity ) getActivity()).setSupportActionBar( toolbar );
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(
                artist.getCover().getBig(),
                binding.background,
                new DisplayImageOptions.Builder().cacheOnDisk( true ).build()
        );

        setRetainInstance( true );
        return binding.getRoot();
    }

    OnBrowserClickListener onBrowserClickListener = new OnBrowserClickListener() {
        @Override
        public void onBrowseClick( String link ) {
            Intent intent = new Intent( Intent.ACTION_VIEW );
            intent.setData( Uri.parse(link) );
            startActivity( intent );
        }
    };

}
