package com.example.lhj30.socialcontact;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import ContactDB.DBHelper;
import ContactUser.User;
import Pinyin.CharacterParser;
/**
 * Created by lhj30 on 2016/12/29.
 */

public class DetailModify extends Activity implements ViewFactory {
    //文字控件
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


    //按钮控件
    Button btn_save;
    Button btn_return;

    //头像的按钮
    ImageButton imageButton;

    //判断头像是否变化
    boolean imageChanged = false;
    int currentImagePosition;
    int previousImagePosition;

    //判断数据是否变化
    boolean isDataChanged = false;

    //拥有一个user实例，这个对象由Intent传过来
    User user;
    Gallery gallery;
    ImageSwitcher is;

    //图片选择view
    View imageChooseView;
    //图片选择对话框
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
        setContentView(R.layout.detailmodify);

        //获得Intent
        Intent intent = getIntent();
        //从Intent中得到需要的user对象
        user = (User) intent.getSerializableExtra("user");
        // 加载数据,往控件上赋值
        loadUserData();

        //为按钮添加监听类
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(et_name.getText().toString().equals(""))
                {
                    Toast.makeText(DetailModify.this, "请输入联系人姓名", Toast.LENGTH_LONG);
                }else{
                    setTitle("modify");
                    modify();
                    //flag = false;
                    finish();
                }
            }
        });

        btn_return.setOnClickListener(new View.OnClickListener() {

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



        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loadImage();//加载imageChooseView，只加载一次
                initImageChooseDialog();//加载imageChooseDialog，只加载一次
                imageChooseDialog.show();


            }
        });

    }


    public void loadUserData() {
        // 获得EditText控件
        et_name = (EditText) findViewById(R.id.username_Modify);
        et_mobilePhone = (EditText) findViewById(R.id.mobilephone_modify);
        et_officePhone = (EditText) findViewById(R.id.officephone_modify);
        et_familyPhone = (EditText) findViewById(R.id.familyphone_modify);
        et_position = (EditText) findViewById(R.id.position_modify);
        et_company = (EditText) findViewById(R.id.company_modify);
        et_address = (EditText) findViewById(R.id.address_modify);
        //et_zipCode = (EditText) findViewById(R.id.zipcode);
        et_otherContact = (EditText) findViewById(R.id.othercontact_modify);
        et_email = (EditText) findViewById(R.id.email_modify);
        et_remark = (EditText) findViewById(R.id.remark_modify);

        // 获得Button控件
        btn_save = (Button) findViewById(R.id.save_modify);
        btn_return = (Button) findViewById(R.id.btn_return_modify);
        imageButton = (ImageButton) findViewById(R.id.image_button_modify);

        // 为控件赋值
        et_name.setText(user.username);
        et_mobilePhone.setText(user.mobilePhone);
        et_familyPhone.setText(user.familyPhone);
        et_officePhone.setText(user.officePhone);
        et_company.setText(user.company);
        et_address.setText(user.address);
        //et_zipCode.setText(user.zipCode);
        et_otherContact.setText(user.otherContact);
        et_email.setText(user.email);
        et_remark.setText(user.remark);
        et_position.setText(user.position);
        imageButton.setImageResource(user.imageId);
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
        //user.Favorite=flagF;
        if (imageChanged) {
            user.imageId = images[currentImagePosition % images.length];
        }

        DBHelper helper = new DBHelper(this);
        //打开数据库
        helper.openDatabase();
        helper.modify(user);
        isDataChanged = true;
    }


    /**
     * 装载头像
     */
    public void loadImage() {
        if(imageChooseView == null) {
            LayoutInflater li = LayoutInflater.from(DetailModify.this);
            imageChooseView = li.inflate(R.layout.imageswitch, null);
            gallery = (Gallery)imageChooseView.findViewById(R.id.gallery);
            gallery.setAdapter(new ImageAdapter(this));
            gallery.setSelection(images.length/2);
            is = (ImageSwitcher)imageChooseView.findViewById(R.id.imageswitch);
            is.setFactory(this);
            gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
        super.onDestroy();
    }
}
