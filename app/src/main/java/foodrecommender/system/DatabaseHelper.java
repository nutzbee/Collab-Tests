package foodrecommender.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "users";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PREGNANCY_COUNT = "pregnancy_count";
    private static final String COLUMN_GLUCOSE_COUNT = "glucose_count";
    private static final String COLUMN_BLOOD_PRESSURE = "blood_pressure";
    private static final String COLUMN_SKIN_THICKNESS = "skin_thickness";
    private static final String COLUMN_INSULIN = "insulin";
    private static final String COLUMN_BMI = "bmi";
    private static final String COLUMN_DIABETES_PEDIGREE = "diabetes_pedigree";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_TOTAL_CALORIES = "total_calories";
    private static final String COLUMN_FOOD_ALLERGY = "food_allergy";
    private static final String COLUMN_REQUIRED_NUTRIENT = "required_nutrient";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database table here
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_PREGNANCY_COUNT + " INTEGER, " +
                COLUMN_GLUCOSE_COUNT + " INTEGER, " +
                COLUMN_BLOOD_PRESSURE + " INTEGER, " +
                COLUMN_SKIN_THICKNESS + " INTEGER, " +
                COLUMN_INSULIN + " INTEGER, " +
                COLUMN_BMI + " REAL, " +
                COLUMN_DIABETES_PEDIGREE + " REAL, " +
                COLUMN_AGE + " INTEGER, " +
                COLUMN_TOTAL_CALORIES + " INTEGER, " +
                COLUMN_FOOD_ALLERGY + " TEXT, " +
                COLUMN_REQUIRED_NUTRIENT + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public long addUser(String username, String password, int pregnancyCount, int glucoseCount,
                        int bloodPressure, int skinThickness, int insulin, float bmi,
                        float diabetesPedigree, int age, int totalCalories, String foodAllergy,
                        String requiredNutrient) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the username already exists
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?",
                new String[]{username});

        if (cursor.getCount() > 0) {
            // Username already exists, return an error code or display an error message
            cursor.close();
            db.close();
            return -1;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PREGNANCY_COUNT, pregnancyCount);
        values.put(COLUMN_GLUCOSE_COUNT, glucoseCount);
        values.put(COLUMN_BLOOD_PRESSURE, bloodPressure);
        values.put(COLUMN_SKIN_THICKNESS, skinThickness);
        values.put(COLUMN_INSULIN, insulin);
        values.put(COLUMN_BMI, bmi);
        values.put(COLUMN_DIABETES_PEDIGREE, diabetesPedigree);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_TOTAL_CALORIES, totalCalories);
        values.put(COLUMN_FOOD_ALLERGY, foodAllergy);
        values.put(COLUMN_REQUIRED_NUTRIENT, requiredNutrient);

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return newRowId;
    }
}
