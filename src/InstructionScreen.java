import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class InstructionScreen {
    private JFrame frame;
    private Image backgroundImage;
    private AudioPlayer bgmPlayer;
    private boolean isBgmOn;
    private boolean isSfxOn;

    public InstructionScreen(boolean isBgmOn, boolean isSfxOn) {
        try {
            backgroundImage = ImageIO.read(new File("PC4.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.isBgmOn = isBgmOn;
        this.isSfxOn = isSfxOn;
        bgmPlayer = new AudioPlayer();
    }

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Instructions and Difficulty Selection");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            BackgroundPanel panel = new BackgroundPanel();
            panel.setLayout(null);

            JLabel instructions = new JLabel("<html><center>制限時間：１分<br>ランダムに出てくるJavaの構文５つを<br>全て打てたらクリア</center></html>", JLabel.CENTER);
            instructions.setFont(new Font("Monospaced", Font.BOLD, 24));
            instructions.setForeground(new Color(173, 216, 230)); // ライトブルー
            instructions.setBounds(100, 100, 600, 200);

            JLabel selectDifficultyLabel = new JLabel("難易度", JLabel.CENTER);
            selectDifficultyLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            selectDifficultyLabel.setForeground(new Color(240, 128, 128)); // ライトコーラル
            selectDifficultyLabel.setBounds(-50, 300, 600, 50);

            String[] difficulties = { "Easy", "Medium", "Hard" };
            JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);
            difficultyComboBox.setFont(new Font("Monospaced", Font.BOLD, 24));
            difficultyComboBox.setBackground(Color.BLACK);
            difficultyComboBox.setForeground(new Color(144, 238, 144)); // ライトグリーン
            difficultyComboBox.setBorder(new LineBorder(new Color(144, 238, 144), 2)); // ライトグリーンのボーダー
            difficultyComboBox.setBounds(150, 350, 200, 50);

            JButton okButton = new JButton("OK");
            okButton.setFont(new Font("Monospaced", Font.BOLD, 24));
            okButton.setBackground(Color.BLACK);
            okButton.setForeground(new Color(70, 130, 180)); // スチールブルー
            okButton.setBounds(315, 450, 200, 100);
            okButton.setOpaque(false); // 背景を透明にする
            okButton.setContentAreaFilled(false);
            okButton.setBorderPainted(false);
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isSfxOn) {
                        AudioPlayer sfxPlayer = new AudioPlayer();
                        sfxPlayer.play("決定.wav");
                    }
                    bgmPlayer.stop();
                    String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();
                    frame.dispose(); // 説明画面を閉じる
                    new TypingGame(selectedDifficulty, isBgmOn, isSfxOn).createAndShowGUI(); // タイピングゲーム画面を表示
                }
            });

            JButton backButton = new JButton("戻る");
            backButton.setFont(new Font("Monospaced", Font.BOLD, 24));
            backButton.setBackground(Color.BLACK);
            backButton.setForeground(new Color(255, 182, 193)); // ライトピンク
            backButton.setBounds(645, 643, 100, 50); // ボタンを下に配置
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
                    frame.dispose(); // 説明画面を閉じる
                    new StartScreen().createAndShowGUI(); // スタート画面を表示
                }
            });

            String[] columnNames = {"ランク", "早さの目安"};
            Object[][] data = {
                {"Easy   100-150 key/分"},
                {"Medium 150-200 key/分"},
                {"Hard   200+    key/分"}
            };

            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);
            table.setFont(new Font("Monospaced", Font.BOLD, 18));
            table.setRowHeight(30);
            table.setBounds(390, 320, 550, 200);
            table.setOpaque(false);
            table.setBackground(new Color(0, 0, 0, 0)); // 背景を透明にする
            table.setForeground(new Color(240, 230, 140)); // カーキ
            table.setShowGrid(false);

            panel.add(instructions);
            panel.add(selectDifficultyLabel);
            panel.add(difficultyComboBox);
            panel.add(okButton);
            panel.add(backButton);
            panel.add(table);

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
                        bgmPlayer.play("2.wav");
                    } else {
                        bgmPlayer.stop();
                    }
                }
            });
            panel.add(bgmCheckBox);

            JCheckBox sfxCheckBox = new JCheckBox("🔉効果音 ON/OFF", isSfxOn);
            sfxCheckBox.setFont(new Font("Monospaced", Font.BOLD, 18));
            sfxCheckBox.setForeground(Color.WHITE);
            sfxCheckBox.setBackground(new Color(0, 0, 0, 0)); // 背景を透明にする
            sfxCheckBox.setOpaque(false);
            sfxCheckBox.setBorder(new LineBorder(new Color(135, 206, 250), 2, true));
            sfxCheckBox.setBounds(200, 660, 2000, 30);
            sfxCheckBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    isSfxOn = sfxCheckBox.isSelected();
                }
            });
            panel.add(sfxCheckBox);

            frame.add(panel);
            frame.setVisible(true);

            if (isBgmOn) {
                bgmPlayer.play("2.wav");
            }
        });
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
