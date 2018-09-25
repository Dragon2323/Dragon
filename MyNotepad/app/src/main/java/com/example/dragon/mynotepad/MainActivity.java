package com.example.dragon.mynotepad;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dragon.mynotepad.Activity.addActivity;
import com.example.dragon.mynotepad.Bean.ContentBean;
import com.example.dragon.mynotepad.Sqlite.SQLiteHelper;
import com.example.dragon.mynotepad.adapter.MyBaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tv_option;
    private FloatingActionButton bt_add;
    private ListView lv;
    private DrawerLayout mDrawerLayout;
    private MyBaseAdapter myBaseAdapter;
    public List<ContentBean> lis;
    private SQLiteHelper sql;
    private ContentBean bean;
    private SwitchCompat switchCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
    }

    private void init() {
        switchCompat = (SwitchCompat) findViewById(R.id.sc_button);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tv_option = (TextView) findViewById(R.id.tv_option);
        bt_add = (FloatingActionButton) findViewById(R.id.bt_add);
        lv = (ListView) findViewById(R.id.lv);
        myBaseAdapter = new MyBaseAdapter(this);
        myBaseAdapter.setData(getList(this));
        lv.setAdapter(myBaseAdapter);
        tv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,addActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("requestCode",1);
                startActivity(intent);
                MainActivity.this.finish();
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()){
                    case R.id.sc_button:
                        if (isChecked) {
                            myBaseAdapter.Tag = true;
                            myBaseAdapter.notifyDataSetChanged();
                        } else {
                            myBaseAdapter.Tag = false;
                            myBaseAdapter.notifyDataSetChanged();
                        }
                }
            }
        });
    }
    public List<ContentBean> getList(Context context) {
        sql = new SQLiteHelper(context, "content.db",null,1);
        SQLiteDatabase db = sql.getWritableDatabase();
        lis = new ArrayList<>();
        if (lis != null) {
                Cursor cursor = db.query("content", null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    bean = new ContentBean();
                    bean.content = cursor.getString(cursor.getColumnIndex("content"));
                    bean.id = cursor.getInt(cursor.getColumnIndex("id"));
                    bean.time = cursor.getString(cursor.getColumnIndex("time"));
                    lis.add(bean);
                }
                cursor.close();
            }
        return lis;
    }
}
