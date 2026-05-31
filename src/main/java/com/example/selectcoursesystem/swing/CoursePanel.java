package com.example.selectcoursesystem.swing;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class CoursePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public CoursePanel() {
        setLayout(new BorderLayout());

        // 顶部工具栏
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("刷新");
        topPanel.add(refreshBtn);

        if ("student".equals(UserSession.role)) {
            JButton enrollBtn = new JButton("选课");
            enrollBtn.setBackground(new Color(24, 144, 255));
            enrollBtn.setForeground(Color.WHITE);
            topPanel.add(enrollBtn);
            enrollBtn.addActionListener(e -> doEnroll());
        }

        add(topPanel, BorderLayout.NORTH);

        // 表格
        String[] columns = {"ID", "课程名", "教师", "时间", "已选", "容量"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadCourses());
        loadCourses();
    }

    private void loadCourses() {
        tableModel.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/course/list");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
                            c.getInt("courseId"),
                            c.getString("name"),
                            teacherName,
                            c.optString("schedule", "-"),
                            c.getInt("enrolled"),
                            c.getInt("capacity")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载失败：" + ex.getMessage());
        }
    }

    private void doEnroll() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一门课程");
            return;
        }
        int courseId = (int) tableModel.getValueAt(row, 0);
        String courseName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认选课：" + courseName + "？", "选课确认", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            URL url = new URL("http://localhost:8080/api/enrollment/enroll");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String body = String.format(
                    "{\"studentId\":%d,\"courseId\":%d}",
                    UserSession.studentId, courseId
            );
            conn.getOutputStream().write(body.getBytes());
            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "选课成功！");
                loadCourses();
            } else {
                String err = new String(conn.getErrorStream().readAllBytes());
                JOptionPane.showMessageDialog(this, "选课失败：" + new org.json.JSONObject(err).optString("error"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "操作失败：" + ex.getMessage());
        }
    }
}
