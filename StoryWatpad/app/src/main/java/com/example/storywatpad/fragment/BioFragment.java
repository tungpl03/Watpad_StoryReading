package com.example.storywatpad.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;


public class BioFragment extends Fragment {
    private int authorId;
    private TextView tvBio;  // TextView để hiển thị Bio

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio, container, false);

        // Ánh xạ TextView
        tvBio = view.findViewById(R.id.BioText); // giả sử bạn có TextView với id tvBio trong layout

        // Nhận authorId từ Bundle
        if (getArguments() != null) {
            authorId = getArguments().getInt("author_id", -1);
        }

        // Nếu authorId hợp lệ, tiến hành lấy thông tin tác giả từ cơ sở dữ liệu
        if (authorId != -1) {
            // Lấy thông tin của tác giả từ Database
            DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
            User author = databaseHandler.getUserById(authorId); // Giả sử có phương thức này trong DatabaseHandler

            // Nếu tìm thấy tác giả, hiển thị Bio
            if (author != null) {
                String bio = author.getBio();  // Lấy Bio của tác giả từ đối tượng User
                tvBio.setText(bio);  // Hiển thị Bio lên TextView
            }
        }

        return view;
    }
}
