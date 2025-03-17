package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.model.User;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.storywatpad.R;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {

    private boolean isAccountSettingsVisible = false;

    User user;
    SharedPreferences SP;
    ImageView avatar;
    TextView NameText, EmailText, Bio;
    AppCompatButton backToHome, logOut, aaps, storyByMe, profileSettingsButton, changePasswordButton;

    private DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);
        Init();
        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

        avatar = findViewById(R.id.avatar);
        String userAvatar = user.getAvatarUrl();

        if (userAvatar != null) {
            avatar.setImageURI(Uri.fromFile(new File(userAvatar)));
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }

        NameText.setText(user.getUsername());
        EmailText.setText(user.getEmail());
        Bio.setText(user.getBio());
        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent it = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(it);
            }
        });

        aaps.setOnClickListener(new View.OnClickListener() {
             @Override
                public void onClick(View v) {
                 isAccountSettingsVisible = !isAccountSettingsVisible;
                if(isAccountSettingsVisible){
                    profileSettingsButton.setVisibility(View.VISIBLE);
                    changePasswordButton.setVisibility(View.VISIBLE);
                }
                else{
                    profileSettingsButton.setVisibility(View.GONE);
                    changePasswordButton.setVisibility(View.GONE);
                }
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

        profileSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ProfileActivity.this, ChangeProfileActivity.class);
                startActivity(it);
            }
        });
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
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
        aaps = findViewById(R.id.aaps);
        storyByMe = findViewById(R.id.storyByMe);
        profileSettingsButton = findViewById(R.id.profileSettingsButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        Bio = findViewById(R.id.Bio);
    }
}
