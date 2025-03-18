import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TypingGame {
    private JFrame frame;
    private JTextPane wordPane;
    private JLabel scoreLabel;
    private JLabel charCountLabel;
    private JLabel timerLabel;
    private JProgressBar timerProgressBar;
    private JButton backButton;
    private Timer timer;
    private int score;
    private int charCount;
    private String currentWord;
    private WordGenerator wordGenerator;
    private int timeLeft;
    private final int GAME_DURATION = 60; // ゲームの制限時間（秒）
    private final int WINNING_SCORE = 5; // 勝利のためのスコア
    private int inputIndex = 0;
    private boolean isError = false;
    private String currentIndentation = "";
    private long startTime; // ゲーム開始時間を記録する変数
    private Image backgroundImage;
    private AudioPlayer bgmPlayer;
    private AudioPlayer keyPressAudioPlayer;
    private boolean isBgmOn;
    private boolean isSfxOn;

    public TypingGame(String difficulty, boolean isBgmOn, boolean isSfxOn) {
        wordGenerator = new WordGenerator(difficulty);
        score = 0;
        charCount = 0;
        timeLeft = GAME_DURATION;
        try {
            backgroundImage = ImageIO.read(new File("PC9.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isBgmOn = isBgmOn;
        this.isSfxOn = isSfxOn;
        bgmPlayer = new AudioPlayer();
        keyPressAudioPlayer = new AudioPlayer();
    }

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Typing Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            JLayeredPane layeredPane = new JLayeredPane();
            layeredPane.setLayout(null);

            BackgroundPanel backgroundPanel = new BackgroundPanel();
            backgroundPanel.setBounds(0, 0, 800, 550);

            wordPane = new JTextPane();
            wordPane.setFont(new Font("Monospaced", Font.BOLD, 20));
            wordPane.setEditable(false);
            wordPane.setForeground(new Color(255, 255, 240)); // アイボリー
            wordPane.setBorder(new EmptyBorder(10, 80, 10, 10));
            wordPane.setOpaque(false);

            JScrollPane wordScrollPane = new JScrollPane(wordPane);
            wordScrollPane.setBounds(100, 250, 600, 350); // ここで横幅と高さを調整
            wordScrollPane.setOpaque(false);
            wordScrollPane.getViewport().setOpaque(false);
            wordScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

            scoreLabel = new JLabel("スコア: 0", SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            scoreLabel.setForeground(new Color(173, 216, 230)); // ライトブルー
            scoreLabel.setBounds(0, 80, 400, 50); // 位置を変更
            scoreLabel.setOpaque(false);

            charCountLabel = new JLabel("文字数: 0", SwingConstants.CENTER);
            charCountLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            charCountLabel.setForeground(new Color(144, 238, 144)); // ライトグリーン
            charCountLabel.setBounds(400, 80, 400, 50); // 位置を変更
            charCountLabel.setOpaque(false);

            timerLabel = new JLabel("残り時間: " + timeLeft + " 秒", SwingConstants.CENTER);
            timerLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            timerLabel.setOpaque(true);
            timerLabel.setBackground(new Color(0, 0, 0, 0));
            timerLabel.setForeground(new Color(255, 182, 193)); // ライトピンク
            timerLabel.setBounds(0, 0, 800, 50);
            timerLabel.setOpaque(false);

            timerProgressBar = new JProgressBar(0, GAME_DURATION);
            timerProgressBar.setValue(GAME_DURATION);
            timerProgressBar.setForeground(new Color(147, 112, 219)); // 紫色
            timerProgressBar.setBackground(Color.DARK_GRAY);
            timerProgressBar.setBorder(new EmptyBorder(5, 5, 5, 5));
            timerProgressBar.setBounds(50, 50, frame.getWidth() - 100, 20);
            timerProgressBar.setOpaque(false);

            backButton = new JButton("戻る");
            backButton.setFont(new Font("Monospaced", Font.BOLD, 24));
            backButton.setBackground(Color.BLACK);
            backButton.setForeground(new Color(255, 228, 225)); // ミスティローズ
            backButton.setBounds(100, 600, 100, 50); // ボタンをもう少し上に配置
            backButton.setOpaque(false); // 背景を透明にする
            backButton.setContentAreaFilled(false);
            backButton.setBorderPainted(false);
            backButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isSfxOn) {
                        AudioPlayer sfxPlayer = new AudioPlayer();
                        sfxPlayer.play("戻る.wav");
                    }
                    bgmPlayer.stop();
                    frame.dispose(); // タイピングゲーム画面を閉じる
                    new InstructionScreen(isBgmOn, isSfxOn).createAndShowGUI(); // 難易度選択画面を表示
                }
            });

            layeredPane.add(backgroundPanel, Integer.valueOf(0));
            layeredPane.add(wordScrollPane, Integer.valueOf(1));
            layeredPane.add(scoreLabel, Integer.valueOf(1));
            layeredPane.add(charCountLabel, Integer.valueOf(1));
            layeredPane.add(timerLabel, Integer.valueOf(1));
            layeredPane.add(timerProgressBar, Integer.valueOf(1));
            layeredPane.add(backButton, Integer.valueOf(1));

            JCheckBox bgmCheckBox = new JCheckBox("♪BGM ON/OFF", isBgmOn);
            bgmCheckBox.setFont(new Font("Monospaced", Font.BOLD, 18));
            bgmCheckBox.setForeground(Color.WHITE);
            bgmCheckBox.setBackground(new Color(0, 0, 0, 0)); // 背景を透明にする
            bgmCheckBox.setOpaque(false);
            bgmCheckBox.setBorder(new LineBorder(new Color(135, 206, 250), 2, true));
            bgmCheckBox.setBounds(20, 660, 150, 30);
            bgmCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isBgmOn = bgmCheckBox.isSelected();
                    if (isBgmOn && !bgmPlayer.isPlaying()) {
                        bgmPlayer.play("3.wav");
                    } else {
                        bgmPlayer.stop();
                    }
                }
            });
            layeredPane.add(bgmCheckBox, Integer.valueOf(1));

            JCheckBox sfxCheckBox = new JCheckBox("🔉効果音 ON/OFF", isSfxOn);
            sfxCheckBox.setFont(new Font("Monospaced", Font.BOLD, 18));
            sfxCheckBox.setForeground(Color.WHITE);
            sfxCheckBox.setBackground(new Color(0, 0, 0, 0)); // 背景を透明にする
            sfxCheckBox.setOpaque(false);
            sfxCheckBox.setBorder(new LineBorder(new Color(135, 206, 250), 2, true));
            sfxCheckBox.setBounds(180, 660, 200, 30);
            sfxCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isSfxOn = sfxCheckBox.isSelected();
                }
            });
            layeredPane.add(sfxCheckBox, Integer.valueOf(1));

            frame.add(layeredPane);
            frame.setVisible(true);

            frame.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (timeLeft > 0) {
                        checkInput(e);
                        if (isSfxOn) {
                            keyPressAudioPlayer.play("type.wav"); // キーが押されたときに音声を再生
                        }
                    }
                }
            });
            frame.setFocusable(true);
            frame.requestFocusInWindow();
            frame.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent evt) {
                    backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
                    wordScrollPane.setBounds(100, 250, frame.getWidth() - 200, 350); // 位置とサイズを調整
                    timerProgressBar.setBounds(50, 50, frame.getWidth() - 100, 20);
                    scoreLabel.setBounds(0, 80, frame.getWidth() / 2, 50); // 位置を変更
                    charCountLabel.setBounds(frame.getWidth() / 2, 80, frame.getWidth() / 2, 50); // 位置を変更
                    backButton.setBounds(frame.getWidth() - 150, frame.getHeight() - 150, 100, 50); // 位置を調整
                }
            });

            if (isBgmOn) {
                bgmPlayer.play("3.wav");
            }
            startGame();
        });
    }

    private void startGame() {
        score = 0;
        charCount = 0;
        timeLeft = GAME_DURATION;
        startTime = System.currentTimeMillis(); // ゲーム開始時間を記録
        inputIndex = 0;
        isError = false;
        currentIndentation = "";
        nextWord();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLeft--;
                timerLabel.setText("残り時間: " + timeLeft + " 秒");
                timerProgressBar.setValue(timeLeft);
                if (timeLeft <= 0) {
                    endGame();
                }
            }
        }, 1000, 1000);
    }

    private void endGame() {
        timer.cancel();
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000; // 経過時間を秒単位で計算
        double keysPerSecond = (double) charCount / elapsedTime;
        if (score >= WINNING_SCORE) {
            wordPane.setText("You Win!\n\n\nChars: " + charCount + " (" + String.format("%.2f", keysPerSecond) + " key/sec)");
        } else {
            wordPane.setText("Game Over!\n\nScore: " + score + "\nChars: " + charCount + " (" + String.format("%.2f", keysPerSecond) + " key/sec)");
        }
        // 入力を無効化するためにフレームのフォーカスを解除
        frame.setFocusable(false);
    }

    private void checkInput(KeyEvent e) {
        char inputChar = e.getKeyChar();
        if (inputChar == KeyEvent.VK_TAB) {
            inputChar = '\t';  // タブキーが押された場合はタブ文字として扱う
            e.consume(); // タブキーのデフォルト動作を防ぐ
        }

        StyledDocument doc = wordPane.getStyledDocument();
        Style styleCorrect = wordPane.addStyle("Correct", null);
        Style styleIncorrect = wordPane.addStyle("Incorrect", null);
        Style styleDefault = wordPane.addStyle("Default", null);
        Style styleUnderlineCorrect = wordPane.addStyle("UnderlineCorrect", null);
        Style styleUnderlineIncorrect = wordPane.addStyle("UnderlineIncorrect", null);
        StyleConstants.setForeground(styleCorrect, new Color(0, 255, 127)); // ネオングリーン
        StyleConstants.setForeground(styleIncorrect, new Color(255, 69, 0)); // オレンジレッド
        StyleConstants.setForeground(styleDefault, new Color(255, 255, 240)); // アイボリー
        StyleConstants.setUnderline(styleUnderlineCorrect, true);
        StyleConstants.setUnderline(styleUnderlineIncorrect, true);

        if (isError) {
            // Check if the error character is now correct
            if (inputChar == currentWord.charAt(inputIndex)) {
                isError = false;
                inputIndex++;
                charCount++; // 文字数カウントを増やす
            }
        } else {
            // Check if the input character matches the current character in the word
            if (inputIndex < currentWord.length() && inputChar == currentWord.charAt(inputIndex)) {
                if (inputChar == '\n') {
                    currentIndentation = getIndentationAtIndex(inputIndex + 1);  // 次の行のインデントを取得
                    if (!currentIndentation.isEmpty()) {
                        try {
                            doc.insertString(doc.getLength(), currentIndentation, null);  // 次の行の先頭にインデントを追加
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        inputIndex += currentIndentation.length();
                    }
                }
                inputIndex++;
                charCount++; // 文字数カウントを増やす
            } else {
                // If the character is incorrect, mark it as an error
                isError = true;
            }
        }

        wordPane.setText("");
        try {
            for (int i = 0; i < inputIndex; i++) {
                doc.insertString(doc.getLength(), String.valueOf(currentWord.charAt(i)), styleCorrect);
            }
            if (inputIndex < currentWord.length()) {
                if (isError) {
                    doc.insertString(doc.getLength(), String.valueOf(currentWord.charAt(inputIndex)), styleUnderlineIncorrect);
                } else {
                    doc.insertString(doc.getLength(), String.valueOf(currentWord.charAt(inputIndex)), styleUnderlineCorrect);
                }
                if (inputIndex + 1 < currentWord.length()) {
                    doc.insertString(doc.getLength(), currentWord.substring(inputIndex + 1), styleDefault);
                }
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        scoreLabel.setText("スコア: " + score);
        charCountLabel.setText("文字数: " + charCount);

        if (inputIndex == currentWord.length() && !isError) {
            score++;
            scoreLabel.setText("スコア: " + score);
            charCountLabel.setText("文字数: " + charCount);
            inputIndex = 0;
            nextWord();
            if (score >= WINNING_SCORE) {
                endGame();
            }
        }
    }

    private void nextWord() {
        currentWord = wordGenerator.generateWord();
        inputIndex = 0;
        isError = false;
        currentIndentation = "";
        wordPane.setText("");
        StyledDocument doc = wordPane.getStyledDocument();
        Style styleDefault = wordPane.addStyle("Default", null);
        StyleConstants.setForeground(styleDefault, new Color(255, 255, 240)); // アイボリー
        try {
            doc.insertString(0, currentWord, styleDefault);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getIndentationAtIndex(int index) {
        StringBuilder indentation = new StringBuilder();
        while (index < currentWord.length() && (currentWord.charAt(index) == ' ' || currentWord.charAt(index) == '\t')) {
            indentation.append(currentWord.charAt(index));
            index++;
        }
        return indentation.toString();
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
