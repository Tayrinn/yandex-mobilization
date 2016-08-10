package com.volha.yandex.school.musicartists;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.ActivityDetailsBinding;
import com.volha.yandex.school.musicartists.db.ArtistsOpenHelper;
import com.volha.yandex.school.musicartists.db.DBContentProvider;
import com.volha.yandex.school.musicartists.db.DBUtils;
import com.volha.yandex.school.musicartists.db.DbBackend;
import com.volha.yandex.school.musicartists.detail.ArtistDetailViewModel;
import com.volha.yandex.school.musicartists.detail.OnBrowserClickListener;


import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Volha on 17.04.2016.
 */
public class DetailActivity extends AppCompatActivity {

    public final static String TAG_ARTIST_ID = "tag_artist_id";
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityDetailsBinding binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        final ArtistDetailViewModel model = new ArtistDetailViewModel();
        binding.setArtist(model);

        setContentView(binding.getRoot());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);

        // get artistId from intent
        final int artistId = getIntent().getIntExtra(TAG_ARTIST_ID, 0);

        final DbBackend dbBackend = new DbBackend();
        compositeSubscription.add(Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                Cursor artistCursor = getContentResolver().query(
                        Uri.parse("content://"
                                + DBContentProvider.AUTHORITY
                                + "/artist/"
                                + artistId),
                        null, null, null, null
                );
                if (artistCursor == null) {
                    subscriber.onError(new NullPointerException());
                } else {
                    subscriber.onNext(artistCursor);
                    subscriber.onCompleted();
                }
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(DetailActivity.this, "Server not available, try again later", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Cursor cursor) {
                        cursor.moveToFirst();
                        Artist artist = DBUtils.getArtistFromCursor(cursor);
                        model.setArtist(artist);
                        model.setListener(onBrowserClickListener);
                        toolbar.setTitle(artist.getName());
                        setSupportActionBar(toolbar);
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.displayImage(
                                artist.getCover().getBig(),
                                binding.background,
                                new DisplayImageOptions.Builder().cacheOnDisk(true).build()
                        );
                        cursor.close();
                    }
                }));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    OnBrowserClickListener onBrowserClickListener = new OnBrowserClickListener() {
        @Override
        public void onBrowseClick(String link) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        }
    };


}
