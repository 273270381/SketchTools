package ezviz.ezopensdk.been.db;

import java.io.Serializable;

/**
 * Created by hejunfeng on 2020/7/29 0029
 */

@Table(tableName = "verifycode")
public class Verifcode implements Serializable {
    @PrimaryKey(autoincrement = true, column = "id")
    private int id;

    @Column(column = "name")
    private String name;

    @Column(column = "code")
    private String code;


    public Verifcode() {
    }

    public Verifcode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
