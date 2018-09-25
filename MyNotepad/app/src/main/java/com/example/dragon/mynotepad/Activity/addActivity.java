package com.example.dragon.mynotepad.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.ConditionVariable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dragon.mynotepad.DictationResult;
import com.example.dragon.mynotepad.MainActivity;
import com.example.dragon.mynotepad.R;
import com.example.dragon.mynotepad.Sqlite.SQLiteHelper;
import com.example.dragon.mynotepad.adapter.MyBaseAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.List;
import java.util.jar.Manifest;

import static android.widget.Toast.LENGTH_LONG;

public class addActivity extends AppCompatActivity {
    private TextView tv_option, tv_save;
    private FloatingActionButton bt_microphone;
    private EditText ed_content;
    private DrawerLayout mDrawerLayout;
    private SQLiteHelper sql;
    private Time t;
    private String mtime;
    private int requestCode, position;
    MainActivity mainActivity;
    private SwitchCompat switchCompat;
    public static final String TAG = "addActivity";
    private RecognizerDialog iatDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
    }

    private void init() {
        t = new Time();
        t.setToNow();
        mtime = t.year + "年 " + (t.month + 1) + "月 " + t.monthDay + "日 " + t.hour + "h " + t.minute + "m " + t.second + "s";
        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", 0);
        position = intent.getIntExtra("position", 0);
        tv_option = (TextView) findViewById(R.id.tv_option);
        tv_save = (TextView) findViewById(R.id.tv_save);
        bt_microphone = (FloatingActionButton) findViewById(R.id.bt_microphone);
        ed_content = (EditText) findViewById(R.id.ed_content);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dw_add);
        switchCompat = (SwitchCompat) findViewById(R.id.sc_button);
        sql = new SQLiteHelper(addActivity.this, "content.db", null, 1);
        tv_save.setVisibility(View.VISIBLE);
        switchCompat.setVisibility(View.GONE);
        if (requestCode == 1) {
            mainActivity = new MainActivity();
            ed_content.setText(mainActivity.getList(this).get(position).content);
            ed_content.setSelection(mainActivity.getList(this).get(position).content.length());
            tv_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = sql.getWritableDatabase();
                    String content = ed_content.getText().toString().trim();
                    db.execSQL("update Content set content =" + "'" + content + "'" + " " + "where id=" + mainActivity.getList(addActivity.this).get(position).id);
                    db.execSQL("update Content set time =" + "'" + mtime + "'" + " " + "where id=" + mainActivity.getList(addActivity.this).get(position).id);
                    db.close();
                    Toast.makeText(addActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    openActivity();
                }
            });
        } else {
            tv_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLiteDatabase db = sql.getWritableDatabase();
                    String content = ed_content.getText().toString().trim();
                    ContentValues values = new ContentValues();
                    values.put("content", content);
                    values.put("time", mtime);
                    db.insert("content", null, values);
                    db.close();
                    Toast.makeText(addActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    openActivity();
                }
            });
        }
        tv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        bt_microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(addActivity.this, android.Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(addActivity.this,new String[]{android.Manifest.permission.RECORD_AUDIO},1);
                }else {
                    getVioce();
                }
            }
        });
    }
    /**
     *  跳转方法
     */

    private void openActivity() {
        Intent intent = new Intent(addActivity.this, MainActivity.class);
        startActivity(intent);
        addActivity.this.finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            openActivity();
        }
        return true;
    }

    /**
     * 语音听写方法
     */
    private void getVioce() {
        // ①语音配置对象初始化
        SpeechUtility.createUtility(addActivity.this, SpeechConstant.APPID + "=5af674f6");
        // ②初始化有交互动画的语音识别器
        iatDialog = new RecognizerDialog(addActivity.this, mInitListener);
        //③设置监听，实现听写结果的回调
        iatDialog.setListener(new RecognizerDialogListener() {
            String resultJson = "[";//放置在外边做类的变量则报错，会造成json格式不对（？）

            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                System.out.println("-----------------   onResult   -----------------");
                if (!isLast) {
                    resultJson += recognizerResult.getResultString() + ",";
                } else {
                    resultJson += recognizerResult.getResultString() + "]";
                }

                if (isLast) {
                    //解析语音识别后返回的json格式的结果
                    Gson gson = new Gson();
                    List<DictationResult> resultList = gson.fromJson(resultJson,
                            new TypeToken<List<DictationResult>>() {
                            }.getType());
                    String result = "";
                    for (int i = 0; i < resultList.size() - 1; i++) {
                        result += resultList.get(i).toString();
                    }
                    ed_content.setText(ed_content.getText().toString().trim()+result);
                    //获取焦点
                    ed_content.requestFocus();
                    //将光标定位到文字最后，以便修改
                    ed_content.setSelection(ed_content.length());
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                //自动生成的方法存根
                speechError.getPlainDescription(true);
            }
        });
        //开始听写
        iatDialog.show();
    }

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(addActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:
               if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   getVioce();
               }else {
                   Toast.makeText(addActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
               }
               break;
           default:
       }
    }
}
