package hakke379.translate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final static int CODE_ACTIVITY_LANGUAGE_FROM = 123,
            CODE_ACTIVITY_LANGUAGE_TO = 124,
            CODE_ACTIVITY_FAVORITE = 435;

    ListView listView;
    Button buttonFrom, buttonTo;
    EditText textBoxFrom;
    TextView textBoxTo;
    TranslateTask translateTask;
    LinearLayout linearLayout;
    SQLiteDatabase database;

    private String langCodeFrom, langCodeTo;
    private ListAdapter history;
    //private Resources R = getResources();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DBTranslate dbTranslate = new DBTranslate(this);

        translateTask = new TranslateTask();

        linearLayout = (LinearLayout) findViewById(R.id.layoutA);
        listView = (ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(onItemClickListener());
        history = new ListAdapter(linearLayout.getContext(), BDHistory.getArrayList(), 1);
        listView.setAdapter(history);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener());

        buttonFrom = (Button) findViewById(R.id.buttonFrom);
        buttonFrom.setText("РУССКИЙ");
        langCodeFrom = "ru";
        buttonFrom.setOnClickListener(this);
        buttonTo = (Button) findViewById(R.id.buttonTo);
        buttonTo.setText("АНГЛИЙСКИЙ");
        langCodeTo = "en";
        buttonTo.setOnClickListener(this);
        findViewById(R.id.buttonSwap).setOnClickListener(this);
        findViewById(R.id.buttomClear).setOnClickListener(this);

        textBoxTo = (TextView)findViewById(R.id.textTo);
        textBoxTo.setMovementMethod(new ScrollingMovementMethod());
        textBoxFrom = (EditText)findViewById(R.id.textFrom);
        textBoxFrom.setOnEditorActionListener(onEditorActionListener());
        textBoxFrom.addTextChangedListener(textWatcher());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case CODE_ACTIVITY_LANGUAGE_FROM:
                langCodeFrom = data.getStringExtra("languageCode");
                buttonFrom.setText(data.getStringExtra("language"));
                break;
            case CODE_ACTIVITY_LANGUAGE_TO:
                langCodeTo = data.getStringExtra("languageCode");
                buttonTo.setText(data.getStringExtra("language"));
                break;
            case CODE_ACTIVITY_FAVORITE:
                if (data.getStringExtra("activity") == "settings"){

                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonFrom:
                Intent intent = new Intent(this, LangugesActivivty.class);
                intent.putExtra("ActivityCode", CODE_ACTIVITY_LANGUAGE_FROM);
                startActivityForResult(intent, CODE_ACTIVITY_LANGUAGE_FROM);
                break;
            case R.id.buttonTo:
                LanguagesTask task = new LanguagesTask();
                task.execute(langCodeFrom);
                try {
                    Map<String,String[]> dic = task.get();
                    if (dic.containsKey("error")){
                        ShowError(dic.get("error")[0]);
                    }else {
                        intent = new Intent(this, LangugesActivivty.class);
                        intent.putExtra("languages", dic.get("languages"));
                        intent.putExtra("CodeLanguages", dic.get("CodeLanguages"));
                        startActivityForResult(intent, CODE_ACTIVITY_LANGUAGE_TO);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonSwap:
                String s = (String) buttonFrom.getText();
                buttonFrom.setText(buttonTo.getText());
                buttonTo.setText(s);
                s = langCodeFrom;
                langCodeFrom = langCodeTo;
                langCodeTo = s;
                break;
            case R.id.buttomClear:
                textBoxFrom.setText("");
                textBoxTo.setText("");
                textBoxFrom.setHeight(textBoxTo.getHeight());
                listView.setAdapter(history);
                linearLayout.requestFocus();
                break;
        }
    }

    public void onClickTextBox(View view){
        textBoxFrom.setHeight(textBoxTo.getHeight());
        textBoxFrom.requestFocus();
    }

    // Обработчик нажатия клавиши Enter
    private TextView.OnEditorActionListener onEditorActionListener(){
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (translateTask.isCancelled()){
                    translateTask.cancel(false);
                }
                String text = textBoxFrom.getText().toString();
                if (text.length() == 0){
                    linearLayout.requestFocus();
                    return false;
                }
                TranslatedTask task = new TranslatedTask();
                task.execute(text);
                textBoxFrom.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
                linearLayout.requestFocus();
                history.saveHistory();
                return true;
            }
        };
    }

    // Обработчик изменения текста
    private TextWatcher textWatcher(){
        return new TextWatcher() {
            String text;
            int length;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text = s.toString();
                length = text.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String line = s.toString();
                if (line.length() > length) {
                    if (line.substring(length) == "\n"){
                        line = text;
                        textBoxFrom.setText(line);
                        return;
                    }
                }
                if (translateTask.isCancelled()){
                    translateTask.cancel(false);
                }
                if (line.length() != 0){
                    //translateTask.execute(s.toString());
                }
            }
        };
    }

    // Обработчик списка
    private AdapterView.OnItemClickListener onItemClickListener(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                textBoxFrom.setText(((TextView)view.findViewById(R.id.textHistory1)).getText().toString());
                textBoxTo.setText(((TextView)view.findViewById(R.id.textHistory2)).getText().toString());
                ArrayList<Card> cards = new ArrayList<Card>();
                cards.add(history.cards.get(position));
                AnswerAdapter adapter = new AnswerAdapter(linearLayout.getContext(), cards);
                listView.setAdapter(adapter);
            }
        };
    }

    // обработчик нижней навигации
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        history.saveHistory();
                        listView.setAdapter(history);
                        break;
                    case R.id.action_favor:
                        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                        startActivityForResult(intent, CODE_ACTIVITY_FAVORITE);
                        break;
                }
                return false;
            }
        };
    }

    // Инициализатор класса
    private void initialazer(){

    }

    // Показать окно с ошибкой
    public void ShowError(String massage){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Error")
                .setMessage(massage)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Класс для определения поддерживаемых языков перевода
    private class LanguagesTask extends AsyncTask<String, Void, Map<String,String[]>>{
        @Override
        protected Map<String,String[]> doInBackground(String... params) {
            try {
                Map<String, String []> dic = YandexTranslate.GetMapLanguagesTo(params[0]);
                return dic;
            } catch (IOException e) {
                Map<String,String[]> dic = new HashMap<String, String[]>();
                String[] array = {e.getMessage()};
                dic.put("error", array);
                return dic;
            }
        }

    }

    // Класс для перевода текст
    private class TranslateTask extends AsyncTask<String, Void, Card>{
        @Override
        protected Card doInBackground(String... params) {
            Card card = new Card(langCodeFrom, langCodeTo,
                    params[0]);
            card.Translate();
            return card;
        }

        @Override
        protected void onPostExecute(Card card) {
            super.onPostExecute(card);
            if (card.error != null){
                ShowError(card.error.getMessage());
            }else {
                textBoxTo.setText(card.textTo);
            }
        }
    }

    // Класс для получения перевода текста и его альтернатив
    private class TranslatedTask extends AsyncTask <String, Void, ArrayList<Card>>{

        @Override
        protected ArrayList<Card> doInBackground(String... params) {
            ArrayList<Card> cards;
            try{
                cards = YandexTranslate.Translated(langCodeFrom,
                        langCodeTo, params[0]);

            } catch (IOException e) {
                cards = new ArrayList<Card>();
                Card card = new Card("","", e.getMessage());
                card.error = e;
                cards.add(card);
            }
            return cards;
        }

        @Override
        protected void onPostExecute(ArrayList<Card> cards) {
            super.onPostExecute(cards);
            if (cards.get(0).error != null){
                ShowError(cards.get(0).error.getMessage());
            }else {
                AnswerAdapter adapter = new AnswerAdapter(linearLayout.getContext(), cards);
                listView.setAdapter(adapter);
                history.addCard(cards.get(0));
            }
        }
    }
}
