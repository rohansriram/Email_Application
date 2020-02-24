package com.example.inclass09_group1_9;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {


    Button bt_login;
    Button bt_signup;
    EditText et_email;
    EditText et_password;

    public static int REQ_CODE =101;
    public static String main_key ="main";
    public static String main_key1 ="main1";
    public static SharedPreferences sharedpref ;
    String errorMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Mailer");
        sharedpref = getPreferences(MODE_PRIVATE);
        bt_signup = findViewById(R.id.button_add);
        bt_login = findViewById(R.id.bt_login);
        et_email = findViewById(R.id.et_email);
        et_password= findViewById(R.id.et_password);



        bt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this , SignUpActivity.class);

                startActivity(intent);
                finish();

            }
        });


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_email.getText().toString().equals("")|| et_password.getText().toString().equals("")){
                    et_password.setError("Please enter the credentials");
                    et_email.setError("Please enter the credentials");
                }


                    final OkHttpClient client = new OkHttpClient();
                    RequestBody formBody = new FormBody.Builder()
                            .add("email", et_email.getText().toString())
                            .add("password", et_password.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
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
                                            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    JSONObject sucroot = new JSONObject(json);
                                    SharedPreferences.Editor prefsEditor = sharedpref.edit();
                                    prefsEditor.putString("token", sucroot.getString("token"));
                                    String name = sucroot.getString("user_fname")+ " " + sucroot.getString("user_lname");
                                    prefsEditor.putString("name",name);
                                    prefsEditor.commit();

//                                    String firstname = sucroot.getString("user_fname");
//                                    String lastname = sucroot.getString("user_lname");

                                    Intent emailIntent = new Intent(MainActivity.this, InboxActivity.class);
//                                    emailIntent.putExtra(main_key, firstname);
//                                    emailIntent.putExtra(main_key1, lastname);
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

        });
    }

}
