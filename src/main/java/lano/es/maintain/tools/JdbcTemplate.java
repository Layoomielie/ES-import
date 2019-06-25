package lano.es.maintain.tools;/**
 * ${tag}
 *
 * @author zhanghongjian
 * @Date 2019/6/25 11:03
 */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author：张鸿建
 * @time：2019/6/25
 * @desc：
 **/
public class JdbcTemplate {

    String jdbcId;
    String jdbcStr;
    String userName;
    String password;
    String sqlStatement;
    String indexName;

    public static Template createJdbcTemplate(String currentFolderPath, String sufixTableName, String filename) throws Exception {
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



}
