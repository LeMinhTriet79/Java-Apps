package exercisetimer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.BevelBorder;

// Nếu chưa có FontLoader, hãy yêu cầu mình viết cho nhé
// FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", size) -> Font

public class ExerciseTimerWithTab extends JFrame {
    public ExerciseTimerWithTab() {
        setTitle("Đồng Hồ Đếm Thời Gian");
        setSize(540, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
         setIconImage(new ImageIcon(getClass().getResource("/Icon/clock_di.png")).getImage());


        
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Digital (legacy)
        tabbedPane.addTab("Digital (Legacy)", new ExerciseTimerLegacyPanel());

        // Tab 2: Windows 98
        tabbedPane.addTab("Windows 98", new DigitalWin98Panel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new ExerciseTimerWithTab().setVisible(true));
    }
}

// --------------- TAB 1: DIGITAL (LEGACY, CÓ FONT & ENGLISH) -----------------
class ExerciseTimerLegacyPanel extends JPanel {
    private JLabel timerLabel;
    private JButton startButton, stopButton, resetButton;
    private JTextField hourInput, minuteInput, secondInput;
    private Timer countdownTimer;
    private int totalSeconds;
    private boolean isCounting = false;

    private Font customFontLabel, customFontTimer, customFontButton, customFontTitle;

    public ExerciseTimerLegacyPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(192,192,192));

        // Font: Nếu thiếu FontLoader, nhắn thầy nhé!
        try {
            customFontLabel = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 18f);
            customFontTimer = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 76f);
            customFontButton = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 20f);
            customFontTitle = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 28f);
        } catch (Throwable t) {
            customFontLabel = new Font("Dialog", Font.BOLD, 18);
            customFontTimer = new Font("Dialog", Font.BOLD, 76);
            customFontButton = new Font("Dialog", Font.BOLD, 20);
            customFontTitle = new Font("Dialog", Font.BOLD, 28);
        }

        // TOP: Tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(192,192,192));
        JLabel titleLabel = new JLabel("Digital Countdown Timer");
        titleLabel.setFont(customFontTitle);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // CENTER: Đồng hồ số + Input giờ/phút/giây
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(224,224,224));
        centerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Input fields theo style Win98
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        inputPanel.setBackground(new Color(224,224,224));
        hourInput = createInputField();
        minuteInput = createInputField();
        secondInput = createInputField();
        JLabel colon1 = createColonLabel();
        JLabel colon2 = createColonLabel();
        inputPanel.add(new JLabel("Set: "));
        inputPanel.add(hourInput);
        inputPanel.add(colon1);
        inputPanel.add(minuteInput);
        inputPanel.add(colon2);
        inputPanel.add(secondInput);
        centerPanel.add(inputPanel);

        // Đồng hồ số lớn giữa
        timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(customFontTimer);
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(224,224,224));
        timerLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setPreferredSize(new Dimension(380, 110));
        centerPanel.add(timerLabel);

        add(centerPanel, BorderLayout.CENTER);

        // BOTTOM: Các nút
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        bottomPanel.setBackground(new Color(192,192,192));

        startButton = new JButton("Start");
        startButton.setFont(customFontButton);
        startButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        bottomPanel.add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setFont(customFontButton);
        stopButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        stopButton.setEnabled(false);
        bottomPanel.add(stopButton);

        resetButton = new JButton("Reset");
        resetButton.setFont(customFontButton);
        resetButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        resetButton.setEnabled(false);
        bottomPanel.add(resetButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // SỰ KIỆN
        startButton.addActionListener(e -> {
            totalSeconds = parseInput();
            if (totalSeconds <= 0) {
                JOptionPane.showMessageDialog(this, "Set time > 0!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            setInputsEnabled(false);
            isCounting = true;
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            resetButton.setEnabled(true);
            countdownTimer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (totalSeconds > 0) {
                        totalSeconds--;
                        timerLabel.setText(formatTime(totalSeconds));
                    } else {
                        timerLabel.setText("00:00:00");
                        setInputsEnabled(true);
                        startButton.setEnabled(true);
                        stopButton.setEnabled(false);
                        resetButton.setEnabled(true);
                        isCounting = false;
                        countdownTimer.stop();
                        try { AudioPlayer.playBeep(); } catch (Throwable t) {}
                    }
                }
            });
            timerLabel.setText(formatTime(totalSeconds));
            countdownTimer.start();
        });

        stopButton.addActionListener(e -> {
            if (countdownTimer != null && isCounting) {
                countdownTimer.stop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);
                isCounting = false;
            }
        });

        resetButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            hourInput.setText("00");
            minuteInput.setText("00");
            secondInput.setText("00");
            timerLabel.setText("00:00:00");
            setInputsEnabled(true);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            resetButton.setEnabled(false);
            isCounting = false;
        });
    }

    // Utility: Tạo input style Win98
    private JTextField createInputField() {
        JTextField field = new JTextField("00", 2);
        field.setFont(customFontLabel);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        field.setBackground(Color.WHITE);
        field.setPreferredSize(new Dimension(32, 34));
        // Chỉ cho nhập số
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) || field.getText().length() >= 2) e.consume();
            }
        });
        return field;
    }

    // Utility: tạo dấu :
    private JLabel createColonLabel() {
        JLabel label = new JLabel(":");
        label.setFont(customFontLabel);
        label.setPreferredSize(new Dimension(10, 34));
        return label;
    }

    // Chuyển input thành tổng số giây
    private int parseInput() {
        int h = parseNumber(hourInput.getText());
        int m = parseNumber(minuteInput.getText());
        int s = parseNumber(secondInput.getText());
        return h * 3600 + m * 60 + s;
    }
    private int parseNumber(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    // Định dạng thành HH:MM:SS
    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private void setInputsEnabled(boolean enabled) {
        hourInput.setEnabled(enabled);
        minuteInput.setEnabled(enabled);
        secondInput.setEnabled(enabled);
    }
}


// --------------- TAB 2: WINDOWS 98 (CÓ FONT & ENGLISH) -----------------
class DigitalWin98Panel extends JPanel {
    private JLabel roundLabel, timerLabel;
    private JButton startButton, stopButton;
    private Timer exerciseTimer;
    private Timer clockTimer;
    private ClockPanelWin98 clockPanel;
    private int round = 1;
    private int timeLeft = 45;
    private int currentPhaseDuration = 45;
    private boolean isExercisePhase = true;

    private Font customFontLabel, customFontTimer, customFontButton, customFontTitle;

    public DigitalWin98Panel() {
        setLayout(new BorderLayout());
        setBackground(new Color(192,192,192));

        try {
            customFontLabel = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 30f);
            customFontTimer = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 200f);
            customFontButton = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 20f);
            customFontTitle = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 24f);
        } catch (Throwable t) {
            customFontLabel = new Font("Dialog", Font.BOLD, 20);
            customFontTimer = new Font("Dialog", Font.BOLD, 64);
            customFontButton = new Font("Dialog", Font.BOLD, 18);
            customFontTitle = new Font("Dialog", Font.BOLD, 22);
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(192,192,192));
        roundLabel = new JLabel("Round: " + round);
        roundLabel.setFont(customFontLabel);
        topPanel.add(roundLabel);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(new Color(192,192,192));

        JPanel digitalPanel = new JPanel(new BorderLayout());
        digitalPanel.setBackground(new Color(224,224,224));
        digitalPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JLabel timerTitle = new JLabel("Digital Timer", SwingConstants.CENTER);
        timerTitle.setFont(customFontTitle);
        timerTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 2, 0));
        digitalPanel.add(timerTitle, BorderLayout.NORTH);

        timerLabel = new JLabel(String.valueOf(timeLeft), SwingConstants.CENTER);
        timerLabel.setFont(customFontTimer);
        timerLabel.setForeground(Color.BLACK);
        digitalPanel.add(timerLabel, BorderLayout.CENTER);
        centerPanel.add(digitalPanel);

        JPanel analogWrapper = new JPanel(new BorderLayout());
        analogWrapper.setBackground(new Color(224,224,224));
        analogWrapper.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        clockPanel = new ClockPanelWin98();
        clockPanel.setBackground(new Color(224,224,224));
        analogWrapper.add(clockPanel, BorderLayout.CENTER);
        centerPanel.add(analogWrapper);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(192,192,192));
        startButton = new JButton("Start");
        startButton.setFont(customFontButton);
        startButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        bottomPanel.add(startButton);

        stopButton = new JButton("Stop");
        stopButton.setFont(customFontButton);
        stopButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        stopButton.setEnabled(false);
        bottomPanel.add(stopButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // EVENTS
        startButton.addActionListener(e -> {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            startExerciseTimer();
        });
        stopButton.addActionListener(e -> {
            if (exerciseTimer != null && exerciseTimer.isRunning()) {
                exerciseTimer.stop();
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
            }
        });

        clockTimer = new Timer(1000, e -> clockPanel.repaint());
        clockTimer.start();
    }

    private void startExerciseTimer() {
        exerciseTimer = new Timer(1000, e -> {
            timerLabel.setText(String.valueOf(timeLeft));
            timeLeft--;
            if (timeLeft < 0) {
                try {
                    AudioPlayer.playBeep();
                } catch (Throwable t) {}
                if (isExercisePhase) {
                    isExercisePhase = false;
                    currentPhaseDuration = 15;
                    timeLeft = 15;
                } else {
                    isExercisePhase = true;
                    round++;
                    roundLabel.setText("Round: " + round);
                    currentPhaseDuration = 45;
                    timeLeft = 45;
                }
            }
        });
        exerciseTimer.start();
    }

    private class ClockPanelWin98 extends JPanel {
    public ClockPanelWin98() {
        setPreferredSize(new Dimension(200, 200));
        setBackground(new Color(192, 192, 192)); // Gray background
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(w, h) / 2 - 18;

        // Draw clock border
        g2.setColor(Color.DARK_GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(cx - radius, cy - radius, 2 * radius, 2 * radius);

        // Draw minute dots
        for (int i = 0; i < 60; i++) {
            double angle = Math.toRadians(i * 6 - 90);
            int x = cx + (int) ((radius - 6) * Math.cos(angle));
            int y = cy + (int) ((radius - 6) * Math.sin(angle));
            if (i % 5 != 0) { // minute dot
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 2, y - 2, 4, 4);
            }
        }

        // Draw hour cubes
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            int x = cx + (int) ((radius - 6) * Math.cos(angle));
            int y = cy + (int) ((radius - 6) * Math.sin(angle));
            // 3D cube effect
            g2.setColor(new Color(0, 180, 200)); // Cyan blue
            g2.fillRect(x - 7, y - 7, 14, 14);
            g2.setColor(Color.BLACK);
            g2.drawRect(x - 7, y - 7, 14, 14);
            g2.setColor(Color.WHITE);
            g2.drawLine(x - 7, y - 7, x + 7, y - 7); // top highlight
            g2.drawLine(x - 7, y - 7, x - 7, y + 7); // left highlight
        }

        // ===== Draw the hands: emulate 3D style =====
        // Lấy thông tin giờ phút giây từ hệ thống
        java.util.Calendar now = java.util.Calendar.getInstance();
        int hour = now.get(java.util.Calendar.HOUR);
        int minute = now.get(java.util.Calendar.MINUTE);
        int second = now.get(java.util.Calendar.SECOND);

        // Kim giờ: 3D
        drawHand3D(g2, cx, cy, hour * 30 + minute / 2 - 90, radius - 60, 9, new Color(0, 180, 200));
        // Kim phút: 3D
        drawHand3D(g2, cx, cy, minute * 6 - 90, radius - 38, 6, new Color(0, 180, 200));
        // Kim giây: Mảnh
        double secAngle = Math.toRadians(second * 6 - 90);
        int sx = cx + (int)((radius - 30) * Math.cos(secAngle));
        int sy = cy + (int)((radius - 30) * Math.sin(secAngle));
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(cx, cy, sx, sy);

        // Red center dot
        g2.setColor(Color.RED);
        g2.fillOval(cx - 4, cy - 4, 8, 8);

        g2.dispose();
    }

    // Draw a hand with 3D effect (similar to Win98)
    private void drawHand3D(Graphics2D g2, int cx, int cy, double angleDeg, int length, int width, Color color) {
        double angle = Math.toRadians(angleDeg);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        int x1 = cx + (int)(length * cos);
        int y1 = cy + (int)(length * sin);

        // Tạo đa giác tay kim (hình thang)
        int w2 = width / 2;
        Polygon hand = new Polygon();
        hand.addPoint(cx - (int)(w2 * sin), cy + (int)(w2 * cos));
        hand.addPoint(cx + (int)(w2 * sin), cy - (int)(w2 * cos));
        hand.addPoint(x1 + (int)(w2 * sin), y1 - (int)(w2 * cos));
        hand.addPoint(x1 - (int)(w2 * sin), y1 + (int)(w2 * cos));

        // Vẽ bóng đổ
        Polygon shadow = new Polygon();
        for (int i = 0; i < hand.npoints; i++) {
            shadow.addPoint(hand.xpoints[i] + 2, hand.ypoints[i] + 2);
        }
        g2.setColor(new Color(100, 100, 100, 80));
        g2.fillPolygon(shadow);

        // Vẽ chính kim
        g2.setColor(color);
        g2.fillPolygon(hand);
        g2.setColor(Color.WHITE);
        g2.drawPolygon(hand);
    }
}

}
