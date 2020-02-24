package com.example.inclass09_group1_9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CreateNewEmailActivity extends AppCompatActivity {

    Spinner spinner;
    EditText et_subject;
    EditText editText_message;
    Button bt_send;
    Button bt_delete;
    String errorMessage;
    ArrayList<String> userslist = new ArrayList<>();
    String name;
    ArrayList<String> userID = new ArrayList<>();
    ArrayAdapter<String> staticAdapter;
    int selected_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_email);

        spinner = findViewById(R.id.spinner);
        et_subject = findViewById(R.id.et_subject);
        editText_message = findViewById(R.id.editText_message);
        bt_send = findViewById(R.id.bt_send);
        bt_delete = findViewById(R.id.bt_delete);

        setTitle("Create new Mail");

         new CreateNewEmailActivity.asyncParse().execute("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users");


        staticAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,userslist);
       // staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(staticAdapter);


       spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

               Log.d("demo", "onItemSelected: " +spinner.getSelectedItem().toString());
               Log.d("demo", "onItemSelected: compare to the id  "+userID.get(i));
               Log.d("demo","onItemSelected : compare to user Name"+ userslist.get(i));
               selected_id=i;
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });



        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String token = MainActivity.sharedpref.getString("token","");
                Log.d("demo", "onCreate: " +token);

                final OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormBody.Builder()
                        .add("receiver_id", userID.get(selected_id))
                        .add("subject", et_subject.getText().toString())
                        .add("message", editText_message.getText().toString())
                        .build();
                Request request = new Request.Builder()
                        .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                        .post(formBody)
                        .addHeader("Authorization", "BEARER "+""+token)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                        try {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(CreateNewEmailActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                        } finally {

                        }

                    }
                });
            }
        });


        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private class asyncParse extends AsyncTask<String, Void , ArrayList<String>>{

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);

            Log.d("demo", "onPostExecute: " +strings.get(0));
            staticAdapter.notifyDataSetChanged();


        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {

            String token = MainActivity.sharedpref.getString("token","");
            Log.d("demo", "onCreate: " +token);

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(strings[0])
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", "BEARER "+""+token)
                    .build();

            try{
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                Log.d("demo", "doInBackground: " +json);

                JSONObject root = new JSONObject(json);
                String status = root.getString("status");
                if (status.equals("error")) {
                    errorMessage = root.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CreateNewEmailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    JSONArray users = root.getJSONArray("users");
                    for (int i = 0; i < users.length(); i++) {
                        JSONObject jsonarticle = users.getJSONObject(i);
                        name = jsonarticle.getString("fname") + " " + jsonarticle.getString("lname");
                        userID.add(jsonarticle.getString("id"));
                        userslist.add(name);

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return userslist;
        }
    }



}
