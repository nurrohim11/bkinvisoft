package com.rohim.invisoft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.rohim.invisoft.utils.ServerUrl;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import co.id.gmedia.coremodul.ApiVolley;
import co.id.gmedia.coremodul.ItemValidation;
import co.id.gmedia.coremodul.SessionManager;
import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsername, edtPassword;
    CircularProgressButton btnLogin;
    SessionManager sessionManager;
    ItemValidation iv = new ItemValidation();
    KAlertDialog pDialogProses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        if (sessionManager.getSPSudahLogin()){
            startActivity(new Intent(LoginActivity.this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        initUi();
        pDialogProses = new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        pDialogProses.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogProses.setTitleText("Processing...");
        pDialogProses.setCancelable(false);
    }

    private void initUi(){
        edtUsername = findViewById(R.id.edt_username);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtUsername.getText().toString().length() == 0){
                    edtUsername.setError("Username harap diisi");
                    edtUsername.requestFocus();
                    return;

                }else{
                    edtUsername.setError(null);
                }

                if(edtPassword.getText().toString().length() == 0){

                    edtPassword.setError("Password harap diisi");
                    edtPassword.requestFocus();
                    return;

                }else{
                    edtPassword.setError(null);
                }

                doLogin();
            }
        });
    }

    private void doLogin(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        pDialogProses.show();
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", edtUsername.getText().toString());
            jBody.put("password", edtPassword.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(LoginActivity.this, jBody, "POST", ServerUrl.url_login, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                pDialogProses.dismiss();
                try {
                    Log.d("LoginActivity","onsuccess "+result);
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");
                    String message = response.getJSONObject("metadata").getString("message");

                    if(iv.parseNullInteger(status) == 200){
                        Toasty.success(LoginActivity.this, message, Toast.LENGTH_SHORT, true).show();

                        JSONObject jo = response.getJSONObject("response");
                        sessionManager.saveSPString(SessionManager.SP_NAMA, jo.getString("nama"));
                        sessionManager.saveSPString(SessionManager.SP_USERNAME, jo.getString("username"));
                        sessionManager.saveSPString(SessionManager.SP_LEVEL, jo.getString("level"));
                        sessionManager.saveSPString(SessionManager.SP_NO_TELP, jo.getString("no_telp"));
                        sessionManager.saveSPString(SessionManager.SP_ID, jo.getString("id"));
                        // Shared Pref ini berfungsi untuk menjadi trigger session login
                        sessionManager.saveSPBoolean(SessionManager.SP_SUDAH_LOGIN, true);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }, 1000);
                    }else{
                        Toasty.error(LoginActivity.this, message, Toast.LENGTH_SHORT, true).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(LoginActivity.this, "Terjadi kesalahan data", Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void onError(String result) {
                pDialogProses.dismiss();
                Toasty.error(LoginActivity.this, "Terjadi kesalahan saat mengambil data", Toast.LENGTH_SHORT, true).show();
            }
        });
    }

}
