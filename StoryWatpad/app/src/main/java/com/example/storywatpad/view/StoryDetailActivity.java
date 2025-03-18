package com.example.storywatpad.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.StoryTag;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.ChapterAdapter;
import com.example.storywatpad.view.adapter.TagAdapter;

import java.util.List;

import xyz.schwaab.avvylib.AvatarView;

public class StoryDetailActivity extends AppCompatActivity {
    private TextView tvStoryTitle, tvStoryCategory, tvStoryDescription, tvTotalChapters,txtUserName,tvViews,tvLikes,tvParts;
    private ImageView storyView;
    private RecyclerView rvChapters, tagRecyclerView;
    private ChapterAdapter chapterAdapter;
    private TagAdapter tagAdapter;
    private DatabaseHandler databaseHandler;
    private int storyId;
    AvatarView avatarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        // Khai báo nút btnBack
        ImageButton btnBack = findViewById(R.id.btnBack);

// Xử lý sự kiện khi bấm nút Back
        btnBack.setOnClickListener(view -> {
            finish(); // Đóng Activity hiện tại để quay lại MainActivity
        });

        // Nhận storyId từ Intent
        storyId = getIntent().getIntExtra("story_id", -1);

        // Khởi tạo DatabaseHandler
        databaseHandler = new DatabaseHandler(this);
        // Ánh xạ View
        txtUserName = findViewById(R.id.txtUserName);
        avatarView = findViewById(R.id.avatarView);
        tvViews = findViewById(R.id.tvViews);


        tvLikes = findViewById(R.id.tvLikes);
        tvParts = findViewById(R.id.tvParts);

        // Ánh xạ các thành phần UI
        tvStoryTitle = findViewById(R.id.txtTitle);
        tvStoryCategory = findViewById(R.id.tvStoryCategory);
        tvStoryDescription = findViewById(R.id.tvStoryDescription);
        tvTotalChapters = findViewById(R.id.tvTotalChapters);
        storyView = findViewById(R.id.storyView);
        rvChapters = findViewById(R.id.rvChapters);
        tagRecyclerView = findViewById(R.id.tag_recycler_view);

        // Lấy dữ liệu từ SQLite
        loadStoryDetails();
        loadChapters();
        loadTags();
        loadViewLikePartCount(storyId);
        loadAuthorInfo(storyId);

    }

    private void loadStoryDetails() {
        Story story = databaseHandler.getStoryById(storyId);
        if (story != null) {
            tvStoryTitle.setText(story.getTitle());
            tvStoryCategory.setText("✧･ﾟ: *✧ (" + story.getTitle() + ")");
            tvStoryDescription.setText(story.getDescription());

            // Lấy ảnh bìa từ drawable
            int imageResId = getResources().getIdentifier(story.getDrawableImageName(), "drawable", getPackageName());
            if (imageResId != 0) {
                storyView.setImageResource(imageResId);
            } else {
                storyView.setImageResource(R.drawable.logocomic); // Ảnh mặc định
            }
        }
    }

    private void loadChapters() {
        List<Chapter> chapters = databaseHandler.getChaptersByStoryId(storyId);
        tvTotalChapters.setText(chapters.size() + " parts");

        chapterAdapter = new ChapterAdapter(this, chapters);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(chapterAdapter);
    }

    private void loadTags() {
        List<StoryTag> tagList = databaseHandler.getTagsByStoryId(storyId);
        tagAdapter = new TagAdapter(this, tagList);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setAdapter(tagAdapter);
    }
    // Lấy thông tin tác giả dựa trên StoryId
    private void loadAuthorInfo(int storyId) {
        User author = databaseHandler.getUserByStoryId(storyId);
        if (author != null) {
            txtUserName.setText(author.getUsername());

            // Load ảnh avatar
            if (author.getAvatarUrl() != null && !author.getAvatarUrl().isEmpty()) {
                int imageResId = getResources().getIdentifier(author.getAvatarUrl(), "drawable", getPackageName());
                if (imageResId != 0) {
                    avatarView.setImageResource(imageResId);
                } else {
                    avatarView.setImageResource(R.drawable.anhavartar);
                }
            }
        }
    }
    private void loadViewLikePartCount(int storyId) {
        runOnUiThread(() -> {
            // Lấy dữ liệu từ SQLite
            int viewCount = databaseHandler.getViewCountForStory(storyId);
            int likeCount = databaseHandler.getLikeCountByStoryId(storyId);
            int partCount = databaseHandler.getChaptersByStoryId(storyId).size();

            Log.d("View_Test", "Views: " + viewCount);
            Log.d("Like_Test", "Likes: " + likeCount);
            Log.d("Part_Test", "Parts: " + partCount);

            // Kiểm tra nếu TextView không bị null
            if (tvViews != null) {
                tvViews.setText(String.valueOf(viewCount));
            } else {
                Log.e("Error", "tvViews is null");
            }

            if (tvLikes != null) {
                tvLikes.setText(String.valueOf(likeCount));
            } else {
                Log.e("Error", "tvLikes is null");
            }

            if (tvParts != null) {
                tvParts.setText(String.valueOf(partCount));
            } else {
                Log.e("Error", "tvParts is null");
            }
        });
    }

}
