package com.today.wis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WiseListAdapter extends BaseAdapter
{
    private ArrayList<WistListItem> mItems = new ArrayList<>();

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public WistListItem getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        Context context = parent.getContext();

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_wise, parent, false);
        }

        TextView tvGetWise = convertView.findViewById(R.id.tv_get_wise);
        TextView tvGetName = convertView.findViewById(R.id.tv_get_name);

        WistListItem WistListItem = getItem(position);

        tvGetWise.setText(WistListItem.getWise());
        tvGetName.setText(WistListItem.getName());

        return convertView;
    }

    /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
    public void addItem(String wise, String name)
    {

        WistListItem mItem = new WistListItem();

        mItem.setWise(wise);
        mItem.setName(name);

        mItems.add(mItem);

    }
}
