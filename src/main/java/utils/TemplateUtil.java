package utils;

import com.intellij.ui.TitledSeparator;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;

public class TemplateUtil {

    public static JPanel createKeyValuePanel(String[] keys, String[] values, int cols) {
        JPanel finalPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 0;

        int firstColumnRows = (int) Math.ceil((double) keys.length / cols);
        int remainingColumnRows = keys.length % cols == 0 ? keys.length / cols : (keys.length / cols) + 1;

        boolean isFirstColumnOdd = firstColumnRows % 2 != 0;

        for (int i = 0; i < cols; i++) {
            JPanel columnPanel = new JPanel(new GridBagLayout());
            GridBagConstraints columnGbc = new GridBagConstraints();
            columnGbc.fill = GridBagConstraints.HORIZONTAL;
            columnGbc.gridy = GridBagConstraints.RELATIVE;
            columnGbc.anchor = GridBagConstraints.NORTHWEST;
            columnGbc.insets = JBUI.insets(5, 5, 5, 15);
            columnGbc.weightx = 0;

            int columnRows = (i == 0 && cols % 2 != 0) ? firstColumnRows : remainingColumnRows;

            for (int j = 0; j < columnRows; j++) {
                int index = i * firstColumnRows + j - (i > 0 ? firstColumnRows - remainingColumnRows : 0);

                if (index < keys.length) {
                    columnPanel.add(new JLabel("<html><strong>" + keys[index] + ":</strong></html>"), columnGbc);

                    columnGbc.gridx = 1;
                    columnGbc.weightx = 1;
                    columnGbc.fill = GridBagConstraints.HORIZONTAL;

                    columnPanel.add(new JLabel(values[index]), columnGbc);

                    columnGbc.gridx = 0;
                    columnGbc.weightx = 0;
                    columnGbc.fill = GridBagConstraints.HORIZONTAL;
                } else if (isFirstColumnOdd && i > 0) {
                    columnPanel.add(new JLabel("<html><strong>&nbsp;</strong></html>"), columnGbc);

                    columnGbc.gridx = 1;
                    columnGbc.weightx = 1;
                    columnGbc.fill = GridBagConstraints.HORIZONTAL;

                    columnPanel.add(new JLabel(""), columnGbc);

                    columnGbc.gridx = 0;
                    columnGbc.weightx = 0;
                    columnGbc.fill = GridBagConstraints.HORIZONTAL;
                }
            }

            gbc.weightx = 1.0 / cols;
            finalPanel.add(columnPanel, gbc);
        }

        finalPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, finalPanel.getPreferredSize().height));
        return finalPanel;
    }

    public static TitledSeparator getSeparator(String title) {
        TitledSeparator titledSeparator = new TitledSeparator();
        titledSeparator.setText(title);
        titledSeparator.setBorder(JBUI.Borders.empty(20, 0));
        titledSeparator.setMaximumSize(new Dimension(Integer.MAX_VALUE, titledSeparator.getPreferredSize().height));
        return titledSeparator;
    }

    public static String fmtByte(long bytes) {
        double sizeInMB = bytes / (1024.0 * 1024.0);
        if (sizeInMB >= 1024) {
            double sizeInGB = sizeInMB / 1024.0;
            return String.format("%.2f Gb", sizeInGB);
        } else {
            return String.format("%.2f Mb", sizeInMB);
        }
    }

    public static String fmtDouble(Double value) {

        if (value == null) {
            return null;
        } else {
            return String.format("%.3f", value);
        }
    }

    public static String formatDuration(Long seconds) {
        if (seconds == null) {
            return null;
        }

        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        minutes = minutes % 60;
        hours = hours % 24;

        return String.format("%d days %d hours %d minutes", days, hours, minutes);
    }

}
