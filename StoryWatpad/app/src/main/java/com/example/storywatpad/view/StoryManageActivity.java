    package com.example.storywatpad.view;

    import android.content.SharedPreferences;
    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.appcompat.widget.AppCompatButton;
    import androidx.recyclerview.widget.GridLayoutManager;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.storywatpad.DatabaseHandler;
    import com.example.storywatpad.R;
    import com.example.storywatpad.model.Story;
    import com.example.storywatpad.model.User;
    import com.example.storywatpad.view.adapter.AccountAdapter;
    import com.example.storywatpad.view.adapter.StoryAdapter;
    import com.example.storywatpad.view.adapter.StoryManageAdapter;

    import java.util.ArrayList;
    import java.util.List;

    public class StoryManageActivity extends AppCompatActivity {

        RecyclerView storyList;
        List<Story> stories = new ArrayList<>();
        private DatabaseHandler db = new DatabaseHandler(this);
        SharedPreferences SP;
        StoryManageAdapter adapter;
        User user;
        AppCompatButton back;

        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.story_manage_layout);

            SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
            user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));

            storyList = findViewById(R.id.storyList);
            storyList.setLayoutManager(new GridLayoutManager(this,2)); // Quan trọng để hiển thị danh sách

            stories = db.getAllStoryForAd();

            adapter = new StoryManageAdapter(stories, this, db);
            storyList.setAdapter(adapter);

            back = findViewById(R.id.back);
            back.setOnClickListener(view -> {
                finish(); // Đóng Activity hiện tại để quay lại MainActivity
            });
        }
    }
