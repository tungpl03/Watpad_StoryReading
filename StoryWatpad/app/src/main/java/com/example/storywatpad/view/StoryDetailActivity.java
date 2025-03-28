package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Comment;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.StoryTag;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.ChapterAdapter;
import com.example.storywatpad.view.adapter.CommentAdapter;
import com.example.storywatpad.view.adapter.TagAdapter;

import java.util.ArrayList;
import java.util.List;

import xyz.schwaab.avvylib.AvatarView;

public class StoryDetailActivity extends AppCompatActivity {
    private TextView tvStoryTitle, tvStoryCategory, tvStoryDescription, tvTotalChapters, txtUserName, tvViews, tvLikes, tvParts;
    private ImageView storyView;
    private RecyclerView rvChapters, tagRecyclerView,rvComments;
    private ChapterAdapter chapterAdapter;
    private TagAdapter tagAdapter;
    private DatabaseHandler databaseHandler;
    private int storyId;
    AvatarView avatarView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText etComment;
    private Button btnSubmitComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);

        // Khai báo nút btnBack
        ImageButton btnBack = findViewById(R.id.btnBack);

        // Xử lý sự kiện khi bấm nút Back
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(StoryDetailActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Đảm bảo quay về MainActivity và không giữ lại các Activity trước đó
            startActivity(intent);
            finish();  // Kết thúc StoryDetailActivity
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
        tvStoryTitle = findViewById(R.id.txtTitle);
        tvStoryCategory = findViewById(R.id.tvStoryCategory);
        tvStoryDescription = findViewById(R.id.tvStoryDescription);
        tvTotalChapters = findViewById(R.id.tvTotalChapters);
        storyView = findViewById(R.id.storyView);
        rvChapters = findViewById(R.id.rvChapters);
        tagRecyclerView = findViewById(R.id.tag_recycler_view);
        // Khởi tạo các phần tử trong layout
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        // Set LayoutManager cho RecyclerView
        rvComments.setLayoutManager(new LinearLayoutManager(this));

        // Lấy danh sách bình luận từ cơ sở dữ liệu
        loadComments();

        // Cài đặt sự kiện cho nút gửi bình luận
        btnSubmitComment.setOnClickListener(v -> postComment());
        Button btnRead = findViewById(R.id.btnRead);

        btnRead.setOnClickListener(v -> startReading());

        // Xử lý sự kiện khi nhấn vào AvatarView hoặc tên tác giả
        avatarView.setOnClickListener(v -> openAuthorProfile());

        txtUserName.setOnClickListener(v -> openAuthorProfile());

        // Lấy dữ liệu từ SQLite
        loadStoryDetails();
        loadChapters();
        loadTags();
        loadViewLikePartCount(storyId);
        loadAuthorInfo(storyId);
    }

    // Mở AuthorProfileActivity và chuyển authorId
    private void openAuthorProfile() {
        // Lấy authorId từ storyId
        Story story = databaseHandler.getStoryById(storyId);
        if (story != null) {
            int authorId = story.getAuthor_id();
            Intent intent = new Intent(StoryDetailActivity.this, AuthorProfileActivity.class);
            intent.putExtra("story_id", story.getStory_id());
            intent.putExtra("author_id", authorId);
            startActivity(intent);
        }
    }

    private void startReading() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Bạn cần đăng nhập để đọc truyện!", Toast.LENGTH_SHORT).show();
            return;
        }

        Chapter firstChapter = databaseHandler.getFirstChapterByStoryId(storyId);
        if (firstChapter == null) {
            Toast.makeText(this, "Truyện chưa có chương nào!", Toast.LENGTH_SHORT).show();
            return;
        }

        int chapterId = firstChapter.getChapterId();

        Intent intent = new Intent(this, ChapterDetailActivity.class);
        intent.putExtra("story_id", storyId);
        intent.putExtra("chapter_id", chapterId);
        startActivity(intent);

        AppCompatButton btnLibrary = findViewById(R.id.btnLibrary);
        btnLibrary.setOnClickListener(v -> {
            Intent intent2 = new Intent(StoryDetailActivity.this, BookmarkActivity.class);
            startActivity(intent2);
        });
    }

    private void loadStoryDetails() {
        Story story = databaseHandler.getStoryById(storyId);
        if (story != null) {
            tvStoryTitle.setText(story.getTitle());
            tvStoryCategory.setText("✧･ﾟ: *✧ (" + story.getTitle() + ")");
            tvStoryDescription.setText(story.getDescription());

            int imageResId = getResources().getIdentifier(story.getDrawableImageName(), "drawable", getPackageName());
            if (imageResId != 0) {
                storyView.setImageResource(imageResId);
            } else {
                storyView.setImageResource(R.drawable.logocomic); // Ảnh mặc định
            }
        }
    }
    private ChapterAdapter.OnItemClickListener listener;
    private void loadChapters() {
        List<Chapter> chapters = databaseHandler.getChaptersByStoryId(storyId);
        tvTotalChapters.setText(chapters.size() + " parts");

        chapterAdapter = new ChapterAdapter(this, chapters,listener);
        rvChapters.setLayoutManager(new LinearLayoutManager(this));
        rvChapters.setAdapter(chapterAdapter);
    }

    private void loadTags() {
        List<StoryTag> tagList = databaseHandler.getTagsByStoryId(storyId);
        tagAdapter = new TagAdapter(this, tagList);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagRecyclerView.setAdapter(tagAdapter);
    }

    private void loadAuthorInfo(int storyId) {
        User author = databaseHandler.getUserByStoryId(storyId);
        if (author != null) {
            txtUserName.setText(author.getUsername());

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
            int viewCount = databaseHandler.getViewCountForStory(storyId);
            int likeCount = databaseHandler.getLikeCountByStoryId(storyId);
            int partCount = databaseHandler.getChaptersByStoryId(storyId).size();

            if (tvViews != null) {
                tvViews.setText(String.valueOf(viewCount));
            }

            if (tvLikes != null) {
                tvLikes.setText(String.valueOf(likeCount));
            }

            if (tvParts != null) {
                tvParts.setText(String.valueOf(partCount));
            }
        });
    }
    private void loadComments() {
        // Get the list of comments from the database using the storyId
        commentList = databaseHandler.getCommentsByStoryId(storyId);

        // Create a new list to store the comments with their replies
        List<Comment> nestedCommentList = new ArrayList<>();

        // Loop through the comments and add replies to the parent comment
        for (Comment comment : commentList) {
            if (comment.getParentCommentId() == null) {
                // If it's a parent comment, add it to the list
                nestedCommentList.add(comment);

                // Now add the replies to this comment (if any)
                for (Comment reply : commentList) {
                    if (reply.getParentCommentId() != null && reply.getParentCommentId().equals(comment.getCommentId())) {
                        nestedCommentList.add(reply);
                    }
                }
            }
        }

        // Initialize the adapter with context, nestedCommentList, and databaseHandler
        commentAdapter = new CommentAdapter(this, nestedCommentList, databaseHandler);

        // Set the adapter to RecyclerView
        rvComments.setAdapter(commentAdapter);
    }





    // Hàm xử lý khi người dùng gửi bình luận
    private void postComment() {
        String commentText = etComment.getText().toString().trim();

        // Kiểm tra nếu bình luận trống
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Comment
        Comment newComment = new Comment();
        newComment.setUserId(1); // Lấy userId từ session đăng nhập (1 là ví dụ)
        newComment.setStoryId(storyId);
        newComment.setContent(commentText);
        newComment.setParentCommentId(null); // Trường hợp bình luận gốc (parent comment)

        // Thêm bình luận vào cơ sở dữ liệu
        long commentId = databaseHandler.addComment(newComment);

        // Nếu thêm thành công, cập nhật danh sách bình luận
        if (commentId != -1) {
            newComment.setCommentId((int) commentId);
            commentList.add(newComment);
            commentAdapter.notifyItemInserted(commentList.size() - 1);
            etComment.setText(""); // Xóa nội dung trong EditText
            Toast.makeText(this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to post comment. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}

