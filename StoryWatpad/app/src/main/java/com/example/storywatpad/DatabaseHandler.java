package com.example.storywatpad;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.storywatpad.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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

}
