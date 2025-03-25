package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;
    private DatabaseHandler databaseHandler;

    public CommentAdapter(Context context, List<Comment> comments, DatabaseHandler databaseHandler) {
        this.context = context;
        this.commentList = comments;
        this.databaseHandler = databaseHandler;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        // Get the top-level comment at this position
        Comment comment = commentList.get(position);

        // Filter out only top-level comments (where ParentCommentId is null)
        if (comment.getParentCommentId() != null) {
            // Skip rendering for replies, only render top-level comments
            return;
        }

        // Get the username from the database using userId
        String username = databaseHandler.getUsernameById(comment.getUserId());
        holder.tvUsername.setText(username);  // Set the username dynamically
        holder.tvCommentContent.setText(comment.getContent());

        // Set up the reply button to show the reply input field
        holder.btnReply.setOnClickListener(v -> {
            holder.replyLayout.setVisibility(View.VISIBLE);
            holder.tvReplyToUser.setText("Replying to: " + username);
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences("wadpadlogin", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1); // Get userId, default to -1 if not found

        // Set up the reply button to post replies
        holder.btnSubmitReply.setOnClickListener(v -> {
            String replyContent = holder.etReplyContent.getText().toString();
            if (!replyContent.isEmpty()) {
                // Create a new reply and save it to the database
                Comment reply = new Comment(
                        userId,     // User ID from session or context (replace with appropriate user ID)
                        comment.getChapterId(),
                        comment.getStoryId(),
                        comment.getCommentId(),  // Parent comment ID, correctly setting it to the current comment's ID
                        replyContent,
                        getCurrentTimestamp(),
                        getCurrentTimestamp()
                );

                // Insert the reply into the database
                long replyId = databaseHandler.insertReplyComment(reply);

//                // Manually add the reply to the in-memory list
//                commentList.add(reply);  // Add the reply to the main list of comments

                // Notify the adapter to update the nested RecyclerView
                notifyItemChanged(position);  // Refresh the parent comment item to show the new reply

                // Hide the reply input field
                holder.replyLayout.setVisibility(View.GONE);
            }
        });

        // Set up nested RecyclerView for replies (if any)
        List<Comment> replies = getRepliesForComment(comment);
        if (!replies.isEmpty()) {
            // Set up nested RecyclerView for replies
            ReplyAdapter replyAdapter = new ReplyAdapter(context, replies, databaseHandler);
            holder.rvReplies.setLayoutManager(new LinearLayoutManager(context));
            holder.rvReplies.setAdapter(replyAdapter);
            holder.rvReplies.setVisibility(View.VISIBLE);  // Show the replies RecyclerView
        } else {
            holder.rvReplies.setVisibility(View.GONE); // Hide if there are no replies
        }
    }



    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvCommentContent, btnReply, tvReplyToUser;
        AppCompatEditText etReplyContent;
        AppCompatButton btnSubmitReply;
        LinearLayout replyLayout;
        RecyclerView rvReplies;

        public CommentViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            btnReply = itemView.findViewById(R.id.btnReply);
            tvReplyToUser = itemView.findViewById(R.id.tvReplyToUser);
            etReplyContent = itemView.findViewById(R.id.etReplyContent);
            btnSubmitReply = itemView.findViewById(R.id.btnSubmitReply);
            replyLayout = itemView.findViewById(R.id.replyLayout);
            rvReplies = itemView.findViewById(R.id.rvReplies); // Nested RecyclerView for replies
        }
    }

    private List<Comment> getRepliesForComment(Comment parentComment) {
        // Query the database for replies where parentCommentId equals the parent comment's ID
        return databaseHandler.getRepliesForComment(parentComment.getCommentId());
    }


    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
