package exercisetimer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExerciseTimer extends JFrame {
    // Các thành phần giao diện
    private JLabel roundLabel;      // Hiển thị số hiệp (round)
    private JLabel timerLabel;      // Hiển thị đồng hồ số (digital timer)
    private JButton startButton;    // Nút “Bắt đầu”
    private JButton stopButton;     // Nút “Dừng”
    
    // Timer cho digital countdown và cập nhật đồng hồ kim
    private Timer exerciseTimer;    
    private Timer clockTimer;       
    
    // Panel hiển thị đồng hồ kim
    private ClockPanel clockPanel;
    
    // Các biến của bài tập
    private int round = 1;                
    private int timeLeft = 45;            
    private int currentPhaseDuration = 45; 
    private boolean isExercisePhase = true;  

    public ExerciseTimer() {
        setTitle("Exercise Timer");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // --- Panel trên: hiển thị số hiệp ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roundLabel = new JLabel("Hiệp: " + round);
        roundLabel.setFont(new Font("Dialog", Font.BOLD, 20)); // Phóng to chữ "Hiệp"
        topPanel.add(roundLabel);
        add(topPanel, BorderLayout.NORTH);
        
        // --- Panel giữa: chia làm 2 cột (digital timer và analog clock) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        
        // Digital timer (đồng hồ số)
        timerLabel = new JLabel(String.valueOf(timeLeft), SwingConstants.CENTER);
        // Sử dụng custom font cho digital timer, kích thước lớn (64f)
        Font customFontLarge = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 80f);
        if (customFontLarge != null) {
            timerLabel.setFont(customFontLarge);
        } else {
            // Fallback nếu không tải được font tùy chỉnh
            timerLabel.setFont(new Font("Dialog", Font.BOLD, 64));
        }
        timerLabel.setForeground(Color.BLACK); // Chữ đen trên nền sáng
        JPanel timerPanel = new JPanel(new BorderLayout());
        timerPanel.add(timerLabel, BorderLayout.CENTER);
        // Đặt nền của panel digital timer thành màu xám trắng (light gray)
        timerPanel.setBackground(Color.LIGHT_GRAY);
        timerPanel.setOpaque(true);
        
        // Trang trí khung của digital timer theo phong cách Windows 98:
        // Sử dụng viền bevel kèm theo tiêu đề "Digital Timer"
        Border raisedBevel = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        // Sử dụng custom font cho tiêu đề (Digital Timer) với kích thước nhỏ (14f)
        Font customFontSmall = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 30f);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(raisedBevel, "Digital Timer");
        if (customFontSmall != null) {
            titledBorder.setTitleFont(customFontSmall);
        }
        Border compoundBorder = BorderFactory.createCompoundBorder(titledBorder, emptyBorder);
        timerPanel.setBorder(compoundBorder);
        
        centerPanel.add(timerPanel);
        
        // Analog clock (đồng hồ kim)
        clockPanel = new ClockPanel();
        centerPanel.add(clockPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // --- Panel dưới: chứa nút “Bắt đầu” và “Dừng” ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startButton = new JButton("Bắt đầu");
        bottomPanel.add(startButton);
        stopButton = new JButton("Dừng");
        stopButton.setEnabled(false);  // Ban đầu, nút "Dừng" bị vô hiệu
        bottomPanel.add(stopButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Xử lý sự kiện nút “Bắt đầu”
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                startExerciseTimer();
            }
        });
        
        // Xử lý sự kiện nút “Dừng”
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
        
        // Timer cập nhật giao diện đồng hồ kim mỗi giây
        clockTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clockPanel.repaint();
            }
        });
        clockTimer.start();
    }
    
    // Phương thức khởi động timer đếm ngược (digital timer)
    private void startExerciseTimer() {
        exerciseTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerLabel.setText(String.valueOf(timeLeft));
                timeLeft--;
                
                if (timeLeft < 0) {
                    //Toolkit.getDefaultToolkit().beep();
                    AudioPlayer.playBeep();
                    if (isExercisePhase) {
                        isExercisePhase = false;
                        currentPhaseDuration = 15;
                        timeLeft = 15;
                    } else {
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
    
    // Lớp vẽ đồng hồ kim (analog clock) dựa trên thời gian của giai đoạn hiện tại
    private class ClockPanel extends JPanel {
        public ClockPanel() {
            setPreferredSize(new Dimension(200, 200));
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
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
            
            // Tính toán vị trí kim dựa trên phần trăm thời gian trôi qua của giai đoạn hiện tại
            double elapsed = currentPhaseDuration - timeLeft;
            if (elapsed < 0) elapsed = 0;
            double fraction = elapsed / currentPhaseDuration;
            double theta = Math.toRadians(fraction * 360 - 90);
            int handLength = diameter / 2 - 15;
            int handX = centerX + (int)(handLength * Math.cos(theta));
            int handY = centerY + (int)(handLength * Math.sin(theta));
            
            // Vẽ kim đồng hồ màu đỏ và điểm trung tâm
            g.setColor(Color.RED);
            g.drawLine(centerX, centerY, handX, handY);
            g.fillOval(centerX - 3, centerY - 3, 6, 6);
        }
    }
    
    public static void main(String[] args) {
        try {
            // Áp dụng Look and Feel Windows Classic (gần giống Windows 98)
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ExerciseTimer frame = new ExerciseTimer();
                frame.setVisible(true);
            }
        });
    }
}
