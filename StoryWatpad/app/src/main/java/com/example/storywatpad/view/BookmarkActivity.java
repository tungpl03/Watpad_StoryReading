package com.example.storywatpad.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.BookmarkAdapter;

import java.util.List;

public class BookmarkActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        recyclerView = findViewById(R.id.recyclerViewBookmark);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);
        List<Story> bookmarks = databaseHandler.getAllBookmarks();

        if (bookmarks.isEmpty()) {
            Toast.makeText(this, "Chưa có truyện nào được lưu", Toast.LENGTH_SHORT).show();
        }

        bookmarkAdapter = new BookmarkAdapter(bookmarks, this);
        recyclerView.setAdapter(bookmarkAdapter);
    }
}

