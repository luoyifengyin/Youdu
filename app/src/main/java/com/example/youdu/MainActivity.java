package com.example.youdu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youdu.bean.User;
import com.example.youdu.util.AccountManager;
import com.example.youdu.util.MyCallback;

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
                String userName = accountText.getText().toString();
                String password = passwordText.getText().toString();
                AccountManager.login(userName, password, new MyCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent = new Intent(MainActivity.this, BooksActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int code) {
                        switch (code){
                            case AccountManager.PASSWORD_WRONG:
                                Toast.makeText(MainActivity.this, R.string.password_error, Toast.LENGTH_SHORT).show();
                                passwordText.requestFocus();
                                break;
                            case AccountManager.USERNAME_NOT_EXIST:
                                Toast.makeText(MainActivity.this, R.string.username_not_exist, Toast.LENGTH_SHORT).show();
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
                String username = accountText.getText().toString();
                String password = passwordText.getText().toString();
                AccountManager.register(username, password, new MyCallback() {
                    @Override
                    public void onSuccess(Object o) {
                        Toast.makeText(MainActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int code) {
                        switch (code) {
                            case AccountManager.USERNAME_EXIST:
                                Toast.makeText(MainActivity.this, R.string.username_exist, Toast.LENGTH_LONG).show();
                                accountText.requestFocus();
                                break;
                            case AccountManager.PASSWORD_TOOSHORT:
                                Toast.makeText(MainActivity.this, R.string.password_too_short, Toast.LENGTH_SHORT).show();
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
