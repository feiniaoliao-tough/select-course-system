package com.example.selectcoursesystem.swing;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel msgLabel;

    public LoginFrame() {
        setTitle("学生选课系统");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // 标题
        JLabel title = new JLabel("学生选课系统", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        // 用户名
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("用户名："), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("密　码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // 提示信息
        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(msgLabel, gbc);

        // 按钮栏
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        JButton loginBtn = new JButton("登录");
        JButton registerBtn = new JButton("注册");
        loginBtn.setPreferredSize(new Dimension(90, 32));
        registerBtn.setPreferredSize(new Dimension(90, 32));
        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        gbc.gridy = 4;
        panel.add(btnPanel, gbc);

        add(panel);

        // 登录事件
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin());

        // 注册跳转
        registerBtn.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            msgLabel.setText("用户名和密码不能为空");
            return;
        }
        try {
            URL url = new URL("http://localhost:8080/api/user/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            if (conn.getResponseCode() == 200) {
                String resp = new String(conn.getInputStream().readAllBytes());
                JSONObject json = new JSONObject(resp);
                UserSession.username = json.getString("username");
                UserSession.role = json.getString("role");
                UserSession.userId = json.getInt("userId");
                if (json.has("studentId")) {
                    UserSession.studentId = json.getInt("studentId");
                }
                new MainFrame().setVisible(true);
                dispose();
            } else {
                msgLabel.setText("用户名或密码错误");
            }
        } catch (Exception ex) {
            msgLabel.setText("连接失败，请先启动后端");
        }
    }
}
