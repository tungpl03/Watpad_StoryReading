package com.example.storywatpad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import com.example.storywatpad.model.Chapter;
import com.example.storywatpad.model.Story;
import com.example.storywatpad.model.StoryTag;
import com.example.storywatpad.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
                        cursor.getString(8));
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

}
