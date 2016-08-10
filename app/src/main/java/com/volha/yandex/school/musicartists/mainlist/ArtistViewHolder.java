package com.volha.yandex.school.musicartists.mainlist;

import android.support.v7.widget.RecyclerView;

import com.volha.yandex.school.musicartists.databinding.ListItemArtistBinding;

/**
 * Created by Volha on 17.04.2016.
 */
public class ArtistViewHolder extends RecyclerView.ViewHolder {

    public ListItemArtistBinding binding;
    public ArtistViewModel viewModel = new ArtistViewModel();

    public ArtistViewHolder(ListItemArtistBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        this.binding.setArtist(viewModel);
    }
}
