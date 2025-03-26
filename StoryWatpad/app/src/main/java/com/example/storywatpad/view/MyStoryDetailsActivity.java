package com.example.storywatpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.Genre;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.ChapterAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyStoryDetailsActivity extends AppCompatActivity implements ChapterAdapter.OnItemClickListener {
    private EditText etTitle, etDescription, etTags;
    private Spinner spinnerGenre;
    private ImageView ivCoverImage;
    private Button btnUpdate,btnAddChapter;
    private RecyclerView rvChapters;
    private DatabaseHandler db;
    private List<Genre> genreList;
    private ChapterAdapter chapterAdapter;
    private int storyId;
    private Story story;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_story_details);

        // Khởi tạo views
        etTitle = findViewById(R.id.et_title_content);
        etDescription = findViewById(R.id.et_description_content);
        etTags = findViewById(R.id.et_tags_content);
        spinnerGenre = findViewById(R.id.spinner_genre);
        ivCoverImage = findViewById(R.id.iv_cover_image);
        btnUpdate = findViewById(R.id.btn_update);
        btnAddChapter= findViewById(R.id.btn_add_chapter);
        rvChapters = findViewById(R.id.rv_chapters);

        // Khởi tạo database
        db = new DatabaseHandler(this);

        // Lấy storyId từ Intent
        storyId = getIntent().getIntExtra("story_id", -1);
        if (storyId == -1) {
            Toast.makeText(this, "Không tìm thấy truyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load thông tin truyện
        loadStoryDetails();

        // Load danh sách thể loại vào Spinner
        loadGenres();

        // Thiết lập RecyclerView cho danh sách chương
        rvChapters.setLayoutManager(new LinearLayoutManager(this));

        List<Chapter> chapterList = db.getChaptersByStoryId(storyId);
        chapterAdapter = new ChapterAdapter(this, chapterList, this);
        rvChapters.setAdapter(chapterAdapter);

        // Thêm sự kiện click cho nút Add Chapter
        btnAddChapter.setOnClickListener(v -> {
            Intent intent = new Intent(MyStoryDetailsActivity.this, AddChapterActivity.class);
            intent.putExtra("story_id", storyId);
            startActivityForResult(intent, 2); // Mở AddChapterActivity và chờ kết quả
        });

        // Thêm sự kiện click cho nút Update
        btnUpdate.setOnClickListener(v -> updateStory());


        // Thiết lập RecyclerView cho danh sách chương
//        rvChapters.setLayoutManager(new LinearLayoutManager(this));
//        chapterAdapter = new ChapterAdapter(this, chapterList);
//        rvChapters.setAdapter(chapterAdapter);
    }
    private void loadStoryDetails() {
        story = db.getStoryById(storyId);
        if (story == null) {
            Toast.makeText(this, "Không tìm thấy truyện", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị thông tin truyện
        etTitle.setText(story.getTitle());
        etDescription.setText(story.getDescription());
        etTags.setText("fanfic, shatou, tondinhsa, vuongsokham"); // Giả sử tags được lưu dưới dạng text
//        if (story.getCoverImageUrl() != null && !story.getCoverImageUrl().isEmpty()) {
//            Picasso.get().load(story.getCoverImageUrl()).placeholder(R.drawable.placeholder_image).into(ivCoverImage);
//        } else {
//            ivCoverImage.setImageResource(R.drawable.placeholder_image);
//        }
    }

    private void loadGenres() {
        genreList = db.getAllGenres();
        List<String> genreNames = new ArrayList<>();
        int selectedPosition = 0;
        for (int i = 0; i < genreList.size(); i++) {
            Genre genre = genreList.get(i);
            genreNames.add(genre.getName());
            if (genre.getGenreId() == story.getGenre_id()) {
                selectedPosition = i;
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);
        spinnerGenre.setSelection(selectedPosition);
    }

    private void updateStory() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String tags = etTags.getText().toString().trim();
        int selectedGenrePosition = spinnerGenre.getSelectedItemPosition();
        int genreId = genreList.get(selectedGenrePosition).getGenreId();

        // Kiểm tra dữ liệu đầu vào
        if (title.isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề truyện");
            return;
        }
        if (description.isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả truyện");
            return;
        }

        // Cập nhật thông tin truyện
        story.setTitle(title);
        story.setDescription(description);
        story.setGenre_id(genreId);
        // Tags không được lưu vào database trong ví dụ này, bạn có thể thêm cột tags nếu cần

        // Cập nhật trong database
        db.updateStory(story);
        Intent intent = new Intent(MyStoryDetailsActivity.this, MyStoryActivity.class);
        startActivity(intent);
        // Hiển thị thông báo thành công
        Toast.makeText(this, "Cập nhật truyện thành công", Toast.LENGTH_SHORT).show();

        // Đóng activity
        finish();
    }
    @Override
    public void onItemClick(Chapter chapter) {
        Intent intent = new Intent(MyStoryDetailsActivity.this, UpdateChapterActivity.class);
        intent.putExtra("chapter_id", chapter.getChapterId());
        startActivity(intent);
    }
}