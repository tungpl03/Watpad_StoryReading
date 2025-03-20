package com.example.storywatpad.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.storywatpad.R;
import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.User;

import java.util.List;

public class AccountAdapter  extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>{

    private Context context;
    private List<User> users;

    public AccountAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_account_item, parent, false);
        return  new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtId.setText(String.valueOf(user.getUserId()));
        holder.txtName.setText(user.getUsername());
        holder.txtEmail.setText(user.getEmail());
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
