package com.example.selectcoursesystem.swing;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("学生选课系统 - " + UserSession.username + "（" + UserSession.role + "）");
        setSize(800, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 顶部导航
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(24, 144, 255));
        topBar.setPreferredSize(new Dimension(800, 48));
        JLabel titleLabel = new JLabel("  学生选课系统", SwingConstants.LEFT);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBackground(new Color(24, 144, 255));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 标签页
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        tabs.addTab("课程列表", new CoursePanel());
        if ("student".equals(UserSession.role)) {
            tabs.addTab("我的选课", new MyEnrollmentPanel());
            tabs.addTab("我的成绩", new MyGradePanel());
        }
        tabs.addTab("热门课程", new TopCoursePanel());
        add(tabs, BorderLayout.CENTER);

        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }
}