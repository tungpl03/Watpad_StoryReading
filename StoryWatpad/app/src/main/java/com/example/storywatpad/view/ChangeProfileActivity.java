package com.example.storywatpad.view;


import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;

public class ChangeProfileActivity extends AppCompatActivity {

    User user;
    SharedPreferences SP;

    ImageView avatar;
    TextView tvUserName, tvBio;
    Button save;
    ImageButton btnBack;
    LinearLayout bio, loAvatar, username;
    ActivityResultLauncher<Intent> resultLauncher;
    String savedImagePath;
    DatabaseHandler db = new DatabaseHandler(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.profile_setting);


        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        Init();

        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

        tvBio.setText(user.getBio());
        tvUserName.setText(user.getUsername());

        //Load avatar
        String userAvatar = user.getAvatarUrl();
        ImageView btnBack = findViewById(R.id.btnBack);
        // Xử lý sự kiện khi bấm nút Back
        btnBack.setOnClickListener(view -> {
            finish(); // Đóng Activity hiện tại để quay lại MainActivity
        });

        if (userAvatar != null) {
            avatar.setImageURI(Uri.fromFile(new File(userAvatar)));
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }
        registerResult();

        loAvatar.setOnClickListener(view -> pickImage());


        bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Edit Biography", tvBio);
            }
        });
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog("Change Username", tvUserName);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String finalAvatarPath = (savedImagePath != null && !savedImagePath.isEmpty())
                        ? savedImagePath
                        : user.getAvatarUrl();
                db.upDateProfile(user.getEmail(), finalAvatarPath, (String) tvBio.getText(), (String)tvUserName.getText());

            Intent it = new Intent(ChangeProfileActivity.this, ProfileActivity.class);
            startActivity(it);

            }
        });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            avatar.setImageURI(imageUri);

                                // Lưu ảnh vào bộ nhớ trong
                                savedImagePath = saveImageToInternalStorage(imageUri);


                        } else {
                            Toast.makeText(ChangeProfileActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
}
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            String fileName = getFileNameFromUri(imageUri);

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Lưu vào bộ nhớ trong
            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File directory = wrapper.getDir("images", MODE_PRIVATE);
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void showEditDialog(String title, TextView textView) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText edtInput = dialogView.findViewById(R.id.edtInput);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        tvDialogTitle.setText(title);
        edtInput.setText(textView.getText().toString());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSave.setOnClickListener(v -> {
            textView.setText(edtInput.getText().toString());
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void Init() {
        btnBack = findViewById(R.id.btnSent);
        avatar = findViewById(R.id.avatar);
        tvBio = findViewById(R.id.tvbio);
        tvUserName = findViewById(R.id.tvusername);
        bio = findViewById(R.id.bio);
        loAvatar = findViewById(R.id.loAvatar);
        username = findViewById(R.id.username);
        save = findViewById(R.id.saveProfile);

    }
}
