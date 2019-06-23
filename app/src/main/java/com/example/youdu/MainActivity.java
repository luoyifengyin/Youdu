package com.example.youdu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youdu.bean.UserInfo;
import com.example.youdu.util.AccountManager;
import com.example.youdu.util.MyCallback;
import com.example.youdu.util.LocalDataManager;

public class MainActivity extends AppCompatActivity {

    public static final String URL = "https://127.0.0.1/youdu/";

    private TextView accountText;

    private EditText passwordText;

    private Button loginBtn;

    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountText = findViewById(R.id.acount);
        passwordText = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login_button);
        registerBtn = findViewById(R.id.register_button);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = accountText.getText().toString().trim();
                final String password = passwordText.getText().toString();
                if (username.isEmpty()) {
                    accountText.setError("请输入用户名");
                    accountText.requestFocus();
                    return;
                }
                AccountManager.login(username, password, new MyCallback<UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        LocalDataManager.getInstance(MainActivity.this).saveUser(userInfo);
                        Intent intent = new Intent(MainActivity.this, IndexActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int code) {
                        switch (code){
                            case AccountManager.PASSWORD_WRONG:
                                passwordText.setError("密码错误，请重新输入");
                                passwordText.requestFocus();
                                break;
                            case AccountManager.USERNAME_NOT_EXIST:
                                accountText.setError("用户名不存在，请重新输入");
                                accountText.requestFocus();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = accountText.getText().toString().trim();
                String password = passwordText.getText().toString();
                if (username.isEmpty()) {
                    accountText.setError("请输入用户名");
                    accountText.requestFocus();
                    return;
                }
                AccountManager.register(username, password, new MyCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(MainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code) {
                        switch (code) {
                            case AccountManager.USERNAME_EXIST:
                                accountText.setError("注册失败，用户名已存在");
                                accountText.requestFocus();
                                break;
                            case AccountManager.PASSWORD_TOOSHORT:
                                passwordText.setError("密码长度必须大于等于6位");
                                passwordText.requestFocus();
                                break;
                            default:
                                Toast.makeText(MainActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
