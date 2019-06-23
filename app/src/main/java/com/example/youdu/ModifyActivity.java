package com.example.youdu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.youdu.bean.UserInfo;
import com.example.youdu.net.ModifyApi;
import com.example.youdu.util.Encryption;
import com.example.youdu.util.LocalDataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ModifyActivity extends AppCompatActivity {
    private Retrofit retrofit;
    private UserInfo mUserInfo;
    private EditText mUserNameEt;
    private EditText mOldPwdEt;
    private EditText mNewPwdEt;
    private EditText mNewPwdConfirmEt;
    private Button mSubmitBtn;
    private Button mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mUserInfo = LocalDataManager.getInstance(this).getUserInfo();
        mUserNameEt = findViewById(R.id.et_name);
        mOldPwdEt = findViewById(R.id.et_old_pwd);
        mNewPwdEt = findViewById(R.id.et_new_pwd);
        mNewPwdConfirmEt = findViewById(R.id.et_new_pwd_confirm);
        mSubmitBtn = findViewById(R.id.btn_submission);
        mBackBtn = findViewById(R.id.btn_back);

        mUserNameEt.setText(mUserInfo.getName());
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = mUserNameEt.getText().toString().trim();
                String oldPwd = mOldPwdEt.getText().toString();
                String newPwd = mNewPwdEt.getText().toString();
                String newPwdConfirm = mNewPwdConfirmEt.getText().toString();
                if (newName.isEmpty()) {
                    mUserNameEt.setError("用户名不能为空");
                    mUserNameEt.requestFocus();
                    return;
                }
                if (oldPwd.isEmpty()) {
                    mOldPwdEt.setError("旧密码不能为空");
                    mOldPwdEt.requestFocus();
                    return;
                }
                if (newPwd.equals(newPwdConfirm)) {
                    retrofit.create(ModifyApi.class)
                            .request(mUserInfo.getUid(), Encryption.md5(oldPwd), newName, Encryption.md5(newPwd))
                            .enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Boolean res = response.body();
                                    if (res != null && res) {
                                        Toast.makeText(ModifyActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                        mOldPwdEt.setText("");
                                        mNewPwdEt.setText("");
                                        mNewPwdConfirmEt.setText("");
                                    }
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    t.printStackTrace();
                                }
                            });
                } else {
                    mNewPwdConfirmEt.setError("确认密码与新密码不一致");
                    mNewPwdConfirmEt.setText("");
                    mNewPwdConfirmEt.requestFocus();
                    return;
                }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
