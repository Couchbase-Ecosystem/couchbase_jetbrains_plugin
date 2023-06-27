package com.couchbase.intellij.tools;


import com.couchbase.intellij.database.ActiveCluster;
import utils.ProcessUtils;

import java.io.File;

public class CBShell {

//    public static void openNewTerminal() {
//        try {
//            String scriptPath = CBFolders.getInstance().getCbShellPath() + File.separator + "autopass.sh";
//
//            String[] cmd = {"osascript", "-e", "tell application \"Terminal\"\n" + "activate\n" + "do script \"source ~/.bash_profile; '" + scriptPath + "' \\\"" + scriptPath + "\\\" \\\"--connstr\\\" \\\"" + ActiveCluster.getInstance().getClusterURL() + "\\\" \\\"-u\\\" \\\"" + ActiveCluster.getInstance().getUsername() + "\\\" \\\"-p\\\" \\\"" + ActiveCluster.getInstance().getPassword() + "\\\"\" in front window\n" + "end tell"};
//            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
//            Process process = processBuilder.start();
//            ProcessUtils.printOutput(process, "asee");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void openNewTerminal() {
        try {
            String scriptPath = CBFolders.getInstance().getCbShellPath() + File.separator + "autopass.sh";
            String password = ActiveCluster.getInstance().getPassword();


            String[] cmd = {"osascript", "-e", "tell application \"Terminal\"\n" +
                    "activate\n" +
                    "do script \"source ~/.bash_profile; '" +
                    scriptPath + "' \\\"" + CBFolders.getInstance().getCbShellPath() + "\\\" \\\"--connstr\\\" \\\"" + ActiveCluster.getInstance().getClusterURL() + "\\\" \\\"-u\\\" \\\"" + ActiveCluster.getInstance().getUsername() + "\\\" \\\"-p\\\" \\\"" +
                    password +
                    "\\\"\"\n" + "end tell"};
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();
            ProcessUtils.printOutput(process, "asee");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        openNewTerminal();
    }
}
