package com.demo.vjrutnat.mp3.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.fragments.FragmentPlay;
import com.demo.vjrutnat.mp3.helper.ItemTouchHelperViewHolder;
import com.demo.vjrutnat.mp3.helper.OnStartDragListener;
import com.demo.vjrutnat.mp3.models.Song;
import com.demo.vjrutnat.mp3.utils.Constants;
import com.demo.vjrutnat.mp3.helper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IceMan on 11/20/2016.
 */

public class SongListPlayingAdapter extends RecyclerView.Adapter<SongListPlayingAdapter.ViewHolderSongPlaying> implements ItemTouchHelperAdapter{

    public static final String KEY_ID_SWITH = "key_id_switch";
    private final OnStartDragListener mOnStartDragListener;
    Context mContext;
    ArrayList<Song> mData;
    LayoutInflater mLayoutInflater;

    public SongListPlayingAdapter(Context mContext, ArrayList<Song> mData, OnStartDragListener mOnStartDragListener) {
        this.mContext = mContext;
        this.mData = mData;
        this.mOnStartDragListener = mOnStartDragListener;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolderSongPlaying onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_song_playing, null);
        ViewHolderSongPlaying holder = new ViewHolderSongPlaying(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolderSongPlaying holder, int position) {
        Song item = mData.get(position);
        holder.setId(position);
        String path =  mData.get(position).getAlbumImagePath();
        if(path != null) {
            Glide.with(mContext).load(path).into(holder.imgAlbum);
        }else{
            holder.imgAlbum.setImageResource(R.drawable.default_cover_big);
        }
        holder.tvTitle.setText(item.getTitle());
        holder.tvArtist.setText(item.getArtist());
        holder.rlHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mOnStartDragListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mData, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolderSongPlaying extends RecyclerView.ViewHolder implements View.OnClickListener, ItemTouchHelperViewHolder {
        int id;
        private RelativeLayout rlHandle;
        private ImageView imgAlbum;
        private TextView tvTitle;
        private TextView tvArtist;


        public ViewHolderSongPlaying(View itemView) {
            super(itemView);
            rlHandle = (RelativeLayout) itemView.findViewById(R.id.rl_info_song_play);
            imgAlbum = (ImageView) itemView.findViewById(R.id.img_album_song_play);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_song_name_play);
            tvArtist = (TextView) itemView.findViewById(R.id.tv_artist_song_play);
            itemView.setOnClickListener(this);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Constants.ACTION_SWITCH_SONG);
            intent.putExtra(KEY_ID_SWITH, id);
            mContext.sendBroadcast(intent);

            Intent intent1 = new Intent(Constants.ACTION_CHANGE_ALBUM_ART);
            intent1.putExtra(FragmentPlay.KEY_ALBUM_PLAY,mData.get(id).getAlbumImagePath());
            mContext.sendBroadcast(intent1);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}
