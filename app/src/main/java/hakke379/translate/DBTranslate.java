package hakke379.translate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Жуэль on 24.04.2017.
 */

public class DBTranslate extends SQLiteOpenHelper {

    public static final String DB_NAME = "Translate.sqlitе3 ";

    public static final String TABLE_HISTORI = "HISTORY";
    public static final String TABLE_FAVOR = "FAVORITS";
    public static final int  DB_VARSION = 1;

    public static final String KAY_ID = "_ID";
    public static final String LANG_TO = "TEXTTO";
    public static final String LANG_FROM = "TEXTFROM";
    public static final String CODE_TO = "CODETO";
    public static final String CODE_FROM = "CODEFROM";

    public static final String FAVOR = "FAVOR";


    public DBTranslate(Context context){
        super(context, DB_NAME, null, DB_VARSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_HISTORI +
        "(" + KAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LANG_TO + " TEXT," +
                LANG_FROM + " TEXT," +
                CODE_TO + " TEXT," +
                CODE_FROM + " TEXT," +
                FAVOR + " INTEGER" + ")");

        db.execSQL("CREATE TABLE " + TABLE_FAVOR +
                "(" + KAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LANG_TO + " TEXT," +
                LANG_FROM + " TEXT," +
                CODE_TO + " TEXT," +
                CODE_FROM + " TEXT," +
                FAVOR + " INTEGER" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORI);
                onCreate(db);
    }
}
