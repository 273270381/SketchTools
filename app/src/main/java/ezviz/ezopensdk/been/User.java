package ezviz.ezopensdk.been;

import java.io.Serializable;

public class User implements Serializable {
    private String account;
    private String birthday;
    private String email;
    private String name;
    private String phone;
    private String sex;
    private String userId;


    public User() {
    }

    public User(String account, String birthday, String email, String name, String phone, String sex, String userId) {
        this.account = account;
        this.birthday = birthday;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.sex = sex;
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getSex() {
        return sex;
    }

    public String getUserId() {
        return userId;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
