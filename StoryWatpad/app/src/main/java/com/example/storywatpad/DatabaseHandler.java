package com.example.storywatpad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.Comment;
import com.example.storywatpad.model.Follower;
import com.example.storywatpad.model.Genre;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.StoryTag;
import com.example.storywatpad.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHandler extends SQLiteOpenHelper {
    Context dbContext;
    SQLiteDatabase db;
    static String dbPath="/data/data/com.example.storywatpad/databases/ReadingStoryDB.sqlite";
    static String dbName="ReadingStoryDB.sqlite";
    static int dbVersion=1;
    public DatabaseHandler (Context context){
        super(context,dbName,null,dbVersion);
        dbContext=context;
    }
    public void DB2SDCard() {
        try {
            File file = new File(dbPath);
            if (!file.exists()) {
                // Kiểm tra xem thư mục databases đã tồn tại chưa, nếu chưa thì tạo mới
                File dbFolder = new File(dbContext.getApplicationInfo().dataDir + "/databases/");
                if (!dbFolder.exists()) {
                    dbFolder.mkdir();
                }

                InputStream in = dbContext.getAssets().open(dbName);
                OutputStream out = new FileOutputStream(dbPath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                out.flush();
                out.close();
                in.close();
                Log.d("DB_COPY", "Database đã được sao chép thành công.");
            } else {
                Log.d("DB_COPY", "Database đã tồn tại, không cần sao chép.");
            }
        } catch (Exception e) {
            Log.e("DB_ERROR", "Lỗi sao chép database: " + e.getMessage());
        }
    }

    public Cursor getCursor(String sql){
        db=SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
        Cursor c=db.rawQuery(sql,null);
        return c;
    }
    public void execsql(String sql){
        db=SQLiteDatabase.openDatabase(dbPath,null,SQLiteDatabase.OPEN_READWRITE);
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public int getViewCountForStory(int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int totalViews = 0;

        String query = "SELECT SUM([View]) FROM ReadingHistory WHERE StoryId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            totalViews = cursor.getInt(0);
        }
        cursor.close();
        return totalViews;
    }

    public int getLikeCountByStoryId(int storyId) {
        int likeCount = 0;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT COUNT(*) FROM ReadingHistory WHERE StoryId = ? AND [Like] = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            likeCount = cursor.getInt(0); // Lấy giá trị COUNT(*)
        }

        cursor.close();
        db.close();
        return likeCount;
    }

    public List<StoryTag> getTagsByStoryId(int storyId) {
        List<StoryTag> tagList = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT t.storyTagId, t.name FROM StoryTag t " +
                "INNER JOIN StoryTagMapping stm ON t.storyTagId = stm.storyTagId " +
                "WHERE stm.storyId = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                tagList.add(new StoryTag(id, name));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tagList;
    }
    public Story getStoryById(int storyId) {
        Story story = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT * FROM Story WHERE StoryId  = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            story = new Story(
                    cursor.getInt(0),  // story_id
                    cursor.getInt(1),  // author_id
                    cursor.getString(2), // title
                    cursor.getString(3), // description
                    cursor.getString(4), // CoverImageUrl
                    cursor.getInt(5), // genre_id
                    cursor.getString(6), // status
                    cursor.getString(7), // created_at
                    cursor.getString(8)  // updated_at
            );
        }

        cursor.close();
        db.close();
        return story;
    }

    public List<Chapter> getChaptersByStoryId(int storyId) {
        List<Chapter> chapterList = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT * FROM Chapter WHERE StoryId  = ? ORDER BY CreatedAt ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            do {
                Chapter chapter = new Chapter(
                        cursor.getInt(0),  // chapterId
                        cursor.getInt(1),  // storyId
                        cursor.getString(2), // title
                        cursor.getString(3), // content
                        cursor.getString(4), // createdAt
                        cursor.getString(5)  // updatedAt
                );
                chapterList.add(chapter);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return chapterList;
    }
    public User getUserByStoryId(int storyId) {
        User user = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        // Truy vấn để lấy AuthorId từ bảng Story, sau đó lấy thông tin từ bảng User
        String query = "SELECT u.* FROM User u " +
                "JOIN Story s ON u.UserId = s.AuthorId " +
                "WHERE s.StoryId = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),  // UserId
                    cursor.getString(1), // Username
                    cursor.getString(2), // Email
                    cursor.getString(3), // PasswordHash
                    cursor.getString(4), // AvatarUrl
                    cursor.getString(5), // Bio
                    cursor.getString(6), // Role
                    cursor.getString(7),  // CreatedAt
                    cursor.getString(8),
                    cursor.getString(9)
            );
        }

        cursor.close();
        db.close();
        return user;
    }

    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT * FROM User WHERE UserId = ?";  // Truy vấn User theo UserId
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),  // UserId
                    cursor.getString(1), // Username
                    cursor.getString(2), // Email
                    cursor.getString(3), // PasswordHash
                    cursor.getString(4), // AvatarUrl
                    cursor.getString(5), // Bio
                    cursor.getString(6), // Role
                    cursor.getString(7), // CreatedAt
                    cursor.getString(8),
                    cursor.getString(9)
            );
        }

        cursor.close();
        db.close();
        return user;
    }


    public User getAccount(String email, String pass) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM User WHERE Email = ? AND PasswordHash = ?",
                new String[]{email, pass}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9));
        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return user;
    }
    public User getAccountWithEmail(String email) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM User WHERE Email = ?",
                new String[]{email}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9));
        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return user;
    }
    public void changePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User SET PasswordHash = ? WHERE Email = ?", new String[]{newPassword, email});
        db.close();
    }
    public Chapter getFirstChapterByStoryId(int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Chapter chapter = null;

        String query = "SELECT * FROM Chapter WHERE StoryId = ? ORDER BY ChapterId ASC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            chapter = new Chapter(
                    cursor.getInt(0),   // ChapterId
                    cursor.getInt(1),   // StoryId
                    cursor.getString(2),// Title
                    cursor.getString(3) // Content
            );
        }
        cursor.close();
        db.close();
        return chapter;
    }
    public void updateReadingHistory(int userId, int storyId, int chapterId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem đã có bản ghi chưa
        String query = "SELECT [View] FROM ReadingHistory WHERE UserId = ? AND StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            // Nếu đã tồn tại, cập nhật View +1
            int currentView = cursor.getInt(0);
            ContentValues values = new ContentValues();
            values.put("View", currentView + 1);
            values.put("LastReadAt", System.currentTimeMillis()); // Cập nhật thời gian đọc gần nhất

            db.update("ReadingHistory", values, "UserId = ? AND StoryId = ? AND ChapterId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(storyId), String.valueOf(chapterId)});
        } else {
            // Nếu chưa có, tạo mới
            ContentValues values = new ContentValues();
            values.put("UserId", userId);
            values.put("StoryId", storyId);
            values.put("ChapterId", chapterId);
            values.put("Like", 0);
            values.put("View", 1); // Lần đầu đọc -> View = 1
            values.put("LastReadAt", System.currentTimeMillis());

            db.insert("ReadingHistory", null, values);
        }

        cursor.close();
        db.close();
    }

    public Chapter getChapterById(int storyId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Chapter chapter = null;

        String query = "SELECT * FROM Chapter WHERE StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            chapter = new Chapter(
                    cursor.getInt(0),   // ChapterId
                    cursor.getInt(1),   // StoryId
                    cursor.getString(2),// Title
                    cursor.getString(3) // Content
            );
        }
        cursor.close();
        db.close();
        return chapter;
    }
    public Chapter getChapterById(int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Chapter WHERE ChapterId = ?", new String[]{String.valueOf(chapterId)});
        Chapter chapter = null;
        if (cursor.moveToFirst()) {
            int storyId = cursor.getInt(cursor.getColumnIndexOrThrow("StoryId"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("Title"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("Content"));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("CreatedAt"));
            String updatedAt = cursor.getString(cursor.getColumnIndexOrThrow("UpdatedAt"));
            chapter = new Chapter(chapterId, storyId, title, content, createdAt, updatedAt);
        }
        cursor.close();
        db.close();
        return chapter;
    }
    public String getStoryTitleById(int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String title = "";

        String query = "SELECT Title FROM Story WHERE StoryId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            title = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return title;
    }
    public int getChapterViewCount(int storyId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int chapterViews = 0;

        String query = "SELECT COALESCE(SUM([View]), 0) FROM ReadingHistory WHERE StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            chapterViews = cursor.getInt(0);
        }
        cursor.close();
        return chapterViews;
    }


    public int getChapterLikeCount(int storyId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int likes = 0;

        String query = "SELECT COUNT(*) FROM ReadingHistory WHERE StoryId = ? AND ChapterId = ? AND [Like] = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            likes = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return likes;
    }
    public boolean isChapterLiked(int userId, int storyId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean isLiked = false;

        String query = "SELECT [Like] FROM ReadingHistory WHERE UserId = ? AND StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            isLiked = cursor.getInt(0) == 1;
        }
        cursor.close();
        db.close();
        return isLiked;
    }
    public int getCommentCount(int storyId, int chapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;

        String query = "SELECT COUNT(*) FROM Comment WHERE StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public void updateLikeStatus(int userId, int storyId, int chapterId, boolean isLiked) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem bản ghi đã tồn tại trong ReadingHistory chưa
        String checkQuery = "SELECT * FROM ReadingHistory WHERE UserId = ? AND StoryId = ? AND ChapterId = ?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(userId), String.valueOf(storyId), String.valueOf(chapterId)});

        if (cursor.moveToFirst()) {
            // Nếu đã tồn tại, cập nhật Like (1 hoặc 0)
            ContentValues values = new ContentValues();
            values.put("Like", isLiked ? 1 : 0);

            db.update("ReadingHistory", values, "UserId = ? AND StoryId = ? AND ChapterId = ?",
                    new String[]{String.valueOf(userId), String.valueOf(storyId), String.valueOf(chapterId)});
        } else {
            // Nếu chưa tồn tại, tạo bản ghi mới trong ReadingHistory
            ContentValues values = new ContentValues();
            values.put("UserId", userId);
            values.put("StoryId", storyId);
            values.put("ChapterId", chapterId);
            values.put("View", 1); // Lượt xem mặc định là 1 khi tạo mới
            values.put("Like", isLiked ? 1 : 0);

            db.insert("ReadingHistory", null, values);
        }

        cursor.close();
        db.close();
    }
    // Lấy chương tiếp theo
    public Chapter getNextChapter(int storyId, int currentChapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Chapter WHERE StoryId = ? AND ChapterId > ? ORDER BY ChapterId ASC LIMIT 1",
                new String[]{String.valueOf(storyId), String.valueOf(currentChapterId)});

        if (cursor.moveToFirst()) {
            Chapter chapter = new Chapter(cursor.getInt(0), cursor.getInt(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));
            cursor.close();
            return chapter;
        }

        cursor.close();
        return null;
    }

    // Lấy chương trước đó
    public Chapter getPreviousChapter(int storyId, int currentChapterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Chapter WHERE StoryId = ? AND ChapterId < ? ORDER BY ChapterId DESC LIMIT 1",
                new String[]{String.valueOf(storyId), String.valueOf(currentChapterId)});

        if (cursor.moveToFirst()) {
            Chapter chapter = new Chapter(cursor.getInt(0), cursor.getInt(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));
            cursor.close();
            return chapter;
        }

        cursor.close();
        return null;
    }
    public List<Story> getStoriesByAuthorId(int authorId) {
        List<Story> storyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();  // Lấy đối tượng SQLiteDatabase

        // Truy vấn SQL để lấy tất cả các câu chuyện theo authorId
        Cursor cursor = db.rawQuery(
                "SELECT * FROM Story WHERE AuthorId = ?",
                new String[]{String.valueOf(authorId)});  // Truyền authorId vào câu truy vấn

        // Kiểm tra nếu có kết quả
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Tạo đối tượng Story và thêm vào danh sách
                Story story = new Story(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                storyList.add(story);  // Thêm truyện vào danh sách
            } while (cursor.moveToNext());  // Tiến đến truy vấn tiếp theo
        }

        // Đóng con trỏ sau khi sử dụng xong
        if (cursor != null) {
            cursor.close();
        }

        // Trả về danh sách các truyện
        return storyList;
    }


    public void upDateProfile(String email, String AvatarUrl, String bio, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User SET AvatarUrl = ?, Bio = ?, Username = ? WHERE Email = ?", new String[]{AvatarUrl,bio,username, email});
        db.close();
    }

    public void banAccount(String newStatus, User user){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User SET status = ? WHERE UserId = ?", new String[]{newStatus, String.valueOf(user.getUserId())});
        db.close();
    }

    public List<User> getAllUser() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        Cursor cursor = getCursor("SELECT * FROM User WHERE Role = 'user'");

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getString(9)
                );
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }
    public List<Story> getAllBookmarks() {
        List<Story> bookmarks = new ArrayList<>();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);

        String query = "SELECT s.*, " +
                "       COALESCE(rh.LastReadAt, 0) AS LastReadAt " +
                "FROM Story s " +
                "JOIN Bookmark b ON s.StoryId = b.StoryId " +
                "LEFT JOIN ReadingHistory rh ON s.StoryId = rh.StoryId AND rh.UserId = b.UserId";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Story story = new Story(
                        cursor.getInt(0),  // story_id
                        cursor.getInt(1),  // author_id
                        cursor.getString(2), // title
                        cursor.getString(3), // description
                        cursor.getString(4), // CoverImageUrl
                        cursor.getInt(5),    // genre_id
                        cursor.getString(6), // status
                        cursor.getString(7), // created_at
                        cursor.getString(8)  // updated_at
                );

                story.setLastReadAt(cursor.getLong(9));  // LastReadAt từ ReadingHistory
                bookmarks.add(story);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return bookmarks;
    }

    public List<Story> getAllStoryForAd() {
        List<Story> allStories = new ArrayList<>();
        Cursor cursor = getCursor("SELECT * FROM Story");
        if (cursor.moveToFirst()) {
            do{
                Story st = new Story(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8),
                        cursor.getInt(9) == 1);
                allStories.add(st);
            }while (cursor.moveToNext());
        }
        cursor.close();

        return allStories;


}
public List<Story> getStoryForAuthor(int id) {
    List<Story> allStories = new ArrayList<>();
    Cursor cursor = this.getReadableDatabase().rawQuery(
            "SELECT * FROM Story WHERE AuthorId = ? and isHidden = 0",
            new String[]{String.valueOf(id)}
    );
    if (cursor.moveToFirst()) {
        do{
            Story st = new Story(cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    Boolean.parseBoolean(cursor.getString(9)));
            allStories.add(st);
        }while (cursor.moveToNext());
    }
    cursor.close();

    return allStories;


}
    // Method to follow a user
    public boolean followUser(int followerId, int followingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the user is already following
        Cursor cursor = db.rawQuery("SELECT * FROM Follower WHERE FollowerId = ? AND FollowingId = ?",
                new String[]{String.valueOf(followerId), String.valueOf(followingId)});
        if (cursor.getCount() > 0) {
            // If the user is already following, return false
            cursor.close();
            return false;
        }

        // Insert the new follow relationship
        ContentValues values = new ContentValues();
        values.put("FollowerId", followerId);
        values.put("FollowingId", followingId);
        values.put("CreatedAt", getCurrentDateTime()); // You can use a helper method to get current datetime
        db.insert("Follower", null, values);
        cursor.close();
        return true;
    }
    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    // Check if the user is already following the author
    public boolean isFollowing(int followerId, int followingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Follower WHERE FollowerId = ? AND FollowingId = ?",
                new String[]{String.valueOf(followerId), String.valueOf(followingId)});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    // Add a new follower
    public void addFollower(Follower follower) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FollowerId", follower.getFollowerId());
        values.put("FollowingId", follower.getFollowingId());
        values.put("CreatedAt", follower.getCreatedAt());
        db.insert("Follower", null, values);
    }

    // Remove a follower
    public boolean unfollowUser(int currentUserId, int authorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("Follower", "FollowerId = ? AND FollowingId = ?",
                new String[]{String.valueOf(currentUserId), String.valueOf(authorId)});
        return rowsDeleted > 0;
    }

    // Method to get the number of followers for a user
    public int getFollowersCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Follower WHERE FollowingId = ?", new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }


    public User getAuthor(int userId) {
        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM User WHERE UserId = ? ",
                new String[]{String.valueOf(userId)}
        );

        User user = null;

        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7),
                    cursor.getString(8),
                    cursor.getString(9));
        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return user;
    }

    public void hiddenStory(int newIsHidden, Story story) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE Story SET isHidden = ? WHERE StoryId = ?", new String[]{String.valueOf(newIsHidden), String.valueOf(story.getStory_id())});
        db.close();
    }

    public String getGenrebyId(int genreId) {

        Cursor cursor = this.getReadableDatabase().rawQuery(
                "SELECT * FROM Genre WHERE GenreId = ? ",
                new String[]{String.valueOf(genreId)}
        );

        Genre g = null;

        if (cursor.moveToFirst()) {
            g = new Genre(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
                    );

        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return g.getName();
    }
    public long addComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ChapterId", comment.getChapterId());
        values.put("UserId", comment.getUserId());
        values.put("StoryId", comment.getStoryId());
        values.put("ParentCommentId", comment.getParentCommentId());
        values.put("Content", comment.getContent());
        values.put("CreatedAt", System.currentTimeMillis());  // Lưu thời gian tạo

        // Thêm bình luận vào cơ sở dữ liệu
        long commentId = db.insert("Comment", null, values);
        db.close();
        return commentId;
    }


    public List<Comment> getCommentsByStoryId(int storyId) {
        List<Comment> commentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM Comment WHERE StoryId = ? AND ChapterId IS NULL ORDER BY CreatedAt DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment();
                comment.setCommentId(cursor.getInt(0));
                comment.setUserId(cursor.getInt(1));
                comment.setStoryId(cursor.getInt(3));
                comment.setParentCommentId(cursor.isNull(4) ? null : cursor.getInt(4));
                comment.setContent(cursor.getString(5));
                comment.setCreatedAt(cursor.getString(6));

                commentList.add(comment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return commentList;
    }
    public String getUsernameById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String username = "";

        String query = "SELECT Username FROM User WHERE userId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            username = cursor.getString(0);
        }
        cursor.close();
        return username;
    }
    public long insertReplyComment(Comment comment) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Get the current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues contentValues = new ContentValues();
        contentValues.put("UserId", comment.getUserId());
        contentValues.put("ChapterId", comment.getChapterId());
        contentValues.put("StoryId", comment.getStoryId());
        contentValues.put("ParentCommentId", comment.getParentCommentId());  // This is the ParentCommentId
        contentValues.put("Content", comment.getContent());
        contentValues.put("CreatedAt", timestamp);
        contentValues.put("UpdatedAt", timestamp);  // assuming it's the same for both

        // Insert the comment and get the ID of the inserted row
        long replyId = db.insert("Comment", null, contentValues);

        // Close the database connection
        db.close();

        // Return the replyId
        return replyId;
    }


    public List<Comment> getRepliesForComment(int parentCommentId) {
        List<Comment> replies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to get replies based on parentCommentId
        String query = "SELECT * FROM Comment WHERE ParentCommentId = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(parentCommentId)});

        if (cursor.moveToFirst()) {
            do {
                // Create a new Comment object from the cursor
                Comment reply = new Comment();
                reply.setCommentId(cursor.getInt(0));
                reply.setUserId(cursor.getInt(1));
                reply.setChapterId(cursor.getInt(2));
                reply.setStoryId(cursor.getInt(3));
                reply.setParentCommentId(cursor.getInt(4));
                reply.setContent(cursor.getString(5));
                reply.setCreatedAt(cursor.getString(6));
                reply.setUpdatedAt(cursor.getString(7));

                replies.add(reply);  // Add the reply to the list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return replies;
    }

    public List<Comment> getCommentsByChapterId(int storyId, int chapterId) {
        List<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("Comment", null, "StoryId = ? AND ChapterId = ?",
                new String[]{String.valueOf(storyId), String.valueOf(chapterId)},
                null, null, "CreatedAt ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Comment comment = new Comment();
                comment.setCommentId(cursor.getInt(0));
                comment.setUserId(cursor.getInt(1));
                comment.setStoryId(cursor.getInt(3));
                comment.setChapterId(cursor.getInt(2));
                comment.setParentCommentId(cursor.isNull(4) ? null : cursor.getInt(4));
                comment.setContent(cursor.getString(5));
                comment.setCreatedAt(cursor.getString(6));
                comment.setUpdatedAt(cursor.getString(7));
                comments.add(comment);
            }
            cursor.close();
        }
        return comments;
    }

    public void deleteStory(int storyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa các chương liên quan trước
        db.delete("chapter", "StoryId = ?", new String[]{String.valueOf(storyId)});
        // Xóa truyện
        db.delete("story", "StoryId = ?", new String[]{String.valueOf(storyId)});
        db.close();
    }

    public void addStory(String title, String description, String coverImageUrl, int genreId, int authorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("coverimageurl", coverImageUrl);
        values.put("genreid", genreId);
        values.put("authorid", authorId);
        db.insert("story", null, values);
        db.close();
    }

    // Cập nhật truyện
    public void updateStory(Story story) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", story.getTitle());
        values.put("description", story.getDescription());
        values.put("coverimageurl", story.getCoverImageUrl());
        values.put("genreid", story.getGenre_id());
        values.put("authorid", story.getAuthor_id());
        db.update("story", values, "storyid = ?", new String[]{String.valueOf(story.getStory_id())});
        db.close();
    }

    public List<Genre> getAllGenres() {
        List<Genre> genreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Genre", null);
        if (cursor.moveToFirst()) {
            do {
                int genreId = cursor.getInt(cursor.getColumnIndexOrThrow("GenreId"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                genreList.add(new Genre(genreId, name,null));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return genreList;
    }

    // Thêm chương mới
    public void addChapter(int storyId, String title, String content, String createdAt, String updatedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("storyid", storyId);
        values.put("title", title);
        values.put("content", content);
        values.put("createdat", createdAt);
        values.put("updatedat", updatedAt);
        db.insert("chapter", null, values);
        db.close();
    }
    // Cập nhật chương
    public void updateChapter(Chapter chapter) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("StoryId", chapter.getStoryId());
        values.put("Title", chapter.getTitle());
        values.put("Content", chapter.getContent());
        values.put("CreatedAt", chapter.getCreatedAt());
        values.put("UpdatedAt", chapter.getUpdatedAt());
        db.update("Chapter", values, "ChapterId = ?", new String[]{String.valueOf(chapter.getChapterId())});
        db.close();
    }

    // Xóa chương
    public void deleteChapter(int chapterId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Chapter", "ChapterId = ?", new String[]{String.valueOf(chapterId)});
        db.close();
    }
    public int getChapterCount(int storyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(1) FROM Chapter WHERE StoryId = ?",
                new String[]{String.valueOf(storyId)}
        );
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public String getGenreName(int genreId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT Name  FROM  Genre WHERE  GenreId = ?",
                new String[]{String.valueOf(genreId)}
        );
        String genreName = "Unknown";
        if (cursor.moveToFirst()) {
            genreName = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return genreName;
    }


}
