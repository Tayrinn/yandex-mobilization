package com.volha.yandex.school.musicartists;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.mainlist.ArtistsRecyclerAdapter;
import com.volha.yandex.school.musicartists.retrofit.ApiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArtistsRecyclerAdapter adapter;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        ImageLoader imageLoader = ImageLoader.getInstance();
        adapter = new ArtistsRecyclerAdapter( new ArrayList<Artist>(), imageLoader, this );
        recyclerView = ( RecyclerView ) findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( adapter );
        // todo: add color scheme
        swipeRefreshLayout = ( SwipeRefreshLayout ) findViewById( R.id.swipeRefresh );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadArtists();
            }
        } );

        downloadArtists();
    }

    private void downloadArtists() {

        ApiServices apiServices = new ApiServices();

        apiServices
                .getArtists()
                .timeout( 30, TimeUnit.SECONDS )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribeOn( Schedulers.newThread() )
                .subscribe( new Subscriber<List<Artist>>() {
                    @Override
                    public void onCompleted() {

                        swipeRefreshLayout.setRefreshing( false );
                    }

                    @Override
                    public void onError( Throwable e ) {
                        // TODO : errors handle
                    }

                    @Override
                    public void onNext( List<Artist> artists ) {

                        adapter.updateData( ( ArrayList<Artist> ) artists );
                    }
                } );

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}
