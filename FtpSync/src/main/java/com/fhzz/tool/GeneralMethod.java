package com.fhzz.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeneralMethod {
	private static File file = null;
	private static FileReader fileReader = null;
	private static FileWriter fileWriter = null;
	private static BufferedReader bufferedReader = null;
	private static BufferedWriter bufferedWriter = null;
	private static PrintWriter printWriter = null;

	public static void main(String[] args) {
//		String s_url = "http://127.0.0.1:2222/ss/car.jpg";
//		String savePath = "d:/tmp";
//		String fileName = "test.jpg";
//		getURLToFile(s_url, savePath, fileName);
//		Map<String, String> birAgeSex = ;
//		System.out.println(getBirAgeSex("442501197005164039").get("birthday"));
		Date startTime = new Date();
//		System.out.println(dateFormat(date, "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.YEAR, 1), "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.MONTH, 1), "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.DATE, -90), "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.HOUR_OF_DAY, 1), "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.MINUTE, 1), "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(dateFormat(dateCalc(date, Calendar.SECOND, 1), "yyyy-MM-dd HH:mm:ss"));
//		Date endTime = dateCalc(startTime, Calendar.SECOND, 1200008);
//		System.out.println(getDateDiff(dateFormat(date, "yyyy-MM-dd HH:mm:ss"), dateFormat(d1, "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss"));
//		System.out.println(getDateDiff(date.getTime(),d1.getTime()));
//		System.out.println(getDateDiffByType(startTime, endTime, "minute"));
		System.out.println(getUUID_16());
	}


	/**
	 * Description:根据给定日期和格式生成不同形式的日期
	 * @param date :日期
	 * @param format ：返回日期格式(eg: yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String dateFormat(Date date, String format) {
		String strdate = "";
		if (date != null) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			strdate = df.format(date);
		}
		return strdate;
	}

	/**
	 * Description: 字符串类型日期转日期类型
	 * @param strdate：字符串类型日期
	 * @param format：字符串类型日期格式 (格式为2006-11-04(format="yyyy-MM-dd")或2006/11/04(format="yyyy/MM/dd"))
	 * @return
	 */
	public static Date strToDate(String strdate, String format) {
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat(format);
		try {
			date = df.parse(strdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Description:double类型的数值设置四舍五入保留小数位数
	 * @param doubValue：double类型数值
	 * @param scale：保留小数位数
	 * @return
	 */
	public static double round(double doubValue, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(doubValue));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * Description:double 类型的浮点数相加求准确值
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * Description:double 类型的浮点数相减求准确值
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * Description:double 类型的浮点数相乘求准确值
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * Description:double 类型的浮点数相除得相对的准确值，当除不尽时，由scale指定精度，以后的数字四舍五入
	 * @param v1
	 * @param v2
	 * @param scale:保留小数位数
	 * @return
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * @Description:日期的计算:根据给定日期(时间)计算之前/后的日期(时间) 小时等的计算 amount-计算的数字,可以为负数
	 * @param date
	 * @param calendarField --通过calendarField[Calendar.SECOND]参数来控制是对年、月、日、时、分、秒的计算
	 *                      Calendar.YEAR --年 ,Calendar.MONTH --月, Calendar.DATE --日
	 *                      Calendar.HOUR_OF_DAY --时, Calendar.MINUTE --分
	 *                      Calendar.SECOND --秒 ,Calendar.MILLISECOND --毫秒
	 * @param amount        --计算的数字,可以为负数，正数为加，负数为减
	 * @return
	 */
	public static Date dateCalc(Date date, int calendarField, int amount) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendarField, amount);
		date = calendar.getTime();
		return date;
	}

	public static String getStr(String s) {
		String s2 = "";
		if (s == null || s.trim().equals("")) {// "||"为短路操作符
			s2 = "";
		} else {
			s2 = s.trim();
		}
		try {
			s2 = new String(s2.getBytes("iso-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s2;
	}

	/**
	 * Description:获得uuid(32位的主键)
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * Description:得到16位的UUID-(数字)
	 * @return
	 */
	public static String getUUID_16() {
		int machineId = 1;// 最大支持1-9个集群机器部署
		int hashCodeV = UUID.randomUUID().toString().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		String string = machineId + String.format("%015d", hashCodeV);
		return string;
	}
	

	public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
			"o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
			"U", "V", "W", "X", "Y", "Z" };
 
	/**
	 * Description:获得8位的UUID-(码)
	 * @return
	 */
	public static String getUUID_8() {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = UUID.randomUUID().toString().replace("-", "");
		for (int i = 0; i < 8; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(chars[x % 0x3E]);
		}
		return shortBuffer.toString();
 
	}

	// 读文件
	public static String getReadString(File file) {
		String fileContents = "";
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String readLineValue = "";
			while ((readLineValue = bufferedReader.readLine()) != null) {
				fileContents = fileContents + readLineValue + "\r\n";
			}
		} catch (FileNotFoundException e) {
			// System.out.println("FileNotFoundException:"+e);
		} catch (IOException e) {
			// System.out.println("IOException:"+e);
		}
		return fileContents;
	}

	// 读文件
	public String getReadString2(File file) {
		String fileContents = "";
		try {
			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String readLineValue = "";
			while ((readLineValue = bufferedReader.readLine()) != null) {
				fileContents = fileContents + readLineValue + ",";
			}
		} catch (FileNotFoundException e) {
			// System.out.println("FileNotFoundException:"+e);
		} catch (IOException e) {
			// System.out.println("IOException:"+e);
		}
		return fileContents;
	}

	// 写文件
	public static void setWriteString(File file, String writeString) {
		try {
			fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.newLine();
			bufferedWriter.write(writeString);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// System.out.println("IOException:"+e);
		}
	}
	
	/**
	 * Description:
	 * @param filePath :文件路径
	 * @param fileName ：文件名称
	 * @param writeString ：写入的文件内容
	 * @param isNewLine： 是否在新的一行写入(true--在新行写入，false--在行尾追加)
	 */
	public static void setWriteString(String filePath,String fileName, String writeString,boolean isNewLine) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
//				System.out.println("文件/目录已经存在");
			} else {
				file.mkdirs();
//				System.out.println("文件/目录不存在，创建文件/目录");
			}
			file = new File( filePath+File.separator+fileName);
			fileWriter = new FileWriter(file, true);
			bufferedWriter = new BufferedWriter(fileWriter);
			if (isNewLine) {
				bufferedWriter.newLine();
			}
			bufferedWriter.write(writeString);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			// System.out.println("IOException:"+e);
		}
	}

	/**
	 * Description:带逗号的字符串转成字符串数组
	 * @param synid
	 * @return
	 */
	public static String[] strToStrArray(String synid) {
		String[] idsArray;
		if (synid == null || "".equals(synid)) {
			idsArray = null;
		} else if (synid.indexOf(",") > -1) {
			idsArray = synid.split(",");
		} else {
			idsArray = new String[] { synid };// 只有一个id的情况，没有逗号
		}
		return idsArray;
	}

	/**
	 * Description:字符串数组转换成以逗号连接的字符串(同步数据在批量删除或者确认时使用到)
	 * @param ids
	 * @return
	 */
	public static String strArrayToStr(String[] ids) {
		if (null == ids || ids.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (String s : ids) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(s);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 使用文件通道的方式复制文件
	 * 
	 * @param s -- 源文件
	 * @param t -- 复制到的新文件
	 * @author: liuxt
	 */

	public static void fileChannelCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 匹配是否包含数字
	 * 
	 * @param str 可能为中文，也可能是-19162431.1254，不使用BigDecimal的话，变成-1.91624311254E7
	 * @return
	 * @author liuxutao
	 * @date 2016年11月14日下午7:41:22
	 */
	public static boolean isNumeric(String str) {
		// 该正则表达式可以匹配所有的数字 包括负数
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		String bigStr;
		try {
			bigStr = new BigDecimal(str).toString();
		} catch (Exception e) {
			return false;// 异常 说明包含非数字。
		}

		Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
		if (isNum.matches()) {
			return true;
		}
		return false;
	}
	
	/**
	 * Description:判断字符串中是否含有中文
	 * @param str
	 * @return
	 */
	public static Boolean isGB2312(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;

	}

	/**
	 * @Title:
	 * @Description: 从URL中下载文件，塔吊设备对接接口需要用到
	 * @param @param s_url -- url地址
	 * @param @param savePath --文件保存路径
	 * @param @param fileName --保存的文件名称
	 * @author liuxutao
	 * @date 2018-4-24 下午4:00:19
	 */
//	public static void getFileFromURL(String s_url, String savePath, String fileName) {
//
//		try {
//			File file = new File(savePath + File.separator + fileName);
//			URL url;
//			url = new URL(s_url);
//			FileUtils.copyURLToFile(url, file);
//			System.out.println("download successfull!");
//		} catch (MalformedURLException e) {
//			System.out.println(savePath + File.separator + fileName);
//			System.out.println(s_url);
//			e.printStackTrace();
//		} catch (IOException e) {
//			System.out.println(savePath + File.separator + fileName);
//			System.out.println(s_url);
//			e.printStackTrace();
//		}
//	}

	/**
	 * 通过身份证号码获取出生日期、性别、年龄
	 * 
	 * @param certificateNo
	 * @return 返回的出生日期格式：1990-01-01 性别格式：F-女，M-男
	 */
	public static Map<String, String> getBirAgeSex(String certificateNo) {
		String birthday = "";
		String age = "";
		String sexCode = "";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		char[] number = certificateNo.toCharArray();
		boolean flag = true;
		if (number.length == 15) {
			for (int x = 0; x < number.length; x++) {
				if (!flag)
					return new HashMap<String, String>();
				flag = Character.isDigit(number[x]);
			}
		} else if (number.length == 18) {
			for (int x = 0; x < number.length - 1; x++) {
				if (!flag)
					return new HashMap<String, String>();
				flag = Character.isDigit(number[x]);
			}
		}
		if (flag && certificateNo.length() == 15) {
			birthday = "19" + certificateNo.substring(6, 8) + "-" + certificateNo.substring(8, 10) + "-"
					+ certificateNo.substring(10, 12);
			sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 3, certificateNo.length()))
					% 2 == 0 ? "F" : "M";
			age = (year - Integer.parseInt("19" + certificateNo.substring(6, 8))) + "";
		} else if (flag && certificateNo.length() == 18) {
			birthday = certificateNo.substring(6, 10) + "-" + certificateNo.substring(10, 12) + "-"
					+ certificateNo.substring(12, 14);
			sexCode = Integer.parseInt(certificateNo.substring(certificateNo.length() - 4, certificateNo.length() - 1))
					% 2 == 0 ? "F" : "M";
			age = (year - Integer.parseInt(certificateNo.substring(6, 10))) + "";
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("birthday", birthday);
		map.put("age", age);
		map.put("sexCode", sexCode);
		return map;
	}

	/**
	 * Description:空对象判断
	 * @param patterns
	 * @return
	 */
	public static boolean isEmpty(Object... patterns) {
		if (patterns == null || patterns.length == 0)
			return true;
		for (Object obj : patterns) {
			if (obj == null || "".equals(obj) || "".equals(obj.toString().trim())
					|| "null".equalsIgnoreCase(obj.toString())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Description:非空对象判断
	 * @param patterns
	 * @return
	 */
	public static boolean isNotEmpty(Object... patterns) {
		return !isEmpty(patterns);
	}
	/**
	 * Description:根据类型返回两个字符串类型日期相隔的时间间隔（天/时/分/秒/毫秒）
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @param format：时间格式 （eg. yyyy-MM-dd HH:mm:ss）
	 * @param type:还回时间间隔类型 day--天，hour--小时，minute--分，second--秒，msecond--毫秒
	 * @return 还回时间间隔
	 */
	public static double getDateDiffByType(String startTime, String endTime,String format, String type) {     
        // 按照传入的格式生成一个simpledateformate对象     
        SimpleDateFormat sd = new SimpleDateFormat(format);     
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
        long nh = 1000 * 60 * 60;// 一小时的毫秒数     
        long nm = 1000 * 60;// 一分钟的毫秒数     
        long ns = 1000;// 一秒钟的毫秒数     
        double diff;     
        double result = 0;//返回值
          
        try {
        	diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();//获得两个时间的毫秒时间差异 
            switch (type) {
    		case "day":
    			result = diff/nd;// 计算差多少天 
    			break;
    		case "hour":
    			result = diff/nh;// 计算差多少小时   
    			break;
    		case "minute":
    			result = diff/nm;// 计算差多少分钟     
    			break;
    		case "second":
    			result = diff/ns;// 计算差多少秒     
    			break;
    		case "msecond":
    			result = diff;// 计算差多少毫秒    
    			break;
			default:
				break;
			}
        } catch (ParseException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
        return round(result,2);//返回结果四舍五入保留两位小数 
    }
	/**
	 * Description:根据类型返回两个字符串类型日期相隔的时间间隔（天/时/分/秒/毫秒）
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @param type:还回时间间隔类型 day--天，hour--小时，minute--分，second--秒，msecond--毫秒
	 * @return 还回时间间隔
	 */
	public static double getDateDiffByType(Date startTime, Date endTime,String type) {     
		// 按照传入的格式生成一个simpledateformate对象     
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
		long nh = 1000 * 60 * 60;// 一小时的毫秒数     
		long nm = 1000 * 60;// 一分钟的毫秒数     
		long ns = 1000;// 一秒钟的毫秒数     
		double diff;     
		double result = 0;//返回值
		diff = endTime.getTime() - startTime.getTime();//获得两个时间的毫秒时间差异   
		switch (type) {
		case "day":
			result = diff/nd;// 计算差多少天 
			break;
		case "hour":
			result = diff/nh;// 计算差多少小时   
			break;
		case "minute":
			result = diff/nm;// 计算差多少分钟     
			break;
		case "second":
			result = diff/ns;// 计算差多少秒     
			break;
		case "msecond":
			result = diff;// 计算差多少毫秒  
			break;
		default:
			break;
		}
		return round(result,2);//返回结果四舍五入保留两位小数 
	}
	/**
	 * Description:计算两个Long类型的日期相差的天数-小时数-分钟数-秒数
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @return 返回值（eg. 时间相差：13天21小时20分钟8秒。）
	 */
	public static String getDateDiff(long startTime, long endTime) {     
		// 按照传入的格式生成一个simpledateformate对象     
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
		long nh = 1000 * 60 * 60;// 一小时的毫秒数  
		long nm = 1000 * 60;// 一分钟的毫秒数     
		long ns = 1000;// 一秒钟的毫秒数     
		long diff;     
		long day = 0;     
		long hour = 0;     
		long min = 0;     
		long sec = 0;
		String result = "";//返回值
		diff = endTime - startTime; // 获得两个时间的毫秒时间差异
		day = diff / nd;// 计算差多少天
		hour = diff % nd / nh + day * 24;// 计算差多少小时     
		min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟     
		sec = diff % nd % nh % nm / ns;// 计算差多少秒     
		result = "时间相差：" + day + "天" + (hour - day * 24) + "小时"  + (min - day * 24 * 60) + "分钟" + sec + "秒。";
		return result;    
	}
	/**
	 * Description:计算两个日期类型的日期相差的天数-小时数-分钟数-秒数
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @return 返回值（eg. 时间相差：13天21小时20分钟8秒。）
	 */
	public static String getDateDiff(Date startTime, Date endTime) {     
		// 按照传入的格式生成一个simpledateformate对象     
		long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
		long nh = 1000 * 60 * 60;// 一小时的毫秒数  
		long nm = 1000 * 60;// 一分钟的毫秒数     
		long ns = 1000;// 一秒钟的毫秒数     
		long diff;     
		long day = 0;     
		long hour = 0;     
		long min = 0;     
		long sec = 0;
		String result = "";//返回值
		diff = endTime.getTime() - startTime.getTime(); // 获得两个时间的毫秒时间差异
		day = diff / nd;// 计算差多少天
		hour = diff % nd / nh + day * 24;// 计算差多少小时     
		min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟     
		sec = diff % nd % nh % nm / ns;// 计算差多少秒     
		result = "时间相差：" + day + "天" + (hour - day * 24) + "小时"  + (min - day * 24 * 60) + "分钟" + sec + "秒。";
		return result;    
	}
	/**
	 * Description:计算两个字符串类型的日期相差的天数-小时数-分钟数-秒数
	 * @param startTime：开始时间
	 * @param endTime：结束时间
	 * @param format：时间格式 （eg. yyyy-MM-dd HH:mm:ss）
	 * @return 返回值（eg. 时间相差：13天21小时20分钟8秒。）
	 */
	public static String getDateDiff(String startTime, String endTime,String format) {     
        // 按照传入的格式生成一个simpledateformate对象     
        SimpleDateFormat sd = new SimpleDateFormat(format);     
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒数     
        long nh = 1000 * 60 * 60;// 一小时的毫秒数     
        long nm = 1000 * 60;// 一分钟的毫秒数     
        long ns = 1000;// 一秒钟的毫秒数     
        long diff;     
        long day = 0;     
        long hour = 0;     
        long min = 0;     
        long sec = 0;
        String result = "";//返回值
        try {
        	diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime(); // 获得两个时间的毫秒时间差异
            day = diff / nd;// 计算差多少天
            hour = diff % nd / nh + day * 24;// 计算差多少小时     
            min = diff % nd % nh / nm + day * 24 * 60;// 计算差多少分钟     
            sec = diff % nd % nh % nm / ns;// 计算差多少秒 
            result = "时间相差：" + day + "天" + (hour - day * 24) + "小时"  + (min - day * 24 * 60) + "分钟" + sec + "秒。";
        } catch (ParseException e) {     
            // TODO Auto-generated catch block     
            e.printStackTrace();     
        }     
        return result;    
    }
}