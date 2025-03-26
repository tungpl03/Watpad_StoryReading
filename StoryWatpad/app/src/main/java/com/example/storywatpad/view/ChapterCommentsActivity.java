package com.example.storywatpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Comment;
import com.example.storywatpad.view.adapter.ChapterCommentAdapter;
import com.example.storywatpad.view.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChapterCommentsActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnSubmitComment;
    private ImageView btnBack;
    private TextView tvChapterTitle;
    private DatabaseHandler databaseHandler;
    private int chapterId, storyId, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_comments);

        // Initialize views
        rvComments = findViewById(R.id.rvComments);
        etComment = findViewById(R.id.etComment);
        btnSubmitComment = findViewById(R.id.btnSubmitComment);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);

        // Get data from intent
        chapterId = getIntent().getIntExtra("chapter_id", -1);
        storyId = getIntent().getIntExtra("story_id", -1);

        // Initialize database handler
        databaseHandler = new DatabaseHandler(this);

        // Get user ID from SharedPreferences
        userId = getSharedPreferences("wadpadlogin", MODE_PRIVATE).getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Please log in to post a comment.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set chapter title (this can be fetched from the database if needed)
        tvChapterTitle.setText("Chapter " + chapterId); // Example, adjust as needed

        // Load comments for this chapter
        loadChapterComments();

        // Set up comment submit button
        btnSubmitComment.setOnClickListener(v -> postComment());
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChapterCommentsActivity.this, ChapterDetailActivity.class);
            intent.putExtra("story_id", storyId);
            intent.putExtra("chapter_id", chapterId);
            startActivity(intent);
            finish();
        });
    }

    private void loadChapterComments() {
        // Fetch comments for this chapter from the database
        List<Comment> comments = databaseHandler.getCommentsByChapterId(storyId, chapterId);

        // Set up the ChapterCommentAdapter with comments for the chapter
        ChapterCommentAdapter chapterCommentAdapter = new ChapterCommentAdapter(this, comments, databaseHandler);

        // Set up RecyclerView to show comments for this chapter
        rvComments.setAdapter(chapterCommentAdapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));

    }


    private void postComment() {
        String commentContent = etComment.getText().toString().trim();

        if (commentContent.isEmpty()) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Comment object
        Comment newComment = new Comment();
        newComment.setUserId(userId);
        newComment.setStoryId(storyId);
        newComment.setChapterId(chapterId);
        newComment.setContent(commentContent);
        newComment.setParentCommentId(null); // ParentCommentId is null since it's a top-level comment

        // Add comment to database
        long commentId = databaseHandler.addComment(newComment);

        if (commentId != -1) {
            // If comment is added successfully, update the RecyclerView
            newComment.setCommentId((int) commentId);
            loadChapterComments(); // Reload comments to show the new one
            etComment.setText(""); // Clear the input field
            Toast.makeText(this, "Comment posted successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to post comment. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
