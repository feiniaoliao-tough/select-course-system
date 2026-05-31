package com.example.selectcoursesystem.swing;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyGradePanel extends JPanel {

    private DefaultTableModel tableModel;

    public MyGradePanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("刷新");
        topPanel.add(refreshBtn);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"课程名", "教师", "分数", "等级"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadGrades());
        loadGrades();
    }

    private void loadGrades() {
        tableModel.setRowCount(0);
        try {
            URL url = new URL("http://localhost:8080/api/grade/my/" + UserSession.studentId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                String resp = new String(conn.getInputStream().readAllBytes());
                JSONArray arr = new JSONArray(resp);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject g = arr.getJSONObject(i);
                    JSONObject enrollment = g.optJSONObject("enrollment");
                    JSONObject course = enrollment != null ? enrollment.optJSONObject("course") : null;
                    String courseName = course != null ? course.optString("name", "-") : "-";
                    String teacherName = "-";
                    if (course != null && !course.isNull("teacher")) {
                        teacherName = course.getJSONObject("teacher").optString("name", "-");
                    }
                    tableModel.addRow(new Object[]{
                            courseName,
                            teacherName,
                            g.optDouble("score", 0),
                            g.optString("gradeLevel", "-")
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载失败：" + ex.getMessage());
        }
    }
}
