package com.example.storywatpad.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText oldPassword, newPassword,cfNewPassword;
    Button save;
    SharedPreferences SP;
    User user;
    private DatabaseHandler db = new DatabaseHandler(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.change_password);
        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        Init();
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassword.getText().toString().trim();
                String newPass = newPassword.getText().toString().trim();
                String cfNewPass = cfNewPassword.getText().toString().trim();

                if(!user.getPasswordHash().equals(oldPass)){
                    Toast.makeText(ChangePasswordActivity.this,
                            "Check old password again!",
                            Toast.LENGTH_LONG).show();
                }
                else if(!newPass.equals(cfNewPass)){
                    Toast.makeText(ChangePasswordActivity.this, "Password and confirm password must be the same!", Toast.LENGTH_SHORT).show();

                }
                else if(newPass.equals(oldPass)){
                    Toast.makeText(ChangePasswordActivity.this, "New password must be different from old password!", Toast.LENGTH_SHORT).show();

                }
                else {
                    db.changePassword(SP.getString("Email",""), newPass);
                    Toast.makeText(ChangePasswordActivity.this, "Change password successful!", Toast.LENGTH_SHORT).show();
                Intent it = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                startActivity(it);
                }
            }
        });

    }

    private void Init() {
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        cfNewPassword = findViewById(R.id.cfNewPassword);
        save = findViewById(R.id.save);
    }


}
