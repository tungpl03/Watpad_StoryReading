package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;

public class LoginActivity extends AppCompatActivity {
    Button btnLogin;
    EditText edtEmail, edtPassword;
    SharedPreferences SP;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Init();

        SP = getSharedPreferences("prmprefs", MODE_PRIVATE);

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
        edtPassword = findViewById(R.id.edtPass);
    }
}
