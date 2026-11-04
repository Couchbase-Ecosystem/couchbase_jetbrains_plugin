package com.couchbase.intellij.tree;

import com.couchbase.client.java.manager.search.SearchIndexManager;
import com.couchbase.intellij.DocumentFormatter;
import com.couchbase.intellij.actions.PasswordConnectionAction;
import com.couchbase.intellij.VirtualFileKeys;
import com.couchbase.intellij.database.ActiveCluster;
import com.couchbase.intellij.database.ClusterUiTask;
import com.couchbase.intellij.database.DataLoader;
import com.couchbase.intellij.database.collections.CollectionListing;
import com.couchbase.intellij.database.collections.CollectionSources;
import com.couchbase.intellij.persistence.FavoriteCollections;
import com.couchbase.intellij.persistence.storage.FavoriteCollectionsStorage;
import com.couchbase.intellij.database.InferHelper;
import com.couchbase.intellij.persistence.storage.QueryFiltersStorage;
import com.couchbase.intellij.persistence.storage.RelationshipStorage;
import com.couchbase.intellij.tools.CBExport;
import com.couchbase.intellij.tools.CBImport;
import com.couchbase.intellij.tools.CBTools;
import com.couchbase.intellij.tools.PillowFightDialog;
import com.couchbase.intellij.tools.cbmigrate.DynamoMigrationDialog;
import com.couchbase.intellij.tools.cbmigrate.MigrationDialog;
import com.couchbase.intellij.tools.dialog.DDLExportDialog;
import com.couchbase.intellij.tools.dialog.ExportDialog;
import com.couchbase.intellij.tools.dialog.ImportDialog;
import com.couchbase.intellij.tree.docfilter.DocumentFilterDialog;
import com.couchbase.intellij.tree.node.*;
import com.couchbase.intellij.tree.overview.IndexOverviewDialog;
import com.couchbase.intellij.tree.overview.ServerOverviewDialog;
import com.couchbase.intellij.tree.overview.apis.CouchbaseRestAPI;
import com.couchbase.intellij.workbench.Log;
import com.couchbase.intellij.workbench.EditorOpening;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileWrapper;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.ColorPicker;
import com.intellij.ui.ColorPickerListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.TimeUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import java.util.Objects;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TreeRightClickListener {

    public static int searchWorkbenchCounter = 0;

    private static void runMutation(Project project, ClusterUiTask.Connection original, String title, java.util.function.Consumer<com.couchbase.client.java.Cluster> operation, Runnable completed) {
        ClusterUiTask.submit(project, original, title, connection -> {
            connection.requireWritable();
            operation.accept(connection.sdk());
            return true;
        }, ignored -> completed.run(), error -> Messages.showErrorDialog(project,
                "Could not complete the operation. " + error.getMessage(), title));
    }


    /** The primary index has its own management call; every other index is dropped by name. */
    static void dropIndex(com.couchbase.client.java.manager.query.CollectionQueryIndexManager indexes, String indexName) {
        if ("#primary".equals(indexName)) indexes.dropPrimaryIndex();
        else indexes.dropIndex(indexName);
    }

    /** A menu acts on the connection and node it was opened for, never on whatever is current when it is clicked. */
    static boolean isMenuTargetCurrent(ClusterUiTask.Connection menuConnection, Object treeRoot, DefaultMutableTreeNode node) {
        return menuConnection.isCurrent() && node.getParent() != null && node.getRoot() == treeRoot;
    }

    private static boolean requireMenuTarget(Project project, ClusterUiTask.Connection menuConnection, Tree tree, DefaultMutableTreeNode node, String title) {
        if (isMenuTargetCurrent(menuConnection, tree.getModel().getRoot(), node)) return true;
        Messages.showErrorDialog(project, "The connection or tree changed after this menu was opened. Refresh the tree and try again.", title);
        return false;
    }


    public static void handle(Tree tree, Project project, JPanel toolbarPanel, MouseEvent e, DefaultMutableTreeNode clickedNode) {
        int row = tree.getClosestRowForLocation(e.getX(), e.getY());
        tree.setSelectionRow(row);
        DefaultActionGroup menu = menuFor(tree, project, toolbarPanel, e, clickedNode);
        if (menu != null) showPopup(e, tree, menu);
    }

    /** The context menu of {@code clickedNode}, or null when it has none. Keyboard shortcuts use it without showing it. */
    public static @org.jetbrains.annotations.Nullable DefaultActionGroup menuFor(Tree tree, Project project, JPanel toolbarPanel,
                                                                                @org.jetbrains.annotations.Nullable MouseEvent e, DefaultMutableTreeNode clickedNode) {
        Object userObject = clickedNode.getUserObject();
        if (userObject instanceof ConnectionNodeDescriptor) {
            return handleConnectionRightClick(project, toolbarPanel, e, clickedNode, (ConnectionNodeDescriptor) userObject, tree);
        } else if (userObject instanceof BucketNodeDescriptor) {
            return handleBucketRightClick(project, e, clickedNode, tree);
        } else if (userObject instanceof ScopeNodeDescriptor) {
            return handleScopeRightClick(project, e, clickedNode, tree);
        } else if (userObject instanceof CollectionsNodeDescriptor) {
            return handleCollectionSRightClick(project, e, clickedNode, tree);
        } else if (userObject instanceof CollectionNodeDescriptor) {
            return handleCollectionRightClick(project, e, clickedNode, (CollectionNodeDescriptor) userObject, tree);
        } else if (userObject instanceof FileNodeDescriptor) {
            return handleDocumentRightClick(project, e, clickedNode, (FileNodeDescriptor) userObject, tree);
        } else if (userObject instanceof SearchIndexNodeDescriptor) {
            return handleSearchIndexRightClick(project, e, clickedNode, (SearchIndexNodeDescriptor) userObject, tree);
        } else if (userObject instanceof IndexNodeDescriptor) {
            return handleIndexRightClick(project, e, clickedNode, (IndexNodeDescriptor) userObject, tree);
        } else if (userObject instanceof SchemaDataNodeDescriptor) {
            return handleSchemaDataNodeDescriptorClick(project, e, clickedNode, (SchemaDataNodeDescriptor) userObject, tree);
        }
        return null;
    }

    private static DefaultActionGroup handleSchemaDataNodeDescriptorClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, SchemaDataNodeDescriptor userObject, Tree tree) {

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        Map<String, String> map = RelationshipStorage.getInstance().getValue().getRelationships()
                                                     .get(ActiveCluster.getInstance().getId());

        if (map != null && map.containsKey(userObject.getPath())) {
            AnAction clearRelationShip = new AnAction("Clear Relationship") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    RelationshipStorage.getInstance().getValue().getRelationships()
                                       .get(ActiveCluster.getInstance().getId()).remove(userObject.getPath());

                    userObject.setReference(null);
                    tree.revalidate();
                }
            };
            actionGroup.add(clearRelationShip);
        } else {
            AnAction relationShipDialog = new AnAction("Add Relationship") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    RelationshipDialog dialog = new RelationshipDialog(userObject.getPath(), userObject, tree);
                    dialog.show();
                }
            };
            actionGroup.add(relationShipDialog);
        }


        return actionGroup;
    }

    private static DefaultActionGroup handleConnectionRightClick(Project project, JPanel toolBarPanel, MouseEvent e, DefaultMutableTreeNode clickedNode, ConnectionNodeDescriptor userObject, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        if (userObject.isActive()) {

            AnAction clusterOverview = new AnAction("Cluster Overview") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    ServerOverviewDialog overview = new ServerOverviewDialog(true);
                    overview.show();
                }
            };
            actionGroup.add(clusterOverview);
            actionGroup.addSeparator();

            if (!ActiveCluster.getInstance().isReadOnlyMode() && !ActiveCluster.getInstance().isCapella()) {
                // Add "Create New Bucket" option
                AnAction createNewBucketItem = new AnAction("Create New Bucket") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        try {
                            new NewBucketCreationDialog(project).show((cancelled, err) -> {
                                TreePath treePath = new TreePath(clickedNode.getPath());
                                tree.collapsePath(treePath);
                                tree.expandPath(treePath);
                                tree.invalidate();
                            });
                        } catch (Exception ex) {
                            Log.error("Bucket Creation failed ", ex);
                        }
                    }
                };
                actionGroup.add(createNewBucketItem);
            }

            AnAction refreshBuckets = new AnAction("Refresh Buckets") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    TreePath treePath = new TreePath(clickedNode.getPath());
                    tree.collapsePath(treePath);
                    tree.expandPath(treePath);
                }
            };
            actionGroup.add(refreshBuckets);
            actionGroup.addSeparator();

            AnAction menuItem = new AnAction("Disconnect") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    TreeActionHandler.disconnectFromCluster(clickedNode, userObject, tree);
                }
            };
            actionGroup.add(menuItem);

            DefaultActionGroup tools = new DefaultActionGroup("Tools", true);

            // Tool entries are always listed; each one disables itself with a reason while its tool is
            // still downloading or the connection cannot use it.
            AnAction pillowFight = new PasswordConnectionAction("Pillow Fight", CBTools.Type.CBC_PILLOW_FIGHT, true) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    PillowFightDialog pillowFightDialog = new PillowFightDialog(project);
                    pillowFightDialog.show();
                }
            };
            tools.add(pillowFight);
            tools.addSeparator();

            AnAction ddlExport = new AnAction("DDL Export") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    DDLExportDialog dialog = new DDLExportDialog();
                    dialog.show();
                }
            };
            tools.add(ddlExport);

            AnAction cbexport = new PasswordConnectionAction("Data Export", CBTools.Type.CB_EXPORT, false) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    ExportDialog dialog = new ExportDialog();
                    dialog.show();
                }
            };
            tools.add(cbexport);

            AnAction cbimport = new PasswordConnectionAction("Data Import", CBTools.Type.CB_IMPORT, true) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    ImportDialog importDialog = new ImportDialog(project);
                    importDialog.show();
                }
            };
            tools.add(cbimport);
            tools.addSeparator();

            // Migration Dialog
            AnAction cbMigration = new PasswordConnectionAction("MongoDB to Couchbase Migration", CBTools.Type.CBMIGRATE, true) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    MigrationDialog dialog = new MigrationDialog(project);
                    dialog.show();
                }
            };

            AnAction dynamo = new PasswordConnectionAction("DynamoDB to Couchbase Migration", CBTools.Type.CBMIGRATE, true) {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    DynamoMigrationDialog dialog = new DynamoMigrationDialog(project);
                    dialog.show();
                }
            };
            tools.add(cbMigration);
            tools.add(dynamo);
            tools.addSeparator();

            DefaultActionGroup settings = new DefaultActionGroup("Settings", true);
            DefaultActionGroup colors = new DefaultActionGroup("Connection Colors", true);

            AnAction colorAction = new AnAction("Set Connection Color") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    Color initialColor = JBColor.RED;
                    ColorPickerListener colorPickerListener = new ColorPickerListener() {
                        @Override
                        public void colorChanged(Color newColor) {
                            if (newColor != null) {
                                Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, newColor);
                                Border margin = BorderFactory.createEmptyBorder(0, 0, 1, 0); // Top, left, bottom, right
                                // margins
                                Border compound = BorderFactory.createCompoundBorder(margin, line);
                                toolBarPanel.setBorder(compound);
                                toolBarPanel.revalidate();
                                ActiveCluster.getInstance().setColor(newColor);
                            }
                        }

                        @Override
                        public void closed(@Nullable Color color) {
                            // do nothing
                        }
                    };

                    ColorPicker.showDialog(tree, "Choose a Color for This Connection", initialColor, true,
                                           List.of(colorPickerListener), true);
                }
            };
            colors.add(colorAction);

            if (!ActiveCluster.getInstance().isReadOnlyMode()) {
                AnAction readOnlyMode = new AnAction("Enable Read-Only Mode") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        ActiveCluster.getInstance().setReadOnlyMode(true);
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showWarningDialog(
                                "<html>The " + "<strong>Read Only Mode</strong> is a simple guardrail in the plugin to avoid " + "unwanted changes in sensible environments. Please note that this is a <strong>best " + "effort</strong> approach. For true read-only approach, connect to the cluster using " + "read-only credentials.</html>",
                                "Couchbase Plugin Warning"));

                    }
                };
                settings.add(readOnlyMode);
            } else {
                AnAction readWriteMode = new AnAction("Disable Read-Only Mode") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        ActiveCluster.getInstance().setReadOnlyMode(false);
                    }
                };
                settings.add(readWriteMode);
            }

            AnAction clearCache = new AnAction("Clear Schema Cache") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    ActiveCluster cluster = ActiveCluster.getInstance();
                    if (cluster != null) {
                        cluster.clearSchema();
                    }
                }
            };
            settings.add(clearCache);

            if (ActiveCluster.getInstance().getColor() != null) {
                AnAction clearConnectionColor = new AnAction("Clear") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        toolBarPanel.setBorder(JBUI.Borders.empty());
                        toolBarPanel.revalidate();
                        ActiveCluster.getInstance().setColor(null);
                    }
                };
                colors.add(clearConnectionColor);
            }

            DefaultActionGroup relMapping = new DefaultActionGroup("Relationship Mapping", true);
            AnAction exportRel = new AnAction("Export Relationships") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    RelationshipSettingsManager.showExportDialog(project);
                }
            };
            relMapping.add(exportRel);

            AnAction importRel = new AnAction("Import Relationships") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    RelationshipSettingsManager.showImportDialog(project, tree);
                }
            };
            relMapping.add(importRel);


            settings.add(colors);
            settings.add(relMapping);
            actionGroup.add(tools);
            actionGroup.add(settings);
        } else {
            AnAction menuItem = new AnAction("Connect") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    TreeActionHandler.connectToCluster(project, userObject.getSavedCluster(), tree, toolBarPanel);
                }
            };
            actionGroup.add(menuItem);
        }

        actionGroup.addSeparator();
        AnAction editConnection = new AnAction("Edit Connection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                NewConnectionDialog dialog = new NewConnectionDialog(project, tree, userObject.getSavedCluster(),
                                                                     clickedNode);
                dialog.show();
            }
        };

        actionGroup.add(editConnection);

        AnAction deleteConnection = new AnAction("Delete Connection") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                int choice = DeleteConfirmation.ask(project,
                        "Delete the connection '" + userObject.getText() + "'? Its saved password, favorites and cached schema are removed too.",
                        "Delete Connection", "Delete");
                if (choice == Messages.YES) {
                    TreeActionHandler.deleteConnection(clickedNode, userObject, tree);
                }
            }
        };
        actionGroup.add(deleteConnection);

        return actionGroup;
    }

    private static DefaultActionGroup handleBucketRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        AnAction menuItem = new AnAction("Refresh Scopes") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                TreePath treePath = new TreePath(clickedNode.getPath());
                tree.collapsePath(treePath);
                tree.expandPath(treePath);
            }
        };
        actionGroup.add(menuItem);

        String overviewBucket = ((BucketNodeDescriptor) clickedNode.getUserObject()).getText();
        actionGroup.add(new AnAction("Bucket Overview") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                new ServerOverviewDialog(true, overviewBucket).show();
            }
        });

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            // Add "Add New Scope" option
            AnAction addNewScopeItem = new AnAction("Add New Scope") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    try {

                        String bucketName = ((BucketNodeDescriptor) clickedNode.getUserObject()).getText();

                        NewScopeCreationDialog entityCreationDialog = new NewScopeCreationDialog(project, bucketName);
                        entityCreationDialog.show();

                        if (entityCreationDialog.isOK()) {
                            String scopeName = entityCreationDialog.getEntityName();
                            DataLoader.listScopes(clickedNode, tree);
                        }
                    } catch (Exception ex) {
                        Log.error("Scope creation failed ", ex);
                    }
                }
            };
            actionGroup.add(addNewScopeItem);

            // Add "Delete Bucket" option
            ClusterUiTask.Connection original = ClusterUiTask.capture(ActiveCluster.getInstance());
            AnAction deleteBucketItem = new AnAction("Delete Bucket") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    try {

                        String bucketName = ((BucketNodeDescriptor) clickedNode.getUserObject()).getText();
                        if (!requireMenuTarget(project, original, tree, clickedNode, "Delete Bucket")) return;

                        // Show confirmation dialog before deleting bucket
                        int result = DeleteConfirmation.ask(project, "Are you sure you want to delete the bucket " + bucketName + "?", "Delete Bucket", "Delete");
                        if (result != Messages.YES || !requireMenuTarget(project, original, tree, clickedNode, "Delete Bucket")) {
                            return;
                        }

                        runMutation(project, original, "Deleting bucket", sdk -> sdk.buckets().dropBucket(bucketName), () -> {
                            if (clickedNode.getParent() instanceof DefaultMutableTreeNode connection && connection.getRoot() == tree.getModel().getRoot())
                                DataLoader.listBuckets(connection, tree);
                        });
                    } catch (Exception ex) {
                        ApplicationManager.getApplication().invokeLater(() -> Messages.showInfoMessage(
                                "An error " + "occurred while creating the bucket. Check the Log tab for more.",
                                "Couchbase Plugin " + "Error"));
                        Log.error("Bucket deletion failed ", ex);
                    }
                }
            };
            actionGroup.add(deleteBucketItem);
        }

        return actionGroup;
    }


    private static DefaultActionGroup handleCollectionSRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        CollectionsNodeDescriptor cols = (CollectionsNodeDescriptor) clickedNode.getUserObject();
        String bucketName = cols.getBucket();
        String scopeName = cols.getScope();

        actionGroup.add(collectionSearchAction(project, tree, clickedNode));

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            // Add "Add New Collection" option
            AnAction addNewCollectionItem = new AnAction("Add New Collection") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {

                    NewCollectionDialog dialog = new NewCollectionDialog(project, bucketName, scopeName, clickedNode,
                                                                         tree);
                    dialog.show();
                }
            };

            actionGroup.add(addNewCollectionItem);
        }
        return actionGroup;
    }

    private static AnAction collectionSearchAction(Project project, Tree tree, DefaultMutableTreeNode collectionsNode) {
        CollectionsNodeDescriptor cols = (CollectionsNodeDescriptor) collectionsNode.getUserObject();
        return new AnAction("Search Collection...") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                showCollectionSearch(project, tree, collectionsNode);
            }
        };
    }

    public static void showCollectionSearch(Project project, Tree tree, DefaultMutableTreeNode collectionsNode) {
        CollectionsNodeDescriptor cols = (CollectionsNodeDescriptor) collectionsNode.getUserObject();
        var original = ClusterUiTask.capture(ActiveCluster.getInstance());
        new CollectionSearchDialog(project, CollectionSources.forScope(cols.getBucket(), cols.getScope()), name -> {
                    if (!original.isCurrent() || collectionsNode.getRoot() != tree.getModel().getRoot()) return;
                    cols.addSearchAdded(name);
                    TreePath path = new TreePath(collectionsNode.getPath());
                    if (!tree.isExpanded(path)) {
                        tree.expandPath(path);
                    } else {
                        DataLoader.listCollections(collectionsNode, tree,
                                Math.max(cols.getPagedLoaded(), CollectionListing.PAGE_SIZE));
                    }
                }).show();
    }

    private static DefaultActionGroup handleScopeRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        ScopeNodeDescriptor scope = (ScopeNodeDescriptor) clickedNode.getUserObject();
        String bucketName = scope.getBucket();
        String scopeName = scope.getText();

        for (int i = 0; i < clickedNode.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) clickedNode.getChildAt(i);
            if (child.getUserObject() instanceof CollectionsNodeDescriptor) {
                actionGroup.add(collectionSearchAction(project, tree, child));
                break;
            }
        }

        AnAction refreshCollections = new AnAction("Refresh Collections") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                TreePath treePath = new TreePath(clickedNode.getPath());
                tree.collapsePath(treePath);
                tree.expandPath(treePath);
            }
        };
        actionGroup.add(refreshCollections);

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            // Add "Add New Collection" option
            AnAction addNewCollectionItem = new AnAction("Add New Collection") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {

                    NewCollectionDialog dialog = new NewCollectionDialog(project, bucketName, scopeName, clickedNode,
                                                                         tree);
                    dialog.show();
                }
            };

            actionGroup.add(addNewCollectionItem);

            if (!"_default".equals(scope.getText())) {
                actionGroup.addSeparator();
                // Add "Delete Scope" option
                ClusterUiTask.Connection original = ClusterUiTask.capture(ActiveCluster.getInstance());
                AnAction deleteScopeItem = new AnAction("Delete Scope") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        try {
                            if (!requireMenuTarget(project, original, tree, clickedNode, "Delete Scope")) return;

                            // Show confirmation dialog before deleting scope
                            int result = DeleteConfirmation.ask(project, "Are you sure you want to delete the scope " + scopeName + "?", "Delete Scope", "Delete");
                            if (result != Messages.YES || !requireMenuTarget(project, original, tree, clickedNode, "Delete Scope")) {
                                return;
                            }

                            runMutation(project, original, "Deleting scope", sdk -> sdk.bucket(bucketName).collections().dropScope(scopeName), () -> {
                                if (clickedNode.getParent() instanceof DefaultMutableTreeNode bucket && bucket.getRoot() == tree.getModel().getRoot())
                                    DataLoader.listScopes(bucket, tree);
                            });
                        } catch (Exception ex) {
                            Log.error("Scope deletion failed ", ex);
                        }
                    }
                };
                actionGroup.add(deleteScopeItem);

            }
        }

        actionGroup.addSeparator();

        // Listed even while cbimport/cbexport are still downloading; the entries disable themselves with a reason.
        AnAction simpleImport = new PasswordConnectionAction("Simple Import", CBTools.Type.CB_IMPORT, true) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("json");
                VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
                if (file != null) { // cancelling the chooser is not an error
                    CBImport.simpleScopeImport(scope.getBucket(), scope.getText(), file.getPath(), project);
                }
            }
        };
        actionGroup.add(simpleImport);

        AnAction simpleExport = new PasswordConnectionAction("Simple Export", CBTools.Type.CB_EXPORT, false) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FileSaverDescriptor fsd = new FileSaverDescriptor("Simple Scope Export",
                                                                  "Choose where you want to " + "save the file:");
                VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project)
                                                               .save(("cb_export-" + scope.getText() + "-" + TimeUtils.getCurrentDateTime() + ".json"));
                if (wrapper != null) {
                    File file = wrapper.getFile();
                    CBExport.simpleScopeExport(scope.getBucket(), scope.getText(), file.getAbsolutePath());
                }
            }
        };
        actionGroup.add(simpleExport);


        if (ActiveCluster.getInstance().hasQueryService()) {
            actionGroup.addSeparator();
            AnAction erDiagram = new AnAction("View ER Diagram") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    try {
                        MermaidERDiagramDialog dialog = new MermaidERDiagramDialog(bucketName, scopeName);
                        dialog.show();
                    } catch (Exception ex) {
                        Log.error(
                                "Failed to load the ER Diagram. Double check if the JRE that you are running your " + "IDE has support for JCEF." + " https://plugins.jetbrains.com/docs/intellij/jcef" + ".html#enabling-jcef",
                                ex);

                    }
                }
            };
            actionGroup.add(erDiagram);
        }


        return actionGroup;
    }

    private static DefaultActionGroup handleDocumentRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, FileNodeDescriptor col, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        String bucket = col.getBucket();
        String scope = col.getScope();
        String collection = col.getCollection();
        String docId = col.getId();

        actionGroup.add(new AnAction("Copy Document ID") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                TreeCopy.toClipboard(docId);
            }
        });
        actionGroup.add(new AnAction("Copy Document", "Remembers the document so it can be pasted into a collection", null) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                DocumentClipboard.copy(new DocumentClipboard.CopiedDocument(ActiveCluster.getInstance().getId(),
                        bucket, scope, collection, docId));
            }
        });
        actionGroup.addSeparator();

        AnAction viewMetaData = new AnAction("View Metadata") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                ActiveCluster active = ActiveCluster.getInstance();
                com.couchbase.intellij.database.ClusterUiTask.submit(project, active, "Reading document metadata", connection ->
                        connection.active().hasQueryService() ? DataLoader.getDocMetadata(connection, bucket, scope, collection, docId)
                                : CouchbaseRestAPI.getMetaDocument(bucket, scope, collection, docId), metadata -> {
                    if (metadata == null) return;
                    VirtualFile file = new LightVirtualFile("(read-only) " + docId + "_meta.json",
                            FileTypeManager.getInstance().getFileTypeByExtension("json"), metadata);
                    DocumentFormatter.formatFile(project, file);
                    EditorOpening.openFile(project, file);
                }, error -> Messages.showErrorDialog(project, "Could not read document metadata. " + error.getMessage(), "Document Metadata"));

            }
        };
        actionGroup.add(viewMetaData);
        actionGroup.addSeparator();

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {

            ClusterUiTask.Connection original = ClusterUiTask.capture(ActiveCluster.getInstance());
            AnAction deleteDoc = new AnAction("Delete Document") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    if (!requireMenuTarget(project, original, tree, clickedNode, "Delete Document")) return;
                    int result = DeleteConfirmation.askForDocument(project, col.getId());
                    if (result != Messages.YES || !requireMenuTarget(project, original, tree, clickedNode, "Delete Document")) {
                        return;
                    }

                    runMutation(project, original, "Deleting document", sdk ->
                            sdk.bucket(bucket).scope(scope).collection(collection).remove(col.getId()), () -> {
                        if (col.getVirtualFile() != null) FileEditorManager.getInstance(project).closeFile(col.getVirtualFile());
                        if (clickedNode.getParent() != null && clickedNode.getRoot() == tree.getModel().getRoot())
                            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);
                    });

                }
            };
            actionGroup.add(deleteDoc);
        }

        return actionGroup;
    }

    private static DefaultActionGroup handleIndexRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, IndexNodeDescriptor idx, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction viewIdxStatsAction = new AnAction("View Stats") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                IndexOverviewDialog dialog = new IndexOverviewDialog(project, idx.getBucket(), idx.getScope(),
                                                                     idx.getCollection(), idx.getText().substring(0, idx
                        .getText().lastIndexOf('.')));
                dialog.show();
            }
        };
        actionGroup.add(viewIdxStatsAction);

        if (idx.getVirtualFile() instanceof LightVirtualFile definition) {
            actionGroup.add(new AnAction("Copy Definition") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    TreeCopy.toClipboard(definition.getContent().toString());
                }
            });
        }

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            ClusterUiTask.Connection original = ClusterUiTask.capture(ActiveCluster.getInstance());
            String indexName = idx.getText().substring(0, idx.getText().lastIndexOf('.'));
            String keyspace = TreeCopy.qualifiedName(idx.getBucket(), idx.getScope(), idx.getCollection());
            actionGroup.addSeparator();
            actionGroup.add(new AnAction("Drop Index") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    if (!requireMenuTarget(project, original, tree, clickedNode, "Drop Index")) return;
                    int result = DeleteConfirmation.ask(project,
                            "<html>Drop the index <strong>" + indexName + "</strong> on " + keyspace + "?</html>", "Drop Index", "Drop");
                    if (result != Messages.YES || !requireMenuTarget(project, original, tree, clickedNode, "Drop Index")) return;
                    runMutation(project, original, "Dropping index", sdk -> dropIndex(
                            sdk.bucket(idx.getBucket()).scope(idx.getScope()).collection(idx.getCollection()).queryIndexes(), indexName), () -> {
                        if (clickedNode.getParent() instanceof DefaultMutableTreeNode parent
                                && parent.getUserObject() instanceof IndexesNodeDescriptor && parent.getRoot() == tree.getModel().getRoot())
                            DataLoader.listIndexes(parent, tree);
                    });
                }
            });
        }

        return actionGroup;
    }

    private static void showPopup(MouseEvent e, Tree tree, DefaultActionGroup actionGroup) {
        ActionManager.getInstance().createActionPopupMenu("CouchbaseExplorer", actionGroup)
                .getComponent().show(tree, e.getX(), e.getY());
    }

    private static DefaultActionGroup handleCollectionRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, CollectionNodeDescriptor col, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        AnAction openDocument = new AnAction("Open Document") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                OpenDocumentDialog dialog = new OpenDocumentDialog(false, project, tree, col.getBucket(),
                                                                   col.getScope(), col.getText());
                dialog.show();
            }
        };
        actionGroup.add(openDocument);

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            AnAction createDocument = new AnAction("Create Document") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    OpenDocumentDialog dialog = new OpenDocumentDialog(true, project, tree, col.getBucket(),
                                                                       col.getScope(), col.getText());
                    dialog.show();
                }
            };
            actionGroup.add(createDocument);

            ClusterUiTask.Connection pasteConnection = ClusterUiTask.capture(ActiveCluster.getInstance());
            String target = TreeCopy.qualifiedName(col.getBucket(), col.getScope(), col.getText());
            AnAction pasteDocument = new AnAction("Paste Document") {
                @Override
                public void update(@NotNull AnActionEvent e) {
                    DocumentClipboard.CopiedDocument copied = DocumentClipboard.get();
                    e.getPresentation().setVisible(copied != null);
                    if (copied == null) return;
                    boolean sameConnection = Objects.equals(copied.connectionId(), ActiveCluster.getInstance().getId());
                    e.getPresentation().setEnabled(sameConnection);
                    e.getPresentation().setDescription(sameConnection
                            ? "Paste '" + copied.id() + "' into " + target
                            : "The copied document belongs to another connection.");
                }

                @Override
                public @NotNull ActionUpdateThread getActionUpdateThread() {
                    return ActionUpdateThread.BGT;
                }

                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    DocumentClipboard.CopiedDocument copied = DocumentClipboard.get();
                    if (copied == null || !requireMenuTarget(project, pasteConnection, tree, clickedNode, "Paste Document")) return;
                    ClusterUiTask.submit(project, pasteConnection, "Pasting document " + copied.id(), connection -> {
                        connection.requireWritable();
                        com.couchbase.client.java.Cluster sdk = connection.sdk();
                        String content = DocumentPaste.readJson(
                                sdk.bucket(copied.bucket()).scope(copied.scope()).collection(copied.collection()), copied.id());
                        return DocumentPaste.paste(new DocumentPaste.SdkTarget(
                                sdk.bucket(col.getBucket()).scope(col.getScope()).collection(col.getText())),
                                copied.id(), content, target, new DocumentPaste.DialogPrompter(project));
                    }, written -> {
                        if (written == null) return;
                        if (tree.isExpanded(new TreePath(clickedNode.getPath()))) DataLoader.listDocuments(clickedNode, tree, 0);
                        com.intellij.notification.NotificationGroupManager.getInstance().getNotificationGroup("Couchbase document saves")
                                .createNotification("Pasted '" + written + "' into " + target, com.intellij.notification.NotificationType.INFORMATION)
                                .notify(project);
                    }, error -> Messages.showErrorDialog(project, "Could not paste the document. " + error.getMessage(), "Paste Document"));
                }
            };
            actionGroup.add(pasteDocument);
        }

        actionGroup.add(new AnAction("Copy Qualified Name", "Copies `bucket`.`scope`.`collection`", null) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                TreeCopy.toClipboard(TreeCopy.qualifiedName(col.getBucket(), col.getScope(), col.getText()));
            }
        });

        actionGroup.addSeparator();
        String connectionId = ActiveCluster.getInstance().getId();
        FavoriteCollections favorites = FavoriteCollectionsStorage.getInstance().getValue();
        boolean isFavorite = favorites.isFavorite(connectionId, col.getBucket(), col.getScope(), col.getText());
        AnAction toggleFavorite = new AnAction(isFavorite ? "Remove from Favorites" : "Add to Favorites") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CollectionTreeActions.toggleFavorite(tree, clickedNode);
            }
        };
        actionGroup.add(toggleFavorite);
        actionGroup.addSeparator();
        String filter = "Add Document Filter";
        boolean hasDeleteFilter = false;
        if (col.getQueryFilter() != null) {
            filter = "Edit Document Filter";
            hasDeleteFilter = true;
        }
        AnAction menuItem = new AnAction(filter) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                CollectionNodeDescriptor desc = new CollectionNodeDescriptor(col.getText(), null, col.getBucket(),
                                                                             col.getScope(), null);
                String field = DataLoader.getIndexedField(desc);

                DocumentFilterDialog dialog = new DocumentFilterDialog(tree, clickedNode, col.getBucket(),
                                                                       col.getScope(), col.getText(), field != null);
                dialog.show();
            }
        };
        actionGroup.add(menuItem);

        if (hasDeleteFilter) {
            AnAction clearDocFilter = new AnAction("Clear Document Filter") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    QueryFiltersStorage.getInstance().getValue()
                                       .saveQueryFilter(ActiveCluster.getInstance().getId(), col.getBucket(),
                                                        col.getScope(), col.getText(), null);

                    col.setQueryFilter(null);
                    TreePath treePath = new TreePath(clickedNode.getPath());
                    tree.collapsePath(treePath);
                    tree.expandPath(treePath);
                }
            };
            actionGroup.add(clearDocFilter);
        }


        actionGroup.addSeparator();
        AnAction refreshDocuments = new AnAction("Refresh Documents") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                InferHelper.invalidateInferCacheIfOlder(col.getBucket(), col.getScope(), col.getText(),
                                                        TimeUnit.MINUTES.toMillis(1));
                DataLoader.listDocuments(clickedNode, tree, 0);
            }
        };
        actionGroup.add(refreshDocuments);

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {

            if (!"_default".equals(col.getScope()) || (!"_default".equals(col.getText()) && "_default".equals(
                    col.getScope()))) {
                // Add "Delete Collection" option
                actionGroup.addSeparator();
                ClusterUiTask.Connection original = ClusterUiTask.capture(ActiveCluster.getInstance());
                AnAction deleteCollectionItem = new AnAction("Delete Collection") {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e) {
                        try {
                            if (!requireMenuTarget(project, original, tree, clickedNode, "Delete Collection")) return;

                            int result = DeleteConfirmation.ask(project, "Are you sure you want to delete the collection " + col.getText() + "?",
                                    "Delete Collection", "Delete");
                            if (result != Messages.YES || !requireMenuTarget(project, original, tree, clickedNode, "Delete Collection")) {
                                return;
                            }

                            runMutation(project, original, "Deleting collection", sdk ->
                                    sdk.bucket(col.getBucket()).collections().dropCollection(col.getScope(), col.getText()), () -> {
                                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) clickedNode.getParent();
                                if (parent != null && parent.getRoot() == tree.getModel().getRoot())
                                    DataLoader.listCollections(parent, tree);
                            });
                        } catch (Exception ex) {
                            Log.error("Collection deletion failed ", ex);
                        }
                    }
                };
                actionGroup.add(deleteCollectionItem);
            }
        }

        // Listed even while cbimport/cbexport are still downloading; the entries disable themselves with a reason.
        actionGroup.addSeparator();

        AnAction simpleImport = new PasswordConnectionAction("Simple Import", CBTools.Type.CB_IMPORT, true) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor(
                        "json");
                VirtualFile file = FileChooser.chooseFile(descriptor, project, null);
                if (file != null) { // cancelling the chooser is not an error
                    CBImport.simpleCollectionImport(col.getBucket(), col.getScope(), col.getText(),
                                                    file.getPath(), null);
                }
            }
        };
        actionGroup.add(simpleImport);

        AnAction simpleExport = new PasswordConnectionAction("Simple Export", CBTools.Type.CB_EXPORT, false) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                FileSaverDescriptor fsd = new FileSaverDescriptor("Simple Collection Export",
                                                                  "Choose where you " + "want to save the file:");
                VirtualFileWrapper wrapper = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project)
                                                               .save(("cb_export-" + col.getScope() + "_" + col.getText() + "-" + TimeUtils.getCurrentDateTime() + ".json"));
                if (wrapper != null) {
                    File file = wrapper.getFile();
                    CBExport.simpleCollectionExport(col.getBucket(), col.getScope(), col.getText(),
                                                    file.getAbsolutePath(), null);
                }
            }
        };
        actionGroup.add(simpleExport);

        return actionGroup;
    }


    private static DefaultActionGroup handleSearchIndexRightClick(Project project, MouseEvent e, DefaultMutableTreeNode clickedNode, SearchIndexNodeDescriptor idx, Tree tree) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();


        AnAction newWorkbench = new AnAction("New Search Workbench") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Project project = e.getProject();
                        searchWorkbenchCounter++;
                        String fileName = "search" + searchWorkbenchCounter + ".cbs.json";
                        String fileContent = """
                                {
                                  "query": {
                                    "query": "your_query_here"
                                  },
                                  "fields": ["*"]
                                }
                                """;
                        VirtualFile virtualFile = new LightVirtualFile(fileName, FileTypeManager.getInstance()
                                                                                                .getFileTypeByExtension(
                                                                                                        "cbs.json"),
                                                                       fileContent);
                        virtualFile.putUserData(VirtualFileKeys.BUCKET, idx.getBucket());
                        virtualFile.putUserData(VirtualFileKeys.SEARCH_INDEX, idx.getIndexName());
                        EditorOpening.openFile(project, virtualFile);
                    } catch (Exception ex) {
                        Log.error(ex);
                        ex.printStackTrace();
                    }
                });
            }
        };

        actionGroup.add(newWorkbench);

        if (!ActiveCluster.getInstance().isReadOnlyMode()) {
            AnAction deleteDoc = new AnAction("Delete Index") {
                @Override
                public void actionPerformed(@NotNull AnActionEvent e) {
                    int result = DeleteConfirmation.ask(project, "<html>Are you sure you want to delete the index <strong>" + idx.getIndexName() + "</strong>?</html>",
                            "Delete Index", "Delete");
                    if (result != Messages.YES) {
                        return;
                    }

                    try {
                        SearchIndexManager idxManager = ActiveCluster.getInstance().get().searchIndexes();
                        idxManager.dropIndex(idx.getIndexName());

                        if (idx.getVirtualFile() != null) {
                            try {
                                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                                fileEditorManager.closeFile(idx.getVirtualFile());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.debug("Could not close the file", ex);
                            }
                        }

                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) clickedNode.getParent();
                        if (parentNode != null) {
                            ((DefaultTreeModel) tree.getModel()).removeNodeFromParent(clickedNode);
                        }
                        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parentNode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Log.error("An error occurred while trying to delete the index " + idx.getIndexName(), ex);
                        Messages.showErrorDialog("Could not delete the index. Please check the logs for more.",
                                                 "Couchbase Plugin Error");
                    }

                }
            };
            actionGroup.add(deleteDoc);
        }

        return actionGroup;
    }
}
