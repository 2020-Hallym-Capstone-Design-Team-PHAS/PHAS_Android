package com.example.phas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.visualizer.amplitude.AudioRecordView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //--------------------------- 레이아웃 관련 ---------------------------//
    Toolbar toolBar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    TextView choice;
    //---------------------------------------------------------------------//

    private final static String TAG = "MainActivity";

    //--------------------------- 소리 파형 변수 ---------------------------//
    private Timer timer = null;
    private TimerTask timerTask = null;
    private AudioRecordView audioRecordView;
    //---------------------------------------------------------------------//

    PostJSONResult JSONResult;
    String result = "";
    String sName;
    int count = 1;

    //-------------------------- 소리녹음 및 재생 --------------------------//
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;

    String mPath = null;

    boolean isRecording = false;
    boolean isPlaying = false;
    //---------------------------------------------------------------------//

    Button mBtRecord = null;
    Button mBtPlay = null;
    Button mBtHistory = null;
    Button mSend = null;

    Boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        //-------------------------- drawer 메뉴 --------------------------//
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon); //뒤로가기 버튼 이미지 지정

        drawerLayout = findViewById(R.id.drawer_layout_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.caution) {
                    Intent intent1 = new Intent(MainActivity.this, CautionActivity.class);
                    startActivity(intent1);
                } else if (id == R.id.dogRegister) {
                    Intent intent1 = new Intent(MainActivity.this, DogRegisterActivity.class);
                    startActivity(intent1);
                } else if (id == R.id.dogList) {
                    Intent intent1 = new Intent(MainActivity.this, DogListActivity.class);
                    startActivity(intent1);
                }
                else if (id == R.id.appInfo) {
//                    Intent intent1 = new Intent(MainActivity.this, AppInfoActivity.class);
//                    startActivity(intent1);
                } else if (id == R.id.logout) {
                    Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });

        audioRecordView = findViewById(R.id.audioRecordView);

        //-------------------------- 어플리케이션 권한 여부 --------------------------//
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

        mBtRecord = findViewById(R.id.record);
        mBtRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn1 = findViewById(R.id.play);
                Button btn2 = findViewById(R.id.history);

                if(Values.dName != null) {
                    if (!isRecording) {
                        initAudioRecorder();
                        mRecorder.start();
                        startDrawing();
                        isRecording = true;
                        mBtRecord.setText("녹음 멈추기");
                    } else {
                        mRecorder.stop();
                        mRecorder.reset();
                        stopDrawing();
                        isRecording = false;
                        mBtRecord.setText("다시 녹음하기");
                        Toast.makeText(MainActivity.this, "결과가 저장되었습니다!", Toast.LENGTH_SHORT).show();
                    }

                    btn1.setEnabled(flag);
                    btn2.setEnabled(flag);
                    flag = !flag;
                } else {
                    Toast.makeText(MainActivity.this, "강아지를 먼저 선택해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        mBtPlay = findViewById(R.id.play);
        mBtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying) {
                    try {
                        mPlayer.reset();
                        mPlayer.setDataSource(mPath);
                        Log.d("Confirm Path", mPath);
                        mPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.start();
                    isPlaying = true;
                    mBtPlay.setText("멈춤");
                } else {
                    mPlayer.stop();
                    isPlaying = false;
                    mBtPlay.setText("재생하기");
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
                mBtPlay.setText("재생하기");
            }
        });

        mBtHistory = findViewById(R.id.history);
        mBtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

    //-------------------------- 메인화면에서 강아지 선택 -------------------------//
        choice = findViewById(R.id.choice);
        choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", Values.email);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    JSONResult = new PostJSON().execute("http://210.115.230.131:10480/dogs/doginfo_user", jsonObject.toString()).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    ArrayList<DogItemData> data = new ArrayList<DogItemData>();
                    result = JSONResult.getResultStr();

                    JSONObject json = null;
                    json = new JSONObject(result);

                    sName = json.getString("dog_name");
                    String match1 = "[\\[\\]]";
                    sName = sName.replaceAll(match1, "");
                    String match2 = "\"";
                    sName = sName.replaceAll(match2, "");

                    final String[] aName = sName.split(",");

                    if (aName == null || aName.length == 0) {
                        Toast.makeText(MainActivity.this, "강아지를 먼저 등록해주세요.", Toast.LENGTH_LONG).show();
                    } else {
                     AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                             .setTitle("강아지 선택")
                             .setItems(aName, new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialog, int item) {
                                     Values.dName = aName[item];
                                     choice.setText(aName[item]);
                                     Log.d("dog name test", Values.dName);
                                 }
                             });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //-------------------------- 파일 서버로 전송 -------------------------//
        mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(Values.path);
                Log.d("path test", Values.path);
                new HttpMultiPart(f);
            }
        });
    }

    //-------------------------- 소리 녹음 및 재생 --------------------------//
    MediaPlayer.OnCompletionListener mListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mBtPlay.setText("Start Playing");
        }
    };

    void initAudioRecorder() {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH시 mm분 ss초"); // 날짜, 시간으로 파일명 저장
        String getTime = simpleDate.format(mDate);

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas"); // 폴더 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }
        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas/" + getTime + ".aac"; // 현재 시간을 이름으로 파일 생성
        Values.path = mPath;
        Values.file = getTime + ".aac";

        Log.d("path test", mPath);

        mRecorder.reset();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setAudioEncodingBitRate(16);
        mRecorder.setAudioSamplingRate(44100);

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

    //-------------------------- 소리 파형 -------------------------- //
    private void startDrawing() {
        audioRecordView.recreate();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                int currentMaxAmplitude = mRecorder.getMaxAmplitude() * 4;
                audioRecordView.update(currentMaxAmplitude); //redraw view
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    private void stopDrawing() {
        timer.cancel();
    }

    //-------------------------- drawer layout -------------------------- //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }
}
