package com.volha.yandex.school.musicartists.mainlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.volha.yandex.school.musicartists.R;
import com.volha.yandex.school.musicartists.data.Artist;
import com.volha.yandex.school.musicartists.databinding.ListItemArtistBinding;
import com.volha.yandex.school.musicartists.ui.MainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Volha on 17.04.2016.
 */
public class ArtistsRecyclerAdapter extends RecyclerView.Adapter<ArtistViewHolder> {

    private List<Artist> artists;
    private ImageLoader imageLoader;
    private DisplayImageOptions imageOptions;
    private ImageLoadingListener loadingListener;
    private MainFragment fragment;

    public ArtistsRecyclerAdapter(List<Artist> artists, ImageLoader imageLoader, MainFragment fragment) {
        this.artists = artists;
        this.imageLoader = imageLoader;
        this.fragment = fragment;
        setHasStableIds(true);
        this.imageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.musician)
                .showImageOnFail(R.drawable.musician)
                .build();
        this.loadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                view.setVisibility(View.VISIBLE);
                ((ImageView) view).setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                view.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        final ArtistViewHolder holder = new ArtistViewHolder(ListItemArtistBinding.inflate(inflater, parent, false));
        holder.viewModel.setListener(new OnArtistListItemClickListener() {
            @Override
            public void onItemClick() {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Artist artist = artists.get(position);
                    fragment.startDetailFragment(holder.binding.albumCover, artist.getId());
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ArtistViewHolder holder, int position) {
        final Artist artist = artists.get(position);
        holder.viewModel.albums.set(artist.getAlbums());
        holder.viewModel.tracks.set(artist.getTracks());
        holder.viewModel.name.set(artist.getName());
        holder.viewModel.genres.set(artist.getGenresString());
        imageLoader.displayImage(
                artist.getCover().getSmall(),
                holder.binding.albumCover,
                imageOptions,
                loadingListener
        );
        ViewCompat.setTransitionName(holder.binding.albumCover, artist.getId() + fragment.getString(R.string.album_cover_transition_name));
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public long getItemId(int position) {
        return artists.get(position).getId();
    }

    public void updateData(List<Artist> artists) {
        this.artists.clear();
        this.artists.addAll(artists);
        notifyDataSetChanged();
    }
}
