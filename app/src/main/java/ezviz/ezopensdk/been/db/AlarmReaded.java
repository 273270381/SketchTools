package ezviz.ezopensdk.been.db;

import java.io.Serializable;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
@Table(tableName = "alarmReaded")
public class AlarmReaded implements Serializable {
    @PrimaryKey(autoincrement = true, column = "id")
    private int id;

    @Column(column = "type0")
    private String type0;
    @Column(column = "type1")
    private String type1;
    @Column(column = "type2")
    private String type2;
    @Column(column = "type3")
    private String type3;
    @Column(column = "type4")
    private String type4;
    @Column(column = "type5")
    private String type5;
    @Column(column = "type6")
    private String type6;

    public int getId() {
        return id;
    }

    public String getType0() {
        return type0;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public String getType3() {
        return type3;
    }

    public String getType4() {
        return type4;
    }

    public String getType5() {
        return type5;
    }

    public String getType6() {
        return type6;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType0(String type0) {
        this.type0 = type0;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public void setType4(String type4) {
        this.type4 = type4;
    }

    public void setType5(String type5) {
        this.type5 = type5;
    }

    public void setType6(String type6) {
        this.type6 = type6;
    }
}
