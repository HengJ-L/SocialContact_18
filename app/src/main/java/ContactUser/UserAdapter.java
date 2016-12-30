package ContactUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lhj30.socialcontact.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lhj30 on 2016/12/28.
 */

public class UserAdapter extends BaseAdapter{

    private Context ctx;
    //
    private ViewHolder holder;
    List<User> list;
    Map<String, Integer> selector;//键值是索引表的字母，值为对应在listview中的位置

    /**
     * 字母表
     */
    String index[];

    public UserAdapter(Context context, List<User> list,
                       String[] index) {
        this.ctx = context;
        this.list = list;
        this.index = index;
        selector = new HashMap<String, Integer>();
        for (int j = 0; j < index.length; j++) {// 循环字母表，找出list中对应字母的位置
            for (int i = list.size()-1; i >=0; i--) {
                if (list.get(i).zipCode.toLowerCase().equals(index[j].toLowerCase()))
                    selector.put(index[j], i);
            }
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(ctx).inflate(
                        R.layout.listitem, null);
                holder.user_mark = (ImageView) convertView.findViewById(R.id.user_mark);
                holder.user_image = (ImageView) convertView.findViewById(R.id.user_image);
                holder.tv_showname = (TextView) convertView.findViewById(R.id.tv_showname);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_showmobilephone = (TextView) convertView.findViewById(R.id.tv_showmobilephone);
                holder.tv_mobilephone = (TextView) convertView.findViewById(R.id.tv_mobilephone);
                holder.tv_index = (TextView) convertView.findViewById(R.id.tv_index);
                holder.indexview=(LinearLayout)convertView.findViewById(R.id.indexview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            User item = list.get(position);
            holder.user_image.setImageResource(item.imageId);
            holder.tv_name.setText(item.username);
            holder.tv_mobilephone.setText(item.mobilePhone);
            holder.tv_index.setText(item.zipCode);

            // 显示index
            String currentStr = item.zipCode;
            // 上一项的index
            String previewStr = (position - 1) >= 0 ? list.get(position - 1).zipCode
                    : " ";
            /**
             * 判断是否上一次的存在
             */
            if (!previewStr.equals(currentStr)) {
                holder.tv_index.setVisibility(View.VISIBLE);
                holder.indexview.setVisibility(View.VISIBLE);
                holder.tv_index.setText(currentStr);//显示所在的另一个index
            } else {
                holder.indexview.setVisibility(View.GONE);
                holder.tv_index.setVisibility(View.GONE);
            }
        } catch (OutOfMemoryError e) {
            Runtime.getRuntime().gc();
        } catch (Exception ex) {
            // handler.sendEmptyMessage(CommonMessage.PARSE_ERROR);
            ex.printStackTrace();
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_index;
        ImageView user_mark;
        ImageView user_image;
        TextView tv_showname;
        TextView tv_name;
        TextView tv_showmobilephone;
        TextView tv_mobilephone;
        LinearLayout indexview;
    }
    public Map<String, Integer> getSelector() {
        return selector;
    }

    public void setSelector(Map<String, Integer> selector) {
        this.selector = selector;
    }

    public String[] getIndex() {
        return index;
    }

    public void setIndex(String[] index) {
        this.index = index;
    }


}
