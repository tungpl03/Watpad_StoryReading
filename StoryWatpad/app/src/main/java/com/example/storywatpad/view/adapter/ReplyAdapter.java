package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Comment;

import java.util.List;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private List<Comment> replyList;
    private Context context;
    private DatabaseHandler databaseHandler;

    public ReplyAdapter(Context context, List<Comment> replies, DatabaseHandler databaseHandler) {
        this.context = context;
        this.replyList = replies;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReplyViewHolder holder, int position) {
        Comment comment = replyList.get(position);

        // Get the username from the database using userId
        String username = databaseHandler.getUsernameById(comment.getUserId());

        holder.tvUsername.setText(username);  // Set the username dynamically
        holder.tvCommentContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvCommentContent;

        public ReplyViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }
}
