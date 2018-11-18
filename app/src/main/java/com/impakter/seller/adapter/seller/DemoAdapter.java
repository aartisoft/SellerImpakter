package com.impakter.seller.adapter.seller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.impakter.seller.R;

import java.util.ArrayList;
import java.util.List;

public class DemoAdapter extends BaseAdapter {
    private Activity context;
    private List<String> listSubComments = new ArrayList<>();
    LayoutInflater inflater;

    public DemoAdapter(Activity context, List<String> listSubComments) {
        this.context = context;
        this.listSubComments = listSubComments;
        inflater = LayoutInflater.from(context);
    }

    public DemoAdapter(Activity context) {
        this.context = context;
        this.listSubComments = listSubComments;
        inflater = LayoutInflater.from(context);
        fakeData();
    }

    private void fakeData() {
        listSubComments.add("a");
        listSubComments.add("a");
        listSubComments.add("a");
        listSubComments.add("a");
        listSubComments.add("a");
    }

    @Override
    public int getCount() {
        return listSubComments.size();
    }

    @Override
    public Object getItem(int position) {
        return listSubComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_sub_comment, parent, false);
        }
        return convertView;
    }
}
