package com.example.inclass09_group1_9;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {

    Button bt_cancel;
    Button button_signup;
    EditText et_firstname , et_lastname, et_email, et_password , et_repeatpassword;
    String errorMessage;
    public static String signUp_key ="key";
    public static String signUp_key1 ="key1";
    public boolean fnflag , lnflag, eflag, pflag , rflag =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button_signup = findViewById(R.id.button_signup);
        bt_cancel = findViewById(R.id.bt_cancel);
        et_firstname = findViewById(R.id.et_firstname);
        et_lastname = findViewById(R.id.et_lastname);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_repeatpassword = findViewById(R.id.et_repeat);

        setTitle("Sign Up");

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
              startActivity(intent);
               finish();
            }
        });


        button_signup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if(et_firstname.getText().toString().equals("")){
                    et_firstname.setError("Please enter the value");
                    fnflag=true;
                }

                if(et_lastname.getText().toString().equals("")){
                    et_lastname.setError("Please enter the value");
                    lnflag= true;
                }

                if(et_email.getText().toString().equals("")){
                    et_email.setError("Please enter the value");
                    eflag=true;
                }

                if(!(et_password.getText().toString().equals(et_repeatpassword.getText().toString()))){

                    et_password.setError("Passwords don't match");
                    et_repeatpassword.setError("Passwords don't match");
                    pflag=true;
                    rflag= true;
                }
                else {
                    pflag= false;
                    rflag = false;
                }

                if(et_password.getText().toString().equals("")){
                    et_password.setError("Please enter the password");
                    pflag=true;
                }
                if(et_repeatpassword.getText().toString().equals("")){
                    et_repeatpassword.setError("Please enter the password");
                    rflag=true;
                }

                if(!rflag && !pflag) {
                    final OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", et_email.getText().toString())
                            .add("fname", et_firstname.getText().toString())
                            .add("lname", et_lastname.getText().toString())
                            .add("password", et_password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                            .post(formBody)
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build();


                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {

                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                            String json = response.body().string();
                            try {
                                JSONObject root = new JSONObject(json);
                                String message = root.getString("status");
                                if (message.equals("error")) {
                                    errorMessage = root.getString("message");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    JSONObject sucroot = new JSONObject(json);
                                    String name = sucroot.getString("user_fname")+ " " + sucroot.getString("user_lname");

                                    SharedPreferences.Editor prefsEditor = MainActivity.sharedpref.edit();
                                    prefsEditor.putString("token", sucroot.getString("token"));
                                    prefsEditor.putString("name",name);
                                    prefsEditor.commit();

                                    Intent emailIntent = new Intent(SignUpActivity.this, InboxActivity.class);
//                                    emailIntent.putExtra(signUp_key, firstname);
//                                    emailIntent.putExtra(signUp_key1, lastname);
                                    startActivity(emailIntent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("demo", json);
                        }
                    });
                }
            }
        });
    }
}
