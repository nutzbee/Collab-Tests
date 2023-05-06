package foodrecommender.system;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UserDAO {
    private DatabaseHelper databaseHelper;

    public UserDAO(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public ArrayList<User> getUsers(String username) {
        ArrayList<User> userList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME +
                " WHERE " + DatabaseHelper.COLUMN_USERNAME + " = ?", new String[]{username});

        // Retrieve column values using column indexes or column names
        int usernameIndex  = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
        int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
        int pregnancyCountIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PREGNANCY_COUNT);

        if (cursor.moveToFirst()) {
            do {
                // Retrieve column values using column indexes
                String retrievedUsername = cursor.getString(usernameIndex);
                String password = cursor.getString(passwordIndex);
                int pregnancyCount = cursor.getInt(pregnancyCountIndex);
                // Retrieve other columns as needed

                // Create a User object with the retrieved values
                User user = new User(retrievedUsername , password, pregnancyCount);
                // Set other retrieved values in the User object as needed

                // Add the User object to the userList
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }

    public ArrayList<String> getUsernames(String username) {
        ArrayList<String> usernameList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_USERNAME + " FROM " + DatabaseHelper.TABLE_NAME +
                " WHERE " + DatabaseHelper.COLUMN_USERNAME + " = ?", new String[]{username});
        int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
        if (cursor.moveToFirst()) {
            do {
                String retrievedUsername = cursor.getString(usernameIndex);
                usernameList.add(retrievedUsername);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usernameList;
    }

    public ArrayList<String> getPasswords(String username) {
        ArrayList<String> passwordList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_PASSWORD + " FROM " + DatabaseHelper.TABLE_NAME+
                " WHERE " + DatabaseHelper.COLUMN_USERNAME + " = ?", new String[]{username});
        int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
        if (cursor.moveToFirst()) {
            do {
                String password = cursor.getString(passwordIndex);
                passwordList.add(password);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return passwordList;
    }
}
