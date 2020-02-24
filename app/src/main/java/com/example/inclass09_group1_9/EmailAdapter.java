package com.example.inclass09_group1_9;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class EmailAdapter  extends RecyclerView.Adapter<EmailAdapter.ViewHolder>  {

    public static String email_key ="email";
    String token = MainActivity.sharedpref.getString("token","");
    ArrayList<Email> mdata;

    public EmailAdapter(ArrayList<Email> mdata) {
        this.mdata = mdata;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Email email = mdata.get(position);
        holder.tv_subject.setText(email.subject);
        holder.tv_date.setText(email.date);
        holder.newEmail = email;



    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_subject;
        TextView tv_date;
        ImageView imageView_delete;
        Email newEmail;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tv_subject = (TextView) itemView.findViewById(R.id.tv_subject);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            imageView_delete= (ImageView)  itemView.findViewById(R.id.imageView_delete);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(),DisplayMail.class);
                    intent.putExtra(email_key,  newEmail);
                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.findViewById(R.id.imageView_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    removeAt(getPosition());

                    final OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/delete/"+newEmail.getEmailID())
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .addHeader("Authorization", "BEARER "+""+token)
                            .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override public void onResponse(Call call, Response response) throws IOException {


                        }
                    });

                }
            });

        }

        public void removeAt(int position){

            mdata.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mdata.size());
        }

    }
}
