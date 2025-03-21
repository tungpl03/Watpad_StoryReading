package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.User;

import java.util.List;

public class StorySearchViewAdapter extends RecyclerView.Adapter<StorySearchViewAdapter.StoryViewHolder> {
    private List<Story> storyList;
    private Context context;
    private DatabaseHandler databaseHandler;

    public StorySearchViewAdapter(List<Story> storyList, Context context) {
        this.storyList = storyList;
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
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

        // Set tiêu đề truyện
        holder.tvTitle.setText(story.getTitle());

        // Lấy tên tác giả từ UserId
        User author = databaseHandler.getUserById(story.getAuthor_id());
        if (author != null) {
            holder.tvAuthor.setText(author.getUsername());
        } else {
            holder.tvAuthor.setText("Unknown Author");
        }

        // Set mô tả truyện
        holder.tvDescription.setText(story.getDescription());

        // Lấy số lượt xem
        int viewCount = databaseHandler.getViewCountForStory(story.getStory_id());
        holder.tvViews.setText(viewCount + " Reads");

        // Lấy số lượt thích
        int likeCount = databaseHandler.getLikeCountByStoryId(story.getStory_id());
        holder.tvLikes.setText(likeCount + " Votes");

        // Lấy số chương
        int chapterCount = databaseHandler.getChaptersByStoryId(story.getStory_id()).size();
        holder.tvParts.setText(chapterCount + " Parts");

        // Hiển thị ảnh bìa truyện từ drawable (nếu có)
        int imageResId = context.getResources().getIdentifier(
                story.getDrawableImageName(), "drawable", context.getPackageName());
        if (imageResId != 0) {
            holder.imgStory.setImageResource(imageResId);
        } else {
            holder.imgStory.setImageResource(R.drawable.logocomic); // Ảnh mặc định
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }
    public void updateData(List<Story> newStories) {
        this.storyList.clear();
        this.storyList.addAll(newStories);
        notifyDataSetChanged();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvViews, tvLikes, tvParts, tvDescription;
        ImageView imgStory;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvParts = itemView.findViewById(R.id.tvParts);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgStory = itemView.findViewById(R.id.imgStory);
        }
    }
}
