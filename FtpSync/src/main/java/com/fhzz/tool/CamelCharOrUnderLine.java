package com.fhzz.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName: CamelCharOrUnderLine
 * @Description: TODO(数据库下划线和驼峰标识的互换)
 * @date 2017年8月11日 下午4:50:49
 */
public class CamelCharOrUnderLine {

    public static final char UNDERLINE = '_';

    /**
     *  * 将驼峰式命名的字符串和下划线标志的互转，主要为了数据库。</br>
     *  
     */
    public static String camelCharToUnderLine(String parm) {

        if (parm == null || "".equals(parm.trim())) {
            return "";
        }
        int len = parm.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = parm.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append(UNDERLINE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(Character.toLowerCase(c));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String underlineToCamel(String param) {
        if (param == null || "".equals(param.trim())) {
            return "";
        }
        StringBuilder sb = new StringBuilder(param);
        Matcher mc = Pattern.compile("_").matcher(param);
        int i = 0;
        while (mc.find()) {
            int position = mc.end() - (i++);
            // String.valueOf(Character.toUpperCase(sb.charAt(position)));
            sb.replace(position - 1, position + 1, sb.substring(position, position + 1).toUpperCase());
        }
        return sb.toString();
    }

}
