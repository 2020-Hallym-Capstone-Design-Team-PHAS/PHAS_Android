package com.example.phas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpMultiPart {

    Context mContext;

    public HttpMultiPart(final Context mContext) {
        this.mContext = mContext;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", Values.email)
                .addFormDataPart("dog_name", Values.dName)
                .addFormDataPart("audio_file", Values.file, RequestBody.create(MultipartBody.FORM, new File(Values.path)))
                .build();

        Request request = new Request.Builder()
                .url("http://210.115.230.131:10480/heartbeat/save")
                .post(requestBody)
                .build();

        OkHttpClient client = new OkHttpClient();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.body().string());

                    Values.normal = json.getInt("predict");

                    if(Values.normal == 1) {
                        Values.status = "정상";
                    }
                    Values.percent = json.getString("normal");

                    Log.d("cal test", String.valueOf(Values.normal) + " " + Values.percent);

                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("진단 결과");
                            builder.setMessage("진단 결과 : " + Values.status + " 입니다. \n정확도는 " + Values.percent + "% 입니다. \n본 자료는 참고용으로만 사용해주세요!");
                            builder.setPositiveButton("네!",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                            builder.show();
                        }
                    }, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
