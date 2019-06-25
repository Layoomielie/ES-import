package lano.es.maintain.tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 导入数据库
 *
 * @author 胡晓光
 * @CreateTime 下午3:58:44
 */
public class ESAddImportDatabase {
    String currentFolderPath, host, dbname, username, password, sourceKey;

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            help();
            System.exit(-1);
        }
        String currentFolderPath = args[0];
        String host = args[1];
        String username = args[2];
        String password = args[3];
        String dbname = args[4];
        String sourceKey = args[5];
        String type = args[6];

        ESAddImportDatabase e = new ESAddImportDatabase(currentFolderPath, host, username, password, dbname, sourceKey);
        e.importDatabase(type);
    }

    ESAddImportDatabase(String currentFolderPath, String host, String username, String password, String dbname, String sourcekey) {
        this.currentFolderPath = currentFolderPath;
        this.host = host;
        this.dbname = dbname;
        this.username = username;
        this.password = password;
        this.sourceKey = sourcekey;
    }

    public void importDatabase(String type) throws Exception {
        Connection conn = null;
        try {
            System.out.println("尝试连接数据库 : host=" + host + ", dbname=" + dbname + ", username=" + username + ", password=" + password);
            conn = getConnection(host, dbname, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("无法连接数据库 [" + host + "] username :" + username + " password :" + password);
            return;
        }

        System.out.println("连接成功");
        if ("canal".equals(type)) {
            this.createCanalFile(conn);
        } else if ("logstash".equals(type)) {
            this.createLogstashFile(conn);
        } else {
            System.out.println("请检查最后一位参数 只能为 canal 或logstash");
            System.exit(-1);
        }
    }

    public void createCanalFile(Connection conn) throws Exception {
        if (conn != null) {
            try {
                List<String> tableNames = getTableNames(conn);
                for (String tableName : tableNames) {
                    String sufixTableName = Utils.cutFirst(tableName);
                    String sql = createSelectSQL(conn, tableName);
                    Template template = Template.createTemplate(this.currentFolderPath, sufixTableName, "template.yml");
                    template.format(sourceKey, sufixTableName, sql);
                    template.save(".yml");
                }
            } finally {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void createLogstashFile(Connection conn) throws Exception {
        if (conn != null) try {
            List<String> tableNames = getTableNames(conn);
            StringBuilder text = new StringBuilder();
            for (String tableName : tableNames) {
                Template template = Template.createTemplate(this.currentFolderPath, tableName, "jdbc.cfg");
                String jdbcConnect = "jdbc:mysql://" + this.host + ":3306/" + this.dbname + "?useSSL=false";
                String sql = "select * from " + tableName;
                template.format(tableName, jdbcConnect, this.username, this.password, sql, Utils.cutFirst(tableName));
                // System.out.println(template.content);
                text.append(template.content);
            }
            Template template = Template.createTemplate(this.currentFolderPath, this.dbname, "gxpt.cfg");
            template.format(text.toString(), this.host);
            template.save(".cfg");
        } finally {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void help() {
        System.out.println("参数：[mysql服务器ip地址] [账号]  [密码]");
    }

    protected Connection getConnection(String host, String dbname, String usrename, String password) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + dbname + "?useUnicode=true&characterEncoding=utf8", usrename, password);
        return conn;
    }

    protected List<String> getTableNames(Connection conn) throws Exception {
        DatabaseMetaData dbMetaData = conn.getMetaData();
        ResultSet rs = dbMetaData.getTables(null, null, null, new String[]{"TABLE"});
        List<String> result = new ArrayList<>();
        while (rs.next()) {
            result.add(rs.getString("TABLE_NAME"));
        }
        return result;
    }

    protected String createSelectSQL(Connection conn, String tableName) throws Exception {
        String sql = "select * from " + tableName + " limit 0, 1";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rstable = ps.executeQuery();

        // 结果集元数据
        ResultSetMetaData meta = rstable.getMetaData();

        List<String> fields = new ArrayList<>();

        // 表列数量
        int columeCount = meta.getColumnCount();
        for (int i = 1; i <= columeCount; i++) {
            fields.add(Utils.convertCamelCase(Utils.cutFirst(meta.getColumnName(i).toLowerCase())));
        }

        String fieldStr = StringUtils.join(fields.toArray(), ",");

        return "select " + fieldStr + " from " + tableName;
    }

}
