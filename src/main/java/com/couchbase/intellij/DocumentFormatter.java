package com.couchbase.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.util.IncorrectOperationException;

public class DocumentFormatter {

    public static void formatFile(Project project, VirtualFile virtualFile) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.isDisposed()) {
                return;
            }
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile != null) {
                CommandProcessor.getInstance().executeCommand(project, () -> {
                    ApplicationManager.getApplication().runWriteAction(() -> {
                        try {
                            CodeStyleManager.getInstance(project).reformat(psiFile);
                        } catch (IncorrectOperationException ioe) {
                            // handle exception
                            ioe.printStackTrace();
                        }
                    });
                }, "Format File", null);
            }
        });
    }
}
