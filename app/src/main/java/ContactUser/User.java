package ContactUser;

import java.io.Serializable;
/**
 * Created by lhj30 on 2016/12/28.
 */

public class User implements Serializable {
    public int _id;

    public String username;

    public String mobilePhone;

    public String officePhone;

    public String familyPhone;

    public String position;

    public String company;

    public String address;

    public String zipCode;

    public String email;

    public String otherContact;

    public String remark;

    public int imageId;

    public int privacy;//1代表隐私用户  0代表普通用户

    public int Favorite;//代表是否被收藏

    public String getOrder() {
        if (zipCode.equals("#"))
            return "{";
        return zipCode;
    }
}
