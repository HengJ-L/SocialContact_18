package com.example.lhj30.socialcontact;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import java.util.ArrayList;
import java.util.List;

import ContactDB.DBHelper;
import ContactUser.User;
import Pinyin.CharacterParser;
/**
 * Created by lhj30 on 2016/12/28.
 */

public class UserDetail extends Activity implements ViewFactory{
    int flagF = 0;
    int calormes = 0;

    EditText et_name;
    EditText et_mobilePhone;
    EditText et_officePhone;
    EditText et_familyPhone;
    EditText et_position;
    EditText et_company;
    EditText et_address;
    //EditText et_zipCode;
    EditText et_otherContact;
    EditText et_email;
    EditText et_remark;

    Button btn_save;
    ImageButton btn_return;
    Button btn_delete;
    //头像的按钮
    ImageButton imageButton;
    //用flag来判断按钮的状态   false表示查看点击修改状态  true表示点击修改保存状态
    boolean flag = false;
    boolean imageChanged = false;
    boolean isDataChanged = false;

    int currentImagePosition;
    int previousImagePosition;

    //表示状态：打电话，发短信，发邮件
    String[] callData;


    //拥有一个user实例，这个对象由Intent传过来
    User user;
    Gallery gallery;
    ImageSwitcher is;

    //选择View对象
    View numChooseView;
    View numChooseView2;
    View imageChooseView;

    //号码选择的对话框
    AlertDialog numChooseDialog;
    AlertDialog numChooseDialog2;
    AlertDialog imageChooseDialog;
    /**
     * 所有的图像图片
     */
    private int[] images
            = new int[]{R.drawable.icon
            , R.drawable.image1, R.drawable.image2, R.drawable.image3
            , R.drawable.image4, R.drawable.image5, R.drawable.image6
            , R.drawable.image7, R.drawable.image8, R.drawable.image9
            , R.drawable.image10, R.drawable.image11, R.drawable.image12
            , R.drawable.image13, R.drawable.image14, R.drawable.image15
            , R.drawable.image16, R.drawable.image17, R.drawable.image18
            , R.drawable.image19, R.drawable.image20};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdetail);

        //获得Intent
        Intent intent = getIntent();
        //从Intent中得到需要的user对象
        user = (User) intent.getSerializableExtra("user");
        // 加载数据,往控件上赋值
        loadUserData();
        // 设置EditText不可编辑
        setEditTextDisable();

        //为按钮添加监听类
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                DBHelper helper = new DBHelper(getApplicationContext());
                helper.openDatabase(); //打开数据库，
                List<User> list;
                list=helper.getUsers(user.username);
                User user2=list.get(0);
                Intent intent = new Intent(UserDetail.this, DetailModify.class);
                User item = new User();
                item._id = Integer.parseInt(String.valueOf(user2._id));
                item.address = String.valueOf(user2.address);
                item.company = String.valueOf(user2.company);
                item.email = String.valueOf(user2.email);
                item.familyPhone = String.valueOf(user2.familyPhone);
                item.mobilePhone = String.valueOf(user2.mobilePhone);
                item.officePhone = String.valueOf(user2.officePhone);
                item.otherContact = String.valueOf(user2.otherContact);
                item.position = String.valueOf(user2.position);
                item.remark = String.valueOf(user.remark);
                item.username = String.valueOf(user2.username);
                item.zipCode = String.valueOf(user2.zipCode);
                item.imageId = Integer.parseInt(String.valueOf(user2.imageId));
                item.Favorite=Integer.parseInt(String.valueOf(user2.Favorite));

                int position=Integer.parseInt(item.remark);
                intent.putExtra("user",item);

                /*将postition作为请求码传过去  用于标识修改项的位置*/
                startActivityForResult(intent,position);
            }
        });


        btn_return.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isDataChanged) {
                    setResult(4);
                } else {
                    setResult(5);
                }
                finish();
            }
        });

        btn_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserDetail.this).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                                setResult(4);
                                finish();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setTitle("是否要删除?").create().show();

            }
        });


        imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImage();//加载imageChooseView，只加载一次
                initImageChooseDialog();//加载imageChooseDialog，只加载一次
                imageChooseDialog.show();


            }
        });

        /**
         * 添加Button
         */
        final ImageButton favorite=(ImageButton)findViewById(R.id.Favorite);
        final ImageView Fview=(ImageView)findViewById(R.id.Fview);
        ImageButton call = (ImageButton) findViewById(R.id.call);
        ImageButton mes = (ImageButton) findViewById(R.id.mes);
        ImageButton ema = (ImageButton) findViewById(R.id.ema);
        /**
         * 为每一个Button添加事件
         */
        call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callData == null) {
                    loadAvailableCallData();
                }
                if (callData.length == 0) {
                    //提示没有可用的号码
                    Toast.makeText(UserDetail.this, "没有可用的号码！", Toast.LENGTH_LONG).show();
                } else if (callData.length == 1) {
                    //如果之有一个可用的号码，这直接使用这个号码拨出
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + callData[0]));
                    if (ActivityCompat.checkSelfPermission(UserDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                } else {
                    //如果有2个或者2个以上号码，弹出号码选择对话框
                    if (numChooseDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetail.this);
                        LayoutInflater inflater = LayoutInflater.from(UserDetail.this);
                        numChooseView = inflater.inflate(R.layout.numchoose, null);
                        ListView lv = (ListView) numChooseView.findViewById(R.id.num_list);
                        ArrayAdapter array =
                                new ArrayAdapter(UserDetail.this, android.R.layout.simple_list_item_1, callData);
                        lv.setAdapter(array);
                        lv.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                                    long arg3) {
                                String num = String.valueOf(arg0.getItemAtPosition(arg2));
                                Intent intent = null;
                                intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +num));
                                if (ActivityCompat.checkSelfPermission(UserDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                startActivity(intent);
                                //对话框消失
                                numChooseDialog.dismiss();
                                refresh();
                            }});


                        builder.setView(numChooseView);
                        numChooseDialog = builder.create();

                    }
                    numChooseDialog.show();
                }
            }
        });
        mes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callData == null) {
                    loadAvailableCallData();
                }
                if(callData.length == 0) {
                    //提示没有可用的号码
                    Toast.makeText(UserDetail.this, "没有可用的号码！", Toast.LENGTH_LONG).show();
                } else if(callData.length == 1) {
                    //如果之后又一个可用的号码，这直接使用这个号码拨出
                    Intent intent =
                            new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + callData[0]));
                    startActivity(intent);
                } else {
                    if(numChooseDialog2 == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserDetail.this);
                        LayoutInflater inflater = LayoutInflater.from(UserDetail.this);
                        numChooseView2 = inflater.inflate(R.layout.numchoose2, null);
                        ListView lv = (ListView)numChooseView2.findViewById(R.id.num_list2);
                        ArrayAdapter array =
                                new ArrayAdapter(UserDetail.this,android.R.layout.simple_list_item_1,callData);
                        lv.setAdapter(array);
                        lv.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String num = String.valueOf(parent.getItemAtPosition(position));
                                Intent intent = null;
                                intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + num));
                                startActivity(intent);
                                //对话框消失
                                numChooseDialog2.dismiss();
                                refresh();
                            }
                        });

                        builder.setView(numChooseView2);
                        numChooseDialog2 = builder.create();
                    }
                    numChooseDialog2.show();
                }
            }
        });
        ema.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.email.equals("")) {
                    Toast.makeText(UserDetail.this, "没有可用的邮箱！", Toast.LENGTH_LONG).show();
                } else {
                    Uri emailUri = Uri.parse("mailto:" + user.email);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, emailUri);
                    startActivity(intent);
                }
            }
        });

        //设置收藏button图标及点击事件
        if (user.Favorite==1)
        {
            Fview.setImageResource(R.drawable.like_fill);
        }
        favorite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.Favorite==1)
                {
                    flagF=0;
                    //Toast.makeText(UserDetail.this, "取消收藏", Toast.LENGTH_LONG).show();
                    modify();
                    Fview.setImageResource(R.drawable.like);
                }else{
                    flagF=1;
                    //Toast.makeText(UserDetail.this, "收藏", Toast.LENGTH_LONG).show();
                    modify();
                    Fview.setImageResource(R.drawable.like_fill);
                }
            }
        });


    }

    public void  refresh(){
        onCreate(null);
    }

    /**
     * 获得布局文件中的控件，并且根据传递过来user对象对控件进行赋值
     */
    public void loadUserData() {
        //获得linearlayout控件
        LinearLayout et_mobilePhoneL = (LinearLayout) findViewById(R.id.mobilephoneL);
        LinearLayout et_officePhoneL = (LinearLayout) findViewById(R.id.officephoneL);
        LinearLayout et_familyPhoneL = (LinearLayout) findViewById(R.id.familyphoneL);
        LinearLayout et_positionL = (LinearLayout) findViewById(R.id.positionL);
        LinearLayout et_companyL = (LinearLayout) findViewById(R.id.companyL);
        LinearLayout et_addressL = (LinearLayout) findViewById(R.id.addressL);
        LinearLayout et_otherContactL = (LinearLayout) findViewById(R.id.othercontactL);
        LinearLayout et_emailL = (LinearLayout) findViewById(R.id.emailL);

        // 获得EditText控件
        et_name = (EditText) findViewById(R.id.username);
        et_mobilePhone = (EditText) findViewById(R.id.mobilephone);
        et_officePhone = (EditText) findViewById(R.id.officephone);
        et_familyPhone = (EditText) findViewById(R.id.familyphone);
        et_position = (EditText) findViewById(R.id.position);
        et_company = (EditText) findViewById(R.id.company);
        et_address = (EditText) findViewById(R.id.address);
        et_otherContact = (EditText) findViewById(R.id.othercontact);
        et_email = (EditText) findViewById(R.id.email);
        et_remark = (EditText) findViewById(R.id.remark);

        // 获得Button控件
        btn_save = (Button) findViewById(R.id.save);
        btn_return = (ImageButton) findViewById(R.id.btn_return);
        btn_delete = (Button) findViewById(R.id.delete);
        imageButton = (ImageButton) findViewById(R.id.image_button);
        //favorite=(ImageButton)findViewById(R.id.Favorite);

        // 为控件赋值
        et_name.setText(user.username);
        if (!user.mobilePhone.equals(""))
        {
            et_mobilePhoneL.setVisibility(View.VISIBLE);
            et_mobilePhone.setText(user.mobilePhone);
        }
        if(!user.familyPhone.equals(""))
        {
            et_familyPhoneL.setVisibility(View.VISIBLE);
            et_familyPhone.setText(user.familyPhone);
        }
        if(!user.officePhone.equals(""))
        {
            et_officePhoneL.setVisibility(View.VISIBLE);
            et_officePhone.setText(user.officePhone);
        }
        if(!user.company.equals(""))
        {
            et_companyL.setVisibility(View.VISIBLE);
            et_company.setText(user.company);
        }
        if(!user.address.equals(""))
        {
            et_addressL.setVisibility(View.VISIBLE);
            et_address.setText(user.address);
        }
        if (!user.otherContact.equals(""))
        {
            et_otherContactL.setVisibility(View.VISIBLE);
            et_otherContact.setText(user.otherContact);
        }
        if(!user.email.equals(""))
        {
            et_emailL.setVisibility(View.VISIBLE);
            et_email.setText(user.email);
        }
        if(!user.position.equals(""))
        {
            et_positionL.setVisibility(View.VISIBLE);
            et_position.setText(user.position);
        }
        et_remark.setText(user.remark);
        imageButton.setImageResource(user.imageId);
    }

    /**
     * 修改值后重新加载数据
     */
    public void loadUserDataNew(User user) {
        //获得linearlayout控件
        LinearLayout et_mobilePhoneL = (LinearLayout) findViewById(R.id.mobilephoneL);
        LinearLayout et_officePhoneL = (LinearLayout) findViewById(R.id.officephoneL);
        LinearLayout et_familyPhoneL = (LinearLayout) findViewById(R.id.familyphoneL);
        LinearLayout et_positionL = (LinearLayout) findViewById(R.id.positionL);
        LinearLayout et_companyL = (LinearLayout) findViewById(R.id.companyL);
        LinearLayout et_addressL = (LinearLayout) findViewById(R.id.addressL);
        LinearLayout et_otherContactL = (LinearLayout) findViewById(R.id.othercontactL);
        LinearLayout et_emailL = (LinearLayout) findViewById(R.id.emailL);

        // 获得EditText控件
        et_name = (EditText) findViewById(R.id.username);
        et_mobilePhone = (EditText) findViewById(R.id.mobilephone);
        et_officePhone = (EditText) findViewById(R.id.officephone);
        et_familyPhone = (EditText) findViewById(R.id.familyphone);
        et_position = (EditText) findViewById(R.id.position);
        et_company = (EditText) findViewById(R.id.company);
        et_address = (EditText) findViewById(R.id.address);
        et_otherContact = (EditText) findViewById(R.id.othercontact);
        et_email = (EditText) findViewById(R.id.email);
        //et_remark = (EditText) findViewById(R.id.remark);

        // 为控件赋值
        et_name.setText(user.username);
        if (!user.mobilePhone.equals(""))
        {
            et_mobilePhoneL.setVisibility(View.VISIBLE);
            et_mobilePhone.setText(user.mobilePhone);
        }else {
            et_mobilePhoneL.setVisibility(View.GONE);
        }
        if(!user.familyPhone.equals(""))
        {
            et_familyPhoneL.setVisibility(View.VISIBLE);
            et_familyPhone.setText(user.familyPhone);
        }else{
            et_familyPhoneL.setVisibility(View.GONE);
        }
        if(!user.officePhone.equals(""))
        {
            et_officePhoneL.setVisibility(View.VISIBLE);
            et_officePhone.setText(user.officePhone);
        }else{
            et_officePhoneL.setVisibility(View.GONE);
        }
        if(!user.company.equals(""))
        {
            et_companyL.setVisibility(View.VISIBLE);
            et_company.setText(user.company);
        }else {
            et_companyL.setVisibility(View.GONE);
        }
        if(!user.address.equals(""))
        {
            et_addressL.setVisibility(View.VISIBLE);
            et_address.setText(user.address);
        }else {
            et_addressL.setVisibility(View.GONE);
        }
        if (!user.otherContact.equals(""))
        {
            et_otherContactL.setVisibility(View.VISIBLE);
            et_otherContact.setText(user.otherContact);
        }else{
            et_otherContactL.setVisibility(View.GONE);
        }
        if(!user.email.equals(""))
        {
            et_emailL.setVisibility(View.VISIBLE);
            et_email.setText(user.email);
        }else {
            et_emailL.setVisibility(View.GONE);
        }
        if(!user.position.equals(""))
        {
            et_positionL.setVisibility(View.VISIBLE);
            et_position.setText(user.position);
        }else {
            et_positionL.setVisibility(View.GONE);
        }
        imageButton.setImageResource(user.imageId);
    }

    /**
     * 设置EditText为不可用
     */
    private void setEditTextDisable() {
        et_name.setEnabled(false);
        et_mobilePhone.setEnabled(false);
        et_officePhone.setEnabled(false);
        et_familyPhone.setEnabled(false);
        et_position.setEnabled(false);
        et_company.setEnabled(false);
        et_address.setEnabled(false);
        //et_zipCode.setEnabled(false);
        et_otherContact.setEnabled(false);
        et_email.setEnabled(false);
        //et_remark.setEnabled(false);
        imageButton.setEnabled(false);
        //favorite.setEnabled(false);

        setColorToGray();

    }

    /**
     * 设置EditText为可用状态
     */
    private void setEditTextAble() {
        et_name.setEnabled(true);
        et_mobilePhone.setEnabled(true);
        et_officePhone.setEnabled(true);
        et_familyPhone.setEnabled(true);
        et_position.setEnabled(true);
        et_company.setEnabled(true);
        et_address.setEnabled(true);
        //et_zipCode.setEnabled(true);
        et_otherContact.setEnabled(true);
        et_email.setEnabled(true);
        // et_remark.setEnabled(true);
        imageButton.setEnabled(true);
        //favorite.setEnabled(true);
        setColorToBlack();
    }

    /**
     *  设置显示的字体颜色为黑色
     */
    private void setColorToBlack() {

        et_name.setTextColor(Color.BLACK);
        et_mobilePhone.setTextColor(Color.BLACK);
        et_officePhone.setTextColor(Color.BLACK);
        et_familyPhone.setTextColor(Color.BLACK);
        et_position.setTextColor(Color.BLACK);
        et_company.setTextColor(Color.BLACK);
        et_address.setTextColor(Color.BLACK);
        //et_zipCode.setTextColor(Color.BLACK);
        et_otherContact.setTextColor(Color.BLACK);
        et_email.setTextColor(Color.BLACK);
        //et_remark.setTextColor(Color.BLACK);
    }

    /**
     *  设置显示的字体颜色为灰色
     */
    private void setColorToGray() {
        et_name.setTextColor(Color.GRAY);
        et_mobilePhone.setTextColor(Color.GRAY);
        et_officePhone.setTextColor(Color.GRAY);
        et_familyPhone.setTextColor(Color.GRAY);
        et_position.setTextColor(Color.GRAY);
        et_company.setTextColor(Color.GRAY);
        et_address.setTextColor(Color.GRAY);
        //et_zipCode.setTextColor(Color.GRAY);
        et_otherContact.setTextColor(Color.GRAY);
        et_email.setTextColor(Color.GRAY);
        //et_remark.setTextColor(Color.GRAY);
    }

    /**
     * 获得最新数据，创建DBHelper对象，更新数据库
     */
    private void modify() {
        user.username = et_name.getText().toString();
        user.address = et_address.getText().toString();
        user.company = et_company.getText().toString();
        user.email = et_email.getText().toString();
        user.familyPhone = et_familyPhone.getText().toString();
        user.mobilePhone = et_mobilePhone.getText().toString();
        user.officePhone = et_officePhone.getText().toString();
        user.otherContact = et_otherContact.getText().toString();
        user.position = et_position.getText().toString();
        user.remark = et_remark.getText().toString();
        CharacterParser characterParser=new CharacterParser();
        if ( characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("#")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("$")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("%")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("^")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("&")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("*")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("+")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("-")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("/")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("!")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("(")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals(")")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("?")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("{")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("}")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("[")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("]")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("|")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("~")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0, 1).equals("@")
                ||characterParser.getSelling(et_name.getText().toString()).substring(0,1).equals(" "))
        {
            user.zipCode="#";
        }else {
            user.zipCode = new CharacterParser().getSelling(et_name.getText().toString()).substring(0, 1);
        }
        user.Favorite=flagF;
        if (imageChanged) {
            user.imageId = images[currentImagePosition % images.length];
        }

        DBHelper helper = new DBHelper(this);
        //打开数据库
        helper.openDatabase();
        helper.modify(user);
        isDataChanged = true;
    }

    private void delete() {
        DBHelper helper = new DBHelper(this);
        //打开数据库
        helper.openDatabase();
        helper.delete(user._id);
    }

    /**
     * 装载头像
     */
    public void loadImage() {
        if(imageChooseView == null) {
            LayoutInflater li = LayoutInflater.from(UserDetail.this);
            imageChooseView = li.inflate(R.layout.imageswitch, null);
            gallery = (Gallery)imageChooseView.findViewById(R.id.gallery);
            gallery.setAdapter(new ImageAdapter(this));
            gallery.setSelection(images.length/2);
            is = (ImageSwitcher)imageChooseView.findViewById(R.id.imageswitch);
            is.setFactory(this);
            gallery.setOnItemSelectedListener(new OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    currentImagePosition = arg2 % images.length;
                    is.setImageResource(images[arg2 % images.length]);

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }});
        }

    }

    public void initImageChooseDialog() {
        if(imageChooseDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择图像")
                    .setView(imageChooseView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    imageChanged = true;
                    previousImagePosition = currentImagePosition;
                    imageButton.setImageResource(images[currentImagePosition%images.length]);
                }
            })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            currentImagePosition = previousImagePosition;

                        }
                    });
            imageChooseDialog = builder.create();
        }
    }
    /**
     * 装载可用的号码
     */
    public void loadAvailableCallData() {
        ArrayList<String> callNums = new ArrayList<String>();
        if(!user.mobilePhone.equals("")) {
            callNums.add(user.mobilePhone);
        }
        if(!user.familyPhone.equals("")) {
            callNums.add(user.familyPhone);
        }

        if(!user.officePhone.equals("")) {
            callNums.add(user.officePhone);
        }


        callData = new String[callNums.size()];

        for(int i=0;i<callNums.size();i++) {
            callData[i] = callNums.get(i);
        }


    }


    /**
     * 自定义头像适配器
     * @author Administrator
     *
     */
    class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return Integer.MAX_VALUE;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        /**
         * gallery从这个方法中拿到image
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(images[position%images.length]);
            iv.setAdjustViewBounds(true);
            iv.setLayoutParams(new Gallery.LayoutParams(200,200));
            iv.setPadding(15, 10, 15, 10);
            return iv;
        }

    }

    @Override
    public View makeView() {
        ImageView view = new ImageView(this);
        view.setBackgroundColor(0xff000000);
        view.setScaleType(ScaleType.FIT_CENTER);
        view.setLayoutParams(new ImageSwitcher.LayoutParams(220,220));
        return view;
    }
    /**
     * 当退出的时候，回收资源
     */
    @Override
    protected void onDestroy() {
        if(is != null) {
            is = null;
        }
        if(gallery != null) {
            gallery = null;
        }
        if(imageChooseDialog != null) {
            imageChooseDialog = null;
        }
        if(imageChooseView != null) {
            imageChooseView = null;
        }
        if(imageButton != null) {
            imageButton = null;
        }
        if(numChooseDialog != null) {
            numChooseDialog = null;
        }
        if(numChooseDialog2 != null) {
            numChooseDialog2 = null;
        }
        if(numChooseView != null) {
            numChooseView = null;
        }
        if(numChooseView2 != null) {
            numChooseView2 = null;
        }

        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        DBHelper helper = new DBHelper(getApplicationContext());//获得所有用户的list
        helper.openDatabase(); //打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。
        List<User> list;
        list=helper.getUsers(user.username);
        loadUserDataNew(list.get(0));
    }
}
