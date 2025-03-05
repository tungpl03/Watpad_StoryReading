package com.example.storywatpad.view;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.ReadingHistory;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.StoryAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvStory,rvHotStory;
    List<Story> arrStory = new ArrayList<>();
    List<Story> hotStories = new ArrayList<>();
    List<ReadingHistory> readingHistories = new ArrayList<>();
    StoryAdapter adapter,hotStoryAdapter;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.homepage);

        db.DB2SDCard();

        hotStories = getHotStories();
        arrStory = getAllStories();
        rvStory = (RecyclerView) findViewById(R.id.rvAllBook);
        rvHotStory = (RecyclerView) findViewById(R.id.rvHotStory);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.search) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvHotStory.setLayoutManager(layoutManager);
        rvHotStory.setNestedScrollingEnabled(false);  // Cho phép cuộn ngang nếu danh sách dài hơn màn hình
        rvStory.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new StoryAdapter(arrStory, this);
        hotStoryAdapter = new StoryAdapter(hotStories, this);
        rvHotStory.setAdapter(hotStoryAdapter);
        rvStory.setAdapter(adapter);
    }

    private List<Story> getHotStories() {
        List<Story> allStories = new ArrayList<>();
        Cursor cursor = db.getCursor("SELECT *,sum(like) as totallike" +
                " FROM Story JOIN ReadingHistory WHERE Story.StoryId = ReadingHistory.StoryId" +
                " GROUP BY Story.StoryId" +
                " ORDER BY totallike desc;");
        if (cursor.moveToFirst()) {
            do{
                Story st = new Story(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
                hotStories.add(st);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return allStories;
    }

    private List<Story> getAllStories() {
        List<Story> hotStories = new ArrayList<>();
        Cursor cursor = db.getCursor("SELECT * FROM Story");
        if (cursor.moveToFirst()) {
            do{
                Story st = new Story(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8));
                hotStories.add(st);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return hotStories;
    }
    
}