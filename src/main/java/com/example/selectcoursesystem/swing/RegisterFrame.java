package com.example.selectcoursesystem.swing;

import javax.swing.*;
import java.awt.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JComboBox<String> roleBox;
    private JLabel msgLabel;

    public RegisterFrame() {
        setTitle("注册账号");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel title = new JLabel("注册账号", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("用户名："), gbc);
        usernameField = new JTextField(15);
        gbc.gridx = 1; panel.add(usernameField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("密　码："), gbc);
        passwordField = new JPasswordField(15);
        gbc.gridx = 1; panel.add(passwordField, gbc);

        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("邮　箱："), gbc);
        emailField = new JTextField(15);
        gbc.gridx = 1; panel.add(emailField, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        panel.add(new JLabel("角　色："), gbc);
        roleBox = new JComboBox<>(new String[]{"student", "teacher"});
        gbc.gridx = 1; panel.add(roleBox, gbc);

        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgLabel.setForeground(Color.RED);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(msgLabel, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        JButton regBtn = new JButton("注册");
        JButton backBtn = new JButton("返回登录");
        regBtn.setPreferredSize(new Dimension(90, 32));
        backBtn.setPreferredSize(new Dimension(100, 32));
        btnPanel.add(regBtn);
        btnPanel.add(backBtn);
        gbc.gridy = 6;
        panel.add(btnPanel, gbc);

        add(panel);

        regBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            msgLabel.setText("所有字段不能为空");
            return;
        }
        try {
            URL url = new URL("http://localhost:8080/api/user/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String body = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}",
                    username, password, email, role
            );
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            if (conn.getResponseCode() == 200) {
                JOptionPane.showMessageDialog(this, "注册成功！请登录");
                new LoginFrame().setVisible(true);
                dispose();
            } else {
                msgLabel.setText("注册失败，用户名可能已存在");
            }
        } catch (Exception ex) {
            msgLabel.setText("连接失败，请先启动后端");
        }
    }
}