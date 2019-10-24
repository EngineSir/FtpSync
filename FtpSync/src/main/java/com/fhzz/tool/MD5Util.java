package com.fhzz.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MD5Util {

	public final static String encrypt(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			/* 获得密文 */
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws IOException {

//		System.out.println(encrypt("123456"));
//		File sourceFile = new File("D:\\ftp2\\ftp2\\git.txt");
//		for(int i=0;i<600;i++) {
//			File targetFile=new File("D:\\ftp2\\ftp2\\20191008",System.currentTimeMillis()+".txt");
//			copyFile(sourceFile,targetFile);
//		}
		//67276538317
		//a[a[0]]=1
		// int x=a[0]
		// a[x]=1
//		int[] a=new int[11];
//		int[] arr= {1 ,3 ,2, 5 ,7 ,8 ,6, 7, 6 ,3 ,7};
//		Random r = new Random();
//		for(int i=0;i<11;i++) {
//			System.out.println(arr[r.nextInt(11)]);
//		}
//		getAddress("广西陆川县");
//		getAddress("武汉市新洲区");
//		
//		getAddress("重庆市合川区龙市镇青坝村6组61号附1号");
//		
//		
//		
//		getAddress("黑龙江省博2罗县");
//		
//		
//		getAddress("宁夏莱西市");
		String s="20191024";
		SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
		String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

		System.out.println(curDate);
		 Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");//这个也行
	     Matcher isNum = pattern.matcher(s);
	     System.out.println(isNum.matches());
	}

	/**
	 * 	复制文件
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile, File targetFile)

			throws IOException {

		// 新建文件输入流并对它进行缓冲

		FileInputStream input = new FileInputStream(sourceFile);

		BufferedInputStream inBuff = new BufferedInputStream(input);

		// 新建文件输出流并对它进行缓冲

		FileOutputStream output = new FileOutputStream(targetFile);

		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		// 缓冲数组

		byte[] b = new byte[1024 * 5];

		int len;

		while ((len = inBuff.read(b)) != -1) {

			outBuff.write(b, 0, len);

		}

		// 刷新此缓冲的输出流

		outBuff.flush();

		// 关闭流

		inBuff.close();

		outBuff.close();

		output.close();

		input.close();

	}
	  /**
	   * 正则切割标准地址，省，市，县
	   * @param addr
	   * @return
	   */
	  public static Map<String,Object> getAddress(String addr){
	        Map<String,Object> map = new HashMap();
	        String city =null;
	        String province = null;
	        String district=null;
	        String are = null;
	        String regex1="(?<province>[^自治区]+自治区|[^省]+省|广西|内蒙古|西藏|宁夏|新疆)";
	        Matcher m1 = Pattern.compile(regex1).matcher(addr);
	        while (m1.find()){
	            province = m1.group("province");
	            if(province != null){
	                addr=addr.replaceFirst(province,"");
	                break;
	            }
	        }
	        String regex2="(?<city>[^辖区]+辖区|[^盟]+盟|[^自治州]+自治州|[^地区]+地区|[^市]+市|.+区划)";
	        Matcher m2 = Pattern.compile(regex2).matcher(addr);
	        while (m2.find()){
	            city = m2.group("city");
	            if (city != null){
	                addr=addr.replaceFirst(city,"");
	                break;
	            }
	        }
	        //String regex3="(?<district>[^市]+市|[^县]+县|[^旗]+旗|.+区)";
	        String regex3="(?<district>[^区]+区|[^县]+县|[^旗]+旗|.+市)";
	        Matcher m3 = Pattern.compile(regex3).matcher(addr);
	        while (m3.find()){
	            district = m3.group("district");
	            if (district != null){
	                addr=addr.replaceFirst(district,"");
	                break;
	            }
	        }
	        String regex4="(?<town>[^区]+区|.*镇|.*乡)";
	        Matcher m4 = Pattern.compile(regex4).matcher(addr);
	        while (m4.find()){
	        	are = m4.group("town");
	            if (are != null){
	                addr=addr.replaceFirst(are,"");
	                break;
	            }
	        }
	        map.put("province",province);
	        map.put("city",city);
	        map.put("district",district);
	        map.put("town",are);
	        map.put("address",addr);
	        System.out.println(map);
	        return map;
	    }


}
