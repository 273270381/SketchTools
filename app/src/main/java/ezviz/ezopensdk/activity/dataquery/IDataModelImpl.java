package ezviz.ezopensdk.activity.dataquery;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.Temp;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public class IDataModelImpl implements DataModel{
    @Override
    public void queryData(String date, MyCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    /** 创建数据库对象 */
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.162:1433/prd_env_dts;charset=utf8","sa","sa123123");
                    //String sql = "select top 20 * from WMS_60 where NODEID = '4028812268176ca801688930b0310004'";
                    String sql = "select  f_301,f_302,f_315,f_311,f_314,f_313,f_1005,datetime from WMS_60 where NODEID = '4028812268176ca801688930b0310004'and datetime like '%"+date+"%'order by id desc";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    List<Temp> list = new ArrayList<>();
                    while (rs.next()) {
                        Temp temp = new Temp();
                        temp.setTemp(rs.getString("f_301"));
                        temp.setPh(rs.getString("f_302"));
                        temp.setOxygen(rs.getString("f_315"));
                        temp.setNitrogen(rs.getString("f_311"));
                        temp.setPermanganate(rs.getString("f_314"));
                        temp.setPhosphorus(rs.getString("f_313"));
                        temp.setPotential(rs.getString("f_1005"));
                        temp.setTime(rs.getString("datetime"));
                        list.add(temp);
                    }
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.queryData(list);
                        }
                    });
                    rs.close();
                    stmt.close();
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("数据库连接失败,请稍后重试");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getCount(MyCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            int count = 0 ;
            @Override
            public void run() {
                try {
                    /** 创建数据库对象 */
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.1.162:1433/prd_env_dts;charset=utf8","sa","sa123123");
                    String sql = "select count(*) from WMS_60 where NODEID = '4028812268176ca801688930b0310004'";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    while (rs.next()){
                        count = rs.getInt(1);
                    }
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.getCount(count);
                        }
                    });
                    rs.close();
                    stmt.close();
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("数据库连接失败,请稍后重试");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getData(int count, MyCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    /** 创建数据库对象 */
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:jtds:sqlserver://183.208.120.208:14333/prd_env_dts;charset=utf8","sa","sa123123");
                    //String sql = "select top 20 * from WMS_60 where NODEID = '4028812268176ca801688930b0310004'";
                    String sql = "select  f_301,f_302,f_315,f_311,f_314,f_313,f_1005,datetime from WMS_60 where NODEID = '4028812268176ca801688930b0310004'order by id desc";
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    List<Temp> list = new ArrayList<>();
                    long step = count / 100 ;
                    long progress = 0 ;
                    while (rs.next()) {
                        Temp temp = new Temp();
                        temp.setTemp(rs.getString("f_301"));
                        temp.setPh(rs.getString("f_302"));
                        temp.setOxygen(rs.getString("f_315"));
                        temp.setNitrogen(rs.getString("f_311"));
                        temp.setPermanganate(rs.getString("f_314"));
                        temp.setPhosphorus(rs.getString("f_313"));
                        temp.setPotential(rs.getString("f_1005"));
                        temp.setTime(rs.getString("datetime"));
                        list.add(temp);
                        long currentsize = list.size();
                        if (currentsize / step != progress){
                            progress = currentsize / step;
                            if (progress % 1 == 0){
                                long finalProgress = progress;
                                AppOperator.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callBack.sendProcess(finalProgress);
                                    }
                                });
                            }
                        }
                    }
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.sendDataList(list);
                        }
                    });
                    rs.close();
                    stmt.close();
                    connection.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
