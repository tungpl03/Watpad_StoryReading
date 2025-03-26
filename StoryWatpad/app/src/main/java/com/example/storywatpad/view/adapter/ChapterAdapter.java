package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.view.ChapterDetailActivity;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private Context context;
    private List<Chapter> chapters;

    public ChapterAdapter(Context context, List<Chapter> chapters) {
        this.context = context;
        this.chapters = chapters;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapters.get(position);
        holder.tvChapterTitle.setText(chapter.getTitle());
        holder.tvChapterDate.setText(chapter.getCreatedAt());

        // Thêm sự kiện click vào từng chapter
        holder.itemView.setOnClickListener(v -> {
            // Khi nhấn vào một phần tử, chuyển sang ChapterDetailActivity
            Intent intent = new Intent(context, ChapterDetailActivity.class);
            intent.putExtra("story_id", chapter.getStoryId());
            intent.putExtra("chapter_id", chapter.getChapterId());  // Gửi chapterId
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle, tvChapterDate;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvChapterDate = itemView.findViewById(R.id.tvChapterDate);
        }
    }
}
