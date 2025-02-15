package com.couchbase.intellij.workbench;

import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileSaverDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import org.jetbrains.annotations.NotNull;
import utils.TimeUtils;

import java.io.File;
import java.io.FileWriter;

public class FileExporter {

    protected static void exportResultToCSV(@NotNull Project project, String content) {
        FileSaverDescriptor fsd = new FileSaverDescriptor("Save CSV File", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project).save(("cb_query_export-" + TimeUtils.getCurrentDateTime() + ".csv"));
        if (wrapper != null) {
            File file = wrapper.getFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
                Messages.showInfoMessage("File saved successfully.", "File Saved");
            } catch (Exception e) {
                Messages.showErrorDialog(e.getMessage(), "Error Saving the File");
            }
        }
    }

    protected static void exportResultToJson(@NotNull Project project, String content) {
        FileSaverDescriptor fsd = new FileSaverDescriptor("Save JSON File", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project).save(("cb_query_export-" + TimeUtils.getCurrentDateTime() + ".json"));
        if (wrapper != null) {
            File file = wrapper.getFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
                Messages.showInfoMessage("File saved successfully.", "File Saved");
            } catch (Exception e) {
                Messages.showErrorDialog(e.getMessage(), "Error Saving File");
            }

        }
    }

    protected static void exportResultToSQLPP(@NotNull Project project, String content) {
        FileSaverDescriptor fsd = new FileSaverDescriptor("Save SQL++ File", "Choose where you want to save the file:");
        VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project).save(("cb_query_export-" + TimeUtils.getCurrentDateTime() + ".sqlpp"));
        if (wrapper != null) {
            File file = wrapper.getFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(content);
                Messages.showInfoMessage("File saved successfully.", "File Saved");
            } catch (Exception e) {
                Messages.showErrorDialog(e.getMessage(), "Error Saving the File");
            }
        }
    }

}
