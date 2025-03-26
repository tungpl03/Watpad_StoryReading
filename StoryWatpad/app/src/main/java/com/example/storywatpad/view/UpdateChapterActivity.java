package com.example.storywatpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateChapterActivity extends AppCompatActivity {
    private EditText etChapterTitle, etChapterContent;
    private Button btnUpdate, btnDelete;
    private DatabaseHandler db;
    private int chapterId;
    private Chapter chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.update_chapter);

        // Khởi tạo views
        etChapterTitle = findViewById(R.id.et_chapter_title);
        etChapterContent = findViewById(R.id.et_chapter_content);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);

        // Khởi tạo database
        db = new DatabaseHandler(this);

        // Lấy chapterId từ Intent
        chapterId = getIntent().getIntExtra("chapter_id", -1);
        if (chapterId == -1) {
            Toast.makeText(this, "Không tìm thấy chương", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load thông tin chương
        loadChapterDetails(chapterId);

        // Thêm sự kiện click cho nút Update
        btnUpdate.setOnClickListener(v -> updateChapter());

        // Thêm sự kiện click cho nút Delete
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }
    private void loadChapterDetails(int chapterId) {
        chapter = db.getChapterById(chapterId);
        if (chapter == null) {
            Toast.makeText(this, "Không tìm thấy chương", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin chương
        etChapterTitle.setText(chapter.getTitle());
        etChapterContent.setText(chapter.getContent());
    }

    private void updateChapter() {
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

        // Lấy thời gian hiện tại cho updated_at
        String updatedAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Cập nhật thông tin chương
        chapter.setTitle(title);
        chapter.setContent(content);
        chapter.setUpdatedAt(updatedAt);

        // Cập nhật trong database
        db.updateChapter(chapter);
        int storyId = chapter.getStoryId();
        Intent intent = new Intent(UpdateChapterActivity.this, MyStoryDetailsActivity.class);
        intent.putExtra("story_id", storyId);
        startActivity(intent);
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Cập nhật chương thành công", Toast.LENGTH_SHORT).show();

        // Trả về kết quả và đóng activity
        setResult(RESULT_OK);
        finish();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chương này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    // Xóa chương
                    db.deleteChapter(chapterId);

                    int storyId = chapter.getStoryId();
                    Intent intent = new Intent(UpdateChapterActivity.this, MyStoryDetailsActivity.class);
                    intent.putExtra("story_id", storyId);
                    startActivity(intent);

                    // Hiển thị thông báo thành công
                    Toast.makeText(this, "Xóa chương thành công", Toast.LENGTH_SHORT).show();

                    // Trả về kết quả và đóng activity
                    setResult(RESULT_OK);
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}