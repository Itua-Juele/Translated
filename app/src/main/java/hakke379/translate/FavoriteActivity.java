package hakke379.translate;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {

    ListView listView;
    ListAdapter favorites;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        favorites = new ListAdapter(this, new ArrayList<Card>(), 2);
        favorites.ArrayFavor();
        listView = (ListView)findViewById(R.id.listView1);
        listView.setAdapter(favorites);
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation1);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener());
    }

    // обработчик нижней навигации
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener(){
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_home:
                        Intent intent = new Intent();
                        intent.putExtra("activity", "home");
                        setResult(RESULT_OK, intent);
                        break;
                    case R.id.action_favor:
                        favorites = new ListAdapter(FavoriteActivity.this, DBFavorite.getArrayList(), 2);
                        listView.setAdapter(favorites);
                        break;
                    case R.id.action_settings:
                        intent = new Intent();
                        intent.putExtra("activity", "settings");
                        setResult(RESULT_OK, intent);
                        break;
                }
                return false;
            }
        };
    }
}
