package com.couchbase.intellij.listener;

import com.couchbase.intellij.workbench.Log;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import utils.OSUtil;

import java.io.IOException;
import java.io.OutputStream;

public class GitIgnore {

    private static final String GIT_IGNORE_FILE = ".gitignore";

    public static void updateGitIgnore(Project project) {

        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {


                String GIT_IGNORE_CONTENT = OSUtil.isWindows() ? "cbcache/" : ".cbcache/";

                VirtualFile baseDir = ProjectUtil.guessProjectDir(project);
                VirtualFile gitignoreFile = baseDir.findChild(GIT_IGNORE_FILE);

                if (gitignoreFile == null) { // If .gitignore file doesn't exist
                    try {
                        gitignoreFile = baseDir.createChildData(null, GIT_IGNORE_FILE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                try {
                    String gitignoreContent = VfsUtil.loadText(gitignoreFile);
                    if (!gitignoreContent.contains(GIT_IGNORE_CONTENT)) {
                        OutputStream os = gitignoreFile.getOutputStream(null);
                        os.write((gitignoreContent + "\n### Couchbase Plugin ###\n" + GIT_IGNORE_CONTENT + "\n").getBytes());
                        os.close();
                    }
                } catch (IOException e) {
                    Log.error(e);
                    e.printStackTrace();
                }
            });
        });
    }
}
