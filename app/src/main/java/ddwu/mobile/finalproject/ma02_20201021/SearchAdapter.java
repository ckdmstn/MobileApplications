package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAdapter extends BaseAdapter implements View.OnClickListener {
    public interface ListBtnClickListener {
        void onListBtnClick(String title, String address);
    }
    public interface BlogBtnClickListener {
        void onBlogBtnClick(String title, String address);
    }

    private Context context;
    private int layout;
    private ArrayList<Search> list;
    private ListBtnClickListener listBtnClickListener;
    private BlogBtnClickListener blogBtnClickListener;
    private LayoutInflater layoutInflater;

    public SearchAdapter(Context context, int layout, ArrayList<Search> list,
                         ListBtnClickListener clickListener, BlogBtnClickListener btnClickListener) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listBtnClickListener = clickListener;
        this.blogBtnClickListener = btnClickListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return list.get(i).get_id();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        SearchAdapter.ViewHolder holder;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);

            holder = new SearchAdapter.ViewHolder();
            holder.textTitle = (TextView)view.findViewById(R.id.tvSearchTitle);
            holder.textAddress = (TextView)view.findViewById(R.id.tvSearchAddress);
            holder.mapBtn = (Button)view.findViewById(R.id.mapBtn);
            holder.blogBtn = (Button)view.findViewById(R.id.blogBtn);
            view.setTag(holder);
        } else {
            holder = (SearchAdapter.ViewHolder)view.getTag();
        }

        holder.textTitle.setText(list.get(position).getTitle());
        holder.textAddress.setText(list.get(position).getAddress());
        String str = list.get(position).getTitle() + "/" + list.get(position).getAddress();
        holder.mapBtn.setOnClickListener(this);
        holder.mapBtn.setTag(str);
        holder.blogBtn.setOnClickListener(this);
        holder.blogBtn.setTag(str);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mapBtn:
                if (this.listBtnClickListener != null) {
                    String str = (String)view.getTag();
                    String[] data = str.split("/"); // data[0] = title, data[1] = address
                    this.listBtnClickListener.onListBtnClick(data[0], data[1]);
                }
                break;
            case R.id.blogBtn:
                if (this.blogBtnClickListener != null) {
                    String str = (String)view.getTag();
                    String[] data = str.split("/"); // data[0] = title, data[1] = address
                    this.blogBtnClickListener.onBlogBtnClick(data[0], data[1]);
                }
                break;
        }
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textAddress;
        Button mapBtn;
        Button blogBtn;
    }

}
