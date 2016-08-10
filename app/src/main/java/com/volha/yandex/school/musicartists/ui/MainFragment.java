package com.volha.yandex.school.musicartists.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.volha.yandex.school.musicartists.R;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.data.Cover;
import com.volha.yandex.school.musicartists.data.RealmString;
import com.volha.yandex.school.musicartists.mainlist.ArtistsRecyclerAdapter;
import com.volha.yandex.school.musicartists.retrofit.ApiServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Volha on 14.07.2016.
 */
public class MainFragment extends Fragment {

    public final static String TAG = "main_fragment";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    private ArtistsRecyclerAdapter adapter;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Realm realm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        downloadArtists();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, null);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.list_progress);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
        realm = Realm.getInstance(realmConfig);
        RealmResults<Artist> artists = realm.where(Artist.class).findAll();

        ImageLoader imageLoader = ImageLoader.getInstance();

        adapter = new ArtistsRecyclerAdapter(getArrayListFromRealmResult(artists), imageLoader, this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if (artists.size() == 0) { // if there is no data in db, show progress
            recyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(getIntColor(R.color.colorAccent), getIntColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                downloadArtists();
            }
        });

        return rootView;
    }

    public int getIntColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }


    public void downloadArtists() {
        ApiServices apiServices = new ApiServices();
        compositeSubscription.add(
                apiServices
                        .getArtists()
                        .timeout(30, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())

                        .subscribe(new Subscriber<List<Artist>>() {
                            @Override
                            public void onCompleted() {

                                stopProgress();
                            }

                            @Override
                            public void onError(Throwable e) {
                                stopProgress();
                                Snackbar.make(recyclerView, R.string.no_data, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.reload, onReloadActionClick)
                                        .setActionTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent))
                                        .show();
                            }

                            @Override
                            public void onNext(List<Artist> artists) {
                                realm.beginTransaction();
                                realm.clear(Artist.class); // delete all manually
                                realm.clear(Cover.class); // because realm don't have
                                realm.clear(RealmString.class); // cascade delete
                                realm.copyToRealm(artists);
                                realm.commitTransaction();
                                Log.d("update", "data");
                                adapter.updateData((ArrayList<Artist>) artists);
                            }
                        })
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        compositeSubscription.unsubscribe();
    }

    public void startDetailFragment(View transitionElement, int artistId) {

        DetailFragment details = DetailFragment.newInstance(artistId);
        details.setSharedElementReturnTransition(new TransitionAnimation());
        details.setSharedElementEnterTransition(new TransitionAnimation());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.fade));
            details.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.slide_bottom));
        }

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(transitionElement, artistId + getString(R.string.album_cover_transition_name))
                .replace(R.id.contentPanel, details, DetailFragment.TAG)
                .addToBackStack(DetailFragment.TAG)
                .commit();
    }

    private View.OnClickListener onReloadActionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            swipeRefreshLayout.setRefreshing(true);
            downloadArtists();
        }
    };

    private void stopProgress() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private ArrayList<Artist> getArrayListFromRealmResult(RealmResults<Artist> artists) {
        ArrayList<Artist> result = new ArrayList<>(artists.size());
        for (Artist artist : artists) {
            result.add(artist);
        }
        return result;
    }
}
