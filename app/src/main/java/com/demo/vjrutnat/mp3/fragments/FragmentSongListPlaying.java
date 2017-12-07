package com.demo.vjrutnat.mp3.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.adapter.SongListPlayingAdapter;
import com.demo.vjrutnat.mp3.helper.OnStartDragListener;
import com.demo.vjrutnat.mp3.helper.SimpleItemTouchHelperCallback;
import com.demo.vjrutnat.mp3.models.Song;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSongListPlaying#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSongListPlaying extends Fragment implements OnStartDragListener{

    public static final String KEY_SONG_LIST = "key_song_list";

    ArrayList<Song> mLstSongPlaying;
    RecyclerView mRvListSongPlaying;
    SongListPlayingAdapter mAdapter;
    View mView;
    private ItemTouchHelper mItemTouchHelper;

    public FragmentSongListPlaying() {
        // Required empty public constructor
    }

    public static FragmentSongListPlaying newInstance(ArrayList<Song> mData) {
        FragmentSongListPlaying fragment = new FragmentSongListPlaying();
        Bundle args = new Bundle();
        args.putSerializable(KEY_SONG_LIST, mData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mLstSongPlaying = (ArrayList<Song>) getArguments().getSerializable(KEY_SONG_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frament_song_list_playing, container, false);

        initControls();
        showListSongPlaying();
        return mView;
    }

    private void showListSongPlaying() {
        mAdapter = new SongListPlayingAdapter(getActivity(), mLstSongPlaying, this);
        mRvListSongPlaying.setAdapter(mAdapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRvListSongPlaying);
    }

    private void initControls() {
        mRvListSongPlaying = (RecyclerView) mView.findViewById(R.id.rv_song_list_playing);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRvListSongPlaying.setLayoutManager(layoutManager);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
