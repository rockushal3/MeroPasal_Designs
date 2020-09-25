package com.example.meropasal.data.interactors.auth;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.meropasal.models.user.User;
import com.example.meropasal.network.API.AuthApi;
import com.example.meropasal.network.RetrofitIniti;
import com.example.meropasal.utiils.Constants;
import com.example.meropasal.views.AuthContract;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupInteractor {
    private AuthContract.Presenter presenter;
    private static final String TAG = "SignupInteractor";

    public SharedPreferences sharedPreferences;

    public SignupInteractor(AuthContract.Presenter presenter, SharedPreferences sharedPreferences){
        this.presenter = presenter;
        this.sharedPreferences = sharedPreferences;
    }

    public void signup(User user){
        AuthApi api = RetrofitIniti.initialize().create(AuthApi.class);
        Call<User> signupresponse = api.register(user);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        signupresponse.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "onResponse: " + response.body().getMessage() );
                if(response.isSuccessful()){

                    editor.putString(Constants.TOKEN, response.body().getToken());
                    editor.putString(Constants.USER_ID, response.body().get_id());

                    if(!response.body().getMessage().equals("Account Already Exists")){
                        editor.putString(Constants.ACCOUNT, "ACCOUNT");
                    }else{

                    }
                    editor.commit();
                    authentication(true);
                }else{

                    authentication(false);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.toString());
                presenter.onFailed("Connection error");
            }
        });
    }


    private void authentication(boolean valid){
        if(valid){
            presenter.onSuccess();
        }else{
            presenter.onFailed("SignUp Failed! Something Went Wrong!");
        }
    }
}
