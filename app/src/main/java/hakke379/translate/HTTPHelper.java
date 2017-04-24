package hakke379.translate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Жуэль on 19.04.2017.
 */

public class HTTPHelper {

    private static final String ENCODING = "UTF-8";

    /*
    Вернуть ответ сервера в вдие строки
    connection - откуда будет возвращена информация*/
    public static String Response(final HttpsURLConnection connection) throws IOException {
        try {
            final int responseCode = connection.getResponseCode();
            final String result = ToString(connection.getInputStream());
            if (connection != null) {
                connection.disconnect();
            }
            if(responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("[Error] No connection to the server: " + result);
            }
            return result;
        } catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }
    public static String Response(final URL url) throws IOException{
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type","text/plain; charset=" + ENCODING);
        connection.setRequestProperty("Accept-Charset", ENCODING);
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return Response(connection);
    }
    /*
    Из Stream в String
    stream - поток*/
    private static String ToString(final InputStream stream) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            String s;
            if (stream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, ENCODING));
                while (null != (s = reader.readLine())) {
                    // Need to strip the Unicode Zero-width Non-breaking Space. For some reason, the Microsoft AJAX
                    // services prepend this to every response
                    stringBuilder.append(s.replaceAll("\uFEFF", ""));
                }
            }
        } catch (IOException e) {
            //throw new Exception("Ошибка при чтении потока данных.", e);
            throw new IOException("Failed to read server response");
        }
        return stringBuilder.toString();
    }
}
