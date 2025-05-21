package exercisetimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExerciseTimer extends JFrame {
    private JLabel roundLabel;      // Hiển thị số hiệp (round)
    private JLabel timerLabel;      // Hiển thị số giây còn lại
    private JButton startButton;    // Nút bắt đầu
    private Timer timer;            // Đối tượng Timer của Swing

    private int round = 1;          // Số hiệp hiện tại
    private int timeLeft = 45;      // Số giây còn lại của giai đoạn (45s cho tập, 15s cho nghỉ)
    private boolean isExercisePhase = true; // true: giai đoạn tập, false: giai đoạn nghỉ

    public ExerciseTimer() {
        setTitle("Exercise Timer");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo panel để hiển thị thông tin hiệp
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roundLabel = new JLabel("Hiệp: " + round);
        roundLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        topPanel.add(roundLabel);
        add(topPanel, BorderLayout.NORTH);

        // Tạo nhãn chính ở giữa để hiển thị đồng hồ đếm ngược
        timerLabel = new JLabel("45", SwingConstants.CENTER);
        timerLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 48));
        add(timerLabel, BorderLayout.CENTER);

        // Tạo panel chứa nút bắt đầu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Bắt đầu");
        startButton.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        bottomPanel.add(startButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Thêm sự kiện cho nút "Bắt đầu"
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false); // Vô hiệu hóa nút sau khi bấm
                startTimer();
            }
        });
    }

    // Phương thức khởi động Timer với interval 1 giây (1000ms)
    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cập nhật hiển thị thời gian
                timerLabel.setText(String.valueOf(timeLeft));
                timeLeft--;

                // Khi hết thời gian của giai đoạn hiện tại
                if (timeLeft < 0) {
                    // Phát tiếng báo chuyển giao đoạn
                    Toolkit.getDefaultToolkit().beep();

                    if (isExercisePhase) {
                        // 45s tập kết thúc, chuyển sang 15s nghỉ
                        isExercisePhase = false;
                        timeLeft = 15;
                    } else {
                        // 15s nghỉ kết thúc, tăng hiệp và quay lại 45s tập
                        isExercisePhase = true;
                        round++;
                        roundLabel.setText("Hiệp: " + round);
                        timeLeft = 45;
                    }
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {
        try {
            // Áp dụng Look and Feel Windows Classic (gần giống Windows 98)
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

            // Thiết lập font mặc định toàn cục cho giao diện
            UIManager.put("Label.font", new Font("MS Sans Serif", Font.PLAIN, 16));
            UIManager.put("Button.font", new Font("MS Sans Serif", Font.PLAIN, 16));
            UIManager.put("Panel.font", new Font("MS Sans Serif", Font.PLAIN, 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            ExerciseTimer frame = new ExerciseTimer();
            frame.setVisible(true);
        });
    }
}
