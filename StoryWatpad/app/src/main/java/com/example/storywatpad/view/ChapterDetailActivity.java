package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;

public class ChapterDetailActivity extends AppCompatActivity {
    private TextView tvStoryTitle, tvChapterTitle, tvChapterContent, tvViews, tvLikes, tvComments;
    private ImageView btnBack, btnMenu,imgHeart;
    private LinearLayout btnVote, btnComment;
    private ScrollView scrollView;
    private GestureDetector gestureDetector;

    private DatabaseHandler databaseHandler;
    private int userId, storyId, chapterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_detail);

        // Nhận dữ liệu từ Intent
        storyId = getIntent().getIntExtra("story_id", -1);
        chapterId = getIntent().getIntExtra("chapter_id", -1);

        // Khởi tạo DatabaseHandler
        databaseHandler = new DatabaseHandler(this);

        // Ánh xạ View
        tvStoryTitle = findViewById(R.id.tvStoryTitle);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        tvChapterContent = findViewById(R.id.tvChapterContent);
        tvViews = findViewById(R.id.tvViews);
        tvLikes = findViewById(R.id.tvLikes);
        tvComments = findViewById(R.id.tvComments);
        btnBack = findViewById(R.id.btnBack);
        btnMenu = findViewById(R.id.btnMenu);
        btnVote = findViewById(R.id.btnVote);
        btnComment = findViewById(R.id.btnComment);
        scrollView = findViewById(R.id.scrollContent);
        imgHeart = findViewById(R.id.imgHeart);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("wadpadlogin", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Bạn cần đăng nhập để đọc truyện!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Hiển thị nội dung chương
        loadChapterDetails();

        // Cập nhật ReadingHistory (View +1)
        databaseHandler.updateReadingHistory(userId, storyId, chapterId);

        // Xử lý sự kiện khi bấm nút Back
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChapterDetailActivity.this, StoryDetailActivity.class);
            intent.putExtra("story_id", storyId);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện khi bấm nút Menu (Hiện danh sách chương)
        btnMenu.setOnClickListener(v -> {
            ChapterMenuBottomSheet bottomSheet = new ChapterMenuBottomSheet(storyId);
            bottomSheet.show(getSupportFragmentManager(), "ChapterMenuBottomSheet");
        });

        // Sự kiện khi bấm nút Vote (Like)
        btnVote.setOnClickListener(v -> toggleLike());

        // Lắng nghe sự kiện vuốt để chuyển chương
        gestureDetector = new GestureDetector(this, new GestureListener());
        scrollView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Lắng nghe cuộn để phát hiện khi đến cuối hoặc đầu trang
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);

            int diffBottom = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
            int diffTop = scrollView.getScrollY();

            if (diffBottom == 0) {  // Cuộn đến cuối trang -> Sang chương tiếp theo
                goToNextChapter();
            } else if (diffTop == 0) {  // Cuộn lên đầu -> Trở lại chương trước
                goToPreviousChapter();
            }
        });
    }

    private void loadChapterDetails() {
        Chapter chapter = databaseHandler.getChapterById(storyId, chapterId);
        if (chapter != null) {
            tvChapterTitle.setText(chapter.getTitle());
            tvChapterContent.setText(chapter.getContent());

            // Kiểm tra trạng thái Like ban đầu
            boolean isLiked = databaseHandler.isChapterLiked(userId, storyId, chapterId);
            if (isLiked) {
                imgHeart.setImageResource(R.drawable.heart_fill_svgrepo_com); // Nếu đã like -> đỏ
            } else {
                imgHeart.setImageResource(R.drawable.heart_svgrepo_com); // Nếu chưa like -> mặc định
            }

            // Lấy số lượt View, Like, Comment
            int views = databaseHandler.getChapterViewCount(storyId, chapterId);
            int likes = databaseHandler.getChapterLikeCount(storyId, chapterId);
            int comments = databaseHandler.getCommentCount(storyId, chapterId);

            tvViews.setText(String.valueOf(views));
            tvLikes.setText(String.valueOf(likes));
            tvComments.setText(String.valueOf(comments));
        } else {
            Toast.makeText(this, "Không tìm thấy chương!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    // Chuyển đến chương tiếp theo
    private void goToNextChapter() {
        Chapter nextChapter = databaseHandler.getNextChapter(storyId, chapterId);
        if (nextChapter != null) {
            chapterId = nextChapter.getChapterId();
            loadChapterDetails();
            databaseHandler.updateReadingHistory(userId, storyId, chapterId);
            scrollView.scrollTo(0, 0); // Đưa về đầu chương
        } else {
            Toast.makeText(this, "Đã đến chương cuối!", Toast.LENGTH_SHORT).show();
        }
    }

    // Quay lại chương trước
    private void goToPreviousChapter() {
        Chapter previousChapter = databaseHandler.getPreviousChapter(storyId, chapterId);
        if (previousChapter != null) {
            chapterId = previousChapter.getChapterId();
            loadChapterDetails();
            databaseHandler.updateReadingHistory(userId, storyId, chapterId);
            scrollView.scrollTo(0, scrollView.getBottom()); // Đưa về cuối chương
        } else {
            Toast.makeText(this, "Đây là chương đầu tiên!", Toast.LENGTH_SHORT).show();
        }
    }

    // Lớp xử lý cử chỉ vuốt
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();

            if (diffY < -SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                goToNextChapter();
                return true;
            }
            if (diffY > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                goToPreviousChapter();
                return true;
            }
            return false;
        }
    }

    private void toggleLike() {
        boolean isLiked = databaseHandler.isChapterLiked(userId, storyId, chapterId);

        if (isLiked) {
            databaseHandler.updateLikeStatus(userId, storyId, chapterId, false);
            imgHeart.setImageResource(R.drawable.heart_svgrepo_com); // Đổi về icon mặc định
            Toast.makeText(this, "Bỏ thích chương", Toast.LENGTH_SHORT).show();
        } else {
            databaseHandler.updateLikeStatus(userId, storyId, chapterId, true);
            imgHeart.setImageResource(R.drawable.heart_fill_svgrepo_com); // Đổi thành icon trái tim đỏ
            Toast.makeText(this, "Thích chương", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật lại số like
        int likes = databaseHandler.getChapterLikeCount(storyId, chapterId);
        tvLikes.setText(String.valueOf(likes));
    }

}
