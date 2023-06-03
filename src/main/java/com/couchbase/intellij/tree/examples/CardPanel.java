package com.couchbase.intellij.tree.examples;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBEmptyBorder;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CardPanel extends JBPanel {
    private boolean isSelected = false;
    private final JBLabel titleLabel;
    private final JBLabel descriptionLabel;
    private final Color defaultTextColor;

    private final Card card;

    public CardPanel(CardDialog parentDialog, Card card) {
        this.card = card;
        setPreferredSize(new Dimension(280, 280));

        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new LineBorder(JBColor.LIGHT_GRAY, 1), new JBEmptyBorder(10)));
        setBackground(JBColor.LIGHT_GRAY);

        JBLabel imageLabel = new JBLabel(card.getImage());
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);

        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(280, 140));
        imagePanel.setLayout(new GridBagLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.add(imageLabel);

        titleLabel = new JBLabel(card.getTitle());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setBorder(JBUI.Borders.empty(15, 0));

        descriptionLabel = new JBLabel("<html>" + card.getDescription() + "</html>");
        descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(descriptionLabel);
        textPanel.setOpaque(false);

        defaultTextColor = titleLabel.getForeground();

        add(imagePanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    setBackground(JBColor.GRAY);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    setBackground(JBColor.LIGHT_GRAY);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                parentDialog.selectCard(CardPanel.this);
            }
        });
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
        setBackground(isSelected ? Color.WHITE : Color.darkGray);
        titleLabel.setForeground(isSelected ? Color.black : defaultTextColor);
        descriptionLabel.setForeground(isSelected ? Color.black : defaultTextColor);
        revalidate();
    }

    public boolean isSelected() {
        return isSelected;
    }

    public Card getCard() {
        return card;
    }
}