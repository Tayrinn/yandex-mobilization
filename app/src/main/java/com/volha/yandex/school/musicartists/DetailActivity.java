package com.volha.yandex.school.musicartists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.ActivityDetailsBinding;
import com.volha.yandex.school.musicartists.detail.ArtistDetailViewModel;

import java.io.IOException;

/**
 * Created by Volha on 17.04.2016.
 */
public class DetailActivity extends AppCompatActivity {

    public final static String TAG_ARTIST = "tag_artist";

    private Artist artist;

    //todo: add shape in bottom image

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        ActivityDetailsBinding binding = ActivityDetailsBinding.inflate( getLayoutInflater() );
        ArtistDetailViewModel model = new ArtistDetailViewModel();
        binding.setArtist( model );

        // get artist from intent, convert from json
        ObjectMapper mapper = new ObjectMapper();
        String jsonArtist = getIntent().getStringExtra( TAG_ARTIST );
        try {
            artist = mapper.readValue( jsonArtist, Artist.class );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        model.setArtist( artist );
        model.listener = onBrowseClickListener;

        setContentView( binding.getRoot() );

        Toolbar toolbar = ( Toolbar ) findViewById( R.id.details_toolbar );
        toolbar.setTitle( artist.getName() );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(
                artist.getCover().getBig(),
                binding.background,
                new DisplayImageOptions.Builder().cacheOnDisk( true ).build()
        );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        if ( item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected( item );
    }

    OnBrowseClickListener onBrowseClickListener = new OnBrowseClickListener() {
        @Override
        public void onBrowseClick( String link ) {
            Intent intent = new Intent( Intent.ACTION_VIEW );
            intent.setData( Uri.parse(link) );
            startActivity( intent );
        }
    };

    public interface OnBrowseClickListener {
        void onBrowseClick(String link);
    }
}
