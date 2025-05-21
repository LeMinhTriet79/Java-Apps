/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package exercisetimer;

/**
 *
 * @author Minh Triet
 */
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
        roundLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(roundLabel);
        add(topPanel, BorderLayout.NORTH);

        // Tạo nhãn chính ở giữa để hiển thị đồng hồ đếm ngược
        timerLabel = new JLabel("45", SwingConstants.CENTER);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        add(timerLabel, BorderLayout.CENTER);

        // Tạo panel chứa nút bắt đầu
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Bắt đầu");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        bottomPanel.add(startButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Thêm sự kiện cho nút "Bắt đầu"
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false); // Vô hiệu hóa nút sau khi bấm để không nhấn lại
                startTimer();
            }
        });
    }

    // Phương thức khởi động Timer với interval 1 giây (1000ms)
    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cập nhật nhãn hiển thị thời gian còn lại
                timerLabel.setText(String.valueOf(timeLeft));
                timeLeft--;

                // Khi hết thời gian của giai đoạn hiện tại
                if (timeLeft < 0) {
                    // Phát âm báo chuyển giao đoạn
                    Toolkit.getDefaultToolkit().beep();

                    if (isExercisePhase) {
                        // Kết thúc 45s tập, chuyển sang 15s nghỉ
                        isExercisePhase = false;
                        timeLeft = 15;
                    } else {
                        // Kết thúc 15s nghỉ, chuyển qua hiệp kế tiếp
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            ExerciseTimer frame = new ExerciseTimer();
            frame.setVisible(true);
        });
    }
}