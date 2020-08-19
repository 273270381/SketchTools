package ezviz.ezopensdk.been.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description 表
 * @author hejunfeng
 * @time 2020/7/17 0017 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public  @interface Table {
    String tableName();
}
