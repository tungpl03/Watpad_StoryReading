package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;

public class LoginActivity extends AppCompatActivity {
    AppCompatButton btnLogin;
    EditText edtEmail, edtPassword;
    SharedPreferences SP;
    TextView tvSignin, btnForgetPassword;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        db.DB2SDCard();
        Init();

        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();

                User user = getAccount(email, pass); // Chỉ gọi 1 lần

                if (user == null) {
                    Toast.makeText(LoginActivity.this,
                            "Check email and password again!",
                            Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences.Editor editor = SP.edit();
                    editor.putString("Email", email);
                    editor.putString("Password", pass);
                    editor.apply();

                    if ("user".equals(user.getRole())) {
                        Intent it = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(it);
                        Toast.makeText(LoginActivity.this,
                                "Login successful! Hello " + user.getUsername(),
                                Toast.LENGTH_LONG).show();
                    } else if ("admin".equals(user.getRole())) {
                        // Xử lý logic cho admin





                    }
                }
            }
        });

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(it);
            }
        });

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it);
            }
        });


    }

    private User getAccount(String email, String pass) {
        Cursor cursor = db.getReadableDatabase().rawQuery(
                "SELECT * FROM User WHERE Email = ? AND PasswordHash = ?",
                new String[]{email, pass}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8));
        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return user;
    }

    private void Init() {
        btnLogin = findViewById(R.id.login);
        edtEmail = findViewById(R.id.email);
        tvSignin = findViewById(R.id.tvSignup);
        edtPassword = findViewById(R.id.edtPass);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);
    }
}
