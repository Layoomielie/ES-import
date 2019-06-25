package lano.es.maintain.tools;

import org.apache.commons.lang.StringUtils;

public class Utils {
	// 首字母转小写
	public static String toLowerCaseFirstChar(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	// 首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	// 去掉第一个下划线前面的字符串
	public static String cutFirst(String str) {
		int pos = str.indexOf("_");
		String subfix = str.substring(pos + 1, str.length());
		return subfix;
	}


	/**
	 * 转换为驼峰命名法
	 * 
	 * @param str
	 * @return
	 * @author 胡晓光
	 * @CreateTime 下午6:00:35
	 */
	public static String convertCamelCase(String str) {
		str = toLowerCaseFirstChar(str);
		String[] p = str.split("_");
		if (p.length > 1) {
			for (int i = 1; i < p.length; i++) {
				p[i] = toUpperCaseFirstOne(p[i]);
			}
		}

		return StringUtils.join(p, "");
	}
}
