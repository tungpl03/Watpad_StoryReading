package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.database.Cursor;
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
import com.example.storywatpad.view.MainActivity;

import java.util.List;

    public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder>{
    List<Story> arrStory;
    Context context;

    public StoryAdapter(List<Story> arrStory, Context context) {
        this.arrStory = arrStory;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = arrStory.get(position);
        holder.tvTitle.setText(story.getTitle());
//        holder.tvView.setText(String.valueOf(1));
        DatabaseHandler db = new DatabaseHandler(context);
        int viewCount = db.getViewCountForStory(story.getStory_id());
        holder.tvView.setText(String.valueOf(viewCount));
        String imageName = story.getDrawableImageName(); // Gọi phương thức mới

        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (imageResId != 0) {
            holder.tvImageCover.setImageResource(imageResId);
        } else {
            holder.tvImageCover.setImageResource(R.drawable.logocomic);
        }

    }



    @Override
    public int getItemCount() {
        return arrStory==null?0:arrStory.size();
    }


    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvView;
        ImageView tvImageCover;
        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageCover = itemView.findViewById(R.id.imageCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvView = itemView.findViewById(R.id.tvView);
        }
    }
}
