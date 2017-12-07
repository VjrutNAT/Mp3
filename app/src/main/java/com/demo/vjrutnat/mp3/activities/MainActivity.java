package com.demo.vjrutnat.mp3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.adapter.MainAdapter;
import com.demo.vjrutnat.mp3.models.ItemListMain;
import com.demo.vjrutnat.mp3.services.PlayMusicService;
import com.demo.vjrutnat.mp3.utils.AppController;
import com.demo.vjrutnat.mp3.utils.Common;
import com.demo.vjrutnat.mp3.utils.Constants;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView mRvListMain;
    ArrayList<ItemListMain> mListMain;
    MainAdapter mMainAdapter;
    LinearLayout currentPlayingBar;
    PlayMusicService musicService;
    ImageView btnPlayPauseCurrent;
    ImageView btnNextCurrent;
    ImageView imgAlbumArtCurrent;
    TextView tvTitle;
    TextView tvArtist;
    ImageView imgBackGround;
    boolean isLoading = true;
    LoadListMain loadListMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        AppController.getInstance().setMainActivity(this);
        initControls();
        initEvents();
        if (!checkPermission()) {
            requestPermission();
        } else {
            showListMain();
        }
        doMainWork();
        Common.setStatusBarTranslucent(true, this);
        registerBroadcastUpdatePlaying();
        initDefaultWallpaper();
    }

    private void initDefaultWallpaper() {
        AppController.getInstance().setDefaultWallpaper(imgBackGround);
    }

    private void doMainWork() {
        if (musicService != null) {
            updatePlayingState();
            showCurrentSong();
        }
    }

    private void showListMain() {
        loadListMain = new LoadListMain();
        loadListMain.execute();
    }

    private void initControls() {
        mRvListMain = (RecyclerView) findViewById(R.id.rv_list_main);
        currentPlayingBar = (LinearLayout) findViewById(R.id.current_playing_bar);
        btnPlayPauseCurrent = (ImageView) findViewById(R.id.btn_play_pause_current);
        btnNextCurrent = (ImageView) findViewById(R.id.btn_next_current);
        imgAlbumArtCurrent = (ImageView) findViewById(R.id.img_album_current_bar);
        tvTitle = (TextView) findViewById(R.id.tv_song_title_current);
        tvArtist = (TextView) findViewById(R.id.tv_artist_current);
        imgBackGround = (ImageView) findViewById(R.id.img_wallpaper_main);
        musicService = (PlayMusicService) AppController.getInstance().getPlayMusicService();

        if (musicService != null) {
            currentPlayingBar.setVisibility(View.VISIBLE);
        } else {
            currentPlayingBar.setVisibility(View.GONE);
        }
    }

    private void initEvents() {
        currentPlayingBar.setOnClickListener(this);
        btnPlayPauseCurrent.setOnClickListener(this);
        btnNextCurrent.setOnClickListener(this);
    }

    BroadcastReceiver broadcastReceiverUpdatePlaying = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            musicService = (PlayMusicService) AppController.getInstance().getPlayMusicService();
            if (musicService != null) {
                currentPlayingBar.setVisibility(View.VISIBLE);
            } else {
                currentPlayingBar.setVisibility(View.GONE);
            }
            showCurrentSong();
            if (musicService != null) {
                if (musicService.isPlaying()) {
                    btnPlayPauseCurrent.setImageResource(R.drawable.pb_pause);
                } else {
                    btnPlayPauseCurrent.setImageResource(R.drawable.pb_play);
                }
            }
        }
    };

    private void registerBroadcastUpdatePlaying() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_UPDATE_PlAY_STATUS);
        registerReceiver(broadcastReceiverUpdatePlaying, intentFilter);
    }

    private void unRegisterBroadcastUpdatePlaying() {
        unregisterReceiver(broadcastReceiverUpdatePlaying);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void updatePlayingState() {
        if (musicService.isPlaying()) {
            btnPlayPauseCurrent.setImageResource(R.drawable.pb_pause);
        } else {
            btnPlayPauseCurrent.setImageResource(R.drawable.pb_play);
        }
    }

    public void showCurrentSong() {
        if (musicService != null) {
            tvTitle.setText(musicService.getCurrentSong().getTitle());
            tvArtist.setText(musicService.getCurrentSong().getArtist());
            String albumPath = musicService.getCurrentSong().getAlbumImagePath();
            if (albumPath != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(albumPath);
                imgAlbumArtCurrent.setImageBitmap(bitmap);
            } else {
                imgAlbumArtCurrent.setImageResource(R.drawable.default_cover);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_playing_bar:
                if (musicService != null) {
                    Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
                    intent.putExtra(PlayMusicActivity.IS_PlAYING, true);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.no_change);
                }
                break;
            case R.id.btn_play_pause_current:
                if (musicService != null) {
                    Intent intent = new Intent(Constants.ACTION_PLAY_PAUSE);
                    if (musicService.isPlaying()) {
                        btnPlayPauseCurrent.setImageResource(R.drawable.pb_play);
                    } else {
                        btnPlayPauseCurrent.setImageResource(R.drawable.pb_pause);
                    }
                    sendBroadcast(intent);
                    showCurrentSong();
                }
                break;
            case R.id.btn_next_current:
                if (musicService != null) {
                    Intent intent = new Intent(Constants.ACTION_NEXT);
                    sendBroadcast(intent);
                    showCurrentSong();
                }
                break;
        }
    }

    public void updatePlayPauseButton() {
        if (musicService != null) {
            if (musicService.isPlaying()) {
                btnPlayPauseCurrent.setImageResource(R.drawable.pb_play);
            } else {
                btnPlayPauseCurrent.setImageResource(R.drawable.pb_pause);
            }
        }
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result1 == PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ READ_EXTERNAL_STORAGE}, Constants.PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted) {
                        showListMain();
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
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.btn_ok), onClickListener)
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppController.getInstance().setMainActivity(null);
        unRegisterBroadcastUpdatePlaying();
    }

    private class LoadListMain extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            isLoading = true;
            if (AppController.getInstance().getLstSong() == null && AppController.getInstance().getLstAlbum() == null
                    && AppController.getInstance().getLstArtist() == null) {
                getListMain();
            } else {
                getListMain();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            mMainAdapter = new MainAdapter(MainActivity.this, mListMain);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            mRvListMain.setLayoutManager(layoutManager);
            mRvListMain.setAdapter(mMainAdapter);
            isLoading = false;
        }
    }


    private void getListMain() {
        mListMain = new ArrayList<>();
        int numSongs = AppController.getInstance().getListSong().size();
        int numAlbums = AppController.getInstance().getListAlbum().size();
        int numArtists = AppController.getInstance().getListArtist().size();
        mListMain.add(new ItemListMain(R.drawable.ic_mm_song, getString(R.string.list_song), numSongs));
        mListMain.add(new ItemListMain(R.drawable.ic_album_white, getString(R.string.album_list), numAlbums));
        mListMain.add(new ItemListMain(R.drawable.ic_artist, getString(R.string.artist_list), numArtists));
    }
}
