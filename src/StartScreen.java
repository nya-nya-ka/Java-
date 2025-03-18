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
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class StartScreen {
    private JFrame frame;
    private Image backgroundImage;
    private AudioPlayer bgmPlayer;
    private boolean isBgmOn = true;
    private boolean isSfxOn = true;

    public StartScreen() {
        try {
            backgroundImage = ImageIO.read(new File("PC.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bgmPlayer = new AudioPlayer();
    }

    public void createAndShowGUI() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Start Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 800);

            BackgroundPanel startPanel = new BackgroundPanel();
            startPanel.setLayout(null);

            JButton startButton = new JButton("　　　");
            startButton.setFont(new Font("Monospaced", Font.BOLD, 24));
            startButton.setBackground(Color.BLACK);
            startButton.setForeground(new Color(135, 206, 250)); // ライトブルー
            startButton.setBounds(295, 550, 200, 100); // ボタンの位置を下に変更
            startButton.setContentAreaFilled(false);
            startButton.setBorderPainted(false);
            startButton.setFocusPainted(false);
            startButton.setOpaque(false); // 背景を透明にする
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (isSfxOn) {
                        AudioPlayer sfxPlayer = new AudioPlayer();
                        sfxPlayer.play("決定.wav");
                    }
                    bgmPlayer.stop();
                    frame.dispose(); // スタート画面を閉じる
                    new InstructionScreen(isBgmOn, isSfxOn).createAndShowGUI(); // 説明画面を表示
                }
            });

            startPanel.add(startButton);

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
                        bgmPlayer.play("1.wav");
                    } else {
                        bgmPlayer.stop();
                    }
                }
            });
            startPanel.add(bgmCheckBox);

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
            startPanel.add(sfxCheckBox);

            frame.add(startPanel);
            frame.setVisible(true);

            if (isBgmOn) {
                bgmPlayer.play("1.wav");
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
