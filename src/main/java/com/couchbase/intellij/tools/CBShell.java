package com.couchbase.intellij.tools;

import com.couchbase.intellij.database.ActiveCluster;

import java.io.File;

public class CBShell {

    public static void openNewTerminal() {
        try {
            String scriptPath = CBFolders.getInstance().getCbShellPath() + File.separator + "autopass.sh";
            String command = CBTools.getShell().getPath() + " --connstr " + ActiveCluster.getInstance().getClusterURL() + " -u " + ActiveCluster.getInstance().getUsername() + " -p";
            String password = ActiveCluster.getInstance().getPassword();

            String[] cmd = {"osascript", "-e", "tell application \"Terminal\"\n" + "activate\n" + "do script \"source ~/.bash_profile; '" + scriptPath + "' '" + command + "' '" + password + "'\" in front window\n" + "end tell"};

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
