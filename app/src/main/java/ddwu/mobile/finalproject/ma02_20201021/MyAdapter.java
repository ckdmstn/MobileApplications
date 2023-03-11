package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter implements View.OnClickListener {
    public interface AlarmBtnClickListener {
        void alarmBtnClick(long _id, String title, String address);
    }

    private Context context;
    private int layout;
    private ArrayList<Diary> diaryList;
    private AlarmBtnClickListener alarmBtnClickListener;
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context, int layout, ArrayList<Diary> diaryList, AlarmBtnClickListener alarmBtnClickListener) {
        this.context = context;
        this.layout = layout;
        this.diaryList = diaryList;
        this.alarmBtnClickListener = alarmBtnClickListener;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return diaryList.size();
    }

    @Override
    public Object getItem(int i) {
        return diaryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return diaryList.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        ViewHolder holder;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);

            holder = new ViewHolder();
            holder.textTitle = (TextView)view.findViewById(R.id.tvTitle);
            holder.textAddress = (TextView)view.findViewById(R.id.tvAddress);
            holder.textContent = (TextView)view.findViewById(R.id.tvContent);
            holder.textDateTime = (TextView)view.findViewById(R.id.tvDateTime);
            holder.alarmBtn = (Button)view.findViewById(R.id.alarmBtn);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.textTitle.setText(diaryList.get(position).title);
        holder.textAddress.setText(diaryList.get(position).address);
        holder.textContent.setText(diaryList.get(position).content);
        if (diaryList.get(position).dateTime != null) {
            holder.textDateTime.setText(diaryList.get(position).dateTime);
        } else {
            holder.textDateTime.setText("");
        }
        String str = diaryList.get(position).id + "/" + diaryList.get(position).title + "/" + diaryList.get(position).address;
        holder.alarmBtn.setOnClickListener(this);
        holder.alarmBtn.setTag(str);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alarmBtn:
                if (this.alarmBtnClickListener != null) {
                    String str = (String)view.getTag();
                    String[] data = str.split("/");
                    long n = Long.parseLong(data[0]);
                    this.alarmBtnClickListener.alarmBtnClick(n, data[1], data[2]);
                }
                break;
        }
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textAddress;
        TextView textContent;
        TextView textDateTime;
        Button alarmBtn;
    }
}
