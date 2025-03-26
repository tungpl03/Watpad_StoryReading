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
import com.example.storywatpad.model.User;

import java.io.File;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

    private Context context;
    private List<User> users;
    private DatabaseHandler db;


    public AccountAdapter(Context context, List<User> users, DatabaseHandler db) {
        this.context = context;
        this.users = users;
        this.db = db;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_account_item, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtId.setText(String.valueOf(user.getUserId()));
        holder.txtName.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());
        holder.itemView.setOnClickListener(v -> showUserDialog(user));
    }

    private void showUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.account_dialog, null);
        builder.setView(dialogView);

        // Ánh xạ View
        TextView name = dialogView.findViewById(R.id.name);
        TextView email = dialogView.findViewById(R.id.email);
        TextView bio = dialogView.findViewById(R.id.bio);
        ImageView avatar = dialogView.findViewById(R.id.avatar);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        AppCompatButton btnBan = dialogView.findViewById(R.id.btnBan);
        if (user.getStatus().equals("active")) {
            btnBan.setText("Ban");
        } else {
            btnBan.setText("Unban");
        }

        String userAvatar = user.getAvatarUrl();
        if (userAvatar != null) {
            avatar.setImageURI(Uri.fromFile(new File(userAvatar)));
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }

        // Gán dữ liệu
        name.setText(user.getUsername());
        email.setText(user.getEmail());
        bio.setText(user.getBio()); // Nếu có cột bio trong database

        AlertDialog dialog = builder.create();
        dialog.show();

        // Bắt sự kiện button
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnBan.setOnClickListener(v -> {

            String newStatus = user.getStatus().equals("active") ? "banned" : "active";

                db.banAccount(newStatus, user);

            user.setStatus(newStatus);

            Toast.makeText(context, "User " + user.getUsername() + " is " + newStatus, Toast.LENGTH_SHORT).show();

            dialog.dismiss();

            // Cập nhật lại RecyclerView
            notifyDataSetChanged();
        });
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class AccountViewHolder extends RecyclerView.ViewHolder {

        TextView txtId, txtName, txtEmail;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtId);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}
