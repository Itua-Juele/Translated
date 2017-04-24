package hakke379.translate;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Жуэль on 18.04.2017.
 */

// Класс для работы с Яндекс переводчиком
public class YandexTranslate extends MainActivity {
    public static final String API_KEY = "trnsl.1.1.20170418T113807Z.10416e8edb936707.77bddaa1934d53865b4a8dbdd9d2ddeb1e636322",
            API_KEY_DIC = "dict.1.1.20170423T173256Z.6728c2a0363521e2.339aa0572f79911280c643aeb684d4f0597d0de2",
            API_SERVER = "https://translate.yandex.net/api/",
            API_METHOD_TRANSLATE = "v1.5/tr.json/translate?",
            API_METHOD_GETLANGS = "v1.5/tr.json/getLangs?",
            API_METHOD_LOOKUP = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?",
            ENCODING = "UTF-8";

    // Перевести с языка langAfter на langBefore текст input
    public static String Translate(final String langAfter, final String langBefore, final String input) throws IOException {
        final String url = API_SERVER + API_METHOD_TRANSLATE +
                "key=" + URLEncoder.encode(API_KEY, ENCODING) +
                "&lang=" + URLEncoder.encode(langAfter, ENCODING) + URLEncoder.encode("-", ENCODING) + URLEncoder.encode(langBefore, ENCODING) +
                "&text=" + URLEncoder.encode(input, ENCODING);
        String response;
        try {
            response = HTTPHelper.Response(new URL(url));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return response.substring(response.indexOf("[\"") + 2, response.length() - 3);
    }

    // Вернуть словарь с поддерживаемыми языками
    public static Map<String, String[]> GetMapLanguagesTo(final String langCodeFrom) throws IOException {
        final String url = API_SERVER + API_METHOD_GETLANGS +
                "key=" + URLEncoder.encode(API_KEY, ENCODING) +
                "&ui=" + URLEncoder.encode(langCodeFrom, ENCODING);
        String response;
        try {
            response = HTTPHelper.Response(new URL(url));
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        response = response.substring(response.indexOf(":{") + 2,
                response.indexOf("\"}") - 1);
        String[] rows = response.split(","), values1 = new String[rows.length - 1],
                values2 = new String[rows.length - 1], values;
        int i = 0;
        for(String row: rows){
            if (row.indexOf(langCodeFrom) != -1) {
                continue;
            }
            values = row.split(":");
            values1[i] = values[0].replaceAll("\\W", "");
            values2[i] = values[1].replaceAll("\\W", "");
            i++;
        }
        Map<String, String[]> dic = new HashMap<String, String[]>();
        dic.put("languages", values1);
        dic.put("CodeLanguages", values2);
        return dic;
    }

    // Вернуть все варианты перевода
    public static ArrayList<Card> Translated(final String langCodeFrom, final String langCodeTo, final String text) throws IOException{
        ArrayList<Card> cards = new ArrayList<Card>();
        try {
            Card card = new Card(langCodeFrom, langCodeTo, text);
            card.Translate();
            cards.add(card);
        } catch (Exception e) {
            cards = new ArrayList<Card>();
            Card card = new Card(langCodeFrom, langCodeTo, text);
            card.error = new IOException("Translation failed");
            cards.add(card);
            return cards;
        }

        String response;
        final String url = API_METHOD_LOOKUP +
                "key=" + URLEncoder.encode(API_KEY_DIC, ENCODING) +
                "&lang=" + URLEncoder.encode(langCodeFrom, ENCODING) + URLEncoder.encode("-", ENCODING) + URLEncoder.encode(langCodeTo, ENCODING) +
                "&text=" + URLEncoder.encode(text, ENCODING);
        try {
            response = HTTPHelper.Response(new URL(url));
        }catch (IOException e){
            return cards;
        }

        try {
            String line = Speech(response);
            if (line != null){
                cards.add(new Card("", "", "speech", line));
            }
            line = Syn(response);
            if (line != null){
                cards.add(new Card("", "", "syn", line));
            }
        } catch (Exception e) {

        }
        return cards;
    }

    // Вернуть часть речи
    private static String Speech(String json){
        json = json.substring(json.indexOf("\"tr\""), json.indexOf("\"syn\"") - 1);
        json = json.replaceAll("\"", "");
        String[] lines = json.split(",");
        if (lines.length > 1){
            String value = lines[1].split(":")[1];
            if (value != "") {
                String s = lines[0].split(":")[1] + " (" + value + ")";
                return s;
            }
        }
        return null;

    }

    // Вернуть синонимы
    private static String Syn(String json){
        json = json.substring(json.indexOf("\"syn\""), json.indexOf(",\"mean\""));
        String s = "";
        while (true){
            if (json.indexOf("\"text\":\"") < 0){
                break;
            }
            json = json.substring(json.indexOf("\"text\":\"") + 8);
            s += json.substring(0, json.indexOf("\""));
        }
        if (s != ""){
            return s;
        }
        return null;
    }
}
