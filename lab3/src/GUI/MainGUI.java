package GUI;

import IO.DataBase;
import Support.DataNameUtils;
import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import sun.reflect.generics.tree.Tree;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MainGUI {

    JPanel jMainPanel;
    private JFrame frame;
    private JTable jTable;//当前界面的Table
    private DefaultTableModel tableModel;

    private String userName;
    private JTextField textDialogName;
    private JPasswordField textDialogPsw;
    private JDialog dialogLogin;

    private static int authority;
    private static String s_id;
    private DataBase dataBase;
    private static int SCREEN_WIDTH;
    private static int SCREEN_HEIGHT;

    private String PANEL_MODE = "";//users, stuff, car ....
    private int selectRowIndex = -1;
    private Vector<String> columns;
    private JScrollPane scrollPane;


    public static void main(String[] args) {

        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
            BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencyAppleLike;
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            e.printStackTrace();
        }

        UIManager.put("RootPane.setupButtonVisible", false);
        setFontForBeautyEye();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        SCREEN_WIDTH = dim.width;
        SCREEN_HEIGHT = dim.height;

        new MainGUI().start();

    }

    /**
     * 修复字体发虚
     */
    private static void setFontForBeautyEye() {
        String[] DEFAULT_FONT = new String[]{
                "Table.font"
                , "TableHeader.font"
                , "CheckBox.font"
                , "Tree.font"
                , "Viewport.font"
                , "ProgressBar.font"
                , "RadioButtonMenuItem.font"
                , "ToolBar.font"
                , "ColorChooser.font"
                , "ToggleButton.font"
                , "Panel.font"
                , "TextArea.font"
                , "Menu.font"
                , "TableHeader.font"
                , "OptionPane.font"
                , "MenuBar.font"
                , "Button.font"
                , "Label.font"
                , "PasswordField.font"
                , "ScrollPane.font"
                , "MenuItem.font"
                , "ToolTip.font"
                , "List.font"
                , "EditorPane.font"
                , "Table.font"
                , "TabbedPane.font"
                , "RadioButton.font"
                , "CheckBoxMenuItem.font"
                , "TextPane.font"
                , "PopupMenu.font"
                , "TitledBorder.font"
                , "ComboBox.font"
        };

        for (String aDEFAULT_FONT : DEFAULT_FONT) {
            UIManager.put(aDEFAULT_FONT, new Font("微软雅黑", Font.PLAIN, 12));
        }
    }

    private void start() {
        dataBase = DataBase.getInstance();
        frame = new JFrame();//顺便初始化一下父容器

        if (dataBase.initConnect()) {

            dialogLogin = new JDialog();
            try {
                dialogLogin.setContentPane(new BackgrouPanel("res/night.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            dialogLogin.setTitle("工资管理系统");

            dialogLogin.setDefaultCloseOperation(DISPOSE_ON_CLOSE);


            JLabel labelName = new JLabel("用户名:");
            JLabel labelPsw = new JLabel("密码 :");
            labelPsw.setForeground(Color.white);
            labelName.setForeground(Color.white);

            textDialogName = new JTextField(17);
            textDialogPsw = new JPasswordField(10);
            JButton butLogin = new JButton("登录");

            JPanelOpen namePanel = new JPanelOpen();
            namePanel.add(labelName, BorderLayout.CENTER);
            namePanel.add(textDialogName, BorderLayout.CENTER);

            JPanelOpen pswPanel = new JPanelOpen();
            pswPanel.add(labelPsw, BorderLayout.CENTER);
            pswPanel.add(textDialogPsw, BorderLayout.CENTER);

            dialogLogin.getContentPane().setLayout(new BorderLayout());
            dialogLogin.getContentPane().add(namePanel, BorderLayout.NORTH);
            dialogLogin.getContentPane().add(pswPanel, BorderLayout.CENTER);
            dialogLogin.getContentPane().add(butLogin, BorderLayout.SOUTH);
            butLogin.addActionListener(logInListener);
            dialogLogin.setSize(new Dimension(400, 320));
            dialogLogin.setResizable(false);
            setCenter(dialogLogin);

        } else {
            JOptionPane.showMessageDialog(frame, "数据库连接失败");
        }

    }


    /**
     * 初始化主框架
     */
    private void initMainFrame() {
        try {
            jMainPanel = new BackgrouPanel("res/city.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setContentPane(jMainPanel);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("欢迎使用 工资管理系统 ~" + userName);
        initMenu();
        createPopupMenu();
        frame.setSize(1500, 1000);
        setCenter(frame);
        frame.setVisible(true);

        JTextFiledOpen field = new JTextFiledOpen();
        field.setFont(new Font("宋体", Font.BOLD, 30));
        field.setText("工资管理系统 数据库实验 2021春季");
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(null);
        field.setEditable(false);

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(field, BorderLayout.CENTER);
        jMainPanel.updateUI();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        JMenuItem newItem;
        JMenuBar mb = new JMenuBar();

        /*
        // 文件
        JMenu menuFile = new JMenu("文件");
        newItem = new JMenuItem("导出");
        newItem.addActionListener(fileListener);
        menuFile.add(newItem);
        */

        // 统计
        JMenu menuStatistics = new JMenu("统计");
        newItem = new JMenuItem("工资记录");
        newItem.addActionListener(statisticsListener);
        menuStatistics.add(newItem);
        newItem = new JMenuItem("年终奖记录");
        newItem.addActionListener(statisticsListener);
        menuStatistics.add(newItem);
        if (authority == 2) {
            newItem = new JMenuItem("部门工资统计");
            newItem.addActionListener(statisticsListener);
            menuStatistics.add(newItem);
            newItem = new JMenuItem("公司工资统计");
            newItem.addActionListener(statisticsListener);
            menuStatistics.add(newItem);
        }

        // 管理
        JMenu menuManage = new JMenu("管理");
        newItem = new JMenuItem("员工");
        newItem.addActionListener(manageListener);
        menuManage.add(newItem);
        if (authority == 2) {
            newItem = new JMenuItem("部门");
            newItem.addActionListener(manageListener);
            menuManage.add(newItem);
            newItem = new JMenuItem("工种");
            newItem.addActionListener(manageListener);
            menuManage.add(newItem);
        }
        newItem = new JMenuItem("考勤");
        newItem.addActionListener(manageListener);
        menuManage.add(newItem);
        newItem = new JMenuItem("加班");
        newItem.addActionListener(manageListener);
        menuManage.add(newItem);

        // 其他
        JMenu menuOther = new JMenu("其他");
        newItem = new JMenuItem("切换账户");
        newItem.addActionListener(switchListener);
        menuOther.add(newItem);
        newItem = new JMenuItem("关于");
        newItem.addActionListener(aboutListener);
        menuOther.add(newItem);

        // 添加到导航栏
        //mb.add(menuFile);
        mb.add(menuStatistics);
        mb.add(menuManage);
        mb.add(menuOther);
        frame.setJMenuBar(mb);

    }

    /**
     * 登陆按钮监视器
     */
    private final ActionListener logInListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //check and set authority
            String nameInput = textDialogName.getText();
            String pswInput = String.valueOf(textDialogPsw.getPassword());
            System.out.println("name:" + nameInput + "\npsw:" + pswInput);
            userName = nameInput;
            try {
                StringBuilder s_idBuilder = new StringBuilder();
                authority = dataBase.checkUser(nameInput, pswInput, s_idBuilder);
                s_id = s_idBuilder.toString();
                if (authority != -1) {
                    dialogLogin.setVisible(false);
                    initMainFrame();

                } else {
                    noticeMsg("用户名或密码错误");
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                noticeMsg("数据库连接失败");
            }
        }
    };

    /**
     * * 切换用户按钮监视器
     */
    private final ActionListener switchListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            frame.getContentPane().removeAll();
            frame.setVisible(false);
            dialogLogin.setVisible(true);
            PANEL_MODE = "";
        }
    };

    /**
     * 关于按钮监视器
     */
    private final ActionListener aboutListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            noticeMsg("Take care, project is under construction.");
        }
    };

    /**
     * 导出按钮监听器
     */
    private final ActionListener fileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            JFileChooser jFileChooser = new JFileChooser();
            File file;
            String path = null;
            if (command.equals("导入")) {
                jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                jFileChooser.showOpenDialog(frame);
                file = jFileChooser.getSelectedFile();
                if (file.isFile()) {
                    path = file.getAbsolutePath();
                }
            } else {
                jFileChooser.showSaveDialog(frame);
                file = jFileChooser.getSelectedFile();
                String typeInName = jFileChooser.getName(file);
                if (typeInName == null || typeInName.equals("")) return;
                path = jFileChooser.getCurrentDirectory() + "\\" + typeInName;
            }

            if (path != null) {
                try {
                    String bat;
                    if (command.equals("导出")) {
                        bat = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe lab3 -uroot " + " -pXIANG1569348 -r" + path + " --skip-lock-tables";
                    } else {
                        bat = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysql.exe lab3 -uroot -pXIANG1569348 -e source " + path;
                    }
                    System.out.println(bat);
                    Process process = Runtime.getRuntime().exec(bat);//save
                    int com = process.waitFor();
                    if (com == 0) {
                        noticeMsg("操作成功");
                    } else {
                        noticeMsg("操作失败");
                    }

                } catch (IOException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    };

    /**
     * 窗口放置桌面中央
     *
     * @param c component waited to be reset
     */
    private void setCenter(Component c) {
//        ((Window) c).pack();
        c.setLocation((SCREEN_WIDTH - c.getWidth()) / 2, (SCREEN_HEIGHT - c.getHeight()) / 2);
        c.setVisible(true);
    }

    private void noticeMsg(String in) {//make code elegant
        JOptionPane.showMessageDialog(frame, in);
    }


    /**
     * 根据不同的表，绘制不同的成对Label与TextField或Combobox，内部通过获取搜索栏的新约束条件获得新的结果并重绘表格Panel
     *
     * @param jPanelSearch Search components' parent components
     */
    private void setSearchPanel(JPanelOpen jPanelSearch) {
        jPanelSearch.setLayout(new FlowLayout());
        setBlankInSearchPanel(jPanelSearch, DataNameUtils.modeToColumns(PANEL_MODE));
        JButton jButton = new JButton("搜索");
        jButton.addActionListener(e -> {
            HashMap<String, String> dataMap = getDataMap(jPanelSearch);
            if (checkSearchData(dataMap)) {
                try {
                    //在这里利用搜索栏中新的约束条件获得新的获取结果
                    if (authority == 1)
                        dataMap.put("员工编号", s_id);
                    ResultSet result = DataBase.getInstance().selectByEqual(DataNameUtils.modeToTable(PANEL_MODE), dataMap);
                    setTablePanel(result, columns);
                    jMainPanel.add(scrollPane, BorderLayout.SOUTH);
                    jMainPanel.updateUI();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    noticeMsg("搜索数据不合法或数据库发生错误");
                }
            }
        });
        jButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.green));
        jPanelSearch.add(jButton);
        if (authority == 2 &&
                (PANEL_MODE.equals("员工") || (PANEL_MODE.equals("部门")) ||
                        (PANEL_MODE.equals("考勤")) || (PANEL_MODE.equals("加班")) ||
                        (PANEL_MODE.equals(("工资记录")))|| (PANEL_MODE.equals("工种")) )) {
            jButton = new JButton("添加");
            jButton.addActionListener(e -> {
                HashMap<String, String> dataMap = getDataMap(jPanelSearch);
                if (PANEL_MODE.equals("考勤")) {
                    dataMap.put("考勤编号", String.format("%04d%02d%02d%4s", Integer.parseInt(dataMap.get("年")), Integer.parseInt(dataMap.get("月")), Integer.parseInt(dataMap.get("日")), dataMap.get("员工编号")));
                    dataMap.put("月考勤统计编号", String.format("%04d%02d%4s", Integer.parseInt(dataMap.get("年")), Integer.parseInt(dataMap.get("月")), dataMap.get("员工编号")));
                } else if (PANEL_MODE.equals("加班")) {
                    dataMap.put("加班编号", String.format("%04d%02d%02d%4s", Integer.parseInt(dataMap.get("年")), Integer.parseInt(dataMap.get("月")), Integer.parseInt(dataMap.get("日")), dataMap.get("员工编号")));
                    dataMap.put("月津贴统计编号", String.format("%04d%02d%4s", Integer.parseInt(dataMap.get("年")), Integer.parseInt(dataMap.get("月")), dataMap.get("员工编号")));
                } else if (PANEL_MODE.equals("工资记录")) {
                    dataMap.put("工资编号", String.format("%04d%02d%4s", Integer.parseInt(dataMap.get("年")), Integer.parseInt(dataMap.get("月")), dataMap.get("员工编号")));
                    // 查询基本工资
                    String base = null;
                    try {
                        Map<String, String> strict = new TreeMap<String, String>();
                        strict.put("员工编号", dataMap.get("员工编号"));
                        ResultSet resultSet = DataBase.getInstance().selectByEqual("staff", strict);
                        resultSet.next();
                        strict.remove("员工编号");
                        strict.put("工种编号", resultSet.getString("c_id"));
                        resultSet = DataBase.getInstance().selectByEqual("career_info", strict);
                        resultSet.next();
                        base = String.valueOf(resultSet.getFloat("base"));
                        dataMap.replace("基本工资", base);
                        dataMap.replace("津贴金额", "0");
                        dataMap.replace("考勤修正", "1");
                        dataMap.replace("工资总额", base);
                    } catch (SQLException exc) {
                        exc.printStackTrace();
                    }
                }
                if (checkNewData(dataMap)) {
                    try {
                        DataBase.getInstance().addRow(PANEL_MODE, dataMap);//单例模式获取数据库管理对象，调用对应的添加行函数
                        tableModel.addRow(map2vector(dataMap));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        noticeMsg("新数据不合法或数据库发生错误");
                        return;
                    }
                    for (int i = 0; i < jPanelSearch.getComponents().length - 2; i++) {//不算button
                        JTextField textField = (JTextField) ((JPanelOpen) jPanelSearch.getComponents()[i]).getComponents()[1];
                        textField.setText("");
                    }
                }
            });
            jPanelSearch.add(jButton);
        }
    }

    private void setBlankInSearchPanel(JPanelOpen jPanelSearch, String[] names) {
        Set<String> comboxSet = comboxMap.keySet();

        for (String name : names) {
            JPanelOpen jPanelO = new JPanelOpen();
            JLabelOpen jLO = new JLabelOpen();
            if (name.equals("考勤编号") || name.equals("月考勤统计编号") ||
                    name.equals("加班编号") || name.equals("月津贴统计编号") ||
                    name.equals("工资编号") || name.equals("年终奖编号") ||
                    authority == 1 && name.equals("员工编号"))
                continue;

            jLO.setText(name);

            if (comboxSet.contains(name)) {
                JComboBox<String> stringJComboBox = getComboBox(name);
                jPanelO.add(jLO);
                jPanelO.add(stringJComboBox);
            } else {
                JTextField jTextField = new JTextField();
                jTextField.setColumns(11);
                jPanelO.add(jLO);
                jPanelO.add(jTextField);
            }

            jPanelSearch.add(jPanelO);
        }
    }

    private HashMap<String, String[]> comboxMap = new HashMap<String, String[]>() {{
    }};

    private JComboBox<String> getComboBox(String name) {
        JComboBox<String> jComboBox = null;
        String[] contentStrings = comboxMap.get(name);
        if (contentStrings != null) {
            jComboBox = new JComboBox<>(contentStrings);
        }
        return jComboBox;
    }

    /**
     * 加载全新的表格
     * after called, scroll panel would be reset, you should add scroll panel to content panel again.
     *
     * @param resultSet data
     * @param columns   columns' names
     */
    private void setTablePanel(ResultSet resultSet, Vector<String> columns) {
        Vector<Vector<String>> vectors = new Vector<>();
        try {
            while (resultSet.next()) {
                Vector<String> tempVec = new Vector<>();
                for (int i = 1; i <= columns.size(); i++) {//从1开始
                    tempVec.add(resultSet.getString(i));
                }
                vectors.add(tempVec);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (scrollPane != null) {
            jMainPanel.remove(scrollPane);
            scrollPane = null;
        }

        tableModel = new DefaultTableModel(vectors, columns) {
            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (updateData(aValue, row, column)) {
                    super.setValueAt(aValue, row, column);//在这里做修改值的限定
                }
            }
        };


        // 修改权限
        jTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if(authority == 1)
                    return false;
                return true;
            }
        };

        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setModel(tableModel);
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mouseRightButtonClick(e, jTable);
            }
        });

        scrollPane = new JScrollPane(jTable);
        scrollPane.setPreferredSize(new Dimension(1500, 700));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    }

    /**
     * 检查单项数据是否格式合法
     *
     * @param name  attribute's name
     * @param value just value
     * @return yes or no
     */
    private boolean checkDataLegal(String name, String value) {
        /*
        //check is legal or not
        if (value.contains("'") || value.contains(" ") || value.contains("=")) {
            noticeMsg("新数据中含有非法字段(\"'\", \" \"), \"=\"");
            return false;
        }
        if (name.equals("事件")) {
            if (DataNameUtils.eventName.indexOf(value) == -1) {
                noticeMsg("事件只有四种类型：借车、还车、损坏维修、罚款");
                return false;//不合法事件代码
            }
        }
        if (name.equals("时间")) {
            if (value.length() != 10 || value.charAt(4) != '-' || value.charAt(4) != '-' || Pattern.compile("[^\\d-]").matcher(value).find()) {
                noticeMsg("时间格式错误");
                return false;
            }
        }
        if (name.equals("车牌号")) {
            if (value.length() != 7) {
                noticeMsg("车牌号格式错误");
                return false;
            }
        }
        if (name.equals("车况")) {
            if (Pattern.compile("[^\\d]+").matcher(value).find()) {//保证全数字，防止后面强转出错
                noticeMsg("只能输入数字");
                return false;
            }
            if (Integer.valueOf(value) < 1 || Integer.valueOf(value) > 5) {
                noticeMsg("只能输入1-5的数字");
                return false;
            }
        }
        if (name.equals("是否会员")) {
            if (!value.equals("Y") && !value.equals("N")) {
                noticeMsg("只能输入Y或N");
                return false;
            }
        }
        if (name.equals("权限等级")) {
            if (Pattern.compile("[^\\d]+").matcher(value).find()) {//保证全数字，防止后面强转出错
                noticeMsg("只能输入数字");
                return false;
            }
            int valueInt = Integer.valueOf(value);
            if (valueInt < 1 || valueInt > 3) {
                noticeMsg("只能输入1-3的数字");
                return false;
            }
            if (valueInt != 3 && authority != 1) {
                noticeMsg("无权限操作");
                return false;
            }
        }

         */

        return true;
    }

    //判断是否为鼠标的BUTTON3按钮，BUTTON3为鼠标右键
    private void mouseRightButtonClick(MouseEvent evt, JTable jTable) {

        if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
            //通过点击位置找到点击为表格中的行
            int focusedRowIndex = jTable.rowAtPoint(evt.getPoint());
            if (focusedRowIndex == -1) {
                return;
            }
            selectRowIndex = focusedRowIndex;
            //将表格所选项设为当前右键点击的行
            jTable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
            //弹出菜单
            if (jPopupMenu != null) {
                jPopupMenu.show(jTable, evt.getX(), evt.getY());
            }
        }
    }

    /**
     * ”管理“监听器
     */
    private final ActionListener statisticsListener = e -> {
        PANEL_MODE = e.getActionCommand();
        frame.getContentPane().removeAll();//移除原有的东西

        ResultSet result = null;
        columns = new Vector<>(Arrays.asList(DataNameUtils.modeToColumns(PANEL_MODE)));
        try {
            if (authority == 2)
                result = DataBase.getInstance().selectByEqual(DataNameUtils.modeToTable(PANEL_MODE), new TreeMap<String, String>());
            else {
                Map<String, String> strict = new TreeMap<String, String>();
                strict.put("员工编号", s_id);
                result = DataBase.getInstance().selectByEqual(DataNameUtils.modeToTable(PANEL_MODE), strict);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            noticeMsg("数据库发生错误");
        }

        if (result != null && columns != null) {
            setTablePanel(result, columns);
            //重新渲染整个界面
            JPanelOpen jPanelSearch = new JPanelOpen();
            if (!(authority == 1 && PANEL_MODE.equals("员工"))) {
                setSearchPanel(jPanelSearch);
            }
            jMainPanel.add(jPanelSearch, BorderLayout.CENTER);
            jMainPanel.add(scrollPane, BorderLayout.SOUTH);
            jMainPanel.updateUI();
        }
    };


    /**
     * ”管理“监听器
     */
    private final ActionListener manageListener = e -> {
        PANEL_MODE = e.getActionCommand();
        frame.getContentPane().removeAll();//移除原有的东西

        ResultSet result = null;
        columns = new Vector<>(Arrays.asList(DataNameUtils.modeToColumns(PANEL_MODE)));
        try {
            if (authority == 2)
                result = DataBase.getInstance().selectByEqual(DataNameUtils.modeToTable(PANEL_MODE), new TreeMap<String, String>());
            else {
                Map<String, String> strict = new TreeMap<String, String>();
                strict.put("员工编号", s_id);
                result = DataBase.getInstance().selectByEqual(DataNameUtils.modeToTable(PANEL_MODE), strict);
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
            noticeMsg("数据库发生错误");
        }

        if (result != null && columns != null) {
            setTablePanel(result, columns);
            //重新渲染整个界面
            JPanelOpen jPanelSearch = new JPanelOpen();
            if (!(authority == 1 && PANEL_MODE.equals("员工"))) {
                setSearchPanel(jPanelSearch);
            }
            jMainPanel.add(jPanelSearch, BorderLayout.CENTER);
            jMainPanel.add(scrollPane, BorderLayout.SOUTH);
            jMainPanel.updateUI();
        }
    };

    private JPopupMenu jPopupMenu;

    //右键菜单
    private void createPopupMenu() {
        if (authority == 3) return;
        jPopupMenu = new JPopupMenu();

        JMenuItem delMenuItem = new JMenuItem();
        delMenuItem.setText("删除本行");
        delMenuItem.addActionListener(evt -> setDeleteRowDialog());
        jPopupMenu.add(delMenuItem);

        /*
        JMenuItem addMenuItem = new JMenuItem();
        addMenuItem.setText("添加新行");
        addMenuItem.addActionListener(evt -> setAddRowDialog());
        jPopupMenu.add(addMenuItem);
         */
    }

    private JDialog dialogAddRow;

    private void setDeleteRowDialog() {
        String key = (String) jTable.getValueAt(selectRowIndex, 0);
        try {
            DataBase.getInstance().deleteRow(PANEL_MODE, key);
            tableModel.removeRow(selectRowIndex);
        } catch (SQLException e) {
            e.printStackTrace();
            noticeMsg("删除失败");
        }
    }

    private void setAddRowDialog() {
        //create a dialog
        dialogAddRow = new JDialog(frame);
        dialogAddRow.setTitle("添加");
        dialogAddRow.setLayout(new FlowLayout());
        JPanel contentPanel = new JPanel();
        dialogAddRow.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        String[] columnNames = DataNameUtils.modeToColumns(PANEL_MODE);//从对应的静态工具类中读取列表行名

        Set<String> comboxSet = comboxMap.keySet();

        for (String s : columnNames) {
            JPanel jPanel = new JPanel();
            JLabel jLabel = new JLabel(s);
            jPanel.add(jLabel, BorderLayout.WEST);
            if (comboxSet.contains(s)) {
                jPanel.add(getComboBox(s), BorderLayout.EAST);
            } else {
                JTextField jTextField = new JTextField(10);
                jPanel.add(jTextField, BorderLayout.EAST);
            }
            contentPanel.add(jPanel);
        }
        JButton jButton = new JButton("确认");


        jButton.addActionListener(e -> {
            //set this hashmap
            HashMap<String, String> map = getDataMap((JPanel) dialogAddRow.getContentPane());
            if (checkNewData(map)) {
                try {
                    DataBase.getInstance().addRow(PANEL_MODE, map);//单例模式获取数据库管理对象，调用对应的添加行函数
                    tableModel.addRow(map2vector(map));
                    dialogAddRow.dispose();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    noticeMsg("新数据不合法或数据库发生错误");
                }
            }
        });

        contentPanel.add(jButton);
        dialogAddRow.setContentPane(contentPanel);
        dialogAddRow.pack();
        setCenter(dialogAddRow);
    }

    private boolean checkNewData(HashMap<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            String value = map.get(key);
            if (!checkDataLegal(key, value)) {//一个个检查
                return false;
            }
        }
        return true;
    }

    private boolean checkSearchData(HashMap<String, String> map) {
        Set<String> set = map.keySet();
        for (String key : set) {
            String value = map.get(key);
            if (value.equals("")) continue;
            if (!checkDataLegal(key, value)) {//一个个检查
                return false;
            }
        }
        return true;
    }

    /**
     * 更新表格数据
     */
    private boolean updateData(Object aValue, int row, int column) {

        String[] columnNames = DataNameUtils.modeToColumns(PANEL_MODE);
        try {
//            if (checkDataLegal(columnNames[column], aValue.toString(), (String) jTable.getValueAt(row, 0))) {
            if (checkDataLegal(columnNames[column], aValue.toString())) {
                DataBase.getInstance().updateData(PANEL_MODE, columnNames[column], aValue.toString(), (String) jTable.getValueAt(row, 0));
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            noticeMsg("新数据格式不合法");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获得用户填入的新行的数据
     * 传入一个JPanel父容器
     * 传出的字符串可能有带空
     *
     * @return map filled with data
     */
    private HashMap<String, String> getDataMap(JPanel jPanelIn) {
        HashMap<String, String> map = new HashMap<>();
        Component[] components = jPanelIn.getComponents();
        for (int i = 0; i < components.length; i++) {//不算button
            JPanel jPanelGet = null;
            if (components[i] instanceof JPanel) {
                jPanelGet = (JPanel) components[i];
                String key = ((JLabel) jPanelGet.getComponents()[0]).getText();
                if (key.equals("顾客") || key.equals("经手员工")) {
                    continue;
                }

                String value;
                try {
                    value = ((JTextField) jPanelGet.getComponents()[1]).getText();
                } catch (Exception e) {
                    value = (String) ((JComboBox) jPanelGet.getComponents()[1]).getSelectedItem();
                }
                map.put(key, value);
                System.out.println("key: " + key + "; value: " + value);
            }
        }
        return map;
    }

    /**
     * hashmap转vector，方便addRow
     *
     * @param map old hashmap
     * @return new vector
     */
    private Vector<String> map2vector(HashMap<String, String> map) {
        Vector<String> vector = new Vector<>();

        String[] columnNames = DataNameUtils.modeToColumns(PANEL_MODE);
        for (String s : columnNames) {
            String v = map.get(s);
            if (v != null) {
                vector.add(v);
            } else {
                vector.add("");
            }
        }

        return vector;
    }

}
