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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Story> bookmarkList;
    private Context context;

    public BookmarkAdapter(List<Story> bookmarkList, Context context) {
        this.bookmarkList = bookmarkList;
        this.context = context;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookmark_story, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Story story = bookmarkList.get(position);
        holder.tvTitle.setText(story.getTitle());




        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(new Date(story.getLastReadAt()));
        holder.tvLastReadAt.setText("Đọc lần cuối: " + formattedDate);

        int imageResId = context.getResources().getIdentifier(
                story.getDrawableImageName(), "drawable", context.getPackageName());

        if (imageResId != 0) {
            holder.imgCover.setImageResource(imageResId);
        } else {
            holder.imgCover.setImageResource(R.drawable.cover1);
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvLastReadAt;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLastReadAt = itemView.findViewById(R.id.tvLastReadAt);
        }
    }
}
