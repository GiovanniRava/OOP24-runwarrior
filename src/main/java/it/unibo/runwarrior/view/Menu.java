package it.unibo.runwarrior.view;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.swing.JLabel;
import javax.swing.ImageIcon;

import it.unibo.runwarrior.controller.GameLoopController;
import it.unibo.runwarrior.model.Score;
import it.unibo.runwarrior.model.save.GameSaveManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the main men of the game, with title, play and level buttons and a shop.
 * It initialize the graphical user interface and handles button actions to
 * navigate to different parts of the game.
 */
public class Menu extends JPanel {
    /**
     * Logger used for exceptions and error messages.
     */
    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());
    private static final long serialVersionUID = 1L;
    private static final int PLAY_BUTTON_PANEL_WIDTH = 250;
    private static final int PLAY_BUTTON_PANEL_HEIGHT = 500;
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 40;
    private static final int SHOP_FRAME_WIDHT = 600;
    private static final int SHOP_FRAME_HEIGHT = 400; 
    private static final Color PLAY_BUTTON_BG_COLOR = new Color(120, 124, 126);
    private static final Color PLAY_BUTTON_BORDER_COLOR = new Color(85, 89, 91);
    private static final Color LEVEL1_BG_COLOR = new Color(218, 165, 32);
    private static final Color LEVEL1_BORDER_COLOR = new Color(180, 130, 25);
    private static final Color LEVEL2_BG_COLOR = new Color(60, 179, 60);
    private static final Color LEVEL2_BORDER_COLOR = new Color(40, 120, 40);
    private static final Color LEVEL3_BG_COLOR = new Color(120, 124, 126);
    private static final Color LEVEL3_BORDER_COLOR = new Color(85, 89, 91);
    private static final Color SHOP_BUTTON_BG_COLOR = new Color(70, 130, 180);
    private static final Color SHOP_BUTTON_BORDER_COLOR = new Color(30, 90, 150);
    private static final int LINEBORDER_THICKNESS = 4;
    private static final int VERTICAL_STRUT_MEDIUM = 20;
    private static final int FONT_DIMENSION = 16;

    private final JFrame frameMenu;
    private transient BufferedImage backGroundImage;
    private transient BufferedImage titleImage;
    private JPanel backgroundMenuPanel;

    /**
     * Menu Constructor.
     *
     * @param externalFrame frame external on wich the game is based
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public Menu(final JFrame externalFrame) {
        this.frameMenu = externalFrame;
        initMenu();
    }

    /**
     * Method that implement the graphic of the menu and manages the buttons.
     */
    public final void initMenu() {
        frameMenu.setTitle("RUN WARRIOR");
        frameMenu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frameMenu.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                final int n = JOptionPane.showConfirmDialog(frameMenu, "Do you want to save your game data?", 
                    "Quitting...", JOptionPane.YES_NO_OPTION); 
                if (n == JOptionPane.YES_OPTION) {
                    GameSaveManager.getInstance().saveGame();
                    System.exit(0);
                } else if (n == JOptionPane.NO_OPTION) {
                    GameSaveManager.getInstance().resetGame();
                    System.exit(0);
                }
            }
        });

        try {
            backGroundImage = ImageIO.read(Menu.class.getResourceAsStream("/Menu/sfondoMenu.png"));
            titleImage = ImageIO.read(Menu.class.getResourceAsStream("/Menu/titolo4.png"));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot load images");
        }

        backgroundMenuPanel = new JPanel() {
            @Override
            protected void paintComponent(final Graphics g) {
                super.paintComponent(g);
                if (backGroundImage != null) {
                    g.drawImage(backGroundImage, 0, 0, getWidth(), getHeight(), this); 
                }
            }
        };
        backgroundMenuPanel.setLayout(new BoxLayout(backgroundMenuPanel, BoxLayout.Y_AXIS));
        backgroundMenuPanel.setOpaque(true);
        final JLabel titleLabel = new JLabel(new ImageIcon(titleImage));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        backgroundMenuPanel.add(Box.createVerticalGlue());
        backgroundMenuPanel.add(titleLabel);
        final JPanel playButtonPanel = new JPanel();
        playButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        playButtonPanel.setOpaque(false);
        playButtonPanel.setAlignmentX(CENTER_ALIGNMENT);
        backgroundMenuPanel.add(Box.createVerticalStrut(VERTICAL_STRUT_MEDIUM));
        backgroundMenuPanel.add(playButtonPanel);
        backgroundMenuPanel.add(Box.createHorizontalGlue());
        playButtonPanel.setPreferredSize(new Dimension(PLAY_BUTTON_PANEL_WIDTH, PLAY_BUTTON_PANEL_HEIGHT));

        final JButton playButton = new JButton("PLAY");
        final Dimension buttonsDimension = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        playButton.setMaximumSize(buttonsDimension);
        playButton.setPreferredSize(buttonsDimension);
        playButton.setMinimumSize(buttonsDimension);
        playButton.setBackground(PLAY_BUTTON_BG_COLOR);
        final Font font = new Font("Cooper Black", Font.BOLD, FONT_DIMENSION);
        playButton.setFont(font);
        playButton.setForeground(Color.BLACK);
        playButton.setBorder(new LineBorder(PLAY_BUTTON_BORDER_COLOR, LINEBORDER_THICKNESS));

        playButton.addActionListener(new ActionListener() {
            private GameLoopController glc;
            @Override
            public void actionPerformed(final ActionEvent e) {
                final GameSaveManager save = GameSaveManager.getInstance();
                playButtonPanel.remove(playButton);
                playButtonPanel.revalidate();
                playButtonPanel.repaint();

                final JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                final JButton level1 = new JButton("LEVEL 1");
                final JButton level2 = new JButton("LEVEL 2");
                final JButton level3 = new JButton("LEVEL 3");
                level2.setEnabled(save.getLevelsCompleted() >= 1);
                level3.setEnabled(save.getLevelsCompleted() >= 2);
                level1.setAlignmentX(CENTER_ALIGNMENT);
                level1.setMaximumSize(buttonsDimension);
                level1.setPreferredSize(buttonsDimension);
                level1.setFont(font);
                level1.setBackground(LEVEL1_BG_COLOR);
                level1.setBorder(new LineBorder(LEVEL1_BORDER_COLOR, LINEBORDER_THICKNESS)); 
                level1.setForeground(Color.BLACK);
                level2.setAlignmentX(CENTER_ALIGNMENT);
                level2.setMaximumSize(buttonsDimension);
                level2.setPreferredSize(buttonsDimension);
                level2.setFont(font);
                level2.setBackground(LEVEL2_BG_COLOR);
                level2.setBorder(new LineBorder(LEVEL2_BORDER_COLOR, LINEBORDER_THICKNESS));
                level2.setForeground(Color.BLACK);
                level3.setAlignmentX(CENTER_ALIGNMENT);
                level3.setMaximumSize(buttonsDimension);
                level3.setPreferredSize(buttonsDimension);
                level3.setFont(font);
                level3.setBackground(LEVEL3_BG_COLOR);
                level3.setBorder(new LineBorder(LEVEL3_BORDER_COLOR, LINEBORDER_THICKNESS));
                level3.setForeground(Color.BLACK);

                level1.addActionListener(level1Event -> {
                    glc = new GameLoopController(frameMenu, "Map1/map_1.txt", "Map1/desert_theme.txt",
                    "/Map1/enemiesMap1.txt", "/Coins/CoinCoordinates_map1.txt", true);
                    glc.getGlp().startGame();
                    frameMenu.getContentPane().removeAll();
                    frameMenu.setContentPane(glc.getGlp());
                    frameMenu.revalidate();
                    frameMenu.repaint();
                    glc.getGlp().setFocusable(true);
                    glc.getGlp().requestFocusInWindow();
                    glc.getGlp().requestFocus();
                });
                level2.addActionListener(level2Event -> {
                        glc = new GameLoopController(frameMenu, "Map2/map2.txt", "Map2/forest_theme.txt",
                        "/Map2/enemiesMap2.txt", "/Coins/CoinCoordinates_map2.txt", true);
                        glc.getGlp().startGame();
                        frameMenu.getContentPane().removeAll();
                        frameMenu.setContentPane(glc.getGlp());
                        frameMenu.revalidate();
                        frameMenu.repaint();
                        glc.getGlp().setFocusable(true);
                        glc.getGlp().requestFocusInWindow();
                        glc.getGlp().requestFocus();
                });
                level3.addActionListener(level3Event -> {
                    glc = new GameLoopController(frameMenu, "Map_3/map_3.txt", "Map_3/map3Theme.txt",
                    "/Map_3/enemiesMap3.txt", "/Coins/CoinCoordinates_map3.txt", true);
                    glc.getGlp().startGame();
                    frameMenu.getContentPane().removeAll();
                    frameMenu.setContentPane(glc.getGlp());
                    frameMenu.revalidate();
                    frameMenu.repaint();
                    glc.getGlp().setFocusable(true);
                    glc.getGlp().requestFocusInWindow();
                    glc.getGlp().requestFocus();
                });
                final JButton shopButton = new JButton("SHOP");
                shopButton.setAlignmentX(CENTER_ALIGNMENT);
                shopButton.setMaximumSize(new Dimension(buttonsDimension));
                shopButton.setPreferredSize(buttonsDimension);
                shopButton.setFont(new Font("Cooper Black", Font.BOLD, FONT_DIMENSION));
                shopButton.setBackground(SHOP_BUTTON_BG_COLOR);
                shopButton.setForeground(Color.WHITE);
                shopButton.setBorder(new LineBorder(SHOP_BUTTON_BORDER_COLOR, LINEBORDER_THICKNESS));

                shopButton.addActionListener(shopEvent -> {
                    final JFrame shopFrame = new JFrame("SHOP");
                    shopFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    shopFrame.setSize(SHOP_FRAME_WIDHT, SHOP_FRAME_HEIGHT);
                    shopFrame.setLocationRelativeTo(null);
                    shopFrame.setResizable(false);
                    final ShopView shopView = new ShopView(new Score());
                    shopFrame.add(shopView);
                    shopFrame.setVisible(true);
                });
                panel.setOpaque(false);
                panel.add(Box.createVerticalStrut(10));
                panel.add(level1);
                panel.add(Box.createVerticalStrut(10));
                panel.add(level2);
                panel.add(Box.createVerticalStrut(10));
                panel.add(level3);
                panel.add(Box.createVerticalStrut(10));
                panel.add(shopButton);
                playButtonPanel.setPreferredSize(new Dimension(PLAY_BUTTON_PANEL_WIDTH, PLAY_BUTTON_PANEL_HEIGHT));
                playButtonPanel.removeAll();
                playButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
                playButtonPanel.add(panel);
                playButtonPanel.revalidate();
                playButtonPanel.repaint();

            }
        });
        playButtonPanel.add(playButton, BorderLayout.CENTER); 
        backgroundMenuPanel.add(playButtonPanel);
        super.setLayout(new BorderLayout());
        super.add(backgroundMenuPanel, BorderLayout.CENTER);
    }

    /**
     * @return the panel of background
     */
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public final JPanel getPanel() {
        return backgroundMenuPanel;
    }
}
