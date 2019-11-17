package com.invictus.reehbayse.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.invictus.reehbayse.models.RosterEntryModel;
import com.invictus.reehbayse.R;

import java.util.List;


public class RosterAdapter extends ArrayAdapter<RosterEntryModel> {

    private Context mContext;
    private List<RosterEntryModel> contacts;
    private int layoutResource;

    public RosterAdapter(@NonNull Context context, int resource, @NonNull List<RosterEntryModel> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.contacts = objects;
        this.layoutResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        ViewHolder holder = null;

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(layoutResource, null);
            holder.tvName = (TextView) view.findViewById(R.id.tvRosterUsername);
            holder.imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvName.setText(contacts.get(position).getJid());
        Log.d("Adapter", "----------------------- holder name: " + contacts.get(position).getJid());

        return view;
    }

    private class ViewHolder {

        private TextView tvName;
        private ImageView imgAvatar;

    }
}
