package com.example.phas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.visualizer.amplitude.AudioRecordView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    //------------------------------------------레이아웃 관련 부분---------------------------------------
    Toolbar toolBar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private final static String TAG = "MainActivity";

    //------------------------------------------소리 파형 변수---------------------------------------
    private Timer timer = null;
    private TimerTask timerTask = null;
    private AudioRecordView audioRecordView;
    //------------------------------------------소리 파형 변수---------------------------------------


    //------------------------------------------소리 녹음 및 재생 변수--------------------------------
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;

    String mPath = null;
    String mPlayPath = null;

    boolean isRecording = false;
    boolean isPlaying = false;
    //------------------------------------------소리 녹음 및 재생 변수--------------------------------

    Button mBtRecord = null;
    Button mBtPlay = null;
    Button mBtHistory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon); //뒤로가기 버튼 이미지 지정

        drawerLayout = findViewById(R.id.drawer_layout_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.nav_first) {
                    Toast.makeText(MainActivity.this, "첫번째", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_second) {
                    Toast.makeText(MainActivity.this, "두번째", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        audioRecordView = findViewById(R.id.audioRecordView);

        //어플리케이션에 권한여부
        int permissionStorage = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionMIC = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);
        if (permissionStorage != PackageManager.PERMISSION_GRANTED &&
                permissionMIC != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS_STORAGE = {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
            };
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_STORAGE, 3);
        }

        mBtRecord = (Button) findViewById(R.id.record);
        mBtRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    initAudioRecorder();
                    mRecorder.start();
                    startDrawing();
                    isRecording = true;
                    mBtRecord.setText("Stop Recording");
                } else {
                    mRecorder.stop();
                    mRecorder.reset();
                    stopDrawing();
                    isRecording = false;
                    mBtRecord.setText("Start Recording");
                }
            }
        });

        mBtPlay = (Button) findViewById(R.id.play);
        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    try {
                        mPlayer.setDataSource(mPlayPath);
                        mPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();

                    isPlaying = true;
                    mBtPlay.setText("Stop Playing");
                } else {
                    mPlayer.stop();

                    isPlaying = false;
                    mBtPlay.setText("Start Playing");
                }
            }
        });

        mRecorder = new MediaRecorder();
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion");
                isPlaying = false;
                mBtPlay.setText("Start Playing");
            }
        });

        mBtHistory = (Button) findViewById(R.id.history);
        mBtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, History.class);
                //startActivity(intent);
            }
        });
    }

    //--------------------------------------소리 녹음 및 재생----------------------------------------
    MediaPlayer.OnCompletionListener mListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mBtPlay.setText("Start Playing");
        }
    };

    void initAudioRecorder() {
        mRecorder.reset();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // 날짜, 시간으로 파일명 저장
        String getTime = simpleDate.format(mDate);

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas");     //폴더 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas/" + getTime + ".aac";         //현재 시간을 이름으로 파일 생성
        mPlayPath = "/storage/emulated/0/Phas/2020-04-27 12:39:01.aac";

        Log.d(TAG, "file path is " + mPath);
        mRecorder.setOutputFile(mPath);

        try {
            mRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    //--------------------------------------소리 녹음 및 재생----------------------------------------


    //------------------------------------------소리 파형-------------------------------------------
    private void startDrawing() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int currentMaxAmplitude = mRecorder.getMaxAmplitude();

                audioRecordView.update(currentMaxAmplitude); //redraw view
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    private void stopDrawing() {
        timer.cancel();
        audioRecordView.recreate();
    }
    //------------------------------------------소리 파형-------------------------------------------


    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }
}
