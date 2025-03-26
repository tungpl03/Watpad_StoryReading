package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.MyStoryAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MyStoryActivity extends AppCompatActivity implements MyStoryAdapter.OnItemClickListener {
    private DatabaseHandler db = new DatabaseHandler(this);
    SharedPreferences SP;
    User user;
    TextView name;
    ImageView ivAddStory;
    private RecyclerView recyclerView;
    MyStoryAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_my_stories);

        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));
        int currentAuthorId = user.getUserId();

        name = findViewById(R.id.name);
        name.setText(user.getUsername());


        recyclerView = findViewById(R.id.rv_my_stories);
        ivAddStory = findViewById(R.id.iv_add_chapter);
        // Thêm sự kiện click cho ImageView dấu cộng
        ivAddStory.setOnClickListener(v -> {
            Log.d("MyStoriesActivity", "Add story button clicked");
            Intent intent = new Intent(MyStoryActivity.this, CreateStoryActivity.class);
            startActivityForResult(intent, 1); // Mở CreateStoryActivity và chờ kết quả
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadStories(currentAuthorId);

        //đoan cuoi - footer

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent = new Intent(MyStoryActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }

            if (item.getItemId() == R.id.search) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent1 = new Intent(MyStoryActivity.this, SearchActivity.class);
                startActivity(intent1);
                return true;
            }
            return false;
        });
    }

    private void loadStories(int currentAuthorId) {
        List<Story> storyList = db.getStoryForAuthor(currentAuthorId);
        int[] chapterCounts = new int[storyList.size()];
        String[] genreNames = new String[storyList.size()];

        for (int i = 0; i < storyList.size(); i++) {
            Story story = storyList.get(i);
            chapterCounts[i] = db.getChapterCount(story.getStory_id());
            genreNames[i] = db.getGenreName(story.getGenre_id());
        }

        adapter = new MyStoryAdapter(this, storyList, genreNames, chapterCounts,this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Story story) {
        // Navigate to MyStoryDetailActivity
        Intent intent = new Intent(MyStoryActivity.this, MyStoryDetailsActivity.class);
        intent.putExtra("story_id", story.getStory_id());
        startActivity(intent);
    }
}