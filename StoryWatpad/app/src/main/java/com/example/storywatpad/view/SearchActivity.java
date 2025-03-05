package com.example.storywatpad.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.StorySearchViewAdapter;
import com.example.storywatpad.view.adapter.StoryTitleAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView rvSearchListTemp, rvSearchSuggestions;
    private List<Story> storyList = new ArrayList<>();
    private List<Story> filteredList = new ArrayList<>();
    private List<Story> titleFilteredList = new ArrayList<>();

    private StorySearchViewAdapter storyAdapter;
    private StoryTitleAdapter titleAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        searchView = findViewById(R.id.searchView);
        rvSearchListTemp = findViewById(R.id.rvSearchListTemp);
        rvSearchSuggestions = findViewById(R.id.rvSearchSuggestions);

        rvSearchListTemp.setLayoutManager(new LinearLayoutManager(this));
        rvSearchSuggestions.setLayoutManager(new LinearLayoutManager(this));

        rvSearchListTemp.setVisibility(View.GONE);

//        storyList.add(new Story("1", 1, "Harry Potter", "Fantasy", 100, 200, "https://picsum.photos/200", 3));
//        storyList.add(new Story("2", 1, "Home Sweet Home", "Horror", 90, 150, "https://picsum.photos/200", 4));
//        storyList.add(new Story("3", 1, "Hunter X Hunter", "Adventure", 80, 120, "https://picsum.photos/200", 5));
//        storyList.add(new Story("4", 1, "Halo Legends", "Sci-Fi", 85, 130, "https://picsum.photos/200", 4));
//        storyList.add(new Story("5", 1, "Hellbound", "Thriller", 70, 100, "https://picsum.photos/200", 4));

        filteredList.addAll(storyList);
        titleFilteredList.addAll(storyList);

        storyAdapter = new StorySearchViewAdapter(filteredList, this);
        rvSearchListTemp.setAdapter(storyAdapter);

        titleAdapter = new StoryTitleAdapter(titleFilteredList, this, title -> {
            searchView.setQuery(title, false);
            filterList(title);
            rvSearchListTemp.setVisibility(View.VISIBLE);
            rvSearchSuggestions.setVisibility(View.GONE);
            hideKeyboard();
        });
        rvSearchSuggestions.setAdapter(titleAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                rvSearchListTemp.setVisibility(View.VISIBLE);
                rvSearchSuggestions.setVisibility(View.GONE);
                hideKeyboard();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTitleList(newText);
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                rvSearchSuggestions.setVisibility(View.GONE);
            }
        });

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterList(searchView.getQuery().toString());
                rvSearchListTemp.setVisibility(View.VISIBLE);
                rvSearchSuggestions.setVisibility(View.GONE);
                hideKeyboard();
                return true;
            }
            return false;
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    rvSearchListTemp.setVisibility(View.GONE); // Ẩn danh sách nếu không có chữ
                } else {
                    rvSearchListTemp.setVisibility(View.VISIBLE); // Hiển thị nếu có chữ
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.search) {
                // Chuyển sang SearchActivity khi bấm vào Search
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });

    }

    private void filterList(String query) {
        filteredList.clear();
        if (TextUtils.isEmpty(query)) {
            filteredList.addAll(storyList);
        } else {
            for (Story story : storyList) {
                if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(story);
                }
            }
        }
        storyAdapter.notifyDataSetChanged();
    }

    private void filterTitleList(String query) {
        titleFilteredList.clear();
        if (TextUtils.isEmpty(query)) {
            rvSearchSuggestions.setVisibility(View.GONE);
        } else {
            for (Story story : storyList) {
                if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    titleFilteredList.add(story);
                }
            }
            rvSearchSuggestions.setVisibility(View.VISIBLE);
        }
        titleAdapter.notifyDataSetChanged();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}