package com.volha.yandex.school.musicartists.mainlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.volha.yandex.school.musicartists.DetailActivity;
import com.volha.yandex.school.musicartists.MainActivity;
import com.volha.yandex.school.musicartists.R;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.ListItemArtistBinding;

import java.util.ArrayList;

/**
 * Created by Volha on 17.04.2016.
 */
public class ArtistsRecyclerAdapter extends RecyclerView.Adapter<ArtistViewHolder> {

    private ArrayList<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;
    private ImageLoadingListener loadingListener;
    private Context context;

    public ArtistsRecyclerAdapter( ArrayList<Artist> artists, ImageLoader imageLoader, Context context ) {
        this.artists = artists;
        this.imageLoader = imageLoader;
        this.context = context;
        this.imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk( true )
                .cacheInMemory( true )
                // TODO : add stub image
                .build();
        this.loadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted( String imageUri, View view ) {
                view.setVisibility( View.GONE );
            }

            @Override
            public void onLoadingFailed( String imageUri, View view, FailReason failReason ) {
                view.setVisibility( View.VISIBLE );
            }

            @Override
            public void onLoadingComplete( String imageUri, View view, Bitmap loadedImage ) {
                view.setVisibility( View.VISIBLE );
                ( (ImageView) view).setImageBitmap( loadedImage );
            }

            @Override
            public void onLoadingCancelled( String imageUri, View view ) {
                view.setVisibility( View.VISIBLE );
            }
        };
    }

    @Override
    public ArtistViewHolder onCreateViewHolder( ViewGroup parent, int viewType ) {

        LayoutInflater inflater = LayoutInflater.from( parent.getContext() );

        return new ArtistViewHolder( ListItemArtistBinding.inflate( inflater, parent, false ) );
    }

    @Override
    public void onBindViewHolder( final ArtistViewHolder holder, int position ) {

        final Artist artist = artists.get( position );
        holder.viewModel.albums.set( artist.getAlbums() );
        holder.viewModel.tracks.set( artist.getTracks() );
        holder.viewModel.name.set( artist.getName() );
        holder.viewModel.genres.set( artist.getGenresString() );
        holder.viewModel.listener = new ArtistViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick() {
                Intent intent = new Intent( context, DetailActivity.class );
                // convert artist in json, to send to DetailActivity
                ObjectMapper mapper = new ObjectMapper();
                String jsonArtist = null;
                try {
                    jsonArtist = mapper.writeValueAsString( artist );
                } catch ( JsonProcessingException e ) {
                    e.printStackTrace();
                }
                intent.putExtra( DetailActivity.TAG_ARTIST, jsonArtist );
                // transition animations
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation( (MainActivity) context, ( View ) holder.binding.albumCover, context.getString( R.string.album_cover_transition_name ) );
                context.startActivity( intent, options.toBundle() );
            }
        };
        imageLoader.displayImage( artist.getCover().getSmall(), holder.binding.albumCover, imageOptions, loadingListener );
    }

    @Override
    public int getItemCount() {

        return artists.size();
    }

    public void updateData( ArrayList<Artist> artists ) {

        this.artists.clear();
        this.artists.addAll( artists );
        notifyDataSetChanged();
    }
}
