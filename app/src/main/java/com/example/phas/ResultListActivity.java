package com.example.phas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultListActivity extends AppCompatActivity {

    private ListView listView = null;
    ResultListAdapter adapter;

    String result = "";
    String status = "";

    PostJSONResult JSONResult;
    JSONArray jArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", Values.email);
            jsonObject.put("dog_name", Values.dName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONResult = new PostJSON().execute("http://210.115.230.131:10480/heartbeat/search/log", jsonObject.toString()).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            result = JSONResult.getResultStr();
            Log.d("result list", result);

            jArray = new JSONArray(result);

            adapter = new ResultListAdapter();

            listView = findViewById(R.id.result_listView);
            listView.setAdapter(adapter);

            for(int i=0; i < jArray.length(); i++) {
                int a = jArray.getJSONObject(i).getInt("heartbeat_normal_condition");
                String date = jArray.getJSONObject(i).getString("create_date");

                if (a == 1) {
                    status = "정상";
                }

                adapter.addItem(status, date);

                Log.d("data check", status + " " + date);
            }

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
