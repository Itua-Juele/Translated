package hakke379.translate;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static hakke379.translate.R.drawable.image_favor;

/**
 * Created by Жуэль on 23.04.2017.
 */
// Класс для отображения ответа перевода
public class AnswerAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Card> arrayList;
    AnswerAdapter(Context context, ArrayList<Card> cards){
        this.context = context;
        arrayList = cards;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.layout_answer, parent, false);
        }

        if (position != 0){
            view.setVisibility(View.GONE);
            return view;
        }
        ImageButton button = (ImageButton)view.findViewById(R.id.buttonFavorWhite);
            button.setOnClickListener(onClickListener());
            if (getCard(0).getFavorites()){
                button.setBackgroundResource(R.drawable.image_favor);
            }
        ((TextView) view.findViewById(R.id.boxTo)).setText(getCard(0).textTo);
        ((TextView) view.findViewById(R.id.lang)).setText(getCard(0).longTo);
        switch (getCount()){
            case 2:
                TextView textView;
                if (getCard(1).textFrom == "speech"){
                    textView = ((TextView) view.findViewById(R.id.textTR));
                    textView.setText(getCard(1).textTo);
                    textView.setVisibility(View.VISIBLE);
                }else {
                    textView = ((TextView) view.findViewById(R.id.textSun));
                    textView.setText(getCard(1).textTo);
                    textView.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.textSunName).setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                textView = ((TextView) view.findViewById(R.id.textTR));
                textView.setText(getCard(1).textTo);
                textView.setVisibility(View.VISIBLE);

                textView = ((TextView) view.findViewById(R.id.textSun));
                textView.setText(getCard(2).textTo);
                textView.setVisibility(View.VISIBLE);
                view.findViewById(R.id.textSunName).setVisibility(View.VISIBLE);
                break;
        }
        return view;
    }

    // Слушатель кнопки
    public ImageButton.OnClickListener onClickListener(){
        return new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = arrayList.get(0);
                if (card.getFavorites()){
                    card.noFavorites(context);
                    if (!card.getFavorites()) {
                        v.setBackgroundResource(R.drawable.image_no_favor);
                    }
                } else {
                    card.Favorites(context);
                    if (card.getFavorites()) {
                        v.setBackgroundResource(R.drawable.image_favor);
                    }
                }
            }
        };
    }

    // карта по позиции
    public Card getCard(int position) {
        return arrayList.get(position);
    }
}
