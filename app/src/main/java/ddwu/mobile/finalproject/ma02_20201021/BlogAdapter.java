package ddwu.mobile.finalproject.ma02_20201021;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BlogAdapter extends BaseAdapter implements View.OnClickListener {
    public interface BlogClickListener {
        void onBlogClick(String link);
    }

    private Context context;
    private int layout;
    private ArrayList<Blog> list;
    private LayoutInflater layoutInflater;
    private BlogClickListener blogClickListener;

    public BlogAdapter(Context context, int layout, ArrayList<Blog> list, BlogClickListener clickListener) {
        this.context = context;
        this.layout = layout;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.blogClickListener = clickListener;
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
        BlogAdapter.ViewHolder holder;

        if (view == null) {
            view = layoutInflater.inflate(layout, viewGroup, false);

            holder = new BlogAdapter.ViewHolder();
            holder.textTitle = (TextView)view.findViewById(R.id.tvBlogTitle);
            holder.textDescription = (TextView)view.findViewById(R.id.tvBlogDescription);
            holder.textDate = (TextView)view.findViewById(R.id.tvDate);
            view.setTag(holder);
        } else {
            holder = (BlogAdapter.ViewHolder)view.getTag();
        }

        holder.textTitle.setText(list.get(position).getTitle());
        holder.textDescription.setText(list.get(position).getDescription());
        holder.textDate.setText(list.get(position).getDate());
        holder.textTitle.setOnClickListener(this);
        holder.textTitle.setTag(list.get(position).getLink());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBlogTitle:
                if (this.blogClickListener != null) {
                    String link = (String)view.getTag();
                    this.blogClickListener.onBlogClick(link);
                }
                break;
        }
    }

    static class ViewHolder {
        TextView textTitle;
        TextView textDescription;
        TextView textDate;
    }
}
