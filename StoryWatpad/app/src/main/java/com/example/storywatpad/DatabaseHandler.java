package com.example.storywatpad;

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
        int viewCount = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM ReadingHistory WHERE StoryId = ? AND [View] = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(storyId)});

        if (cursor.moveToFirst()) {
            viewCount = cursor.getInt(0);
        }
        cursor.close();
        return viewCount;
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
                    cursor.getString(8)
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
                    cursor.getString(8)
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
                    cursor.getString(8));
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
                    cursor.getString(8));
        }

        cursor.close(); // Đóng Cursor sau khi sử dụng
        return user;
    }
    public void changePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User SET PasswordHash = ? WHERE Email = ?", new String[]{newPassword, email});
        db.close();
    }

    public void upDateProfile(String email, String AvatarUrl, String bio, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE User SET AvatarUrl = ?, Bio = ?, Username = ? WHERE Email = ?", new String[]{AvatarUrl,bio,username, email});
        db.close();
    }

}
