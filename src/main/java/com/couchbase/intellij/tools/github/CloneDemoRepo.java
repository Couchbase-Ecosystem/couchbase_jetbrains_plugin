package com.couchbase.intellij.tools.github;

import com.couchbase.intellij.tree.examples.Card;
import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import utils.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CloneDemoRepo {

    public static void cloneAndOpen(String url, String directoryPath) {
        ApplicationManager.getApplication().invokeLater(() -> {
            try {
                // Clone repository
                Git.cloneRepository()
                        .setURI(url)
                        .setDirectory(new File(directoryPath))
                        .call();

                VirtualFile vf = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(directoryPath));
                if (vf != null) {
                    VfsUtil.markDirtyAndRefresh(false, true, true, vf);
                }

                ApplicationManager.getApplication().invokeLater(() -> {
                    assert vf != null;
                    ProjectUtil.openOrImport(vf.getPath(), null, true);
                });

            } catch (GitAPIException e) {
                e.printStackTrace();
                Messages.showErrorDialog("An error occurred while trying to clone the repo.", "Couchbase Plugin Error");
            }
        });
    }

    public static List<Card> getExamples() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/springboot.png"),
                "Spring Boot & Java", "Build a simple REST API with Couchbase's Java SDK 3, Spring Boot and Swagger",
                "https://github.com/couchbase-examples/java-springboot-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/gin_gonic.png"),
                "Golang & Gin Gonic", "In this example you will learn how to create a simple REST API for CRUD operations with Couchbase, Golang and Gin Gonic",
                "https://github.com/couchbase-examples/golang-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/nextjs.png"),
                "NodeJS & NEXT.js", "Somple app that demonstrates how to build a basic front-end and backend CRUD app with Next.js and Couchbase",
                "https://github.com/couchbase-examples/nextjs-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/springdata.png"),
                "Java & Spring Data", "Experiment Couchbase's Spring Data SDK in this simple app CRUD with app with Java and Swagger",
                "https://github.com/couchbase-examples/java-springdata-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/nodejs.png"), "NodeJS", "Learn how to connect to a Couchbase cluster and how to create, read, update, and delete documents, and learn how to write simple parameterized SQL++ queries",
                "https://github.com/couchbase-examples/nodejs-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/flask.png"), "Python & Flask",
                "Try Couchbase with Python, Flask and Swagger in this simple CRUD app",
                "https://github.com/couchbase-examples/python-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/netlify.png"), "NodeJS & Netlify", "Learn how to buil a sample application for Couchbase Capella with Netlify",
                "https://github.com/couchbase-examples/netlify-tutorial.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/aspnet.png"), "ASP.NET", "Learn how to create a simple REST API to manage CRUP operations with Couchbase",
                "https://github.com/couchbase-examples/aspnet-quickstart.git"));
        cards.add(new Card(ImageLoader.loadImageIcon("/assets/frameworks/ottoman.png"), "NodeJS & Ottoman.js", "Ottoman.js brings together the best of NoSQL document databases and relational databases. Get started with it with this simple app",
                "https://github.com/couchbase-examples/ottomanjs-quickstart.git"));

        return cards;
    }
}
