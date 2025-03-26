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
import com.example.storywatpad.model.Story;
import com.example.storywatpad.view.MyStoryDetailsActivity;
import com.example.storywatpad.view.UpdateChapterActivity;

import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private Context context;
    private List<Chapter> chapterList;
    private OnItemClickListener listener;

    public ChapterAdapter(Context context, List<Chapter> chapterList, OnItemClickListener listener) {
        this.context = context;
        this.chapterList = chapterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.tvChapterTitle.setText(chapter.getTitle());
        holder.tvChapterDate.setText(chapter.getCreatedAt());

        // Thêm sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(chapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Chapter chapter); // Truyền đối tượng Story thay vì chỉ storyId
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle, tvChapterDate;

        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            tvChapterDate = itemView.findViewById(R.id.tvChapterDate);
        }
    }
}
