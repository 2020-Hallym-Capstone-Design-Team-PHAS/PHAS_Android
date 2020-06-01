package com.example.phas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    Button register, log_in;
    EditText email, password;

    String sEmail, sPassword;
    int resultCode;
    long backKeyPressedTime; // 뒤로가기를 누른 경우 종료

    PostJSONResult JSONResult;

    // ------------------------- 접근자, 설정자 ------------------------- //
    public String getEmail() {
        return sEmail;
    }

    public String getPassword() {
        return sPassword;
    }

    public void setEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public void setPassword(String sPassword) {
        this.sPassword = sPassword;
    }
    // ----------------------------------------------------------------- //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register = findViewById(R.id.register); // 계정 생성 버튼
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        log_in = findViewById(R.id.log_in); // 로그인 버튼
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmail(email.getText().toString().trim());
                setPassword(password.getText().toString().trim());

                if (getEmail().isEmpty() || getPassword().isEmpty()) { // 아무 값도 입력하지 않은 경우
                    if (getEmail().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("user_id", getEmail());
                        jsonObject.put("user_password", getPassword());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (email.length() > 0) {
                        try {
                            JSONResult = new PostJSON().execute("http://210.115.230.131:10480/users/sign-in", jsonObject.toString()).get();
                            Log.d("JSONResult", JSONResult.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        resultCode = JSONResult.getHttpStatus();
                        Log.d("result", " " + resultCode);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (resultCode == 200) {
                        Toast.makeText(LoginActivity.this, "환영합니다.", Toast.LENGTH_SHORT).show();
                        Values.email = getEmail();
                        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(LoginActivity.this, "아이디나 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() { // 뒤로가기를 2번 누른 경우 종료
        if(System.currentTimeMillis() > backKeyPressedTime+2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로가기를 한번 더 누른 경우 종료됩니다.", Toast.LENGTH_LONG).show();
        }
        else {
            finishAffinity();
            System.runFinalization();
            System.exit(0);
        }
    }
}
