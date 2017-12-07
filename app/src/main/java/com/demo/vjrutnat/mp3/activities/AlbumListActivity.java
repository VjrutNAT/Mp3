package com.demo.vjrutnat.mp3.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.adapter.AlbumListAdapter;
import com.demo.vjrutnat.mp3.adapter.SongListAdapter;
import com.demo.vjrutnat.mp3.models.Song;
import com.demo.vjrutnat.mp3.utils.AppController;
import com.demo.vjrutnat.mp3.utils.Common;
import com.demo.vjrutnat.mp3.utils.Constants;

import java.io.File;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class AlbumListActivity extends AppCompatActivity {

    RecyclerView mRvSongList;
    ImageView mIvAlbumCover;
    ArrayList<Song> mListSong;
    SongListAdapter mAdapter;
    ImageView imgBackGround;
    TextView tvAlbumTitle;
    int mAlbumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_album_detail);
        setSupportActionBar(toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Common.setStatusBarTranslucent(true, this);
        initControls();
        Intent intent = getIntent();
        mAlbumId = intent.getExtras().getInt(AlbumListAdapter.ALBUM_KEY);
        if (!checkPermission()) {
            requestPermission();
        } else {
            getAndShowSongList();
        }
        showCover();
//        getAndShowSongList();
        AppController.getInstance().setDefaultWallpaper(imgBackGround);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAndShowSongList() {
        Intent intent = getIntent();
        mAlbumId = intent.getExtras().getInt(AlbumListAdapter.ALBUM_KEY);
        mListSong = AppController.getInstance().getListSongOfAlbum(mAlbumId);
        mAdapter = new SongListAdapter(this, mListSong);
        mRvSongList.setAdapter(mAdapter);
    }

    private void showCover() {

        String path = mListSong.get(0).getAlbumImagePath();
        if (path != null) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            mIvAlbumCover.setImageURI(uri);
        }
        tvAlbumTitle.setText(mListSong.get(0).getAlbum());
    }

    private void initControls() {
        mRvSongList = (RecyclerView) findViewById(R.id.rv_album_list_play);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRvSongList.setLayoutManager(layoutManager);
        imgBackGround = (ImageView) findViewById(R.id.img_back_ground_album);
        tvAlbumTitle = (TextView) findViewById(R.id.tv_album_title);
        mIvAlbumCover = (ImageView) findViewById(R.id.img_album_list_play);
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result1 == PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, Constants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted) {
                        getAndShowSongList();
                    } else {
                        Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(getString(R.string.ask_permission),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermission();
                                        }
                                    });
                        }
                    }
                }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(AlbumListActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btn_ok), onClickListener)
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .create()
                .show();
    }

}
