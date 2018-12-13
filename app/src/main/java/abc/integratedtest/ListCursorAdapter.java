package abc.integratedtest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by HAN on 2017-10-08.
 */

// DB 의 Select 내역을 담아둔 Cursor 를 커스텀 ListViewItem 레이아웃에 Set 하는 클래스
public class ListCursorAdapter extends CursorAdapter {

    private boolean bClick = false;

    public ListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        return LayoutInflater.from(context).inflate(R.layout.listview_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_text = view.findViewById(R.id.tv_text);
        ImageView iv_feel = view.findViewById(R.id.iv_feel);
        ImageView iv_weather = view.findViewById(R.id.iv_weather);
        TextView tv_createdDate = view.findViewById(R.id.tv_createdDate);

        tv_text.setText(cursor.getString(1));
        int id = cursor.getInt(2);
        if(id == 0) iv_feel.setImageResource(R.drawable.excited);
        else if(id == 1) iv_feel.setImageResource(R.drawable.happy);
        else if(id == 2) iv_feel.setImageResource(R.drawable.worried);
        else if(id == 3) iv_feel.setImageResource(R.drawable.sad);
        else if(id == 4) iv_feel.setImageResource(R.drawable.angry);
        id = cursor.getInt(3);
        if(id == 0) iv_weather.setImageResource(R.drawable.sunny);
        else if(id == 1) iv_weather.setImageResource(R.drawable.cloud);
        else if(id == 2) iv_weather.setImageResource(R.drawable.windy);
        else if(id == 3) iv_weather.setImageResource(R.drawable.rainy);
        else if(id == 4) iv_weather.setImageResource(R.drawable.snowy);
        tv_createdDate.setText(String.format("%s. %s. %s", cursor.getInt(4), cursor.getInt(5), cursor.getInt(6)));

        // 그냥 넣어본 그라데이션
        float[] hsv = {360f / getCount() * cursor.getPosition() + 1, 0.12f, 1.6f};
        view.setBackgroundColor(Color.HSVToColor(hsv));

        CheckBox cb = view.findViewById(R.id.cb_isDeleteItem);

        if(bClick) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.GONE);
        }
    }

    public void toggleVisibility(boolean bClick) {
        this.bClick = bClick;
        notifyDataSetChanged();
    }

}
