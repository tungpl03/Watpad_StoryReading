package com.example.storywatpad.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.adapter.StorySearchViewAdapter;

import java.util.List;


public class StoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private StorySearchViewAdapter adapter;
    private List<Story> storiesList;
    private int authorId;

    public StoriesFragment(int authorId) {
        this.authorId = authorId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stories, container, false);

        recyclerView = rootView.findViewById(R.id.rvStories);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lấy danh sách truyện của tác giả từ database
        DatabaseHandler dbHandler = new DatabaseHandler(getContext());
        storiesList = dbHandler.getStoriesByAuthorId(authorId);
        Log.d("StoriesFragment", "Stories list size: " + storiesList.size());

        // Tạo adapter và thiết lập cho RecyclerView
        adapter = new StorySearchViewAdapter(storiesList, getContext());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}
