package com.example.storywatpad.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.example.storywatpad.DatabaseHandler;
import com.example.storywatpad.R;
import com.example.storywatpad.model.User;
import com.example.storywatpad.view.adapter.AuthorProfilePagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
        TextView txtFollow = findViewById(R.id.txtFollow); // Assuming you have this TextView in your layout



// Get the current userId (the user who is following/unfollowing)
        SharedPreferences sharedPreferences = getSharedPreferences("wadpadlogin", MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        // Check if the user is already following the author
        AtomicBoolean isFollowing = new AtomicBoolean(db.isFollowing(currentUserId, authorId));

        // Initialize the Follow button
        AppCompatButton btnFollow = findViewById(R.id.btnFollow);

        if (isFollowing.get()) {
            // If the user is already following, set button text to "Unfollow"
            btnFollow.setText("Unfollow");
        } else {
            // If the user is not following, set button text to "Follow"
            btnFollow.setText("Follow");
        }

        // Set the initial follower count
        AtomicInteger followersCount = new AtomicInteger(db.getFollowersCount(authorId));
        txtFollow.setText(followersCount + " followers");

        // Button click listener for Follow/Unfollow functionality
        btnFollow.setOnClickListener(v -> {
            if (isFollowing.get()) {
                // Unfollow the user
                boolean isUnfollowed = db.unfollowUser(currentUserId, authorId);

                if (isUnfollowed) {
                    btnFollow.setText("Follow");  // Change button text to "Follow"
                    followersCount.set(db.getFollowersCount(authorId)); // Update the follower count
                    txtFollow.setText(followersCount + " followers"); // Update UI
                    isFollowing.set(false);
                }
            } else {
                // Follow the user
                boolean isFollowed = db.followUser(currentUserId, authorId);

                if (isFollowed) {
                    btnFollow.setText("Unfollow");  // Change button text to "Unfollow"
                    followersCount.set(db.getFollowersCount(authorId)); // Update the follower count
                    txtFollow.setText(followersCount + " followers"); // Update UI
                    isFollowing.set(true);
                }
            }
        });





        NameText.setText(author.getUsername());
        EmailText.setText(author.getEmail());

        // Kiểm tra xem userId trong SharedPreferences có trùng với authorId không

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
    public String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
