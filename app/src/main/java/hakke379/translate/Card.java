package hakke379.translate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.orm.SugarRecord;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Жуэль on 19.04.2017.
 */

// Класс реализует работу с избранными преводами
class Card{
    int id;
    String longFrom;
    String longTo;
    String textFrom;
    String textTo;
    public IOException error;
    // Конструкторы класса для разных ситуаций

    public Card(String longTo, String textFrom){
        this.longTo = longTo;
        this.textFrom = textFrom;
        error = null;
        id = -1;
    }
    public Card(String longFrom, String longTo, String textFrom){
        this(longTo, textFrom);
        this.longFrom = longFrom;
    }
    public Card(String longFrom, String longTo, String textFrom, String textTo){
        this(longFrom, longTo, textFrom);
        this.textTo = textTo;
    }
    public Card(String longFrom, String longTo, String textFrom, String textTo, int id) {
        this(longFrom, longTo, textFrom, textTo);
        this.id = id;
    }

    public void Translate(){
        try {
            textTo = YandexTranslate.Translate(longFrom, longTo, textFrom);
        } catch (IOException e) {
            error = e;
        }
    }

    // Добавить в избранное
    public void Favorites(Context context){
        DBTranslate dbTranslate = new DBTranslate(context);
        SQLiteDatabase database = dbTranslate.getWritableDatabase();
        ContentValues contentValues = getContent();
        database.insert(DBTranslate.TABLE_FAVOR, null, contentValues);
        Cursor cursor = database.query(DBTranslate.TABLE_FAVOR, null, null, null, null, null, null);
        cursor.moveToLast();
        id = cursor.getInt(cursor.getColumnIndex(DBTranslate.KAY_ID));
        database.close();
        dbTranslate.close();
    }

    // Есть ли в избранном
    public boolean getFavorites(){
        if (id < 0){
            return false;
        }
        return true;
    }

    public ContentValues getContent(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBTranslate.CODE_FROM, longFrom);
        contentValues.put(DBTranslate.CODE_TO, longTo);
        contentValues.put(DBTranslate.LANG_FROM, textFrom);
        contentValues.put(DBTranslate.LANG_TO, textTo);
        contentValues.put(DBTranslate.FAVOR, id);
        return contentValues;
    }

    // Убрать из избранного
    public void noFavorites(Context context){
        DBTranslate dbTranslate = new DBTranslate(context);
        SQLiteDatabase database = dbTranslate.getWritableDatabase();
        database.delete(DBTranslate.TABLE_FAVOR, DBTranslate.KAY_ID + "=" + id, null);
        id = -1;
    }

}
