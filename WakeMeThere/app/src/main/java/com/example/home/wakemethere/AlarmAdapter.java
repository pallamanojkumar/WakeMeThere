package com.example.home.wakemethere;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmAdapter extends ArrayAdapter {
    List list = new ArrayList();
    Context context;
    ArrayList<Map<String, String>> alarms;
    DatabaseHelper myDb;

    public AlarmAdapter(Context context, int resource, ArrayList<Map<String, String>> alarms) {
        super(context, resource);
        this.context = context;
        this.alarms = alarms;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        DataHandler handler;
        myDb = new DatabaseHelper(context);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.layout, parent, false);
            handler = new DataHandler();
            handler.alarmSwitch = (Switch) row.findViewById(R.id.switch3);
            handler.alarmName = (TextView) row.findViewById(R.id.textView);
            handler.alarmDist = (TextView) row.findViewById(R.id.textViewkm);
            row.setTag(handler);
        } else {
            handler = (DataHandler) row.getTag();
        }
        AlarmListDataProvider dataProvider;
        dataProvider = (AlarmListDataProvider) this.getItem(position);
        handler.alarmName.setText(dataProvider.getAlarm_name_Resource());
        handler.alarmSwitch.setChecked(dataProvider.isEnabled());
        handler.alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast.makeText(context, isChecked ? "checked at " + position : "unchecked at " + position, Toast.LENGTH_SHORT).show();
                myDb.updateDataInTable(alarms.get(position).get("id"), alarms.get(position).get("name"), alarms.get(position).get("latlng"), alarms.get(position).get("distance")
                        , isChecked ? "1" : "0", alarms.get(position).get("message"));

            }
        });
        handler.alarmDist.setText(Integer.toString(dataProvider.getDistance()));
        //.setOnText(dataProvider.getAlarm_name_Resource());
        return row;
    }

    static class DataHandler {
        Switch alarmSwitch;
        TextView alarmName;
        TextView alarmDist;
    }
}
