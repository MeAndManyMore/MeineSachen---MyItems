package com.example.christian.meinesachen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordListAdapter extends BaseAdapter{

    private Context context;
    private int layout;
    private ArrayList<Model> recordList;

    public RecordListAdapter(Context context, int layout, ArrayList<Model> recordList) {
        this.context = context;
        this.layout = layout;
        this.recordList = recordList;
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView1, imgView2;
        TextView txtSache, txtPreis, txtDatum;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.txtSache = row.findViewById(R.id.rtxtSache);
            holder.txtPreis = row.findViewById(R.id.rtxtPreis);
            holder.txtDatum = row.findViewById(R.id.rtxtDatum);
            holder.imageView1 = row.findViewById(R.id.imgIcon);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder)row.getTag();
        }

        Model model = recordList.get(position);

        holder.txtSache.setText(model.getSache());
        holder.txtPreis.setText(model.getPreis());
        holder.txtDatum.setText(model.getDatum());

        byte[] recordImage = model.getImage1();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage,0,recordImage.length);
        holder.imageView1.setImageBitmap(bitmap);

        return row;
    }
}



























