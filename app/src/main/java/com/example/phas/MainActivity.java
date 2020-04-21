package com.example.phas;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{

    Toolbar toolBar;
    ActionBar actionBar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon); //뒤로가기 버튼 이미지 지정

        drawerLayout = findViewById(R.id.drawer_layout_main);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                String title = menuItem.getTitle().toString();

                if (id == R.id.nav_first) {
                    Toast.makeText(MainActivity.this, "첫번째", Toast.LENGTH_LONG).show();
                } else if (id == R.id.nav_second) {
                    Toast.makeText(MainActivity.this, "두번째", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
    }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }
}
