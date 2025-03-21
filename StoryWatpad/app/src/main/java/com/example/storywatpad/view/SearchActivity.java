package com.example.storywatpad.view;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.StorySearchViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView rvSearchResults;
    private Button btnSearch;
    private Spinner spinnerLength, spinnerLastUpdated, spinnerGenre;
    private View filterContainer;
    private StorySearchViewAdapter searchAdapter;
    private DatabaseHandler databaseHandler;
    private List<Story> searchResults;
    private List<Integer> genreIdList; // Lưu ID của thể loại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Khởi tạo DatabaseHandler
        databaseHandler = new DatabaseHandler(this);

        // Ánh xạ View
        searchView = findViewById(R.id.searchView);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerLength = findViewById(R.id.spinnerLength);
        spinnerLastUpdated = findViewById(R.id.spinnerLastUpdated);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        filterContainer = findViewById(R.id.filterContainer);

        // Ẩn danh sách & bộ lọc khi mới vào màn hình
        rvSearchResults.setVisibility(View.GONE);
        filterContainer.setVisibility(View.GONE);

        // Thiết lập RecyclerView
        searchResults = new ArrayList<>();
        searchAdapter = new StorySearchViewAdapter(searchResults, this);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        rvSearchResults.setAdapter(searchAdapter);

        // Load dữ liệu thể loại từ Database
        loadGenres();

        // Xử lý khi nhấn nút "Search"
        btnSearch.setOnClickListener(v -> {
            String query = searchView.getQuery().toString().trim();
            if (!query.isEmpty()) {
                searchStories(query);
            }
        });

        // Xử lý khi chọn bộ lọc
        AdapterView.OnItemSelectedListener filterListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };

        spinnerLength.setOnItemSelectedListener(filterListener);
        spinnerLastUpdated.setOnItemSelectedListener(filterListener);
        spinnerGenre.setOnItemSelectedListener(filterListener);
    }

    // Load danh sách thể loại từ Database
    private void loadGenres() {
        List<String> genreList = new ArrayList<>();
        genreIdList = new ArrayList<>();

        // Thêm lựa chọn "Tất cả thể loại"
        genreList.add("All Genres");
        genreIdList.add(-1);

        Cursor cursor = databaseHandler.getReadableDatabase().rawQuery("SELECT GenreId, Name FROM Genre", null);
        if (cursor.moveToFirst()) {
            do {
                genreIdList.add(cursor.getInt(0));
                genreList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);

    }

    private void searchStories(String query) {
        searchResults.clear();

        Cursor cursor = databaseHandler.getReadableDatabase().rawQuery(
                "SELECT Story.*, " +
                        "(SELECT COUNT(*) FROM Chapter WHERE Story.StoryId = Chapter.StoryId) AS ChapterCount, " +
                        "(SELECT COALESCE(SUM(ReadingHistory.[View]), 0) FROM ReadingHistory WHERE Story.StoryId = ReadingHistory.StoryId) AS ViewCount " +
                        "FROM Story " +
                        "WHERE Story.Title LIKE ? " +
                        "ORDER BY ViewCount DESC",
                new String[]{"%" + query + "%"}
        );

        if (cursor.moveToFirst()) {
            do {
                Story story = new Story(
                        cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8)
                );
                searchResults.add(story);
            } while (cursor.moveToNext());
        }

        cursor.close();
        searchAdapter.notifyDataSetChanged();

        // Hiển thị bộ lọc khi có kết quả
        if (!searchResults.isEmpty()) {
            rvSearchResults.setVisibility(View.VISIBLE);
            filterContainer.setVisibility(View.VISIBLE);
        } else {
            rvSearchResults.setVisibility(View.GONE);
            filterContainer.setVisibility(View.GONE);
        }
    }

    private void applyFilters() {
        String lengthFilter = spinnerLength.getSelectedItem().toString();
        String updateFilter = spinnerLastUpdated.getSelectedItem().toString();
        int genreId = genreIdList.get(spinnerGenre.getSelectedItemPosition()); // Lấy ID thể loại
        reloadSearchResults();  // Lấy lại tất cả câu chuyện từ Database

        List<Story> filteredResults = new ArrayList<>();
        for (Story story : searchResults) {
            boolean matches = true;

            // **Lọc theo số chương (Length Filter)**
            int chapterCount = databaseHandler.getChaptersByStoryId(story.getStory_id()).size();
            if (lengthFilter.equals("Short (1-10 chapters)") && chapterCount > 10) matches = false;
            if (lengthFilter.equals("Medium (11-50 chapters)") && (chapterCount <= 10 || chapterCount > 50)) matches = false;
            if (lengthFilter.equals("Long (51+ chapters)") && chapterCount <= 50) matches = false;

            // **Lọc theo thể loại (Genre Filter)**
            if (genreId != -1 && story.getGenre_id() != genreId) matches = false;

            // **Lọc theo ngày cập nhật gần đây (Last Updated Filter)**
            if (!updateFilter.equals("All")) {
                Cursor cursor = databaseHandler.getReadableDatabase().rawQuery(
                        "SELECT UpdatedAt FROM Story WHERE StoryId = ?", new String[]{String.valueOf(story.getStory_id())});
                if (cursor.moveToFirst()) {
                    String updatedAt = cursor.getString(0);
                    if (updatedAt != null) {
                        long daysDifference = getDaysDifference(updatedAt);
                        if (updateFilter.equals("Last 7 days") && daysDifference > 7) matches = false;
                        if (updateFilter.equals("Last 30 days") && daysDifference > 30) matches = false;
                    }
                }
                cursor.close();
            }

            // Nếu câu chuyện thỏa mãn tất cả các điều kiện lọc, thêm vào danh sách kết quả
            if (matches) filteredResults.add(story);
        }

        // Cập nhật dữ liệu cho adapter
        searchAdapter.updateData(filteredResults);
    }


    // **Hàm hỗ trợ tính số ngày từ ngày cập nhật đến hiện tại**
    private long getDaysDifference(String updatedAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date updatedDate = sdf.parse(updatedAt);
            Date currentDate = new Date();
            long difference = currentDate.getTime() - updatedDate.getTime();
            return TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
            return Long.MAX_VALUE; // Tránh lọc sai nếu gặp lỗi
        }
    }


    private void reloadSearchResults() {
        searchResults.clear();

        Cursor cursor = databaseHandler.getReadableDatabase().rawQuery(
                "SELECT Story.*, " +
                        "(SELECT COUNT(*) FROM Chapter WHERE Story.StoryId = Chapter.StoryId) AS ChapterCount, " +
                        "(SELECT COALESCE(SUM(ReadingHistory.[View]), 0) FROM ReadingHistory WHERE Story.StoryId = ReadingHistory.StoryId) AS ViewCount " +
                        "FROM Story " +
                        "ORDER BY ViewCount DESC", null
        );

        if (cursor.moveToFirst()) {
            do {
                Story story = new Story(
                        cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                        cursor.getString(6), cursor.getString(7), cursor.getString(8)
                );
                searchResults.add(story);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

}
