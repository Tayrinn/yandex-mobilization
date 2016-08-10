package com.volha.yandex.school.musicartists;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.db.ArtistsOpenHelper;
import com.volha.yandex.school.musicartists.db.DBContentProvider;
import com.volha.yandex.school.musicartists.db.DBUtils;
import com.volha.yandex.school.musicartists.db.DbBackend;
import com.volha.yandex.school.musicartists.mainlist.ArtistsRecyclerAdapter;
import com.volha.yandex.school.musicartists.retrofit.ApiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.psdev.licensesdialog.LicensesDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private ArtistsRecyclerAdapter adapter;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );
        progressBar = ( ProgressBar ) findViewById( R.id.list_progress );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        final ImageLoader imageLoader = ImageLoader.getInstance();
        compositeSubscription.add(
                Observable.create(new Observable.OnSubscribe<Cursor>() {

                    @Override
                    public void call(Subscriber<? super Cursor> subscriber) {
                        Cursor cursor = getContentResolver().query(
                                DBContentProvider.ARTIST_CONTENT_URI, null, null, null, null);
                        subscriber.onNext(cursor);
                        subscriber.onCompleted();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Cursor cursor) {
                        adapter = new ArtistsRecyclerAdapter(
                                getArrayListFromCursor(cursor), imageLoader, MainActivity.this );
                        recyclerView.setAdapter( adapter );
                        if ( cursor.getCount() == 0 ) { // if there is no data in db, show progress
                            recyclerView.setVisibility( View.GONE );
                            progressBar.setVisibility( View.VISIBLE );
                        }
                        cursor.close();
                    }
        }));

        recyclerView = ( RecyclerView ) findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );

        swipeRefreshLayout = ( SwipeRefreshLayout ) findViewById( R.id.swipeRefresh );
        swipeRefreshLayout.setColorSchemeColors( getIntColor( R.color.colorAccent ), getIntColor( R.color.colorPrimary ) );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadArtists();
            }
        } );
        downloadArtists();
    }

    public int getIntColor( int color ) {
        return ContextCompat.getColor( this, color );
    }

    private void downloadArtists() {

        ApiServices apiServices = new ApiServices();
        compositeSubscription.add(
            apiServices
                .getArtists()
                .map(new Func1<List<Artist>, List<Artist>>() {
                    @Override
                    public List<Artist> call(List<Artist> artists) {
                        getContentResolver().delete(DBContentProvider.ARTIST_CONTENT_URI, null, null);
                        for (Artist artist : artists) {
                            getContentResolver().insert(
                                    DBContentProvider.ARTIST_CONTENT_URI,
                                    DBUtils.getValuesFromArtist(artist));
                        }
                        return artists;
                    }
                })
                .subscribeOn( Schedulers.computation() )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribe( new Subscriber<List<Artist>>() {
                    @Override
                    public void onCompleted() {
                        stopProgress();
                    }

                    @Override
                    public void onError( Throwable e ) {
                        stopProgress();
                        e.printStackTrace();
                        Snackbar.make( recyclerView, R.string.no_data, Snackbar.LENGTH_INDEFINITE )
                                .setAction( R.string.reload, onReloadActionClick )
                                .setActionTextColor( ContextCompat.getColor( MainActivity.this, R.color.colorAccent ) )
                                .show();
                    }

                    @Override
                    public void onNext( List<Artist> artists ) {
                        adapter.updateData( artists );
                    }
                } )
        );
    }

    private View.OnClickListener onReloadActionClick = new View.OnClickListener() {
        @Override
        public void onClick( View v ) {
            swipeRefreshLayout.setRefreshing( true );
            downloadArtists();
        }
    };

    private void stopProgress() {
        swipeRefreshLayout.setRefreshing( false );
        progressBar.setVisibility( View.GONE );
        recyclerView.setVisibility( View.VISIBLE );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.unsubscribe();
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        int id = item.getItemId();
        if ( id == R.id.action_notices ) {
            new LicensesDialog.Builder(this)
                    .setNotices(R.raw.notices)
                    .build()
                    .show();
            return true;
        }

        return super.onOptionsItemSelected( item );
    }


    private ArrayList<Artist> getArrayListFromCursor(Cursor cursor) {
        if ( cursor == null || cursor.getCount() == 0 ) {
            return new ArrayList<>();
        }
        ArrayList<Artist> result = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        do {
            result.add(DBUtils.getArtistFromCursor(cursor));
        } while (cursor.moveToNext());
        return result;
    }
}
