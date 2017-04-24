package hakke379.translate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Жуэль on 23.04.2017.
 */

// Класс реализует вид листа истории, избранного и альтернативного перевода
public class ListAdapter extends BaseAdapter{
    ArrayList<Card> cards;
    Context context;
    LayoutInflater layoutInflater;
    int typeList;

    ListAdapter(Context context, ArrayList<Card> cards, int typeList){
        this.context = context;
        this.cards = cards;
        this.typeList = typeList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return cards.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null && typeList != 0) {
            view = layoutInflater.inflate(R.layout.layout_item, parent, false);
        }

        Card card = cards.get(position);
        TextView text1 = (TextView)view.findViewById(R.id.textHistory1);
        TextView text2 = (TextView)view.findViewById(R.id.textHistory2);
        ImageButton button = (ImageButton)view.findViewById(R.id.buttonFavor);

        text1.setText(card.textFrom);
        text2.setText(card.textTo);
        button.getTag(position);
        switch (typeList){
            case 1:
                if (card.getFavorites()){
                    button.setBackgroundResource(R.drawable.image_favor_orange);
                }
                button.setOnClickListener(onClickListener());
                break;
            case 2:
                if (card.getFavorites()){
                    button.setBackgroundResource(R.drawable.image_favor);
                }else {
                    button.setBackgroundResource(R.drawable.image_no_favor);
                }
                button.setOnClickListener(onClickListener());
                break;
        }
        return view;
    }

    // Слушатель для кнопки
    public View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View parent = (View) v.getParent();
                int pos = ((ListView)parent.getParent()).getPositionForView(parent);
                Card card = cards.get(pos);
                switch (typeList){
                    case 1:
                        if (card.getFavorites()){
                            card.noFavorites(context);
                            if (!card.getFavorites()) {
                                v.setBackgroundResource(R.drawable.image_no_favor_orange);
                            }
                        }else {
                            card.Favorites(context);
                            if (card.getFavorites()) {
                                v.setBackgroundResource(R.drawable.image_favor_orange);
                            }
                        }
                        break;
                    case 2:
                        if (card.getFavorites()){
                            card.noFavorites(context);
                            if (!card.getFavorites()) {
                                v.setBackgroundResource(R.drawable.image_no_favor);
                            }
                        }else {
                            card.Favorites(context);
                            if (card.getFavorites()) {
                                v.setBackgroundResource(R.drawable.image_favor);
                            }
                        }
                        break;
                }
            }
        };
    }

    // Добавить карточку в начало истории
    public void addCard(Card card){
        ArrayList<Card> arrayList = new ArrayList<>();
        arrayList.add(card);
        for(Card card1: cards){
            arrayList.add(card);
        }
        cards = arrayList;
    }

    // Сохранить историю
    public void saveHistory(){
        DBTranslate dbTranslate = new DBTranslate(context);
        SQLiteDatabase database = dbTranslate.getWritableDatabase();
        database.delete(DBTranslate.TABLE_HISTORI, null, null);
        for (int i = 0; i < cards.size() && i < 25; i++){
            ContentValues contentValues = cards.get(i).getContent();
            database.insert(DBTranslate.TABLE_HISTORI, null, contentValues);
        }
        database.close();
        database.close();
    }

    // Удалить ичторию
    public void deleteHistory(){
        DBTranslate dbTranslate = new DBTranslate(context);
        SQLiteDatabase database = dbTranslate.getWritableDatabase();
        database.delete(DBTranslate.TABLE_HISTORI, null, null);
        database.close();
        database.close();
    }

    // Вернуть избранное
    public void ArrayFavor(){
        cards = new ArrayList<Card>();
        DBTranslate dbTranslate = new DBTranslate(context);
        SQLiteDatabase database = dbTranslate.getWritableDatabase();
        Cursor cursor = database.query(DBTranslate.TABLE_FAVOR, null, null, null, null, null, null);
        if (cursor == null){
            return;
        }
        cursor.moveToFirst();
        do {
            cards.add(new Card(cursor.getString(cursor.getColumnIndex(DBTranslate.CODE_FROM)),
                    cursor.getString(cursor.getColumnIndex(DBTranslate.CODE_TO)),
                    cursor.getString(cursor.getColumnIndex(DBTranslate.LANG_FROM)),
                    cursor.getString(cursor.getColumnIndex(DBTranslate.LANG_TO)),
                    cursor.getInt(cursor.getColumnIndex(DBTranslate.KAY_ID))));
        }while (cursor.moveToNext());
    }
}
