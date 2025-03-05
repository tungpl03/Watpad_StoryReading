package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;

import java.util.List;

public class StorySearchViewAdapter extends RecyclerView.Adapter<StorySearchViewAdapter.StoryViewHolder> {
    private List<Story> storyList;
    private Context context;

    public StorySearchViewAdapter(List<Story> storyList, Context context) {
        this.storyList = storyList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_search, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.tvTitle.setText(story.getTitle());
//        holder.tvAuthor.setText(String.valueOf(story.getUser_id()));
//        holder.tvViews.setText(String.valueOf(story.getViewRate()));
//        holder.tvLikes.setText(String.valueOf(story.getLikeRate()));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvViews, tvLikes;
        ImageView imgStory;
        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            imgStory = itemView.findViewById(R.id.imgStory);
        }
    }
}
