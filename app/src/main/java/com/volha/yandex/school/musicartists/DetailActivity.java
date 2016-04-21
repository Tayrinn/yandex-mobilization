package com.volha.yandex.school.musicartists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.ActivityDetailsBinding;
import com.volha.yandex.school.musicartists.detail.ArtistDetailViewModel;
import com.volha.yandex.school.musicartists.detail.OnBrowserClickListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Volha on 17.04.2016.
 */
public class DetailActivity extends AppCompatActivity {

    public final static String TAG_ARTIST_ID = "tag_artist_id";

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        ActivityDetailsBinding binding = ActivityDetailsBinding.inflate( getLayoutInflater() );
        ArtistDetailViewModel model = new ArtistDetailViewModel();
        binding.setArtist( model );

        // get artistId from intent
        int artistId = getIntent().getIntExtra( TAG_ARTIST_ID, 0 );

        RealmConfiguration realmConfig = new RealmConfiguration.Builder( this ).build();
        Realm realm = Realm.getInstance( realmConfig );

        Artist artist = realm.where( Artist.class ).equalTo( "id", artistId ).findFirst();
        realm.close();

        model.setArtist( artist );
        model.setListener( onBrowserClickListener );

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

    OnBrowserClickListener onBrowserClickListener = new OnBrowserClickListener() {
        @Override
        public void onBrowseClick( String link ) {
            Intent intent = new Intent( Intent.ACTION_VIEW );
            intent.setData( Uri.parse(link) );
            startActivity( intent );
        }
    };


}
