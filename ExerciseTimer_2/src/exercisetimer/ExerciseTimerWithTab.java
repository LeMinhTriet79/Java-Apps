package exercisetimer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.BevelBorder;
import java.text.SimpleDateFormat;
import java.util.Date;

// Giả sử đã có FontLoader
// FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", size) -> Font

public class ExerciseTimerWithTab extends JFrame {
    static Font globalFont;
    public ExerciseTimerWithTab() {
        setTitle("Clock And Note");
        setUndecorated(true); // Ẩn titlebar hệ thống, để dùng titlebar tự custom
        setSize(540, 400); // Tăng chiều cao để chứa taskbar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImage(new ImageIcon(getClass().getResource("/Icon/clock_di.png")).getImage());
        JPanel mainContainer = new JPanel(new BorderLayout());

        // Title bar Win98 giả lập
        mainContainer.add(new Win98TitleBar("Multimeter"), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(globalFont);
        tabbedPane.addTab("Tab 1", new ExerciseTimerLegacyPanel());
        tabbedPane.addTab("Tab 2", new DigitalWin98Panel());
        mainContainer.add(tabbedPane, BorderLayout.CENTER);

        // Thêm thanh taskbar giả Windows 98
        Windows98Taskbar taskbar = new Windows98Taskbar();
        mainContainer.add(taskbar, BorderLayout.SOUTH);

        setContentPane(mainContainer);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        } catch (Exception e) {}

        // === Áp dụng font custom cho toàn bộ UI ===
        globalFont = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 18f);
        UIManager.put("Label.font", globalFont);
        UIManager.put("Button.font", globalFont);
        UIManager.put("TabbedPane.font", globalFont);
        UIManager.put("TextField.font", globalFont);
        UIManager.put("Panel.font", globalFont);
        UIManager.put("ToolTip.font", globalFont);

        SwingUtilities.invokeLater(() -> new ExerciseTimerWithTab().setVisible(true));
    }
}

// =============== WINDOWS 98 TITLEBAR ===============
class Win98TitleBar extends JPanel {
    private int pX, pY;
    public Win98TitleBar(String title) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 30));
        setBackground(new Color(0, 0, 128)); // Xanh Win98 classic

        // Viền nổi kiểu Win98
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2,2,0,2, Color.WHITE),
            BorderFactory.createMatteBorder(0,0,2,0, new Color(128,128,128))
        ));

        // Icon nhỏ ở bên trái titlebar
        JLabel iconLabel = new JLabel();
        // Đọc icon, resize cho vừa thanh
        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/Icon/clock_di.png"));
        Image img = rawIcon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH); // <-- Chỉnh size nhỏ tại đây
        iconLabel.setIcon(new ImageIcon(img));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        add(iconLabel, BorderLayout.WEST);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 18f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        add(titleLabel, BorderLayout.CENTER);

        // Panel chứa nút ? và X
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 2));
        rightPanel.setOpaque(false);
        Win98IconButton helpBtn = new Win98IconButton("?");
        Win98IconButton closeBtn = new Win98IconButton("X");
        closeBtn.setForeground(Color.BLACK);
        helpBtn.setToolTipText("Help");
        closeBtn.setToolTipText("Close");
        closeBtn.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
                System.exit(0); // Đảm bảo tắt hẳn ứng dụng
            }
        });
        rightPanel.add(helpBtn);
        rightPanel.add(closeBtn);
        add(rightPanel, BorderLayout.EAST);

        // Kéo thả cửa sổ
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                pX = e.getX();
                pY = e.getY();
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(Win98TitleBar.this);
                if (frame != null)
                    frame.setLocation(frame.getX() + evt.getX() - pX, frame.getY() + evt.getY() - pY);
            }
        });
    }
}


class Win98IconButton extends JButton {
    public Win98IconButton(String txt) {
        super(txt);
        setFont(ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 16f));
        setPreferredSize(new Dimension(28, 24));
        setMargin(new Insets(0, 0, 0, 0));
        setBackground(new Color(224, 224, 224));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED),
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(192,192,192))
        ));
        setFocusPainted(false);
        // Hiệu ứng nhấn Win98
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            }
        });
    }
}

// =============== WINDOWS 98 TASKBAR ===============
class Windows98Taskbar extends JPanel {
    private JLabel timeLabel;
    private Timer clockTimer;
    public Windows98Taskbar() {
        setLayout(new BorderLayout());
        setBackground(new Color(192, 192, 192));
        setBorder(BorderFactory.createRaisedBevelBorder());
        setPreferredSize(new Dimension(0, 32));
        // Start button
        JButton startButton = new JButton("Start");
        startButton.setFont(ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 11f));
        startButton.setBorder(BorderFactory.createRaisedBevelBorder());
        startButton.setBackground(new Color(192, 192, 192));
        startButton.setPreferredSize(new Dimension(60, 28));
        startButton.setFocusPainted(false);
        // Add mouse press effect for Start button
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                startButton.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                startButton.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        add(startButton, BorderLayout.WEST);
        // Center panel for running programs (fake taskbar buttons)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        centerPanel.setBackground(new Color(192, 192, 192));
        // Fake running programs
        JButton programButton1 = createTaskbarButton("Exercise Timer");
        programButton1.setBackground(new Color(160, 160, 160)); // Active program
        centerPanel.add(programButton1);
        JButton programButton2 = createTaskbarButton("Notepad");
        centerPanel.add(programButton2);
        JButton programButton3 = createTaskbarButton("Calculator");
        centerPanel.add(programButton3);
        add(centerPanel, BorderLayout.CENTER);
        // System tray area
        JPanel systemTray = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        systemTray.setBackground(new Color(192, 192, 192));
        systemTray.setBorder(BorderFactory.createLoweredBevelBorder());
        // Clock
        timeLabel = new JLabel();
        timeLabel.setFont(ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 25f));
        updateTime();
        systemTray.add(timeLabel);
        add(systemTray, BorderLayout.EAST);
        // Start clock timer
        clockTimer = new Timer(1000, e -> updateTime());
        clockTimer.start();
    }
    private JButton createTaskbarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(ExerciseTimerWithTab.globalFont.deriveFont(Font.PLAIN, 17f));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setBackground(new Color(192, 192, 192));
        button.setPreferredSize(new Dimension(90, 24));
        button.setFocusPainted(false);
        // Add mouse press effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createLoweredBevelBorder());
            }
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        return button;
    }
    private void updateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        timeLabel.setText(sdf.format(new Date()));
    }
}
// --------------- TAB 1: DIGITAL (LEGACY, CÓ FONT & ENGLISH) -----------------
class ExerciseTimerLegacyPanel extends JPanel {
    private JLabel timerLabel, endTimeLabel;
    private JButton startButton, stopButton, resetButton;
    private JTextField hourInput, minuteInput, secondInput;
    private Timer countdownTimer;
    private int totalSeconds = 0;
    private boolean isCounting = false;
    private Font customFontLabel, customFontTimer, customFontButton, customFontTitle;
    public ExerciseTimerLegacyPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(192,192,192));
        try {
            customFontLabel = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 18f);
            customFontTimer = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 76f);
            customFontButton = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 20f);
            customFontTitle = FontLoader.loadCustomFont("BlockCraftMedium-PVLzd.otf", 28f);
        } catch (Throwable t) {
            customFontLabel = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 18f);
            customFontTimer = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 76f);
            customFontButton = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 20f);
            customFontTitle = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 28f);
        }
        // TOP: Tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(192,192,192));
        JLabel titleLabel = new JLabel("Digital Countdown Timer");
        titleLabel.setFont(customFontTitle);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        // CENTER: SET TIME + đồng hồ số lớn + thời gian kết thúc
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(224,224,224));
        centerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        // ==== Khung nhập thời gian ====
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        inputPanel.setBackground(new Color(224,224,224));
        JLabel setLabel = new JLabel("SET TIME:");
        setLabel.setFont(customFontLabel);
        inputPanel.add(setLabel);
        hourInput = createInputField();
        minuteInput = createInputField();
        secondInput = createInputField();
        inputPanel.add(hourInput);
        inputPanel.add(createColonLabel());
        inputPanel.add(minuteInput);
        inputPanel.add(createColonLabel());
        inputPanel.add(secondInput);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(inputPanel);
        // ==== Đồng hồ số lớn ====
        timerLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timerLabel.setFont(customFontTimer);
        timerLabel.setForeground(Color.BLACK);
        timerLabel.setOpaque(true);
        timerLabel.setBackground(new Color(224,224,224));
        timerLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setPreferredSize(new Dimension(380, 110));
        centerPanel.add(timerLabel);
        // ==== Label dự tính giờ kết thúc ====
        endTimeLabel = new JLabel("Ends at: --:--:--", SwingConstants.CENTER);
        endTimeLabel.setFont(customFontButton.deriveFont(Font.PLAIN, 18f));
        endTimeLabel.setOpaque(true);
        endTimeLabel.setBackground(new Color(224,224,224));
        endTimeLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        endTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endTimeLabel.setPreferredSize(new Dimension(260, 32));
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(endTimeLabel);
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
        // ==== SỰ KIỆN ====
        // Khi nhập input: cập nhật đồng hồ lớn, KHÔNG cập nhật khung kết thúc (chỉ reset về mặc định)
        java.awt.event.KeyAdapter updateTime = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                totalSeconds = parseInput();
                timerLabel.setText(formatTime(totalSeconds));
                endTimeLabel.setText("Ends at: --:--:--");
            }
        };
        hourInput.addKeyListener(updateTime);
        minuteInput.addKeyListener(updateTime);
        secondInput.addKeyListener(updateTime);
        // Khi bấm Start
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
            updateEndTimeLabel(totalSeconds); // Chỉ cập nhật ở đây
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
                        endTimeLabel.setText("Ends at: --:--:--");
                        try { AudioPlayer.playBeep(); } catch (Throwable t) {}
                    }
                }
            });
            timerLabel.setText(formatTime(totalSeconds));
            countdownTimer.start();
        });
        // Khi bấm Stop
        stopButton.addActionListener(e -> {
            if (countdownTimer != null && isCounting) {
                countdownTimer.stop();
                setInputsEnabled(true);
                startButton.setEnabled(true);
                stopButton.setEnabled(false);
                resetButton.setEnabled(true);
                isCounting = false;
                endTimeLabel.setText("Ends at: --:--:--");
            }
        });
        // Khi bấm Reset
        resetButton.addActionListener(e -> {
            if (countdownTimer != null) countdownTimer.stop();
            hourInput.setText("00");
            minuteInput.setText("00");
            secondInput.setText("00");
            timerLabel.setText("00:00:00");
            endTimeLabel.setText("Ends at: --:--:--");
            setInputsEnabled(true);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            resetButton.setEnabled(false);
            isCounting = false;
        });
    }
    // Tạo ô nhập kiểu Win98
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
    // Tạo dấu ":"
    private JLabel createColonLabel() {
        JLabel label = new JLabel(":");
        label.setFont(customFontLabel);
        label.setPreferredSize(new Dimension(10, 34));
        return label;
    }
    // Parse input thành tổng giây
    private int parseInput() {
        int h = parseNumber(hourInput.getText());
        int m = parseNumber(minuteInput.getText());
        int s = parseNumber(secondInput.getText());
        return h * 3600 + m * 60 + s;
    }
    private int parseNumber(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
    // Định dạng HH:MM:SS
    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }
    // Enable/disable các ô nhập
    private void setInputsEnabled(boolean enabled) {
        hourInput.setEnabled(enabled);
        minuteInput.setEnabled(enabled);
        secondInput.setEnabled(enabled);
    }
    // Chỉ gọi khi bấm Start (tổng giây đã được truyền vào)
    private void updateEndTimeLabel(int totalSeconds) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.SECOND, totalSeconds);
        int hh = cal.get(java.util.Calendar.HOUR_OF_DAY);
        int mm = cal.get(java.util.Calendar.MINUTE);
        int ss = cal.get(java.util.Calendar.SECOND);
        endTimeLabel.setText(String.format("Ends at: %02d:%02d:%02d", hh, mm, ss));
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
            customFontLabel = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 20f);
            customFontTimer = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 64f);
            customFontButton = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 18f);
            customFontTitle = ExerciseTimerWithTab.globalFont.deriveFont(Font.BOLD, 22f);
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
