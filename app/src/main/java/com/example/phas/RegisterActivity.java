package com.example.phas;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, conPassword, name;
    TextView pwInfo;
    Button register;

    String sEmail, sPassword, sConPassword, sName, sInfo;
    int resultCode;
    boolean flag;

    PostJSONResult JSONResult;

    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$"); // 정규식, 8~16자리까지 가능

    //------------------- 접근자, 설정자 ----------------------//
    public String getEmail() {
        return sEmail;
    }

    public String getName() {
        return sName;
    }

    public String getPassword() {
        return sPassword;
    }

    public String getCheckPassword() {
        return sConPassword;
    }

    public void setEmail(String sEmail) {
        this.sEmail = sEmail;
    }

    public void setName(String sName) {
        this.sName = sName;
    }

    public void setPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public void setCheckPassword(String sConPassword) {
        this.sConPassword = sConPassword;
    }
    //--------------------------------------------------------//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        conPassword = findViewById(R.id.confirm_password);
        name = findViewById(R.id.name);
        pwInfo = findViewById(R.id.pwInfo);

        password.addTextChangedListener(new TextWatcher() { // 비밀번호 정규식 적용하는 부분
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sInfo = password.getText().toString();
                flag = validatePassword(sInfo);

                if (flag == true) {
                    pwInfo.setText("* 사용 가능한 비밀번호 입니다.");
                } else {
                    pwInfo.setText("* 비밀번호는 반드시 [소문자 1개] [숫자 1개]\n   [특수문자 1개] [8-16byte]의 길이여야 합니다.");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sInfo = password.getText().toString();
                flag = validatePassword(sInfo);

                if (flag == true) {
                    pwInfo.setText("* 사용 가능한 비밀번호 입니다.");
                } else {
                    pwInfo.setText("* 비밀번호는 반드시 [소문자 1개] [숫자 1개]\n   [특수문자 1개] [8-16byte]의 길이여야 합니다.");
                    pwInfo.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { // 값이 입력된 후 확인
            }
        });

        register = findViewById(R.id.Register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEmail(email.getText().toString().trim());
                setPassword(password.getText().toString().trim());
                setCheckPassword(conPassword.getText().toString().trim());
                setName(name.getText().toString().trim());

                if (getEmail().isEmpty() || getPassword().isEmpty() || getCheckPassword().isEmpty() || getName().isEmpty()) { // 아무 값도 입력하지 않은 경우
                    if (getEmail().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (getPassword().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (getCheckPassword().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "입력하신 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    } else if (getName().isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (getPassword().equals(getCheckPassword())) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("user_id", getEmail());
                            jsonObject.put("user_password", getPassword());
                            jsonObject.put("user_name", getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (email.length() > 0) {
                            try {
                                JSONResult = new PostJSON().execute("http://210.115.230.131:10480/users/regist", jsonObject.toString()).get();
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
                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent1);
                        } else {
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일 입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this, "입력한 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        Log.d("error", getPassword() + " " + getCheckPassword());
                    }
                }
            }
        });
    }

    public static boolean validatePassword(String pwStr) { // 정규식 값 비교 함수
        Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(pwStr);
        return matcher.matches();
    }
}
