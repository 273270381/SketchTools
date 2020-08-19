package ezviz.ezopensdk.been.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @description 主键
 * @author hejunfeng
 * @time 2020/7/17 0017 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {
    boolean autoincrement() default true;

    String column();
}
