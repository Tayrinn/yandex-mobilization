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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.db.ArtistsOpenHelper;
import com.volha.yandex.school.musicartists.db.DbBackend;
import com.volha.yandex.school.musicartists.mainlist.ArtistsRecyclerAdapter;
import com.volha.yandex.school.musicartists.retrofit.ApiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.psdev.licensesdialog.LicensesDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private ArtistsRecyclerAdapter adapter;
//    private Realm realm;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private SQLiteDatabase db;
    private DbBackend dbBackend = new DbBackend();

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );

        setContentView( R.layout.activity_main );

        progressBar = ( ProgressBar ) findViewById( R.id.list_progress );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        db = new ArtistsOpenHelper(this).getWritableDatabase();
        db.enableWriteAheadLogging();

//        RealmConfiguration realmConfig = new RealmConfiguration.Builder( this ).build();
//        realm = Realm.getInstance( realmConfig );
//        RealmResults<Artist> artists = realm.where( Artist.class ).findAll();

        ImageLoader imageLoader = ImageLoader.getInstance();
        Cursor cursorArtists = dbBackend.getArtistsAndCovers(db);
        adapter = new ArtistsRecyclerAdapter( getArrayListFromCursor(cursorArtists), imageLoader, this );

        recyclerView = ( RecyclerView ) findViewById( R.id.recyclerView );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerView.setItemAnimator( new DefaultItemAnimator() );
        recyclerView.setAdapter( adapter );

        if ( cursorArtists.getCount() == 0 ) { // if there is no data in db, show progress
            recyclerView.setVisibility( View.GONE );
            progressBar.setVisibility( View.VISIBLE );
        }

        swipeRefreshLayout = ( SwipeRefreshLayout ) findViewById( R.id.swipeRefresh );
        swipeRefreshLayout.setColorSchemeColors( getIntColor( R.color.colorAccent ), getIntColor( R.color.colorPrimary ) );
        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadArtists();
            }
        } );
        cursorArtists.close();
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
                .cache()
                .timeout( 30, TimeUnit.SECONDS )
                .observeOn( AndroidSchedulers.mainThread() )
                .subscribeOn( Schedulers.newThread() )
                .subscribe( new Subscriber<List<Artist>>() {
                    @Override
                    public void onCompleted() {

                        stopProgress();
                    }

                    @Override
                    public void onError( Throwable e ) {
                        stopProgress();
                        Snackbar.make( recyclerView, R.string.no_data, Snackbar.LENGTH_INDEFINITE )
                                .setAction( R.string.reload, onReloadActionClick )
                                .setActionTextColor( ContextCompat.getColor( MainActivity.this, R.color.colorAccent ) )
                                .show();
                    }

                    @Override
                    public void onNext( List<Artist> artists ) {
                        db.beginTransaction();
                        dbBackend.clearAll(db);
                        for (Artist artist : artists) {
                            Log.d("insert", "id=" + dbBackend.insertArtist(db, artist));
                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
//                        realm.beginTransaction();
//                        realm.clear( Artist.class ); // delete all manually
//                        realm.clear( Cover.class ); // because realm don't have
//                        realm.clear( RealmString.class ); // cascade delete
//                        realm.copyToRealm( artists );
//                        realm.commitTransaction();
                        adapter.updateData( ( ArrayList<Artist> ) artists );
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
//        realm.close();
        compositeSubscription.unsubscribe();
        db.close();
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
        ArrayList<Artist> result = new ArrayList<>(cursor.getCount());
        if ( cursor.getCount() == 0 )
            return result;
        cursor.moveToFirst();
        do {
            result.add(dbBackend.getArtistFromCursor(cursor));
        } while (cursor.moveToNext());
        return result;
    }
}
