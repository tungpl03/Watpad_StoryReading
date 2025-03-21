package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.fragment.BioFragment;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.AuthorProfilePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthorProfileActivity extends AppCompatActivity {
    ImageView avatar;
    TextView NameText, EmailText;
    User author;
    private DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_profile);

        Init();

        // Lấy authorId từ Intent
        int authorId = getIntent().getIntExtra("author_id", -1);
        author = db.getUserById(authorId);

        avatar = findViewById(R.id.avatar);
        String userAvatar = author.getDrawableImageName();
        int imageResId = this.getResources().getIdentifier(userAvatar, "drawable", this.getPackageName());

        if (imageResId != 0) {
            avatar.setImageResource(imageResId);
        } else {
            avatar.setImageResource(R.drawable.logocomic);
        }

        NameText.setText(author.getUsername());
        EmailText.setText(author.getEmail());

        // Kiểm tra xem userId trong SharedPreferences có trùng với authorId không
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("userId", -1);

        // Nếu userId trùng với authorId, chuyển hướng sang ProfileActivity
        if (userId != -1 && userId == authorId) {
            // Chuyển đến ProfileActivity nếu trùng
            Intent intent = new Intent(AuthorProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();  // Kết thúc AuthorProfileActivity
        } else {
            // Nếu không trùng, tiếp tục xử lý bình thường
            if (authorId != -1) {
                // Khởi tạo ViewPager2 và TabLayout
                ViewPager2 viewPager = findViewById(R.id.vpAuthorProfile);
                TabLayout tabLayout = findViewById(R.id.tbAuthorProfile);

                // Thiết lập ViewPager2 với một FragmentStateAdapter
                AuthorProfilePagerAdapter adapter = new AuthorProfilePagerAdapter(this, authorId);
                viewPager.setAdapter(adapter);

                // Liên kết TabLayout với ViewPager2
                new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Bio");
                    } else {
                        tab.setText("Author's stories");
                    }
                }).attach();
            }
        }
    }

    private void Init() {
        avatar = findViewById(R.id.avatar);
        NameText = findViewById(R.id.NameText);
        EmailText = findViewById(R.id.EmailText);
    }
}
