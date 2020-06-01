package com.example.phas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DogListActivity extends AppCompatActivity {

    private ListView listView = null;

    String result = "";
    String sName;
    int count = 1;

    ArrayList<DogItemData> data;

    PostJSONResult JSONResult;

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
                return false;
            }
        });
    }
}
