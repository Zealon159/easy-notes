package cn.zealon.notes.common.utils;

import org.joda.time.DateTime;

/**
 * 日期工具
 * @author: zealon
 * @since: 2020/12/24
 */
public class DateUtil {

    public final static String YMD_HMS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取当前字符串日期
     * @return
     */
    public static String getNowDateString(){
        return DateTime.now().toString(YMD_HMS);
    }
}
