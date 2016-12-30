package com.example.lhj30.socialcontact;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ContactDB.DBHelper;
import ContactUser.User;
import ContactUser.UserAdapter;
import Pinyin.CharacterParser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout layoutIndex;
    //字母索引表
    private String[] str = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "U", "V", "W", "X", "Y",
            "Z","#"};
    //字体高度
    int height = 55;
    //List适配器
    List<User> list;
    //存储标记的数目
    int markedNum;
    //存储标记条目的_id号
    ArrayList<Integer> deleteId;
    //拥有所有数据的Adapter
    UserAdapter adapter;
    // 中间显示标题的文本
    private TextView tv_show;
    //搜索框
    private ClearEditText mClearEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*添加联系人按钮*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddNew.class);
                startActivityForResult(intent,3);
            }
        });

        //搜索部分
        mClearEditText = (ClearEditText) findViewById(R.id.editText);

        //数据库获取list部分
        DBHelper helper=new DBHelper(getBaseContext());
        helper.openDatabase(); //打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        list = helper.getAllUser();//拿到所有用户的list

        Collections.sort(list, new Comparator<User>() {//排序
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getOrder().compareTo(rhs.getOrder());
            }
        });
        final ListView lv = (ListView) findViewById(R.id.lv_userlist);


        //将数据与adapter集合起来
        adapter = new UserAdapter(this, list, this.str);
        lv.setAdapter(adapter);//将整合好的adapter交给listview，显示给用户看

        //构建索引表视图
        layoutIndex = (LinearLayout) findViewById(R.id.layout);
        layoutIndex.setBackgroundColor(Color.parseColor("#ffffff"));


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, height);
        for (int i = 0; i < str.length; i++) {
            final TextView tv = new TextView(getBaseContext());
            tv.setLayoutParams(params);
            tv.setText(str[i]);
            tv.setPadding(10, 0, 10, 0);
            layoutIndex.addView(tv);
        }
        tv_show = (TextView) findViewById(R.id.tv);
        tv_show.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //列表联系人点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User item = (User) parent.getItemAtPosition(position);
                //int _id = Integer.parseInt(String.valueOf(item._id));

                Intent intent = new Intent(MainActivity.this, UserDetail.class);
                User user = new User();
                user._id = Integer.parseInt(String.valueOf(item._id));
                user.address = String.valueOf(item.address);
                user.company = String.valueOf(item.company);
                user.email = String.valueOf(item.email);
                user.familyPhone = String.valueOf(item.familyPhone);
                user.mobilePhone = String.valueOf(item.mobilePhone);
                user.officePhone = String.valueOf(item.officePhone);
                user.otherContact = String.valueOf(item.otherContact);
                user.position = String.valueOf(item.position);
                user.remark = String.valueOf(position);
                user.username = String.valueOf(item.username);
                user.zipCode = String.valueOf(item.zipCode);
                user.imageId = Integer.parseInt(String.valueOf(item.imageId));
                user.Favorite=Integer.parseInt(String.valueOf(item.Favorite));

                intent.putExtra("user", user);

				/*将postition作为请求码传过去  用于标识修改项的位置*/
                startActivityForResult(intent, position);
            }
        });

        lv.setCacheColorHint(Color.TRANSPARENT);  //设置ListView的背景为透明

        //批量删除功能
        final ImageButton Del=(ImageButton)findViewById(R.id.Del);
        final ImageView DelIcon=(ImageView)findViewById(R.id.DelIcon);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (deleteId == null) {
                    deleteId = new ArrayList<Integer>();
                    DelIcon.setVisibility(View.VISIBLE);
                    Del.setVisibility(View.VISIBLE);
                }
                User item = (User)parent.getItemAtPosition(position);
                Integer _id = Integer.parseInt(String.valueOf(item._id));
                ImageView markedView = (ImageView)view.findViewById(R.id.user_mark);
                if (markedView.getVisibility() == View.VISIBLE) {
                    markedView.setVisibility(View.GONE);
                    deleteId.remove(_id);
                    if (deleteId.size()==0){
                        DelIcon.setVisibility(View.INVISIBLE);
                        Del.setVisibility(View.INVISIBLE);
                    };
                } else {
                    markedView.setVisibility(View.VISIBLE);
                    deleteId.add(_id);
                }
                if (deleteId.size()!=0)
                {
                    DelIcon.setVisibility(View.VISIBLE);
                    Del.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        Del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)                 //选择对话框
                        .setTitle("确定要删除标记的"+deleteId.size()+"条记录吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(getBaseContext());
                                helper.deleteMarked(deleteId);
                                //重置视图
                                list = helper.getAllUser();//拿到所有的用户的list

                                Collections.sort(list, new Comparator<User>() {//排序
                                    @Override
                                    public int compare(User lhs, User rhs) {
                                        return lhs.getOrder().compareTo(rhs.getOrder());
                                    }
                                });
                                ListView lv = (ListView)findViewById(R.id.lv_userlist);


                                //将数据与adapter集合起来
                                adapter = new UserAdapter(getBaseContext(), list, str);
                                lv.setAdapter(adapter);//将整合好的adapter交给listview，显示给用户看
                                deleteId.clear();
                                DelIcon.setVisibility(View.INVISIBLE);
                                Del.setVisibility(View.INVISIBLE);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create()
                        .show();
            }
        });
        Drawable bgDrawable = getResources().getDrawable(R.drawable.list_bg);
        lv.setSelector(bgDrawable);

        layoutIndex.setOnTouchListener(new View.OnTouchListener() {   //滑动定位栏触摸事件
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //数据库获取list部分
                DBHelper helper = new DBHelper(getBaseContext());//获得所有用户的list
                helper.openDatabase(); //打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
                list = helper.getAllUser();//拿到所有用户的list

                Collections.sort(list, new Comparator<User>() {//排序
                    @Override
                    public int compare(User lhs, User rhs) {
                        return lhs.getOrder().compareTo(rhs.getOrder());
                    }
                });
                ListView lv = (ListView) findViewById(R.id.lv_userlist);
                adapter = new UserAdapter(getBaseContext(), list, str);
                float y;
                int index;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        layoutIndex.setBackgroundColor(Color
                                .parseColor("#606060"));
                        y = event.getY();
                        height = layoutIndex.getHeight() / str.length;
                        index = (int) (y / height);
                        if (index > -1 && index < str.length) {// 防止越界
                            String key = str[index];
                            tv_show.setVisibility(View.VISIBLE);
                            tv_show.setText(str[index]);
                            if (adapter.getSelector().containsKey(key)) {
                                int pos = adapter.getSelector().get(key);
                                lv.setSelection(pos + lv.getHeaderViewsCount());
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        y = event.getY();
                        height = layoutIndex.getHeight() / str.length;
                        index = (int) (y / height);
                        if (index > -1 && index < str.length) {// 防止越界
                            String key = str[index];
                            Log.e("滑动 外面","index="+index+str[index]);
                            tv_show.setVisibility(View.VISIBLE);
                            tv_show.setText(str[index]);
                            if (adapter.getSelector().containsKey(key)) {
                                int pos = adapter.getSelector().get(key);
                                lv.setSelection(pos + lv.getHeaderViewsCount());
                                Log.e("!滑动", "pos=" + pos);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        layoutIndex.setBackgroundColor(Color
                                .parseColor("#00ffffff"));
                        tv_show.setVisibility(View.INVISIBLE);
                        break;
                }
                return true;
            }
        });




    }
    @Override
    public void onResume() {
        super.onResume();

        //List适配器
        List<User> list;
        //存储标记的数目
        int markedNum;
        //存储标记条目的_id号
        DBHelper helper = new DBHelper(getBaseContext());//获得所有用户的list
        helper.openDatabase(); //打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        list = helper.getAllUser();//拿到所有保密状态为privacy的用户的list

        Collections.sort(list, new Comparator<User>() {
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getOrder().compareTo(rhs.getOrder());
            }
        });
        ListView lv = (ListView) findViewById(R.id.lv_userlist);

        //将数据与adapter集合起来
        adapter = new UserAdapter(getBaseContext(), list, this.str);
        lv.setAdapter(adapter);//将整合好的adapter交给listview，显示给用户看
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //过滤函数
    private void filterData(String filterStr) {
        List<User> listnew = new ArrayList<User>();
        DBHelper helper = new DBHelper(getBaseContext());//获得所有用户的list
        helper.openDatabase(); //打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        list = helper.getAllUser();//拿到所有保密状态为privacy的用户的list
        Collections.sort(list, new Comparator<User>() {//排序
            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getOrder().compareTo(rhs.getOrder());
            }
        });


        if (TextUtils.isEmpty(filterStr)) {
            listnew = list;
        } else {
            listnew.clear();
            for (User user : list) {
                String name = user.username;
                if (name.getBytes().length != name.length())
                {
                    if (name.indexOf(filterStr.toString()) != -1 ||new CharacterParser().getSelling(name).startsWith(filterStr.toString())) {
                        listnew.add(user);
                    }
                }else{
                    if (name.indexOf(filterStr.toString()) != -1 ||name.startsWith(filterStr.toString())) {
                        listnew.add(user);
                    }
                }

            }
        }
        adapter = new UserAdapter(getBaseContext(), listnew, this.str);
    }
}
