package com.example.storywatpad.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.example.storywatpad.model.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.StoryAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvStory,rvHotStory;
    List<Story> arrStory = new ArrayList<>();
    List<Story> hotStories = new ArrayList<>();
    StoryAdapter adapter,hotStoryAdapter;
    private DatabaseHandler db = new DatabaseHandler(this);
    SharedPreferences SP;
    User user;
    TextView Name;
    ShapeableImageView avatarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.homepage);

        SP = getSharedPreferences("wadpadlogin", MODE_PRIVATE);//KB in a fun, not out
        user = db.getAccount(SP.getString("Email", ""), SP.getString("Password", ""));
        hotStories = getHotStories();
        arrStory = getAllStories();
        rvStory = (RecyclerView) findViewById(R.id.rvAllBook);
        rvHotStory = (RecyclerView) findViewById(R.id.rvHotStory);
        Name = findViewById(R.id.Name);
        Name.setText(user.getUsername());
        avatarView = findViewById(R.id.avatarViewhome);
        String userAvatar = user.getAvatarUrl();

        if (userAvatar != null && !userAvatar.isEmpty()) {
            avatarView.setImageURI(Uri.fromFile(new File(userAvatar)));
        } else {
            avatarView.setImageResource(R.drawable.logocomic);
        }

        avatarView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent it = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(it);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.search) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.bookList) {
                // Chuyển sang BookmarkActivity khi bấm vào Booklist
                Intent intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.write) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent1 = new Intent(MainActivity.this, MyStoryActivity.class);
                startActivity(intent1);
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
        List<Story> hotStories = new ArrayList<>();
        Cursor cursor = db.getCursor("SELECT Story.*,sum(like) as totallike" +
                " FROM Story JOIN ReadingHistory WHERE Story.StoryId = ReadingHistory.StoryId and Story.isHidden = 0" +
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

        return hotStories;
    }

    private List<Story> getAllStories() {
        List<Story> allStories = new ArrayList<>();
        Cursor cursor = db.getCursor("SELECT * FROM Story where isHidden = 0");
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
                allStories.add(st);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return allStories;
    }

//    private User getAccount(String email, String pass) {
//        Cursor cursor = db.getReadableDatabase().rawQuery(
//                "SELECT * FROM User WHERE Email = ? AND PasswordHash = ?",
//                new String[]{email, pass}
//        );
//
//        User user = null;
//
//        if (cursor.moveToFirst()) {
//            user = new User(cursor.getInt(0),
//                    cursor.getString(1),
//                    cursor.getString(2),
//                    cursor.getString(3),
//                    cursor.getString(4),
//                    cursor.getString(5),
//                    cursor.getString(6),
//                    cursor.getString(7),
//                    cursor.getString(8));
//        }
//
//        cursor.close(); // Đóng Cursor sau khi sử dụng
//        return user;
//    }

}