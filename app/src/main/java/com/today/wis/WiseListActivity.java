package com.today.wis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class WiseListActivity extends AppCompatActivity
{
    private ListView listViewWise;
    private WiseListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiselist);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        listViewWise = findViewById(R.id.listview_wiselist);
        adapter = new WiseListAdapter();

        Intent i = getIntent();
        String[] wiselist = i.getStringArrayExtra("wise");
        String[] namelist = i.getStringArrayExtra("name");

        for (int cnt = wiselist.length - 1; cnt >= 0; cnt--)
            adapter.addItem(wiselist[cnt], namelist[cnt]);

        listViewWise.setAdapter(adapter);

        listViewWise.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent postIntent = new Intent(WiseListActivity.this, SubActivity.class);
                postIntent.putExtra("wiseSub", adapter.getItem(i).getWise());
                postIntent.putExtra("nameSub", adapter.getItem(i).getName());
                startActivity(postIntent);
            }
        });
    }
}