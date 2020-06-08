package com.example.phas;

import android.util.Log;

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
    public HttpMultiPart(final File file) {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", Values.email)
                .addFormDataPart("dog_name", Values.dName)
                .addFormDataPart("audio_file",Values.file, RequestBody.create(MultipartBody.FORM, new File(Values.path)))
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
                Log.d("asd", "onResponse: " + response.body().string());
            }
        });
    }
}
