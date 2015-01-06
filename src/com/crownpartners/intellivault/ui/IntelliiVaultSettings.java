package com.crownpartners.intellivault.ui;

import com.crownpartners.intellivault.config.IntelliVaultCRXRepository;
import com.crownpartners.intellivault.config.IntelliVaultPreferences;
import com.crownpartners.intellivault.services.VaultInvokerService;
import com.crownpartners.intellivault.services.impl.IntelliVaultPreferencesService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sean.steimer
 * Date: 3/15/13
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntelliiVaultSettings implements Configurable {

    public static final String FILE_IGNORE_PATTERN_SEPERATOR = ",";
    public static final String CURRENT_DIRECTORY_SYMBOL = ".";
    private JPanel jPanel;
    private JTextField txtVaultDir;
    private JButton btnVaultDirBrowse;
    private JButton btnTempDirBrowse;
    private JTextField txtTempDir;
    private JCheckBox verboseOutputCheckBox;
    private JTextField txtRepoUrl;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtIgnorePatterns;
    private JTextField txtJCRRootDirName;
    private JButton btnRestoreDefaults;

    @Nls
    @Override
    public String getDisplayName() {
        return "IntelliVault";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return "IntelliVault.Plugin.Help";
    }

    @Nullable
    @Override
    public JComponent createComponent() {


        reset();
        return jPanel;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        IntelliVaultPreferencesService preferencesService
                = ServiceManager.getService(IntelliVaultPreferencesService.class);
        preferencesService.setPreferences(getPreferencesFromDialogState());

        VaultInvokerService vltInvoker = ServiceManager.getService(VaultInvokerService.class);
        vltInvoker.forceReInit();
    }



    @Override
    public void reset() {
        IntelliVaultPreferencesService preferencesService
                = ServiceManager.getService(IntelliVaultPreferencesService.class);
        setDialogStateFromPreferences(preferencesService.getPreferences());
      //  setDialogStatic();
    }

    /**
     * Serialize the current dialog state to a preferences object which can be used by IntelliVault.
     * @return the currently selected IntelliVaultPreferences
     */
    private IntelliVaultPreferences getPreferencesFromDialogState() {

        IntelliVaultPreferences preferencesBean = new IntelliVaultPreferences();
        preferencesBean.vaultPath = txtVaultDir.getText();
        preferencesBean.tempDirectory = txtTempDir.getText();
        preferencesBean.rootFolderName = txtJCRRootDirName.getText();

        preferencesBean.verbose = verboseOutputCheckBox.isSelected();

        String ignorePatterns = txtIgnorePatterns.getText();
        if (ignorePatterns != null) {
            String[] ignorePatternsArray = ignorePatterns.split(FILE_IGNORE_PATTERN_SEPERATOR);
            preferencesBean.fileIgnorePatterns = Arrays.asList(ignorePatternsArray);
        }

        preferencesBean.repoConfigs.clear();
        preferencesBean.addRepositoryConfiguration(txtRepoUrl.getText(), txtUsername.getText(), txtPassword.getText());

        return preferencesBean;
    }

    /**
     * De-serialize an IntelliVaultPreferences object to the dialog, setting all dialog fields as defined in the object
     * passed in.
     * @param preferences the preferences object to set the dialog fields from.
     */
    private void setDialogStateFromPreferences(final IntelliVaultPreferences preferences) {
        txtVaultDir.setText(preferences.vaultPath);
        txtTempDir.setText(preferences.tempDirectory);
        txtJCRRootDirName.setText(preferences.rootFolderName);

        verboseOutputCheckBox.setSelected(preferences.verbose);

        StringBuffer buf = new StringBuffer();
        for (String s : preferences.fileIgnorePatterns) {
            buf.append(s).append(FILE_IGNORE_PATTERN_SEPERATOR);
        }
        String ignorePatterns=buf.toString();
        ignorePatterns = ignorePatterns.substring(0, ignorePatterns.length()-1);
        txtIgnorePatterns.setText(ignorePatterns);

        List<IntelliVaultCRXRepository> repositoryList = preferences.getRepositoryList();
        if (repositoryList != null && repositoryList.size() > 0){
            IntelliVaultCRXRepository intelliVaultCRXRepository = repositoryList.get(0);
            txtRepoUrl.setText(intelliVaultCRXRepository.getRepoUrl());
            txtPassword.setText(intelliVaultCRXRepository.getPassword());
            txtUsername.setText(intelliVaultCRXRepository.getUsername());
        }
    }
    private void setDialogStatic() {
        txtVaultDir.setText("C:\\Projekte\\aem\\vault-cli-3.1.6\\bin");
        txtTempDir.setText("C:\\tmp\\");
        txtJCRRootDirName.setText("jcr_root");
        verboseOutputCheckBox.setSelected(true);
        String ignorePatterns=".git,.vlt";
        ignorePatterns = ignorePatterns.substring(0, ignorePatterns.length()-1);
        txtIgnorePatterns.setText(ignorePatterns);

            txtRepoUrl.setText("http://localhost:4502");
            txtPassword.setText("admin");
            txtUsername.setText("admin");

    }

    @Override
    public void disposeUIResources() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
