package com.example.inclass09_group1_9;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

public class DisplayMail extends AppCompatActivity {

    TextView tv_sendername;
    TextView tv_subject;
    TextView tv_createdat;
    TextView tv_message;
    Button button_finish;
    Email email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mail);

        tv_sendername = findViewById(R.id.tv_sendername);
        tv_subject= findViewById(R.id.tv_subject);
        tv_createdat = findViewById(R.id.tv_date);
        tv_message = findViewById(R.id.tv_message);
        button_finish = findViewById(R.id.button_finish);
        setTitle("Display Mail");

        if(getIntent()!=null && getIntent().getExtras()!=null){

            email = (Email) getIntent().getExtras().getSerializable(EmailAdapter.email_key);



            tv_sendername.setText(email.sender);
            tv_subject.setText(email.subject);
           // tv_createdat.setText("null");
            tv_message.setText(email.message);

            button_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

        }

    }
}
