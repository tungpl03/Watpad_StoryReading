package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;
import com.example.storywatpad.validate.Validate;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername, edtEmail, edtPassword, edtRePassword;
    Button btnNext;
    Validate validate = new Validate();
    DatabaseHandler db = new DatabaseHandler(this);
    SharedPreferences SP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        Init();
       SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);
        Events();


    }

    private void Events() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String rePassword = edtRePassword.getText().toString().trim();
                if(username.trim().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Username must not empty!", Toast.LENGTH_SHORT).show();
                }
                else if(!validate.validateEmail(email)){
                    Toast.makeText(RegisterActivity.this, "Email is invalid!", Toast.LENGTH_SHORT).show();
                }
                else if(!validate.validatePassword(password)){
                    Toast.makeText(RegisterActivity.this, "Password is must be 8-24 characters!", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(rePassword)){
                    Toast.makeText(RegisterActivity.this, "Password and confirm password must be the same!", Toast.LENGTH_SHORT).show();
                } else if (db.getAccountWithEmail(email) != null) {
                    Toast.makeText(RegisterActivity.this, "This email already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    CreateAccount(username, email, password, rePassword);
                    User user = db.getAccount(email, password);
                    SharedPreferences.Editor editor = SP.edit();
                    editor.putString("Email", email);
                    editor.putString("Password", password);
                    editor.apply();
                    Toast.makeText(RegisterActivity.this, "Register successful!", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(it);
                }


            }
        });
    }

    private void CreateAccount(String username, String email, String password, String rePassword) {
        db.execsql("INSERT INTO User ( Username, Email, PasswordHash, Role)VALUES ('"+username+"','"+email+"','"+password+"','user')" );

    }

    private void Init() {
        edtUsername = findViewById(R.id.username);
        edtEmail = findViewById(R.id.email);
        edtRePassword = findViewById(R.id.re_password);
        edtPassword = findViewById(R.id.password);
        btnNext = findViewById(R.id.btnNext);
    }

}