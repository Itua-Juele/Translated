package hakke379.translate;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Жуэль on 24.04.2017.
 */

public class BDHistory extends SugarRecord {
    int id;
    String lang_code_from;
    String lang_code_to;
    String text_from;
    String text_to;
    int favor_id;

    public BDHistory(){

    }

    public BDHistory(int id, String lang_code_from, String lang_code_to, String text_from, String text_to, int favor_id) {
        this.id = id;
        this.lang_code_from = lang_code_from;
        this.lang_code_to = lang_code_to;
        this.text_from = text_from;
        this.text_to = text_to;
        this.favor_id = favor_id;
    }

    public BDHistory(Card card){
        lang_code_from = card.longFrom;
        lang_code_to = card.longTo;
        text_from = card.textFrom;
        text_to = card.textTo;
        favor_id = card.id;
    }

    // Вернуть сохраненную историю
    public static ArrayList<Card> getArrayList(){
        ArrayList<Card> cards = new ArrayList<Card>();
        List<BDHistory> bdHistories = BDHistory.listAll(BDHistory.class);
        for(BDHistory bdHistory: bdHistories){
            cards.add(bdHistory.getCard());
        }
        return cards;
    }

    // Вернуть экземпляр Card
    public Card getCard(){
        return new Card(lang_code_from, lang_code_to, text_from, text_to, favor_id);
    }
}
