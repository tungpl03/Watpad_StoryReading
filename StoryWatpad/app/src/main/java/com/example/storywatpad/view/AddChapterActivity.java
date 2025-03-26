package com.example.storywatpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddChapterActivity extends AppCompatActivity {
    private EditText etChapterTitle, etChapterContent;
    private Button btnSave;
    private DatabaseHandler db;
    private int storyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_chapter);

        // Khởi tạo views
        etChapterTitle = findViewById(R.id.et_chapter_title);
        etChapterContent = findViewById(R.id.et_chapter_content);
        btnSave = findViewById(R.id.btn_save);

        // Khởi tạo database
        db = new DatabaseHandler(this);

        // Lấy storyId từ Intent
        storyId = getIntent().getIntExtra("story_id", -1);
        if (storyId == -1) {
            Toast.makeText(this, "Không tìm thấy truyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Thêm sự kiện click cho nút Save
        btnSave.setOnClickListener(v -> saveChapter());
}
    private void saveChapter() {
        String title = etChapterTitle.getText().toString().trim();
        String content = etChapterContent.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (title.isEmpty()) {
            etChapterTitle.setError("Vui lòng nhập tiêu đề chương");
            return;
        }
        if (content.isEmpty()) {
            etChapterContent.setError("Vui lòng nhập nội dung chương");
            return;
        }

        // Lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Thêm chương mới vào database
        db.addChapter(storyId, title, content, currentTime, currentTime);
        Intent intent = new Intent(AddChapterActivity.this, MyStoryDetailsActivity.class);
        intent.putExtra("story_id", storyId);
        startActivity(intent);
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Thêm chương thành công", Toast.LENGTH_SHORT).show();

        // Trả về kết quả và đóng activity
        setResult(RESULT_OK);
        finish();
    }
    }