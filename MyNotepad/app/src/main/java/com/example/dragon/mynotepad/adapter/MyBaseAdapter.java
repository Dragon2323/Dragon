package com.example.dragon.mynotepad.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.dragon.mynotepad.Bean.ContentBean;
import com.example.dragon.mynotepad.R;
import com.example.dragon.mynotepad.Sqlite.SQLiteHelper;
import java.util.List;

/**
 * Created by Dragon on 2018/5/7.
 */

public class MyBaseAdapter extends BaseAdapter {
    public List<ContentBean> lis;
    private Context mcontext;
    private SQLiteHelper sql;
    public boolean Tag = false;

    public MyBaseAdapter(Context context) {
        mcontext = context;
    }

    public void setData(List<ContentBean> lis) {
        this.lis = lis;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return lis.size();
    }

    @Override
    public Object getItem(int position) {
        return lis.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mcontext, R.layout.list_item, null);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        final ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
        iv_delete.setVisibility(View.GONE);
        if (lis != null) {
            tv_content.setText(lis.get(position).content);
            tv_time.setText(lis.get(position).time);
            if (Tag){
                iv_delete.setVisibility(View.VISIBLE);
            }
        }
        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "删除数据", Snackbar.LENGTH_SHORT)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                sql = new SQLiteHelper(mcontext, "content.db", null, 1);
                                SQLiteDatabase db = sql.getWritableDatabase();
                                db.execSQL("delete from Content where id =" + lis.get(position).id);
                                db.close();
                                lis.remove(position);
                                Toast.makeText(mcontext, "删除成功", Toast.LENGTH_SHORT).show();
                                MyBaseAdapter.this.notifyDataSetChanged();
                            }
                        }).show();
            }
        });
        return view;
    }
}
