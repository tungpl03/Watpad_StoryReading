package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;

import java.util.List;

public class StoryTitleAdapter extends RecyclerView.Adapter<StoryTitleAdapter.StoryTitleViewHolder> {
    private List<Story> storyList;
    private Context context;
    private OnItemClickListener listener;

    public StoryTitleAdapter(List<Story> storyList, Context context, OnItemClickListener listener) {
        this.storyList = storyList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryTitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story_title, parent, false);
        return new StoryTitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryTitleViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(story.getTitle()));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class StoryTitleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public StoryTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvStoryTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String title);
    }
}
