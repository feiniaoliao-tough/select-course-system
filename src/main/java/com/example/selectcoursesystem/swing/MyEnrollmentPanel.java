package com.example.selectcoursesystem.swing;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyEnrollmentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public MyEnrollmentPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("刷新");
        JButton dropBtn = new JButton("退课");
        dropBtn.setBackground(new Color(255, 77, 79));
        dropBtn.setForeground(Color.WHITE);
        topPanel.add(refreshBtn);
        topPanel.add(dropBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"选课ID", "课程名", "教师", "时间", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadEnrollments());
        dropBtn.addActionListener(e -> doDrop());
        loadEnrollments();
    }

    private void loadEnrollments() {
        tableModel.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/enrollment/my/" + UserSession.studentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                String resp = new String(conn.getInputStream().readAllBytes());
                JSONArray arr = new JSONArray(resp);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject e = arr.getJSONObject(i);
                    JSONObject course = e.optJSONObject("course");
                    String courseName = course != null ? course.optString("name", "-") : "-";
                    String teacherName = "-";
                    if (course != null && !course.isNull("teacher")) {
                        teacherName = course.getJSONObject("teacher").optString("name", "-");
                    }
                    String schedule = course != null ? course.optString("schedule", "-") : "-";
                    String status = "active".equals(e.getString("status")) ? "已选" : "已退";
                    tableModel.addRow(new Object[]{
                            e.getInt("enrollmentId"),
                            courseName,
                            teacherName,
                            schedule,
                            status
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载失败：" + ex.getMessage());
        }
    }

    private void doDrop() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录");
            return;
        }
        String status = (String) tableModel.getValueAt(row, 4);
        if ("已退".equals(status)) {
            JOptionPane.showMessageDialog(this, "该课程已退选");
            return;
        }
        String courseName = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "确认退课：" + courseName + "？", "退课确认", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            URL url = new URL("http://localhost:8080/api/enrollment/drop");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            // 需要courseId，从课程名反查不方便，这里存courseId在隐藏列
            // 简化处理：重新加载数据找courseId
            String resp2 = new String(new URL("http://localhost:8080/api/enrollment/my/" + UserSession.studentId)
                    .openConnection().getInputStream().readAllBytes());
            JSONArray arr = new JSONArray(resp2);
            int courseId = -1;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject e = arr.getJSONObject(i);
                if (e.getInt("enrollmentId") == (int) tableModel.getValueAt(row, 0)) {
                    courseId = e.getJSONObject("course").getInt("courseId");
                    break;
                }
            }
            String body = String.format("{\"studentId\":%d,\"courseId\":%d}", UserSession.studentId, courseId);
            conn.getOutputStream().write(body.getBytes());
            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "退课成功！");
                loadEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "退课失败");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "操作失败：" + ex.getMessage());
        }
    }
}
