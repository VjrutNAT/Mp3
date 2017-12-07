package com.demo.vjrutnat.mp3.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.vjrutnat.mp3.R;
import com.demo.vjrutnat.mp3.adapter.MainAdapter;
import com.demo.vjrutnat.mp3.adapter.ViewPagerDetailAdapter;
import com.demo.vjrutnat.mp3.services.PlayMusicService;
import com.demo.vjrutnat.mp3.utils.AppController;
import com.demo.vjrutnat.mp3.utils.Common;
import com.demo.vjrutnat.mp3.utils.Constants;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager mViewPager;
    ViewPagerDetailAdapter mVPAdapter;
    ImageView imgBackGround;
    TabLayout tabLayoutDetail;
    String idType;
    ImageView btnPlayPauseCurrent;
    ImageView btnNextCurrent;
    ImageView imgAlbumArtCurrent;
    TextView tvTitle;
    TextView tvArtist;
    PlayMusicService musicService;
    LinearLayout currentPlayingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initControls();
        Common.setStatusBarTranslucent(true,this);
        initEvents();
        getType();
        if (!checkPermission()) {
            requestPermission();
        } else {
            doMainWork();
        }

        Common.setStatusBarTranslucent(true, this);
        registerBroadcastUpdatePlaying();
        AppController.getInstance().setDefaultWallpaper(imgBackGround);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search_detail, menu);
        return super.onCreateOptionsMenu(menu);
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

    private void getType() {
        Intent intent = getIntent();
        idType = intent.getStringExtra(MainAdapter.KEY_MAIN);

        if (idType.equals(getString(R.string.list_song))) {
            mViewPager.setCurrentItem(0);
        } else if (idType.equals(getString(R.string.album_list))) {
            mViewPager.setCurrentItem(1);
        } else if (idType.equals(getString(R.string.artist_list))) {
            mViewPager.setCurrentItem(2);
        }
    }

    private void initEvents() {
        currentPlayingBar.setOnClickListener(this);
        btnPlayPauseCurrent.setOnClickListener(this);
        btnNextCurrent.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void doMainWork() {
        if (musicService != null) {
            updatePlayingState();
            showCurrentSong();
        }
    }

    private void initControls() {
        imgBackGround = (ImageView) findViewById(R.id.img_back_ground_detail);
        tabLayoutDetail = (TabLayout) findViewById(R.id.tablayout_detail);
        mViewPager = (ViewPager) findViewById(R.id.view_pager_detail);
        tabLayoutDetail.setupWithViewPager(mViewPager);
        mVPAdapter = new ViewPagerDetailAdapter(getSupportFragmentManager(), this);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mVPAdapter);
        currentPlayingBar = (LinearLayout) findViewById(R.id.current_playing_bar);
        btnPlayPauseCurrent = (ImageView) findViewById(R.id.btn_play_pause_current);
        btnNextCurrent = (ImageView) findViewById(R.id.btn_next_current);
        imgAlbumArtCurrent = (ImageView) findViewById(R.id.img_album_current_bar);
        tvTitle = (TextView) findViewById(R.id.tv_song_title_current);
        tvArtist = (TextView) findViewById(R.id.tv_artist_current);
        musicService = (PlayMusicService) AppController.getInstance().getPlayMusicService();

        if (musicService != null) {
            currentPlayingBar.setVisibility(View.VISIBLE);
        } else {
            currentPlayingBar.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.current_playing_bar:
                if (musicService != null) {
                    Intent intent = new Intent(DetailActivity.this, PlayMusicActivity.class);
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
                        doMainWork();
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
        new AlertDialog.Builder(DetailActivity.this)
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
}
