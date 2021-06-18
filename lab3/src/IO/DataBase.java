package IO;

import Support.DataNameUtils;

import java.sql.*;
import java.util.*;

public class DataBase {

    public static final String URL = "jdbc:sqlserver://localhost:1433;" + "database=SalarySystem;";
    public static final String USER = "sa";
    public static final String PASSWORD = "1234";

    private Connection connection;

    /**
     * singleton
     */
    private static class InnerHelper {
        private final static DataBase dataBase = new DataBase();
    }

    private DataBase() {
    }

    public static DataBase getInstance() {
        return InnerHelper.dataBase;
    }

    /**
     * @return status of connection
     */
    public boolean initConnect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * return users' authority
     *
     * @param name 输入的用户名
     * @param psw  输入的密码
     * @return authority, -1->no found, 1->root user, 2->administrator, 3->normal user
     * @throws SQLException
     */
    public int checkUser(String name, String psw, StringBuilder s_id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet userResult = statement.executeQuery("SELECT * FROM staff WHERE username = '" + name + "'");//全表搜索
        while (userResult.next()) {
            String psw_right = userResult.getString("password");
            if (userResult.getString("password").equals(psw)) {
                s_id.append(userResult.getString("s_id"));
                String careerId = userResult.getString("c_id");
                ResultSet careerResult = statement.executeQuery("SELECT * FROM career_info WHERE c_id = '" + careerId + "'");//全表搜索
                careerResult.next();
                return careerResult.getInt("authority");
            }
        }
        statement.close();
        return -1;
    }

    //从约束里拿where子句
    private String addWheresToSQL(Map<String, String> map, boolean haveWhere) {
        StringBuilder sql = new StringBuilder();
        boolean isFirst = true;
        if (haveWhere) {
            isFirst = false;
        }
        Set<String> keySet = map.keySet();

        for (String key : keySet) {
            String value = map.get(key);
            if (value.equals("")) continue;
            if (isFirst) {
                sql.append("WHERE ");
                isFirst = false;
            } else {
                sql.append("and ");
            }

            sql.append(DataNameUtils.nameToName(key) + " = '" + value + "' ");
        }
        return String.valueOf(sql);
    }

    public ResultSet selectByEqual(String tableName, Map<String, String> restricts) throws SQLException {
        String sql = String.format("SELECT * FROM %s ", tableName);
        sql = sql + addWheresToSQL(restricts, false);
        Statement statement = connection.createStatement();
        System.out.println(sql);
        //statement.close();
        return statement.executeQuery(sql);
    }

    public ResultSet executeSql(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println(sql);
        //statement.close();
        return statement.executeQuery(sql);
    }


    /**
     * 在数据库中级联删除一行
     *
     * @param tableMode  表格模式的标志字符串
     * @param primaryKey 主键的值
     * @throws SQLException 语句错误或者受到约束
     */
    public void deleteRow(String tableMode, String primaryKey) throws SQLException {
        String tableName = DataNameUtils.modeToTable(tableMode);

        String primaryKeyName = DataNameUtils.tableToPrimaryKey(tableName);
        String sql = String.format("DELETE FROM %s WHERE %s = ?", tableName, primaryKeyName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, primaryKey);
        System.out.println(preparedStatement.toString());
        preparedStatement.execute();
    }

    /**
     * 更新数据
     *
     * @param tableMode  表格模式的标志字符串
     * @param name       更新属性的列名
     * @param value      更新后的值
     * @param primaryKey 主键的值
     * @throws SQLException 语句错误或者受到约束
     */
    public void updateData(String tableMode, String name, String value, String primaryKey) throws SQLException {
        String tableName = DataNameUtils.modeToTable(tableMode);

        //拿到主键的名字
        String primaryKeyName = DataNameUtils.primaryKeyMap.get(tableName);
        if (primaryKeyName == null) primaryKeyName = "id";

        String sql = String.format("UPDATE %s SET %s = ? where %s = ?", tableName, DataNameUtils.nameToName(name), primaryKeyName);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        if (value.equals("")) {
            preparedStatement.setNull(1, Types.CHAR);//this types.* is useless...
        } else {
            preparedStatement.setString(1, value);
        }
        preparedStatement.setString(2, primaryKey);
        System.out.println(preparedStatement.toString());
        preparedStatement.execute();
    }

    /**
     * 添加行
     *
     * @param tableMode 表格模式的标志字符串
     * @param data      一张哈希表存新数据
     * @throws SQLException 语句错误或者受到约束
     */
    public void addRow(String tableMode, HashMap<String, String> data) throws SQLException {
        Statement statement = connection.createStatement();
        String tableName = DataNameUtils.modeToTable(tableMode);
        if (tableName != null) {
            StringBuilder columns = new StringBuilder("");
            StringBuilder values = new StringBuilder("");
            Set<String> keySet = data.keySet();
            Iterator<String> iterator = keySet.iterator();

            boolean isFirst = true;
            while (iterator.hasNext()) {
                String s = iterator.next();
                System.out.println(s);
                String value = data.get(s);
                if (DataNameUtils.eventName.contains(value))
                    value = String.valueOf(DataNameUtils.eventName.indexOf(value) + 1);
                if (value != null && !value.equals("")) {
                    if (isFirst) {//第一个不加逗号
                        isFirst = false;
                        columns.append(DataNameUtils.nameToName(s));
                        values.append("'" + value + "'");
                    } else {
                        columns.append("," + DataNameUtils.nameToName(s));
                        values.append(",'" + value + "'");
                    }
                }
            }
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns.toString(), values.toString());
            System.out.println(sql);
            statement.  execute(sql);
            statement.close();
        }
    }


    public HashMap<String, String> getMapId2Name() throws SQLException {
        HashMap<String, String> hashMap = new HashMap<>();
        Statement statement = connection.createStatement();
        String sql = String.format("SELECT id, NAME\nFROM customer");
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            hashMap.put(resultSet.getString(1), resultSet.getString(2));

        }
        return hashMap;
    }

}
