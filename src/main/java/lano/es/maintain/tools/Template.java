package lano.es.maintain.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import org.apache.commons.lang.StringUtils;

public class Template {
	String sufixTableName;
	String content;
	String currentFolderPath;
	public static Template createTemplate(String currentFolderPath, String sufixTableName, String filename) throws Exception {
		InputStream in = Template.class.getClassLoader().getResourceAsStream(filename);
		// FileInputStream fin = new FileInputStream(in);
		byte[] data = new byte[1024];
		int count = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		while ((count = in.read(data, 0, 1024)) > 0) {
			out.write(data, 0, count);
		}

		String content = new String(out.toByteArray());
		Template template = new Template();
		template.setContent(content);
		template.setSufixTableName(sufixTableName);
		template.setCurrentFolderPath(currentFolderPath);
		return template;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSufixTableName() {
		return sufixTableName;
	}

	public void setSufixTableName(String sufixTableName) {
		this.sufixTableName = sufixTableName;
	}

	public String getCurrentFolderPath() {
		return currentFolderPath;
	}

	public void setCurrentFolderPath(String currentFolderPath) {
		this.currentFolderPath = currentFolderPath;
	}

	public void format(String... params) {
		if (StringUtils.isNotEmpty(this.content)) {
			int i = 0;
			for (String s : params) {
				this.content = this.content.replace("${" + (i++) + "}", s);
			}
		}
	}

	/**
	 * 保存
	 * 
	 * @author 胡晓光
	 * @CreateTime 下午6:14:39
	 */
	public void save(String suffix) throws Exception {
		// 将文件输出到当前的位置，并以响应的文件名
		String filename = this.sufixTableName + suffix;
		
		File parent = new File(currentFolderPath);
		File outputFile = new File(parent, filename);
		
		System.out.println("正在输出" + outputFile.getPath());
		FileWriter writer = new FileWriter(outputFile);
		try {
			writer.write(this.content);
		} finally {
			writer.close();
		}
	}
}
