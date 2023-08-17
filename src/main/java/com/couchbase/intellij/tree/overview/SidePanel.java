package com.couchbase.intellij.tree.overview;

import com.intellij.openapi.ui.popup.ListItemDescriptor;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.Gray;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorWithText;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.popup.list.GroupedItemsListRenderer;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.Consumer;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public final class SidePanel extends JPanel {
    private final JList<SidePanelItem> myList;
    private final DefaultListModel<SidePanelItem> myModel;

    private final Int2ObjectMap<@Nls String> myIndex2Separator = new Int2ObjectOpenHashMap<>();

    public SidePanel(Consumer<String> selectedFunction) {

        setLayout(new BorderLayout());

        myModel = new DefaultListModel<>();
        myList = new JBList<>(myModel);
        myList.setBackground(UIUtil.SIDE_PANEL_BACKGROUND);
        myList.setBorder(JBUI.Borders.emptyTop(5));
        final ListItemDescriptor<SidePanelItem> descriptor = new ListItemDescriptor<>() {
            @Override
            public String getTextFor(final SidePanelItem value) {
                return value.myText;
            }

            @Override
            public String getTooltipFor(final SidePanelItem value) {
                return null;
            }

            @Override
            public Icon getIconFor(final SidePanelItem value) {
                return JBUIScale.scaleIcon(EmptyIcon.create(16, 20));
            }

            @Override
            public boolean hasSeparatorAboveOf(final SidePanelItem value) {
                return getSeparatorAbove(value) != null;
            }

            @Override
            public String getCaptionAboveOf(final SidePanelItem value) {
                return getSeparatorAbove(value);
            }
        };


        myList.setCellRenderer(new GroupedItemsListRenderer<>(descriptor) {
            final CellRendererPane myValidationParent = new CellRendererPane();
            JPanel myExtraPanel;
            SidePanelCountLabel myCountLabel;
            JLabel myItemLabel;

            {
                mySeparatorComponent.setCaptionCentered(false);
                myList.add(myValidationParent);
            }

            @Override
            protected SeparatorWithText createSeparator() {
                return new SidePanelSeparator();
            }

            public JComponent getItemComponent() {
                return myComponent;
            }

            @Override
            protected void layout() {
                myRendererComponent.add(mySeparatorComponent, BorderLayout.NORTH);
                myExtraPanel.add(getItemComponent(), BorderLayout.CENTER);
                myExtraPanel.add(myCountLabel, BorderLayout.EAST);
                myRendererComponent.add(myExtraPanel, BorderLayout.CENTER);
            }

            @Override
            public Component getListCellRendererComponent(JList<? extends SidePanelItem> list, SidePanelItem value, int index, boolean isSelected, boolean cellHasFocus) {

                if (myItemLabel == null) {
                    myItemLabel = new JLabel(value.myText);
                }
                layout();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }

            @Override
            protected JComponent createItemComponent() {
                myExtraPanel = new NonOpaquePanel(new BorderLayout());

                myCountLabel = new SidePanelCountLabel();
                final JComponent component = super.createItemComponent();

                myTextLabel.setForeground(Gray._240);
                myTextLabel.setOpaque(true);

                return component;
            }

            @Override
            protected Color getBackground() {
                return UIUtil.SIDE_PANEL_BACKGROUND;
            }
        });

        add(ScrollPaneFactory.createScrollPane(myList, true), BorderLayout.CENTER);
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        myList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                final SidePanelItem value = myList.getSelectedValue();
                if (value != null) {
                    selectedFunction.consume(value.myText);
                }
            }
        });
    }

    public JList getList() {
        return myList;
    }

    public void addPlace(String text) {
        myModel.addElement(new SidePanelItem(text));
        revalidate();
        repaint();
    }

    public void addSeparator(@Nls String text) {
        myIndex2Separator.put(myModel.size(), text);
    }

    @Nullable
    private @NlsContexts.Separator String getSeparatorAbove(final SidePanelItem item) {
        return myIndex2Separator.get(myModel.indexOf(item));
    }

    public void select(final String option) {
        for (int i = 0; i < myModel.getSize(); i++) {
            SidePanelItem item = myModel.getElementAt(i);
            if (option.equals(item.myText)) {
                myList.setSelectedValue(item, true);
            }
        }
    }

    private static class SidePanelItem {
        private final @Nls String myText;

        SidePanelItem(@Nls String text) {
            this.myText = text;
        }

        @Override
        public String toString() {
            return myText;
        }
    }

}