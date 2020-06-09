package com.example.phas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private String mPath = null;            // 재생파일의 경로를 저장하기 위한 변수
    private MediaPlayer mPlayer = null;     // 파일을 재생하기 위한 MediaPlayer
    boolean isPlaying = false;
    ListViewItem item[] = null;
    Button list_play;
    Button list_delete;

    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas";
    File directory = new File(path);
    File[] files = directory.listFiles();

    ListView listview;
    ListAdapter adapter;
    ArrayList<ListViewItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        items = new ArrayList<ListViewItem>();

        // items 로드.
        loadItemsFromDB(items);

        // Adapter 생성
        adapter = new ListAdapter(this, R.layout.listview_item, items);

        // 리스트뷰 참조 및 Adapter 설정
        listview = findViewById(R.id.sound_list);
        listview.setAdapter(adapter);

        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    mp.stop();
                    mp.reset();
                    isPlaying = false;
                    list_play.setText("재생");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        View ParentView = (View) v.getParent();
        String temp = (String) ParentView.getTag();
        final int position = Integer.parseInt(temp);

        mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Phas/" + files[position].getName();
        list_play = (Button) ParentView.findViewById(R.id.list_play);
        list_delete = (Button) ParentView.findViewById(R.id.list_delete);


        if (list_play.isPressed()) {
            if (!isPlaying) {
                try {
                    initMediaPlayer();
                    mPlayer.prepare();
                    mPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isPlaying = true;
                list_play.setText("||");
            } else {
                isPlaying = false;
                list_play.setText("재생");
                mPlayer.stop();
                mPlayer.reset();
            }
        }

        if (list_delete.isPressed()) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE: // Dialog 에서 yes 버튼을 누른 경우
                            int count, checked;
                            count = adapter.getCount();

                            if (count > 0) {
                                // 현재 선택된 아이템의 position 획득.
                                checked = position;
                                if (checked < count) {
                                    // 아이템 삭제
                                    items.remove(checked);
                                    // listview 선택 초기화.
                                    listview.clearChoices();
                                    // listview 갱신.
                                    adapter.notifyDataSetChanged();

                                    File file = new File(mPath);
                                    file.delete();
                                }
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE: // dialog 창에서 no 버튼을 누른 경우
                            Toast.makeText(HistoryActivity.this, "취소", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
            builder.setMessage("정말 삭제하시겠어요 ?").setPositiveButton("네", dialogClickListener).setNegativeButton("아니요", dialogClickListener).show();

        }
    }

    public boolean loadItemsFromDB(ArrayList<ListViewItem> list) {
        if (list == null) {
            list = new ArrayList<ListViewItem>();
        }

        for (int i = 0; i < files.length; i++) {
            item = new ListViewItem[files.length];
            item[i] = new ListViewItem(files[i].getName());
            item[i].onClickListener = this;
            list.add(item[i]);
        }
        return true;
    }

    public void initMediaPlayer() {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mPath);
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
    }
}