package com.example.phas;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DogListAdapter extends BaseAdapter {

    LayoutInflater inflater = null;
    private ArrayList<DogItemData> m_oData = null;
    private int nListCnt = 0;

    public DogListAdapter(ArrayList<DogItemData> _oData)
    {
        m_oData = _oData;
        nListCnt = m_oData.size();
    }

    @Override
    public int getCount()
    {
        Log.i("TAG", "getCount");
        return nListCnt;
    }

    @Override
    public Object getItem(int position)
    {
        return m_oData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent)
    {
        if (convertView == null)
        {
            final Context context = parent.getContext();
            if (inflater == null)
            {
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.dog_list_item, parent, false);
        }
        TextView oTextTitle = (TextView) convertView.findViewById(R.id.context);

        if(oTextTitle != null) oTextTitle.setText(m_oData.get(position).getsName());

        return convertView;
    }
}
