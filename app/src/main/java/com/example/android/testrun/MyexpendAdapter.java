package com.example.android.testrun;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Codetribe on 2016/12/08.
 */

public class MyexpendAdapter extends BaseExpandableListAdapter {

    private List<String> head_titles;
    private HashMap<String, List<String>> child_titles;
    private Context ctx;

    MyexpendAdapter(List<String> head_titles, HashMap<String, List<String>> child_titles, Context ctx) {
        this.head_titles = head_titles;
        this.child_titles = child_titles;
        this.ctx = ctx;
    }

    @Override
    public int getGroupCount() {
        return head_titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return child_titles.get(head_titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return head_titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childposition) {
        return child_titles.get(head_titles.get(groupPosition)).get(childposition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childposition) {
        return childposition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpended, View convertView, ViewGroup parent) {
        String title = (String) this.getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.perant_layout_expendable, null);

        }
        TextView textView = (TextView) convertView.findViewById(R.id.txtheadingitem);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childposition, boolean b, View convertView, ViewGroup parentview) {

        String title = (String) this.getChild(groupPosition, childposition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_layout_expenable, null);

        }
        TextView textView = (TextView) convertView.findViewById(R.id.txtchilditem);
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setText(title);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childposition) {
        return true;
    }
}
