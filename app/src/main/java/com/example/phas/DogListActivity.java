package com.example.phas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class DogListActivity extends AppCompatActivity {

    private ListView listView = null;

    String result = "";
    String sName;
    int count = 1;
    int resultCode;

    ArrayList<DogItemData> data;

    PostJSONResult JSONResult;

    TextView context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_list);

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
            data = new ArrayList<DogItemData>();
            result = JSONResult.getResultStr();

            JSONObject json = null;
            json = new JSONObject(result);

            sName = json.getString("dog_name");
            String match1 = "[\\[\\]]";
            sName = sName.replaceAll(match1, "");
            String match2 = "\"";
            sName = sName.replaceAll(match2, "");

            String[] aName = sName.split(",");

            if(!result.equals(null)) {
                for (int i = 0; i < aName.length; i++) {

                    String s = aName[i];

                    DogItemData dData = new DogItemData(count, s);
                    dData.setCount(count);
                    dData.setsName(s);
                    data.add(dData);

                    Log.d("list test", s + " " + count);
                    count++;
                }
            }
            else{
                Toast.makeText(DogListActivity.this, "등록된 강아지가 없어요.", Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView = findViewById(R.id.listView);
        final DogListAdapter oAdapter = new DogListAdapter(data);
        listView.setAdapter(oAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                context = view.findViewById(R.id.context);
                final String dogName = context.getText().toString();
                Log.d("강아지 이름", dogName);

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case DialogInterface.BUTTON_POSITIVE: // Dialog 에서 yes 버튼을 누른 경우
                                try {
                                    jsonObject.put("user_id", Values.email);
                                    jsonObject.put("dog_name", dogName);
                                    Log.d("JSON", jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (!dogName.isEmpty()) {
                                    try {
                                        JSONResult = new PostJSON().execute("http://210.115.230.131:10480/dogs/doginfo_del", jsonObject.toString()).get();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        resultCode = JSONResult.getHttpStatus();
                                        Log.d("result", String.valueOf(resultCode));
                                    } catch (Exception e) {
                                        Log.e("Fail 3", e.toString());
                                    }
                                    if (resultCode == 200) {
                                        Toast.makeText(DogListActivity.this, "삭제 완료!", Toast.LENGTH_SHORT).show();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(DogListActivity.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE: // dialog 창에서 no 버튼을 누른 경우
                                Toast.makeText(DogListActivity.this, "취소", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(DogListActivity.this);
                builder.setMessage("정말 삭제하시겠어요 ?").setPositiveButton("네", dialogClickListener).setNegativeButton("아니요", dialogClickListener).show();
                return true;
            }
        });
    }
}
