package Support;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 本类用来管理获得数据库中的列名、属性以及在GUI中的名字
 * 在修改数据库后记得查看本类进行检查。
 * 注意不能持有外部引用，防止内存泄露。
 */
public class DataNameUtils {
    public static HashMap<String, String> primaryKeyMap = new HashMap<String, String>() {{
        put("career_info", "c_id");
        put("department", "d_id");
        put("staff", "s_id");
        put("extra_info", "ei_id");
        put("allowance_month_log", "am_id");
        put("extra", "e_id");
        put("attendance_month_log", "am_id");
        put("attendance", "a_id");
        put("salary_log", "sal_id");
        put("award_log", "a_id");
    }};

    public static String tableToPrimaryKey(String tableName) {
        return primaryKeyMap.get(tableName);
    }

    public static HashMap<String, String[]> tableColumnsMap = new HashMap<String, String[]>() {{
        put("career_info", new String[]{"工种编号", "工种名称", "基本工资", "操作权限"});
        put("department", new String[]{"部门编号", "部门名称"});
        put("staff", new String[]{"员工编号", "员工姓名", "用户名", "密码", "工种编号", "部门编号", "联系电话", "年龄"});
        put("extra_info", new String[]{"加班类型编号", "加班类型名称", "津贴"});
        put("allowance_month_log", new String[]{"月津贴统计编号", "月加班修正金额"});
        put("extra", new String[]{"加班编号", "员工编号", "年", "月", "日", "加班类型编号", "加班时长", "月津贴统计编号"});
        put("attendance_month_log", new String[]{"月考勤统计编号", "月考勤修正比例"});
        put("attendance", new String[]{"考勤编号", "员工编号", "年", "月", "日", "考勤类型", "月考勤统计编号"});
        put("salary_log", new String[]{"工资编号", "员工编号", "年", "月", "基本工资", "津贴金额", "考勤修正", "工资总额"});
        put("award_log", new String[]{"年终奖编号", "员工编号", "年", "奖金总额"});
        put("department_statistics", new String[]{"部门编号", "部门名称", "年", "月", "工资总额"});
        put("company_statistics", new String[]{"年", "月", "工资总额"});
    }};

    public static String[] modeToColumns(String mode) {
        String table = modeToTable(mode);
        return tableColumnsMap.get(table);
    }

    private static HashMap<String, String> nameNameMap = new HashMap<String, String>() {{
        put("编号", "id");
        put("部门名称", "name");
        put("员工编号", "s_id");
        put("员工姓名", "name");
        put("用户名", "username");
        put("密码", "password");
        put("工种编号", "c_id");
        put("部门编号", "d_id");
        put("联系电话", "telephone");
        put("年龄", "age");
        put("加班类型名称", "name");
        put("津贴", "allowance");
        put("月加班修正金额", "allowance");
        put("加班编号", "e_id");
        put("年", "year");
        put("月", "month");
        put("日", "date");
        put("加班类型编号", "ei_id");
        put("加班时长", "length");
        put("月津贴统计编号", "am_id");
        put("月考勤统计编号", "am_id");
        put("月考勤修正比例", "offset");
        put("考勤编号", "a_id");
        put("考勤类型", "type");
        put("年终奖编号", "a_id");
        put("奖金总额", "award");
        put("工资编号", "sal_id");
        put("津贴金额", "allowance");
        put("考勤修正", "attendance");
        put("工资总额", "sum");
        put("工种名称", "name");
        put("基本工资", "base");
        put("操作权限", "authority");
        //put("", "");
    }};

    public static String nameToName(String strIn) {
        return nameNameMap.get(strIn);
    }

    private static HashMap<String, String> modeTableMap = new HashMap<String, String>() {{
        put("工种", "career_info");
        put("员工", "staff");
        put("部门", "department");
        put("考勤", "attendance");
        put("加班", "extra");
        put("加班类型", "extra_info");
        put("月津贴统计", "allowance_month_log");
        put("月考勤统计", "attendance_month_log");
        put("工资记录", "salary_log");
        put("年终奖记录", "award_log");
        put("部门工资统计", "department_statistics");
        put("公司工资统计", "company_statistics");
    }};

    public static String modeToTable(String tableMode) {
        return modeTableMap.get(tableMode);
    }

    public static ArrayList<String> eventName = new ArrayList<>(Arrays.asList("损坏维修", "罚款", "借车", "还车"));

    public static String swtichEventId(String strIn) {
        return String.valueOf(eventName.indexOf(strIn) + 1);
    }
}
