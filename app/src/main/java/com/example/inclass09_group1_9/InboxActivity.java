package com.example.inclass09_group1_9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class InboxActivity extends AppCompatActivity {

    TextView et_name;
   // ArrayList<Email> emailArrayList = new ArrayList<>();
    ArrayList<Email> emailArrayList;
    String errorMessage;
    ListView listview;
    ImageView imageView_addnew;
    ImageView imageView_logout;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mlayoutManager;
    ArrayList<Email> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        et_name = findViewById(R.id.et_name);
        //listview = findViewById(R.id.listview);
        imageView_addnew = findViewById(R.id.imageView_addnew);
        imageView_logout = findViewById(R.id.imageView_logout);

        setTitle("Inbox");
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);


//        if(getIntent()!=null && getIntent().getExtras()!=null){
//
//            String firstname = getIntent().getExtras().getString(MainActivity.main_key);
//            String lastname = getIntent().getExtras().getString(MainActivity.main_key1);
//            et_name.setText(firstname+ " " +lastname);
//
//            String firstname1 = getIntent().getExtras().getString(SignUpActivity.signUp_key);
//            String lastname1 = getIntent().getExtras().getString(SignUpActivity.signUp_key1);
//            et_name.setText(firstname1+ " " +lastname1);
//
//        }

        if(MainActivity.sharedpref.contains("name")){
            String name = MainActivity.sharedpref.getString("name","");
            et_name.setText(name);
        }

        if(isConnected()==true){
            Toast.makeText(InboxActivity.this, "Connected", Toast.LENGTH_SHORT).show();
            new InboxActivity.asyncParse1().execute("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox");

        }
        else
        {
            Toast.makeText(InboxActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
        }


        imageView_addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(InboxActivity.this , CreateNewEmailActivity.class);
                startActivity(intent);
            }
        });

        imageView_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor prefsEditor = MainActivity.sharedpref.edit();
                prefsEditor.clear().commit();
                Intent intent = new Intent(InboxActivity.this , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != connectivityManager.TYPE_WIFI &&
                networkInfo.getType() != connectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    private class asyncParse1 extends AsyncTask<String, Void , ArrayList<Email>> {


        public asyncParse1() {
            super();
        }

        @Override
        protected void onPostExecute(ArrayList<Email> emails) {
            super.onPostExecute(emails);


            recyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mlayoutManager = new LinearLayoutManager(InboxActivity.this);
            recyclerView.setLayoutManager(mlayoutManager);

            mAdapter = new EmailAdapter(emails);
            recyclerView.setAdapter(mAdapter);



        }

        @Override
        protected ArrayList<Email> doInBackground(String... strings) {

           emailArrayList = new ArrayList<>();
            String name;
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
                            Toast.makeText(InboxActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                 }
                else {
                    JSONArray messages = root.getJSONArray("messages");
                    for (int i = 0; i < messages.length(); i++) {
                        JSONObject jsonarticle = messages.getJSONObject(i);
                        Email email = new Email();
                        name = jsonarticle.getString("sender_fname") + " " + jsonarticle.getString("sender_lname");
                        email.setSender(name);
                        email.setDate(jsonarticle.getString("created_at"));
                        email.setMessage(jsonarticle.getString("message"));
                        email.setSubject(jsonarticle.getString("subject"));
                        email.setEmailID(jsonarticle.getString("id"));
                        emailArrayList.add(email);
                        Log.d("demo", "onResponse: HEY SENDER " + email.getSender());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return emailArrayList;
        }
    }



}
