package com.example.storywatpad.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.User;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.List;

public class StoryManageAdapter extends RecyclerView.Adapter<StoryManageAdapter.StoryManageViewHolder> {


    List<Story> arrStory;
    Context context;
    private DatabaseHandler db;

    public StoryManageAdapter(List<Story> arrStory, Context context, DatabaseHandler db) {
        this.arrStory = arrStory;
        this.context = context;
        this.db = db;
    }


    @NonNull
    @Override
    public StoryManageAdapter.StoryManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_story, parent, false);
        return new StoryManageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryManageAdapter.StoryManageViewHolder holder, int position) {
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

        holder.itemView.setOnClickListener(v -> showStoryManageDialog(story));
    }

    private void showStoryManageDialog(Story story) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.story_dialog, null);
        builder.setView(dialogView);

        ImageView imagestory = dialogView.findViewById(R.id.imagestory);

        String imageName = story.getDrawableImageName(); // Gọi phương thức mới

        int imageResId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        if (imageResId != 0) {
            imagestory.setImageResource(imageResId);
        } else {
            imagestory.setImageResource(R.drawable.logocomic);
        }
        TextView title = dialogView.findViewById(R.id.title);
        ShapeableImageView avatar = dialogView.findViewById(R.id.avatar);
        TextView author = dialogView.findViewById(R.id.author);
        TextView genre = dialogView.findViewById(R.id.genre);
        TextView description = dialogView.findViewById(R.id.description);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        AppCompatButton btnHide = dialogView.findViewById(R.id.btnHide);

        if (story.isHidden()) {
            btnHide.setText("unhidden");
        } else {
            btnHide.setText("Hidden");
        }

        User user = db.getAuthor(story.getAuthor_id());
        String userAvatar = user.getAvatarUrl();
        if (userAvatar != null) {
            avatar.setImageURI(Uri.fromFile(new File(userAvatar)));
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }

        title.setText(story.getTitle());
        author.setText(user.getUsername());
        genre.setText(String.valueOf(db.getGenrebyId(story.getGenre_id())));
        description.setText(story.getDescription());
        AlertDialog dialog = builder.create();
        dialog.show();
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnHide.setOnClickListener(v -> {

            int newIsHidden = story.isHidden()? 0 : 1;
            boolean nih = !story.isHidden();
            db.hiddenStory(newIsHidden, story);
            story.setHidden(nih);
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

            dialog.dismiss();

            // Cập nhật lại RecyclerView
            notifyDataSetChanged();

        });
    }

    @Override
    public int getItemCount() {
        return arrStory.size();
    }

    public class StoryManageViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvView;
        ImageView tvImageCover;

        public StoryManageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageCover = itemView.findViewById(R.id.imageCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvView = itemView.findViewById(R.id.tvView);
        }
    }
}
