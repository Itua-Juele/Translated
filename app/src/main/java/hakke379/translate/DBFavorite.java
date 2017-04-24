package hakke379.translate;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Жуэль on 24.04.2017.
 */

public class DBFavorite extends SugarRecord {
    int id;
    String lang_code_from;
    String lang_code_to;
    String text_from;
    String text_to;

    public DBFavorite() {
    }

    public DBFavorite(String lang_code_from, String lang_code_to, String text_from, String text_to) {
        this.lang_code_from = lang_code_from;
        this.lang_code_to = lang_code_to;
        this.text_from = text_from;
        this.text_to = text_to;
        id = getLastId() + 1;
    }

    public DBFavorite(Card card){
        this(card.longFrom, card.longTo, card.textFrom, card.textTo);
    }

    // Сохранить Card в базе и вернуть id записи
    public static int addCard(Card card){
        DBFavorite dbFavorite = new DBFavorite(card);
        dbFavorite.save();
        return dbFavorite.id;
    }

    // Удалить Card из базы
    public static void deleteCard(Card card){
        DBFavorite dbFavorite = new DBFavorite(card);
        dbFavorite.id = card.id;
        dbFavorite.delete();
    }

    // Вернуть id последней записи
    public static int getLastId(){
        List<DBFavorite> dbFavorites = listAll(DBFavorite.class);
        if (dbFavorites == null){
            return 0;
        }else if(dbFavorites.size() == 0){
            return 0;
        }
        return dbFavorites.get(dbFavorites.size()).id;
    }

    // Вернуть экземпляр Card
    public Card getCard(){
        return new Card(lang_code_from, lang_code_to, text_from, text_to, id);
    }

    // Вернуть массив из ибранных Card
    public static ArrayList<Card> getArrayList(){
        ArrayList<Card> cards = new ArrayList<Card>();
        List<DBFavorite> dBFavorites = DBFavorite.listAll(DBFavorite.class);
        for(DBFavorite dBFavorite: dBFavorites){
            cards.add(dBFavorite.getCard());
        }
        return cards;
    }
}
