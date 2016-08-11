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
import com.volha.yandex.school.musicartists.DataProvider;
import com.volha.yandex.school.musicartists.MyApplication;
import com.volha.yandex.school.musicartists.R;
import com.volha.yandex.school.musicartists.Utils;
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
import rx.Observable;
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
    private CompositeSubscription compositeSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, null);
    }

    @Override
    public void onViewCreated(View rootView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        progressBar = (ProgressBar) rootView.findViewById(R.id.list_progress);
        compositeSubscription = new CompositeSubscription();

        ImageLoader imageLoader = MyApplication.from(getContext()).getImageLoader();

        adapter = new ArtistsRecyclerAdapter(imageLoader, this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(getIntColor(R.color.colorAccent), getIntColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(() -> downloadArtists());
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadArtists();
    }

    public int getIntColor(int color) {
        return ContextCompat.getColor(getContext(), color);
    }


    public void downloadArtists() {
        DataProvider dataProvider = new DataProvider();
        compositeSubscription.add(
                dataProvider.getArtists(getContext())
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(2)
                        .doOnCompleted(this::stopProgress)
                        .retryWhen(this::showReloadSnackbar)
                        .doOnNext(artists -> {
                            adapter.updateData(artists);
                            stopProgress();
                        })
                        .subscribe()
        );
    }

    private Observable<Object> showReloadSnackbar(Observable<? extends Throwable> errors) {
        return errors.flatMap( error ->
                Observable.create(
                        subscriber -> Snackbar.make( recyclerView, R.string.no_data, Snackbar.LENGTH_INDEFINITE )
                                .setAction( R.string.reload, v -> { subscriber.onNext(null); subscriber.onCompleted(); })
                                .setActionTextColor( ContextCompat.getColor( getContext(), R.color.colorAccent ) )
                                .show()));
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
            TransitionInflater inflater = TransitionInflater.from(getContext());
            setExitTransition(inflater.inflateTransition(android.R.transition.fade));
            setEnterTransition(inflater.inflateTransition(android.R.transition.fade));
            details.setEnterTransition(inflater.inflateTransition(android.R.transition.slide_bottom));
        }

        getFragmentManager()
                .beginTransaction()
                .addSharedElement(transitionElement, Utils.getSharedArtistName(getContext(), artistId))
                .replace(R.id.contentPanel, details, DetailFragment.TAG)
                .addToBackStack(DetailFragment.TAG)
                .commit();
    }

    private void stopProgress() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
