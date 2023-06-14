package com.couchbase.intellij.tree.examples;


import com.couchbase.intellij.tools.github.CloneDemoRepo;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardDialog extends DialogWrapper {

    private List<Card> cards;
    private JPanel cardsPanel;
    private JBTextField searchField;
    private TextFieldWithBrowseButton directoryField;
    private CardPanel selectedCard;
    private JLabel errorMessage;
    private JPanel wrapperPanel;
    private final List<CardPanel> panelsList = new ArrayList<>();

    public CardDialog(@Nullable Project project) {
        super(project);
        this.cards = CloneDemoRepo.getExamples();
        setTitle("New Project from Template");
        setResizable(false);
        setSize(900, 800);
        init();
    }

    public void selectCard(CardPanel cardPanel) {
        for (CardPanel cpanel : panelsList) {
            if (cpanel.isSelected()) {
                cpanel.setSelected(false);
            }
        }
        selectedCard = cardPanel;
        selectedCard.setSelected(true);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());


        directoryField = new TextFieldWithBrowseButton();
        directoryField.addBrowseFolderListener("Select Directory", "Select the target directory", null, FileChooserDescriptorFactory.createSingleFolderDescriptor(), TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);


        searchField = new JBTextField();
        searchField.getEmptyText().setText("Search for your favorite language or framework");
        searchField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent e) {
                filterCards();
            }
        });

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = JBUI.insets(5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0; // do not stretch
        topPanel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1; // stretch
        topPanel.add(directoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0; // do not stretch
        topPanel.add(new JLabel("Filter Templates:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1; // stretch
        topPanel.add(searchField, gbc);
        topPanel.setBorder(JBUI.Borders.emptyBottom(10));

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new GridLayout(0, 3, 10, 10));

        wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBorder(JBUI.Borders.emptyTop(10));
        wrapperPanel.add(cardsPanel, new GridBagConstraints());

        errorMessage = new JLabel("");
        errorMessage.setForeground(Color.decode("#FF4444"));
        JPanel errorPanel = new JPanel(new BorderLayout());
        errorPanel.add(errorMessage, BorderLayout.WEST);

        JScrollPane scrollPane = new JBScrollPane(wrapperPanel);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(errorPanel, BorderLayout.SOUTH);

        populateCards(cards);

        return centerPanel;
    }


    private void populateCards(List<Card> cards) {
        cardsPanel.removeAll();
        panelsList.clear();

        for (Card card : cards) {
            CardPanel cardPanel = new CardPanel(this, card);
            panelsList.add(cardPanel);

            JPanel cardWrapperPanel = new JPanel();
            cardWrapperPanel.setLayout(new BoxLayout(cardWrapperPanel, BoxLayout.X_AXIS));
            cardWrapperPanel.add(Box.createHorizontalGlue());
            cardWrapperPanel.add(cardPanel);
            cardWrapperPanel.add(Box.createHorizontalGlue());

            cardsPanel.add(cardWrapperPanel);
        }

        wrapperPanel.revalidate();
        cardsPanel.revalidate();
    }

    private void filterCards() {
        String searchText = searchField.getText();
        List<Card> filteredCards = cards.stream().filter(card -> card.getTitle().toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());
        populateCards(filteredCards);
    }

    @Override
    protected void doOKAction() {
        List<String> errors = new ArrayList<>();
        if (selectedCard == null) {
            errors.add("Select a template");
        }

        if (directoryField.getText().isEmpty()) {
            errors.add("Select the folder where the project will be cloned");
        } else if (Files.exists(Paths.get(directoryField.getText()))) {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(directoryField.getText()))) {
                if (dirStream.iterator().hasNext()) {
                    errors.add("The specified directory is not empty");
                }
            } catch (IOException e) {
                errors.add("An error occurred while checking the directory");
            }
        } else {
            errors.add("The specified path does not exist");
        }

        if (errors.isEmpty()) {
            super.doOKAction();
            CloneDemoRepo.cloneAndOpen(selectedCard.getCard().getUrl(), directoryField.getText());
        } else {
            errorMessage.setText("<html>" + errors.stream().collect(Collectors.joining("<br>")) + "</html>");
        }
    }
}
