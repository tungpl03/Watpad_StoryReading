package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;

public class AdminPageActivity extends AppCompatActivity {
    User user;
    SharedPreferences SP;
    CardView accountManage, storyManage, statistic, logout;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);
        Init();
        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);
        Events();


    }

    private void Events() {
        accountManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(AdminPageActivity.this, AccountManageActivity.class);
                startActivity(it);
            }
        });

        storyManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = SP.edit();
                editor.clear();
                editor.apply();
                Intent it = new Intent(AdminPageActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });
    }

    private void Init() {
        accountManage = findViewById(R.id.accountManage);
        storyManage = findViewById(R.id.storyManage);
        statistic = findViewById(R.id.statistic);
        logout = findViewById(R.id.logout);
        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));
    }

}
