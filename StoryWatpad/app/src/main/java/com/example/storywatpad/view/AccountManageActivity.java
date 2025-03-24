package com.example.storywatpad.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.AccountAdapter;

import java.util.ArrayList;
import java.util.List;

public class AccountManageActivity extends AppCompatActivity {

    RecyclerView accountList;
    List<User> userList = new ArrayList<>();
    private DatabaseHandler db = new DatabaseHandler(this);
    SharedPreferences SP;
    AccountAdapter adapter;
    User user;
    AppCompatButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.account_manage);

        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

        accountList = findViewById(R.id.accountList);
        accountList.setLayoutManager(new LinearLayoutManager(this)); // Quan trọng để hiển thị danh sách

        userList = db.getAllUser();

        adapter = new AccountAdapter(this, userList, db);
        accountList.setAdapter(adapter);

        back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            finish(); // Đóng Activity hiện tại để quay lại MainActivity
        });
    }
}
