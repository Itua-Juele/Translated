package hakke379.translate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LangugesActivivty extends AppCompatActivity {

    ListView listView;
    String[] languages; // переменная будет хранить список языков
    String[] languageCode; // переменная будет хранить список кодов языков

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_languges);

        listView = (ListView) findViewById(R.id.listView);

        if (getIntent().getIntExtra("ActivityCode", MainActivity.CODE_ACTIVITY_LANGUAGE_FROM) ==
                MainActivity.CODE_ACTIVITY_LANGUAGE_FROM) {
            // Загружаем список
            languages = getResources().getStringArray(R.array.Languages);
            languageCode = getResources().getStringArray(R.array.LAnguagesCode);
        }else {
            languages = getIntent().getStringArrayExtra("languages");
            languageCode = getIntent().getStringArrayExtra("CodeLanguages");
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                languages);
        listView.setAdapter(arrayAdapter);
        // Устанавливаем обработчик
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("language", languages[(int) id]);
                intent.putExtra("languageCode", languageCode[(int) id]);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
