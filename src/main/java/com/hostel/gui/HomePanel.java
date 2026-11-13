package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.StudentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private StudentDAO studentDAO;
    private RoomDAO roomDAO;
    private AttendanceDAO attendanceDAO;
    private String hostelType;
    private Integer hostelId;

    private JLabel lblTotalStudents, lblTotalRooms, lblOccupiedRooms, lblTodayMarked;

    public HomePanel(String hostelType, Integer hostelId) {
        this.hostelType = hostelType;
        this.hostelId = hostelId;
        this.studentDAO = new StudentDAO();
        this.roomDAO = new RoomDAO();
        this.attendanceDAO = new AttendanceDAO();

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Home Dashboard");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // ----- Main Content (Stats Only) -----
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 30, 30));
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        lblTotalStudents = new JLabel("0", SwingConstants.CENTER);
        lblTotalRooms = new JLabel("0", SwingConstants.CENTER);
        lblOccupiedRooms = new JLabel("0", SwingConstants.CENTER);
        lblTodayMarked = new JLabel("0", SwingConstants.CENTER);

        mainPanel.add(createStatCard("Total Students", lblTotalStudents, UITheme.PRIMARY));
        mainPanel.add(createStatCard("Total Rooms", lblTotalRooms, UITheme.INFO));
        mainPanel.add(createStatCard("Occupied Rooms", lblOccupiedRooms, UITheme.WARNING));
        mainPanel.add(createStatCard("Marked Present Today", lblTodayMarked, UITheme.SUCCESS));

        add(mainPanel, BorderLayout.CENTER);
        refreshData();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, java.awt.Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(30, 30, 30, 30)));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 18));
        titleLabel.setForeground(Color.GRAY);

        valueLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 48));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        lblTotalStudents.setText(String.valueOf(studentDAO.getTotalStudentCount(hostelType)));
        lblTotalRooms.setText(String.valueOf(roomDAO.getTotalRoomCount(hostelId)));
        lblOccupiedRooms.setText(String.valueOf(roomDAO.getOccupiedRoomCount(hostelId)));
        lblTodayMarked.setText(String.valueOf(attendanceDAO.getTodayMarkedCount(hostelType)));
    }
}