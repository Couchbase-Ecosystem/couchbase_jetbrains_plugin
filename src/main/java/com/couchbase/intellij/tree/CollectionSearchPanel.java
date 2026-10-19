package com.couchbase.intellij.tree;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Type-ahead collection search: a text field with a live result list underneath. Searches run on
 * {@code background} and results are applied on {@code ui}; stale results (the user kept typing) are dropped.
 */
public class CollectionSearchPanel extends JPanel {

    private final JBTextField field = new JBTextField();
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JBList<String> list = new JBList<>(model);
    private final JBLabel status = new JBLabel("Type a collection name");
    private final Function<String, List<String>> searcher;
    private final Consumer<String> onChosen;
    private final Executor background;
    private final Executor ui;
    private final Timer debounce;
    private final int debounceMillisValue;
    private int generation;
    private boolean disposed;

    public CollectionSearchPanel(Function<String, List<String>> searcher, Consumer<String> onChosen,
                                 Executor background, Executor ui, int debounceMillis) {
        super(new BorderLayout(0, 6));
        this.searcher = searcher;
        this.onChosen = onChosen;
        this.background = background;
        this.ui = ui;
        this.debounceMillisValue = debounceMillis;

        field.setName("collectionSearchField");
        field.getEmptyText().setText("Type a collection name");
        list.setName("collectionSearchResults");
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        status.setName("collectionSearchStatus");

        debounce = new Timer(Math.max(debounceMillis, 1), e -> runSearch());
        debounce.setRepeats(false);

        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleSearch();
            }
        });

        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && model.getSize() > 0) {
                    moveSelection(1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && model.getSize() > 0) {
                    moveSelection(-1);
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    chooseSelected();
                    e.consume();
                }
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    chooseSelected();
                }
            }
        });

        add(field, BorderLayout.NORTH);
        JBScrollPane scroll = new JBScrollPane(list);
        scroll.setPreferredSize(new Dimension(380, 220));
        add(scroll, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private void scheduleSearch() {
        if (disposed) {
            return;
        }
        // Invalidate the previous query immediately, including during the debounce delay.
        generation++;
        model.clear();
        status.setText("Searching...");
        if (debounceMillisValue <= 0) {
            runSearch();
        } else {
            debounce.restart();
        }
    }

    /** Runs the search for the current text now. */
    public void runSearch() {
        if (disposed) {
            return;
        }
        final int mine = ++generation;
        final String text = field.getText().trim();
        status.setText("Searching...");
        background.execute(() -> {
            List<String> found;
            String error = null;
            try {
                found = searcher.apply(text);
            } catch (Exception e) {
                found = List.of();
                error = e.getMessage() == null ? e.toString() : e.getMessage();
            }
            final List<String> result = found;
            final String err = error;
            ui.execute(() -> {
                if (mine == generation) {
                    showResults(result, err);
                }
            });
        });
    }

    private void showResults(List<String> results, String error) {
        model.clear();
        results.forEach(model::addElement);
        if (error != null) {
            status.setText("Search failed: " + error);
        } else if (results.isEmpty()) {
            status.setText("No collections match");
        } else {
            status.setText(results.size() + (results.size() == 1 ? " collection" : " collections"));
            list.setSelectedIndex(0);
        }
    }

    private void moveSelection(int delta) {
        int next = Math.max(0, Math.min(model.getSize() - 1, list.getSelectedIndex() + delta));
        list.setSelectedIndex(next);
        list.ensureIndexIsVisible(next);
    }

    /** Hands the highlighted result to the callback; does nothing if nothing is highlighted. */
    public void chooseSelected() {
        String selected = getSelected();
        if (selected != null) {
            onChosen.accept(selected);
        }
    }

    public String getSelected() {
        return list.getSelectedValue();
    }

    public void setQuery(String text) {
        field.setText(text);
    }

    public String getQuery() {
        return field.getText();
    }

    public List<String> getResults() {
        return java.util.Collections.list(model.elements());
    }

    public String getStatusText() {
        return status.getText();
    }

    public JComponent getFocusTarget() {
        return field;
    }

    public void dispose() {
        disposed = true;
        generation++;
        debounce.stop();
    }
}
