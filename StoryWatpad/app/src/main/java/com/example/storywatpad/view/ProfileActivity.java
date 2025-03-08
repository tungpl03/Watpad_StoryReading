package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.model.User;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.storywatpad.R;

public class ProfileActivity extends AppCompatActivity {
    User user;
    SharedPreferences SP;
    ImageView avatar;
    TextView NameText, EmailText;
    AppCompatButton backToHome, logOut;

    private DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);
        Init();
        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

        avatar = findViewById(R.id.avatar);
        String userAvatar = user.getDrawableImageName();
        int imageResId = this.getResources().getIdentifier(userAvatar, "drawable", this.getPackageName());

        if (imageResId != 0) {
            avatar.setImageResource(imageResId);
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }

        NameText.setText(user.getUsername());
        EmailText.setText(user.getEmail());
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent it = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(it);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = SP.edit();
                editor.clear();
                editor.apply();
                Intent it = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });


    }

    private void Init() {
        avatar = findViewById(R.id.avatar);
        NameText = findViewById(R.id.NameText);
        EmailText = findViewById(R.id.EmailText);
        backToHome = findViewById(R.id.buttonBackToHome);
        logOut = findViewById(R.id.logOut);
    }
}
