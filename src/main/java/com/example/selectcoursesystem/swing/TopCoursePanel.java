package com.example.selectcoursesystem.swing;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TopCoursePanel extends JPanel {

    private DefaultTableModel tableModel;

    public TopCoursePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("刷新");
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"排名", "课程名", "教师", "已选人数", "容量"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadTop());
        loadTop();
    }

    private void loadTop() {
        tableModel.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/course/top");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                String resp = new String(conn.getInputStream().readAllBytes());
                JSONArray arr = new JSONArray(resp);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    String teacherName = "-";
                    if (!c.isNull("teacher") && !c.getJSONObject("teacher").isNull("name")) {
                        teacherName = c.getJSONObject("teacher").getString("name");
                    }
                    tableModel.addRow(new Object[]{
                            i + 1,
                            c.getString("name"),
                            teacherName,
                            c.getInt("enrolled"),
                            c.getInt("capacity")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载失败：" + ex.getMessage());
        }
    }
}
