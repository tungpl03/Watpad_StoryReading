package com.example.storywatpad.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class ChapterMenuBottomSheet extends BottomSheetDialogFragment {
    private int storyId;
    private RecyclerView rvChapters;
    private DatabaseHandler databaseHandler;

    public ChapterMenuBottomSheet(int storyId) {
        this.storyId = storyId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_chapter_list, container, false);

        TextView tvStoryTitle = view.findViewById(R.id.tvStoryTitle);
        Button btnAddLibrary = view.findViewById(R.id.btnAddLibrary);
        rvChapters = view.findViewById(R.id.rvChapterList);
        databaseHandler = new DatabaseHandler(getContext());

        // Lấy danh sách chương
        List<Chapter> chapterList = databaseHandler.getChaptersByStoryId(storyId);

        // Cài đặt Adapter
        rvChapters.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChapters.setAdapter(new ChapterListAdapter(getContext(), chapterList));

        return view;
    }

    // Adapter cho danh sách chương
    private static class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
        private Context context;
        private List<Chapter> chapterList;

        public ChapterListAdapter(Context context, List<Chapter> chapterList) {
            this.context = context;
            this.chapterList = chapterList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chapter, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Chapter chapter = chapterList.get(position);
            holder.tvChapterTitle.setText(chapter.getTitle());

            holder.itemView.setOnClickListener(v -> {
                // Mở chương được chọn
                Intent intent = new Intent(context, ChapterDetailActivity.class);
                intent.putExtra("story_id", chapter.getStoryId());
                intent.putExtra("chapter_id", chapter.getChapterId());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvChapterTitle;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvChapterTitle = itemView.findViewById(R.id.tvChapterTitle);
            }
        }
    }
}
