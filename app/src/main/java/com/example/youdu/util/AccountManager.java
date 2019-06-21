package com.example.youdu.util;

import com.example.youdu.MainActivity;
import com.example.youdu.bean.UserInfo;
import com.example.youdu.net.LoginApi;
import com.example.youdu.net.RegisterApi;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccountManager {

    public static final byte REGISTER_SUCCESS = 0;
    public static final byte USERNAME_EXIST = 1;
    public static final byte PASSWORD_TOOSHORT = 2;

    public static final byte LOGIN_SUCCESS = 10;
    public static final byte USERNAME_NOT_EXIST = 11;
    public static final byte PASSWORD_WRONG = 12;

    public static final byte FAILURE_UNKNOWN = -1;

    private static Retrofit retrofit;

    static {
        retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static void register(String userName, String password, final MyCallback callback){

        retrofit.create(RegisterApi.class)
                .request(userName, Encryption.md5(password))
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean res = response.body();
                        if (res != null && res)
                            callback.onSuccess(null);
                        else callback.onFailure(USERNAME_EXIST);
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        t.printStackTrace();
                        callback.onFailure(FAILURE_UNKNOWN);
                    }
                });
    }

    public static void login(String userName, String password, final MyCallback<UserInfo> callback) {
        final String passwordMd5 = Encryption.md5(password);
        retrofit.create(LoginApi.class)
                .request(userName, passwordMd5)
                .enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        byte result = FAILURE_UNKNOWN;
                        JSONObject obj = response.body();
                        assert obj != null;
                        try {
                            int code = obj.getInt("code");
                            switch (code){
                                case 0:
                                    result = LOGIN_SUCCESS;
                                    UserInfo user = new Gson().fromJson(obj.getJSONObject("user").toString(), UserInfo.class);
                                    user.setPasswordMd5(passwordMd5);
                                    callback.onSuccess(user);
                                    return;
                                case 1:
                                    result = USERNAME_NOT_EXIST;
                                    break;
                                case 2:
                                    result = PASSWORD_WRONG;
                                    break;
                            }
                            callback.onFailure(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        t.printStackTrace();
                        callback.onFailure(FAILURE_UNKNOWN);
                    }
                });
    }
}
