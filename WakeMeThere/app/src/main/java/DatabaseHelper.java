import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "Alarm.db";
    public static String TABLE_NAME = "alarmdata";

    public static String COL_ID = "id";
    public static String COL_NAME = "name";
    public static String COL_LATLNG = "latlng";
    public static String COL_DIST = "distance";
    public static String COL_ISENABLED = "isenaled";
    public static String COL_MESSAGE = "message";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT , name TEXT , latlng TEXT , distance INTEGER, isenabled INTEGER, message TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertDataToTable(String name, String latlng, long distance, int isEnabled, String mesage){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME,name);
        contentValues.put(COL_LATLNG,latlng);
        contentValues.put(COL_DIST,distance);
        contentValues.put(COL_ISENABLED,isEnabled);
        contentValues.put(COL_MESSAGE,mesage);
        long status =  db.insert(TABLE_NAME, null, contentValues);
        return  status != -1;
    }
}
