package com.example.phas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter {

    private int resourceId;                 // 생성자로부터 전달된 resource id 값을 저장.
    private ArrayList<ListViewItem> data = null;
    Button list_play;

    public ListAdapter(Context context, int resource, ArrayList<ListViewItem> list) {
        super(context, resource, list);

        this.data = list;
        // resource id 값 복사. (super로 전달된 resource를 참조할 방법이 없음.)
        this.resourceId = resource;
    }

    // 새롭게 만든 Layout을 위한 View를 생성하는 코드
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // 생성자로부터 저장된 resourceId(listview_btn_item)에 해당하는 Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(this.resourceId, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        final TextView fileName = (TextView) convertView.findViewById(R.id.fileName);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = (ListViewItem) getItem(position);

        // 아이템 내 각 위젯에 데이터 반영
        fileName.setText(listViewItem.getFileName());

        list_play = (Button) convertView.findViewById(R.id.list_play);
        list_play.setOnClickListener(data.get(position).onClickListener);

        convertView.setTag("" + position);
        return convertView;
    }

}
