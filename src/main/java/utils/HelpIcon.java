package utils;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HelpIcon {

    public static JBLabel createHelpIcon(String tooltipText) {
        return createHelpIcon(tooltipText, 0);
    }

    public static JBLabel createHelpIcon(String tooltipText, int leftMargin) {
        JBLabel helpIcon = new JBLabel();
        if (leftMargin > 0) {
            helpIcon.setBorder(JBUI.Borders.emptyLeft(leftMargin));
        }
        helpIcon.setIcon(AllIcons.General.ContextHelp);
        helpIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                JComponent component = (JComponent) e.getComponent();
                Balloon balloon = JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(
                        tooltipText,
                        null,
                        JBColor.background(),
                        null).createBalloon();
                balloon.showInCenterOf(component);
            }
        });
        return helpIcon;
    }

}