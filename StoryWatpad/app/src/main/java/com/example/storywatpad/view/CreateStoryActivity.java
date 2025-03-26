package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Genre;
import com.example.storywatpad.model.User;

import java.util.ArrayList;
import java.util.List;

public class CreateStoryActivity extends AppCompatActivity {
    private EditText etStoryTitle, etStoryDescription;
    private Spinner spinnerCategory;
    private Button btnAdd;
    private ImageButton btnBack;
    private DatabaseHandler db= new DatabaseHandler(this);;
    private List<Genre> genreList;
    SharedPreferences SP;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_story);

        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));
        int currentAuthorId = user.getUserId();

        // Khởi tạo views
        btnBack = findViewById(R.id.btnBack);
        etStoryTitle = findViewById(R.id.storyTitle);
        etStoryDescription = findViewById(R.id.storyDescription);
        spinnerCategory = findViewById(R.id.category);
        btnAdd = findViewById(R.id.button3);

        // Load danh sách thể loại vào Spinner
        loadCategories();

        // Thêm sự kiện click cho nút Back
        btnBack.setOnClickListener(v -> finish());

        // Thêm sự kiện click cho nút Save
        btnAdd.setOnClickListener(v -> saveStory(currentAuthorId));
    }

    private void loadCategories() {
        genreList = db.getAllGenres();
        Log.d("CreateStoryActivity", "Number of genres loaded: " + genreList.size());
        if (genreList.isEmpty()) {
            Toast.makeText(this, "Không có thể loại nào để hiển thị", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> genreNames = new ArrayList<>();
        for (Genre genre : genreList) {
            genreNames.add(genre.getName());
            Log.d("CreateStoryActivity", "Genre: " + genre.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void saveStory(int currentAuthorId) {
        String title = etStoryTitle.getText().toString().trim();
        String description = etStoryDescription.getText().toString().trim();
        int selectedGenrePosition = spinnerCategory.getSelectedItemPosition();

        // Kiểm tra dữ liệu đầu vào
        if (title.isEmpty() || title.equals("Story Title")) {
            etStoryTitle.setError("Vui lòng nhập tiêu đề truyện");
            return;
        }
        if (description.isEmpty() || description.equals("Story Description")) {
            etStoryDescription.setError("Vui lòng nhập mô tả truyện");
            return;
        }
        if (genreList.isEmpty()) {
            Toast.makeText(this, "Không có thể loại nào để chọn", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy genreId từ Spinner
        int genreId = genreList.get(selectedGenrePosition).getGenreId();

        // Thêm truyện mới vào database
        db.addStory(title, description, null, genreId, currentAuthorId); // coverImageUrl = null vì giao diện chưa có trường nhập
        Intent intent = new Intent(CreateStoryActivity.this, MyStoryActivity.class);
        startActivity(intent);
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Thêm truyện thành công", Toast.LENGTH_SHORT).show();

        // Trả về kết quả và đóng activity
        setResult(RESULT_OK);
        finish();
    }
}