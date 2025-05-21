package exercisetimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExerciseTimer extends JFrame {
    // Thành phần giao diện
    private JLabel roundLabel;      // Hiển thị số hiệp
    private JLabel timerLabel;      // Hiển thị số giây còn lại của bài tập
    private JButton startButton;    // Nút “Bắt đầu”
    private JButton stopButton;     // Nút “Dừng”
    
    // Timer cho digital timer và đồng hồ kim
    private Timer exerciseTimer;    // Dùng để đếm ngược bài tập
    private Timer clockTimer;       // Dùng để cập nhật giao diện đồng hồ kim mỗi giây
    
    // Panel cho đồng hồ kim (vẽ analog clock)
    private ClockPanel clockPanel;

    // Các biến của bài tập
    private int round = 1;                // Số hiệp hiện tại
    private int timeLeft = 45;            // Số giây còn lại của giai đoạn hiện tại
    private int currentPhaseDuration = 45; // Tổng số giây của giai đoạn hiện tại (45 hoặc 15)
    private boolean isExercisePhase = true; // true: giai đoạn tập (45s), false: giai đoạn nghỉ (15s)

    public ExerciseTimer() {
        setTitle("Exercise Timer");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel trên (hiển thị số hiệp)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roundLabel = new JLabel("Hiệp: " + round);
        roundLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        topPanel.add(roundLabel);
        add(topPanel, BorderLayout.NORTH);

        // Panel giữa chia làm 2 cột: bên trái hiển thị digital timer, bên phải hiển thị analog clock
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        
        // Digital timer
        timerLabel = new JLabel(String.valueOf(timeLeft), SwingConstants.CENTER);
        timerLabel.setFont(new Font("MS Sans Serif", Font.BOLD, 48));
        JPanel timerPanel = new JPanel(new BorderLayout());
        timerPanel.add(timerLabel, BorderLayout.CENTER);
        centerPanel.add(timerPanel);

        // Analog clock
        clockPanel = new ClockPanel();
        centerPanel.add(clockPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Panel dưới chứa nút “Bắt đầu” và “Dừng”
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Bắt đầu");
        startButton.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        bottomPanel.add(startButton);

        stopButton = new JButton("Dừng");
        stopButton.setFont(new Font("MS Sans Serif", Font.BOLD, 16));
        stopButton.setEnabled(false); // Vô hiệu hóa nút “Dừng” ban đầu
        bottomPanel.add(stopButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Sự kiện cho nút “Bắt đầu”
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                startExerciseTimer();
            }
        });

        // Sự kiện cho nút “Dừng”
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (exerciseTimer != null && exerciseTimer.isRunning()) {
                    exerciseTimer.stop();
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                }
            }
        });

        // Timer cập nhật đồng hồ kim (vẽ lại mỗi 1 giây)
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clockPanel.repaint();
            }
        });
        clockTimer.start();
    }

    // Phương thức khởi động exerciseTimer (digital countdown)
    private void startExerciseTimer() {
        exerciseTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cập nhật digital timer
                timerLabel.setText(String.valueOf(timeLeft));
                timeLeft--;

                // Khi hết thời gian giai đoạn hiện tại
                if (timeLeft < 0) {
                    AudioPlayer.playBeep();
                    if (isExercisePhase) {
                        // Kết thúc giai đoạn tập 45s, chuyển sang nghỉ 15s
                        isExercisePhase = false;
                        currentPhaseDuration = 15;
                        timeLeft = 15;
                    } else {
                        // Kết thúc giai đoạn nghỉ 15s, chuyển sang tập 45s và tăng số hiệp
                        isExercisePhase = true;
                        round++;
                        roundLabel.setText("Hiệp: " + round);
                        currentPhaseDuration = 45;
                        timeLeft = 45;
                    }
                }
            }
        });
        exerciseTimer.start();
    }

    // Lớp vẽ analog clock dựa trên thời gian của giai đoạn hiện tại
    private class ClockPanel extends JPanel {
        public ClockPanel() {
            setPreferredSize(new Dimension(200, 200));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Tính toán kích thước và vị trí của đồng hồ
            int diameter = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            // Vẽ viền đồng hồ
            g.setColor(Color.BLACK);
            g.drawOval(x, y, diameter, diameter);
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            // Vẽ các tick mark cho 12 giờ
            for (int i = 0; i < 12; i++) {
                double angle = Math.toRadians((i * 30) - 90);
                int innerX = centerX + (int)((diameter / 2 - 10) * Math.cos(angle));
                int innerY = centerY + (int)((diameter / 2 - 10) * Math.sin(angle));
                int outerX = centerX + (int)((diameter / 2 - 2) * Math.cos(angle));
                int outerY = centerY + (int)((diameter / 2 - 2) * Math.sin(angle));
                g.drawLine(innerX, innerY, outerX, outerY);
            }

            // Tính toán vị trí kim dựa trên thời gian của giai đoạn hiện tại:
            // Khi mới bắt đầu (timeLeft == currentPhaseDuration) => elapsed = 0, kim ở 12h.
            // Khi hết thời gian (timeLeft == 0) => elapsed = currentPhaseDuration, kim quay đủ 360 độ.
            double elapsed = currentPhaseDuration - timeLeft;
            if (elapsed < 0) elapsed = 0;
            double fraction = elapsed / currentPhaseDuration;
            // Góc tính từ vị trí 12h (-90 độ); khi fraction tăng từ 0 tới 1, kim quay đầy 360 độ
            double theta = Math.toRadians(fraction * 360 - 90);
            int handLength = diameter / 2 - 15;
            int handX = centerX + (int)(handLength * Math.cos(theta));
            int handY = centerY + (int)(handLength * Math.sin(theta));

            // Vẽ kim đồng hồ màu đỏ
            g.setColor(Color.RED);
            g.drawLine(centerX, centerY, handX, handY);
            // Vẽ điểm trung tâm
            g.fillOval(centerX - 3, centerY - 3, 6, 6);
        }
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
