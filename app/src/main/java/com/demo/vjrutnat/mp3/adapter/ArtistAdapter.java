package com.demo.vjrutnat.mp3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.activities.ArtistListActivity;
import com.demo.vjrutnat.mp3.models.Album;
import com.demo.vjrutnat.mp3.models.Artist;
import com.demo.vjrutnat.mp3.models.Song;

import java.util.ArrayList;

/**
 * Created by IceMan on 11/12/2016.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolderArtist> {

    public static final String KEY_ARTIST = "key_artist";

    Context mContext;
    ArrayList<Artist> mData;
    ArrayList<Song> mSongs;
    LayoutInflater mLayoutInflater;

    public ArtistAdapter(Context mContext, ArrayList<Artist> mData, ArrayList<Song> mSongs) {
        this.mContext = mContext;
        this.mData = mData;
        this.mSongs = mSongs;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolderArtist onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_artist, null);
        ViewHolderArtist holder = new ViewHolderArtist(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderArtist holder, int position) {
        Artist artist = mData.get(position);
        holder.tvArtist.setText(artist.getName());
        holder.setId(position);
    }

    public void filter(ArrayList<Artist> lstArtist) {
        mData = new ArrayList<>();
        mData.addAll(lstArtist);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolderArtist extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvArtist;
        TextView tvNumberAlbum;
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ViewHolderArtist(View itemView) {
            super(itemView);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist_title_item);
            tvNumberAlbum = (TextView) itemView.findViewById(R.id.tv_number_album_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ArtistListActivity.class);
            intent.putExtra(KEY_ARTIST, mData.get(id).getId());
            mContext.startActivity(intent);
        }
    }
}
