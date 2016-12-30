package ContactDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ContactUser.User;

/**
 * Created by lhj30 on 2016/12/28.
 */

public class DBHelper {
    public static final String DB_DBNAME="contact";

    public static final String DB_TABLENAME="user";

    public static final int VERSION = 4;

    public static SQLiteDatabase dbInstance;

    private MyDBHelper myDBHelper;

    private StringBuffer tableCreate;

    private Context context;

    public DBHelper(Context context) {
        this.context = (Context) context;
    }

    public void openDatabase() {
        if(dbInstance == null) {
            myDBHelper = new MyDBHelper(context,DB_DBNAME,VERSION);
            dbInstance = myDBHelper.getWritableDatabase();
        }
    }

    /**
     * 往数据库里面的user表插入一条数据，若失败返回-1
     * @param user
     * @return   失败返回-1
     */
    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.username);
        values.put("mobilephone", user.mobilePhone);
        values.put("officephone", user.officePhone);
        values.put("familyphone", user.familyPhone);
        values.put("address", user.address);
        values.put("othercontact", user.otherContact);
        values.put("email",user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipcode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageid", user.imageId);
        values.put("favorite",user.Favorite);
        return dbInstance.insert(DB_TABLENAME, null, values);
    }

    /**
     * 获得数据库中所有的用户，将每一个用户放到一个User item中去，然后再将User item放到list里面去返回
     * @return list
     */

    public List<User> getAllUser() {
        List<User> list = new ArrayList();
        Cursor cursor = null;
        cursor = dbInstance.query(DB_TABLENAME,
                new String[]{"_id","name","mobilephone","officephone","familyphone","address","othercontact","email","position","company","zipcode","remark","imageid","favorite"},
                null,
                null,
                null,
                null,
                null);

        while(cursor.moveToNext()) {
            User item=new User();
            item._id=cursor.getInt(cursor.getColumnIndex("_id"));
            item.username=cursor.getString(cursor.getColumnIndex("name"));
            item.mobilePhone=cursor.getString(cursor.getColumnIndex("mobilephone"));
            item.officePhone=cursor.getString(cursor.getColumnIndex("officephone"));
            item.familyPhone=cursor.getString(cursor.getColumnIndex("familyphone"));
            item.address=cursor.getString(cursor.getColumnIndex("address"));
            item.otherContact=cursor.getString(cursor.getColumnIndex("othercontact"));
            item.email=cursor.getString(cursor.getColumnIndex("email"));
            item.position= cursor.getString(cursor.getColumnIndex("position"));
            item.company= cursor.getString(cursor.getColumnIndex("company"));
            item.zipCode=cursor.getString(cursor.getColumnIndex("zipcode"));
            item.remark= cursor.getString(cursor.getColumnIndex("remark"));
            item.imageId= cursor.getInt(cursor.getColumnIndex("imageid"));
            item.Favorite=cursor.getInt(cursor.getColumnIndex("favorite"));
            list.add(item);
        }
        return list;
    }

    /*
    修改数据库
    */
    public void modify(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.username);
        values.put("mobilephone", user.mobilePhone);
        values.put("officephone", user.officePhone);
        values.put("familyphone", user.familyPhone);
        values.put("address", user.address);
        values.put("othercontact", user.otherContact);
        values.put("email",user.email);
        values.put("position", user.position);
        values.put("company", user.company);
        values.put("zipcode", user.zipCode);
        values.put("remark", user.remark);
        values.put("imageid", user.imageId);
        values.put("favorite",user.Favorite);

        dbInstance.update(DB_TABLENAME, values, "_id=?", new String[]{String.valueOf(user._id)});
    }

    public void delete(int _id) {
        dbInstance.delete(DB_TABLENAME, "_id=?", new String[]{String.valueOf(_id)});
    }


    public List<User> getUsers(String condition) {
        List<User> list = new ArrayList();
        String strSelection = "";
        String sql = "select * from " + DB_TABLENAME + " where 1=1 and (name like '%" + condition + "%' " +
                "or mobilephone like '%" + condition + "%' or familyphone like '%" + condition + "%' " +
                "or officephone like '%" + condition + "%')" + strSelection;
        Cursor cursor = dbInstance.rawQuery(sql, null);
        while(cursor.moveToNext()) {
            User item=new User();
            item._id=cursor.getInt(cursor.getColumnIndex("_id"));
            item.username=cursor.getString(cursor.getColumnIndex("name"));
            item.mobilePhone=cursor.getString(cursor.getColumnIndex("mobilephone"));
            item.officePhone=cursor.getString(cursor.getColumnIndex("officephone"));
            item.familyPhone=cursor.getString(cursor.getColumnIndex("familyphone"));
            item.address=cursor.getString(cursor.getColumnIndex("address"));
            item.otherContact=cursor.getString(cursor.getColumnIndex("othercontact"));
            item.email=cursor.getString(cursor.getColumnIndex("email"));
            item.position= cursor.getString(cursor.getColumnIndex("position"));
            item.company= cursor.getString(cursor.getColumnIndex("company"));
            item.zipCode=cursor.getString(cursor.getColumnIndex("zipcode"));
            item.remark= cursor.getString(cursor.getColumnIndex("remark"));
            item.imageId= cursor.getInt(cursor.getColumnIndex("imageid"));
            item.Favorite=cursor.getInt(cursor.getColumnIndex("favorite"));
            list.add(item);
        }
        return list;
    }

    public void deleteMarked(ArrayList<Integer> deleteId) {
        StringBuffer  strDeleteId = new StringBuffer();
        strDeleteId.append("_id=");
        for(int i=0;i<deleteId.size();i++) {
            if(i!=deleteId.size()-1) {
                strDeleteId.append(deleteId.get(i) + " or _id=");
            } else {
                strDeleteId.append(deleteId.get(i));
            }
        }
        dbInstance.delete(DB_TABLENAME, strDeleteId.toString(), null);
        //System.out.println(strDeleteId.toString());
    }
    public void deleteAll()
    {
        String sql = "delete from " + DB_TABLENAME;
        dbInstance.execSQL(sql);
    }


    class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context, String name,
                          int version) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            tableCreate = new StringBuffer();
            tableCreate.append("create table ")
                    .append(DB_TABLENAME)
                    .append(" (")
                    .append("_id integer primary key autoincrement,")
                    .append("name text,")
                    .append("mobilephone text,")
                    .append("officephone text,")
                    .append("familyphone text,")
                    .append("address text,")
                    .append("othercontact text,")
                    .append("email text,")
                    .append("position text,")
                    .append("company text,")
                    .append("zipcode text,")
                    .append("remark text,")
                    .append("imageid int,")
                    .append("favorite int")
                    .append(")");
            System.out.println(tableCreate.toString());
            db.execSQL(tableCreate.toString());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String sql = "drop table if exists " + DB_TABLENAME;
            db.execSQL(sql);
            myDBHelper.onCreate(db);
        }

    }

}
