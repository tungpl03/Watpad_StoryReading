package com.example.storywatpad.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;
import com.example.storywatpad.support.*;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText email;
    Button btnSent;
    DatabaseHandler db = new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_forget);
        Init();
        Events();


    }

    private void Events() {
        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String em = email.getText().toString().trim();
                if(db.getAccountWithEmail(em) != null){

                AlertDialog.Builder al = new AlertDialog.Builder(ForgetPasswordActivity.this);
                al.setTitle("Reset confirmed");
                al.setMessage("Do you want to reset your password?\n Your new password will be sent to email.");
                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Reset successful!", Toast.LENGTH_LONG).show();

                        String newPassword = GenPassword.genpassword();
                        String content = "We see that you have asked to reset the password.\n Your new password is: " + newPassword;

                        JavaMailAPI javaMailAPI = new JavaMailAPI(ForgetPasswordActivity.this, em, "Reset password", content);
                        javaMailAPI.execute();

                        db.changePassword(em,newPassword);

                        Intent it = new Intent(ForgetPasswordActivity.this, LoginActivity.class);

                        startActivity(it);

                    }
                });
                al.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //cancel/finish();
                    }
                });
                al.create().show();

                }
                    else{
                    Toast.makeText(getApplicationContext(), "This account does not exist!", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void Init() {
        email = findViewById(R.id.email);
        btnSent = findViewById(R.id.btnSent);
    }

}
