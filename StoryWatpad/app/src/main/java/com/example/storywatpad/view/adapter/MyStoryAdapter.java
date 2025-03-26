package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;

import java.util.List;

public class MyStoryAdapter extends RecyclerView.Adapter<MyStoryAdapter.MyStoryViewHolder> {
    private Context context;
    private List<Story> storyList;
    private String[] genreNames;
    private int[] chapterCounts;
    private OnItemClickListener listener;

    public MyStoryAdapter(Context context, List<Story> storyList, String[] genreNames, int[] chapterCounts, OnItemClickListener listener) {
        this.context = context;
        this.storyList = storyList;
        this.genreNames = genreNames;
        this.chapterCounts = chapterCounts;
        this.listener = listener;
    }

    @Override
    public MyStoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_my_stories_iteam, parent, false);
        return new MyStoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyStoryViewHolder holder, int position) {
        DatabaseHandler db = new DatabaseHandler(context);
        Story story = storyList.get(position);
        holder.titleTextView.setText(story.getTitle());
        holder.chaptersTextView.setText(chapterCounts[position] + " Chapters");
        holder.tagsTextView.setText("Genres: " + genreNames[position]);

        // Thêm sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(story); // Truyền đối tượng Story
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận xóa
            new AlertDialog.Builder(context)
                    .setTitle("Xóa Truyện")
                    .setMessage("Bạn có chắc chắn muốn xóa truyện \"" + story.getTitle() + "\" không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        // Xóa truyện và các chương liên quan
                        db.deleteStory(story.getStory_id());
                        // Xóa truyện khỏi danh sách và cập nhật RecyclerView
                        storyList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, storyList.size());
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }


    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Story story); // Truyền đối tượng Story thay vì chỉ storyId
    }

    class MyStoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, ivDelete;
        TextView titleTextView;
        TextView chaptersTextView;
        TextView tagsTextView;

        public MyStoryViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_story_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            titleTextView = itemView.findViewById(R.id.tv_story_title);
            chaptersTextView = itemView.findViewById(R.id.tv_total_chapters);
            tagsTextView = itemView.findViewById(R.id.tv_story_tags);
        }
    }
}


