package com.hostel.gui;

import java.awt.*;

public class UITheme {
    // ---- Colors ----
    public static final Color PRIMARY = new Color(33, 97, 140);
    public static final Color PRIMARY_DARK = new Color(24, 72, 105);
    public static final Color SIDEBAR_BG = PRIMARY;
    public static final Color SIDEBAR_BTN = new Color(40, 110, 160);
    public static final Color SIDEBAR_BTN_HOVER = new Color(50, 130, 180);

    public static final Color SUCCESS = new Color(46, 204, 113);
    public static final Color DANGER = new Color(180, 50, 50);
    public static final Color WARNING = new Color(255, 165, 0);
    public static final Color INFO = new Color(70, 130, 180);
    public static final Color GRAY = new Color(120, 130, 140);

    public static final Color WHITE = Color.WHITE;
    public static final Color BACKGROUND = Color.WHITE;
    public static final Color CARD_BG = new Color(245, 248, 250);
    public static final Color INFO_BG = new Color(245, 248, 250);
    public static final Color BORDER = new Color(200, 210, 220);

    // ---- Fonts ----
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 18);
    public static final Font HEADER = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BUTTON = new Font("SansSerif", Font.BOLD, 12);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 11);

    // ---- Sizes ----
    public static final int BUTTON_PAD_V = 8;
    public static final int BUTTON_PAD_H = 15;
    public static final int WINDOW_W = 900;
    public static final int WINDOW_H = 600;
    public static final int SIDEBAR_W = 200;
    public static final int INPUT_H = 32;

    /** Standard modern flat button. */
    public static javax.swing.JButton createButton(String text, Color bg) {
        javax.swing.JButton btn = new javax.swing.JButton(text);
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFont(BUTTON);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(BUTTON_PAD_V, BUTTON_PAD_H, BUTTON_PAD_V, BUTTON_PAD_H));
        return btn;
    }
}