package com.example.phas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

public class DogRegisterActivity extends AppCompatActivity {

    Spinner size;
    EditText name, birth, bread;
    Button save;
    int dSize = 1;

    String sName, sBirth, sBread, sSize;
    int resultCode;

    PostJSONResult JSONResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_register);

        size = findViewById(R.id.dog_size);
        name = findViewById(R.id.dog_name);
        bread = findViewById(R.id.dog_bread);
        birth = findViewById(R.id.dog_birth);

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sName = name.getText().toString().trim();
                sBirth = birth.getText().toString().trim();
                sBread = bread.getText().toString().trim();
                sSize = size.getSelectedItem().toString();

                if(sSize.equals("SMALL")) {
                    dSize = 1;
                }
                else if(sSize.equals("MEDIUM")) {
                    dSize = 2;
                }
                else {
                    dSize = 3;
                }

                StringBuffer sb = new StringBuffer(sBirth);

                String slash = "-";

                sb.insert(4, slash);
                sb.insert(7, slash);

                Log.d("StringBuffer size", String.valueOf(sb.capacity()));

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("user_id", Values.email);
                    jsonObject.put("dog_name", sName);
                    jsonObject.put("dog_breed", sBread);
                    jsonObject.put("dog_size", dSize);
                    jsonObject.put("dog_birth", sb);

                    Log.d("send_message", jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(sb.capacity() <= 24) {
                    try {
                        JSONResult = new PostJSON().execute("http://210.115.230.131:10480/dogs/dogregist", jsonObject.toString()).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        resultCode = JSONResult.getHttpStatus();
                        Log.d("result", " " + resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resultCode == 200) {
                        Toast.makeText(DogRegisterActivity.this, "등록에 성공했습니다.", Toast.LENGTH_LONG).show();
                        Intent intent1 = new Intent(DogRegisterActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(DogRegisterActivity.this, "등록에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(DogRegisterActivity.this, "올바른 날짜형식을 입력해주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
