package abc.integratedtest;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by HAN on 2017-10-08.
 */

// SpinnerItem 배열을 저장한 List 를 커스텀 SpinnerItem 레이아웃에 Set 하는 클래스
public class CustomArrayAdapter extends ArrayAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<SpinnerItem> items;
    private final int mResource;

    public CustomArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int i, View view, ViewGroup viewGroup){
        view = mInflater.inflate(mResource, viewGroup, false);

        if(i == getCount() - 1) view.setBackgroundResource(R.drawable.rounded_color);

        ImageView icon = (ImageView)view.findViewById(R.id.iv_selectedIcon);
        TextView txt = (TextView) view.findViewById(R.id.tv_iconText);

        SpinnerItem offerData = items.get(i);

        icon.setImageResource(offerData.getId());
        txt.setText(offerData.getTxt());

        return view;
    }


}
