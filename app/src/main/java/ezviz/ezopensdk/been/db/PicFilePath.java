package ezviz.ezopensdk.been.db;

import java.io.Serializable;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
@Table(tableName = "picfilepath")
public class PicFilePath implements Serializable {
    @PrimaryKey(autoincrement = true, column = "id")
    private int id;

    @Column(column = "path")
    private String path;

    @Column(column = "name")
    private String name;


    public PicFilePath() {
    }

    public PicFilePath(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }
}
