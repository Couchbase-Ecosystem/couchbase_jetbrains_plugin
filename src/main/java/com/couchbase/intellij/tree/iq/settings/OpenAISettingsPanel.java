package com.couchbase.intellij.tree.iq.settings;

import com.couchbase.intellij.tree.CouchbaseWindowFactory;
import com.couchbase.intellij.tree.iq.icons.ChatGPTIcons;
import com.couchbase.intellij.tree.iq.message.ChatGPTBundle;
import com.couchbase.intellij.tree.iq.ui.SupportDialog;
import com.couchbase.intellij.tree.iq.util.OpenAIUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.PortField;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.ui.components.labels.LinkLabel;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class OpenAISettingsPanel implements Configurable, Disposable {
    public static final String FIND_GRANTS = "https://api.openai.com/dashboard/billing/credit_grants";
    public static final String CREATE_API_KEY = "https://api.openai.com/dashboard/user/api_keys";
    private final String[] comboboxItemsString = {
            CouchbaseWindowFactory.CHATGPT_CONTENT_NAME,
            CouchbaseWindowFactory.GPT35_TRUBO_CONTENT_NAME,
            CouchbaseWindowFactory.ONLINE_CHATGPT_CONTENT_NAME};
    private JPanel myMainPanel;
    private JPanel urlTitledBorderBox;
    private JPanel connectionTitledBorderBox;
    private JBTextField readTimeoutField;
    private JBTextField connectionTimeoutField;
    private JCheckBox enableProxyCheckBox;
    private PortField portField;
    private JPanel proxyTitledBorderBox;
    private JBRadioButton proxyDirectChoice;
    private JBRadioButton proxyHttpChoice;
    private JBRadioButton proxySocksChoice;
    private JTextField hostnameField;
    private JPanel proxyOptions;
    private JCheckBox enableAvatarCheckBox;
    private JPanel contentTitledBorderBox;
    private JComboBox<String> firstCombobox;
    private JComboBox<String> secondCombobox;
    private JComboBox<String> thirdCombobox;
    private JCheckBox enableLineWarpCheckBox;
    private JLabel readTimeoutHelpLabel;
    private JLabel connectionTimeoutHelpLabel;
    private JLabel contentOrderHelpLabel;
    private JLabel supportDonate;
    private JPanel supportPanel;
    private JTextField totalField;
    private JBTextField assistantApiKey;
    private JButton createAPIKeyButton;
    private JButton refreshButton;
    private JPanel openaiAssistantTitledBorderBox;
    private JLabel openaiAssistantContentHelpLabel;
    private JCheckBox enableProxyAuthCheckBox;
    private JTextField proxyUsernameField;
    private JBPasswordField proxyPasswordField;
    private JPanel proxyAuthPanel;
    private JPanel corePromptBorderBox;
    private JBTextField prompt1NameField;
    private JBTextField prompt2NameField;
    private JBTextField prompt3NameField;
    private ExpandableTextField prompt1ValueField;
    private ExpandableTextField prompt3ValueField;
    private ExpandableTextField prompt2ValueField;
    private boolean needRestart = false;

    public OpenAISettingsPanel() {
        init();
    }

    private static void register(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
        choice.putClientProperty("value", value);
    }

    private static void setSelected(@NotNull JBRadioButton choice, @NotNull SettingConfiguration.SettingProxyType value) {
        choice.setSelected(value.equals(choice.getClientProperty("value")));
    }

    private void init() {
        register(proxyDirectChoice, SettingConfiguration.SettingProxyType.DIRECT);
        register(proxyHttpChoice, SettingConfiguration.SettingProxyType.HTTP);
        register(proxySocksChoice, SettingConfiguration.SettingProxyType.SOCKS);
        ItemListener proxyTypeChangedListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableProxyOptions(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableProxyOptions(false);
            }
        };
        enableProxyCheckBox.addItemListener(proxyTypeChangedListener);
        enableProxyOptions(false);

        ItemListener proxyAuthChangedListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableProxyAuthOptions(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                enableProxyAuthOptions(false);
            }
        };
        enableProxyAuthCheckBox.addItemListener(proxyAuthChangedListener);
        enableProxyAuthOptions(false);

        readTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.read_timeout.empty_text"));
        connectionTimeoutField.getEmptyText().setText(ChatGPTBundle.message("ui.setting.connection.connection_timeout.empty_text"));
        assistantApiKey.getEmptyText().setText("If it is empty, the API Key in GPT-3.5-Turbo will be used");

        firstCombobox.setModel(new DefaultComboBoxModel<>(comboboxItemsString));
        secondCombobox.setModel(new DefaultComboBoxModel<>(comboboxItemsString));
        thirdCombobox.setModel(new DefaultComboBoxModel<>(comboboxItemsString));

        refreshButton.addActionListener(e -> {
            OpenAIUtil.refreshGranted(myMainPanel);
        });

        createAPIKeyButton.addActionListener(e -> {
            String apiKey = checkKeyExists();
            if (StringUtil.isEmpty(apiKey)) {
                return;
            }
            OpenAIUtil.createAPIKey(apiKey,myMainPanel);
        });

        prompt1ValueField.setFont(JBUI.Fonts.label());
        prompt2ValueField.setFont(JBUI.Fonts.label());
        prompt3ValueField.setFont(JBUI.Fonts.label());
    }

    private String checkKeyExists() {
        if (StringUtil.isNotEmpty(assistantApiKey.getText())){
            return assistantApiKey.getText();
        }
        if (StringUtil.isNotEmpty(OpenAISettingsState.getInstance().apiKey)) {
            return OpenAISettingsState.getInstance().apiKey;
        }
        MessageDialogBuilder.yesNo("API Key is required?",
                        "Please provide an API Key before you can " +
                                "refresh usage or create a new API Key.")
                .yesText("Got it")
                .noText("Close").ask(myMainPanel);
        return null;
    }

    private void enableProxyOptions(boolean enabled) {
        UIUtil.setEnabled(proxyOptions, enabled, true);
    }

    private void enableProxyAuthOptions(boolean enabled) {
        UIUtil.setEnabled(proxyAuthPanel, enabled, true);
    }

    @Override
    public void reset() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        readTimeoutField.setText(state.readTimeout);
        connectionTimeoutField.setText(state.connectionTimeout);

        enableProxyCheckBox.setSelected(state.enableProxy);
        enableAvatarCheckBox.setSelected(state.enableAvatar);
        setProxyChoice(state.proxyType);
        hostnameField.setText(state.proxyHostname);
        portField.setValue(Integer.parseInt(state.proxyPort));
        enableProxyAuthCheckBox.setSelected(state.enableProxyAuth);
        proxyUsernameField.setText(state.proxyUsername);
        proxyPasswordField.setText(state.proxyPassword);

        firstCombobox.setSelectedItem(state.contentOrder.get(1));
        secondCombobox.setSelectedItem(state.contentOrder.get(2));
        thirdCombobox.setSelectedItem(state.contentOrder.get(3));

        enableLineWarpCheckBox.setSelected(state.enableLineWarp);
        assistantApiKey.setText(state.assistantApiKey);

        prompt1NameField.setText(state.prompt1Name);
        prompt1ValueField.setText(state.prompt1Value);
        prompt2NameField.setText(state.prompt2Name);
        prompt2ValueField.setText(state.prompt2Value);
        prompt3NameField.setText(state.prompt3Name);
        prompt3ValueField.setText(state.prompt3Value);

        initHelp();
    }

    @Override
    public @Nullable JComponent createComponent() {
        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        // If you change the order, you need to restart the IDE to take effect
        needRestart = !StringUtil.equals(state.contentOrder.get(1), (String)firstCombobox.getSelectedItem())||
                !StringUtil.equals(state.contentOrder.get(2), (String)secondCombobox.getSelectedItem())||
                !StringUtil.equals(state.contentOrder.get(3), (String)thirdCombobox.getSelectedItem()) ||
                !state.enableLineWarp == enableLineWarpCheckBox.isSelected() ||
                !StringUtil.equals(state.prompt1Name, prompt1NameField.getText()) ||
                !StringUtil.equals(state.prompt2Name, prompt2NameField.getText()) ||
                !StringUtil.equals(state.prompt3Name, prompt3NameField.getText())
        ;

        return
                !StringUtil.equals(state.readTimeout, readTimeoutField.getText()) ||
                !StringUtil.equals(state.connectionTimeout, connectionTimeoutField.getText()) ||
                !state.proxyType.equals(getProxyChoice()) ||
                !state.enableProxy == enableProxyCheckBox.isSelected() ||
                !state.enableAvatar == enableAvatarCheckBox.isSelected() ||
                !StringUtil.equals(state.proxyHostname, hostnameField.getText()) ||
                !StringUtil.equals(state.proxyPort, portField.getValue().toString()) ||
                !state.enableProxyAuth == enableProxyAuthCheckBox.isSelected() ||
                !StringUtil.equals(state.proxyUsername, proxyUsernameField.getText()) ||
                !StringUtil.equals(state.proxyPassword, proxyPasswordField.getText()) ||
                !StringUtil.equals(state.contentOrder.get(1), (String)firstCombobox.getSelectedItem()) ||
                !StringUtil.equals(state.contentOrder.get(2), (String)secondCombobox.getSelectedItem()) ||
                !StringUtil.equals(state.contentOrder.get(3), (String)thirdCombobox.getSelectedItem()) ||
                !state.enableLineWarp == enableLineWarpCheckBox.isSelected() ||
                !StringUtil.equals(state.assistantApiKey,assistantApiKey.getText()) ||
                !StringUtil.equals(state.prompt1Name, prompt1NameField.getText()) ||
                !StringUtil.equals(state.prompt1Value, prompt1ValueField.getText()) ||
                !StringUtil.equals(state.prompt2Name, prompt2NameField.getText()) ||
                !StringUtil.equals(state.prompt2Value, prompt2ValueField.getText()) ||
                !StringUtil.equals(state.prompt3Name, prompt3NameField.getText()) ||
                !StringUtil.equals(state.prompt3Value, prompt3ValueField.getText())
                ;
    }

    @Override
    public void apply() {
        OpenAISettingsState state = OpenAISettingsState.getInstance();

        boolean readTimeoutIsNumber = com.couchbase.intellij.tree.iq.util.StringUtil.isNumber(readTimeoutField.getText());
        boolean connectionTimeoutIsNumber = com.couchbase.intellij.tree.iq.util.StringUtil.isNumber(connectionTimeoutField.getText());
        state.readTimeout = !readTimeoutIsNumber ? "50000" : readTimeoutField.getText();
        state.connectionTimeout = !connectionTimeoutIsNumber ? "50000" : connectionTimeoutField.getText();

        state.proxyType = getProxyChoice();
        state.enableProxy = enableProxyCheckBox.isSelected();
        state.enableAvatar = enableAvatarCheckBox.isSelected();
        state.proxyHostname = hostnameField.getText();

        state.prompt1Name = prompt1NameField.getText();
        state.prompt1Value = prompt1ValueField.getText();
        state.prompt2Name = prompt2NameField.getText();
        state.prompt2Value = prompt2ValueField.getText();
        state.prompt3Name = prompt3NameField.getText();
        state.prompt3Value = prompt3ValueField.getText();

        boolean portIsNumber = com.couchbase.intellij.tree.iq.util.StringUtil.isNumber(portField.getValue().toString());
        state.proxyPort = !portIsNumber ? "0" : portField.getValue().toString();
        state.enableProxyAuth = enableProxyAuthCheckBox.isSelected();
        state.proxyUsername = proxyUsernameField.getText();
        state.proxyPassword = proxyPasswordField.getText();

        String firstSelected = (String) firstCombobox.getSelectedItem();
        String secondSelected = (String) secondCombobox.getSelectedItem();
        String thirdSelected = (String) thirdCombobox.getSelectedItem();

        // Determine whether each location has a different Content
        List<String> strings = new ArrayList<>(3);
        strings.add(firstSelected);
        strings.add(secondSelected);
        strings.add(thirdSelected);
        List<String> collect = strings.stream().distinct().collect(Collectors.toList());
        if (collect.size() != strings.size()) {
            MessageDialogBuilder.yesNo("Duplicate Content exists!", "The content of " +
                            "each position must be unique, please re-adjust the order")
                    .yesText("Ok")
                    .noText("Close").ask(myMainPanel);
            return;
        }

        state.contentOrder.put(1, firstSelected);
        state.contentOrder.put(2, secondSelected);
        state.contentOrder.put(3, thirdSelected);

        state.enableLineWarp = enableLineWarpCheckBox.isSelected();
        state.assistantApiKey = assistantApiKey.getText();

        if (needRestart) {
            boolean yes = MessageDialogBuilder.yesNo("Content order changed!", "Changing " +
                            "the content order requires restarting the IDE to take effect. Do you " +
                            "want to restart to apply the settings?")
                    .yesText("Restart")
                    .noText("Not Now").ask(myMainPanel);
            if (yes) {
                ApplicationManagerEx.getApplicationEx().restart(true);
            }
        }
    }

    @Override
    public void dispose() {
    }

    @NotNull
    private SettingConfiguration.SettingProxyType getProxyChoice() {
        JBRadioButton selected = proxyDirectChoice.isSelected()
                ? proxyDirectChoice
                : proxyHttpChoice.isSelected()
                ? proxyHttpChoice
                : proxySocksChoice.isSelected()
                ? proxySocksChoice : null;

        if (selected == null) {
            selected = proxyDirectChoice;
        }
        assert selected != null;

        return (SettingConfiguration.SettingProxyType)selected.getClientProperty("value");
    }

    private void setProxyChoice(@NotNull SettingConfiguration.SettingProxyType value) {
        setSelected(proxyDirectChoice, value);
        setSelected(proxyHttpChoice, value);
        setSelected(proxySocksChoice, value);
    }

    @Override
    public String getDisplayName() {
        return ChatGPTBundle.message("ui.setting.menu.text");
    }

    private void createUIComponents() {
        connectionTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsConnection = new TitledSeparator(ChatGPTBundle.message("ui.setting.connection.title"));
        connectionTitledBorderBox.add(tsConnection,BorderLayout.CENTER);

        proxyTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsProxy = new TitledSeparator("Proxy Settings");
        proxyTitledBorderBox.add(tsProxy,BorderLayout.CENTER);

        contentTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("Tool Window Settings");
        contentTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        openaiAssistantTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator oaUrl = new TitledSeparator("OpenAI Assistant");
        openaiAssistantTitledBorderBox.add(oaUrl,BorderLayout.CENTER);

        corePromptBorderBox = new JPanel(new BorderLayout());
        TitledSeparator corePrompt = new TitledSeparator("Custom Built-in Prompt");
        corePromptBorderBox.add(corePrompt,BorderLayout.CENTER);


        supportPanel = new JPanel(new BorderLayout());
        supportDonate = new LinkLabel<>("Support / Donate", ChatGPTIcons.SUPPORT);
        supportDonate.setBorder(JBUI.Borders.emptyTop(20));
        supportDonate.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new SupportDialog(null).show();
            }
        });
    }

    public void initHelp() {
        readTimeoutHelpLabel.setFont(JBUI.Fonts.smallFont());
        readTimeoutHelpLabel.setForeground(UIUtil.getContextHelpForeground());

        connectionTimeoutHelpLabel.setFont(JBUI.Fonts.smallFont());
        connectionTimeoutHelpLabel.setForeground(UIUtil.getContextHelpForeground());

        contentOrderHelpLabel.setFont(JBUI.Fonts.smallFont());
        contentOrderHelpLabel.setForeground(UIUtil.getContextHelpForeground());

        openaiAssistantContentHelpLabel.setFont(JBUI.Fonts.smallFont());
        openaiAssistantContentHelpLabel.setForeground(UIUtil.getContextHelpForeground());
    }
}
