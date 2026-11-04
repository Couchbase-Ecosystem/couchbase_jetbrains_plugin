package com.couchbase.intellij.listener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

public class ToolArchiveExtractionTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private static final byte[] SCRIPT = "#!/bin/sh\necho cbimport\n".getBytes(StandardCharsets.UTF_8);

    /** A minimal ustar writer, so the test does not depend on the commons-compress version on the classpath. */
    private static void tarEntry(ByteArrayOutputStream out, String name, byte[] content, boolean directory) throws IOException {
        byte[] header = new byte[512];
        byte[] nameBytes = name.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(nameBytes, 0, header, 0, nameBytes.length);
        put(header, 100, "0000755");
        put(header, 108, "0000000");
        put(header, 116, "0000000");
        put(header, 124, String.format("%011o", content.length));
        put(header, 136, "00000000000");
        Arrays.fill(header, 148, 156, (byte) ' ');
        header[156] = (byte) (directory ? '5' : '0');
        put(header, 257, "ustar");
        put(header, 263, "00");
        int sum = 0;
        for (byte b : header) sum += b & 0xff;
        put(header, 148, String.format("%06o", sum));
        header[154] = 0;
        header[155] = ' ';
        out.write(header);
        out.write(content);
        int padding = (512 - content.length % 512) % 512;
        out.write(new byte[padding]);
    }

    private static void put(byte[] header, int offset, String text) {
        byte[] bytes = text.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, header, offset, bytes.length);
    }

    @Test
    public void linuxToolTarballsAreUnpackedWithTheirBinDirectory() throws Exception {
        ByteArrayOutputStream tar = new ByteArrayOutputStream();
        tarEntry(tar, "bin/", new byte[0], true);
        tarEntry(tar, "bin/cbimport", SCRIPT, false);
        tar.write(new byte[1024]);
        File archive = folder.newFile("couchbase-server-tools_7.6.0-linux_x86_64.tar.gz");
        try (GZIPOutputStream gz = new GZIPOutputStream(new FileOutputStream(archive))) {
            gz.write(tar.toByteArray());
        }
        File target = folder.newFolder("cbimport_export");

        DependenciesDownloader.extractArchive(archive, target);

        File binary = new File(target, "bin/cbimport");
        assertTrue("bin/cbimport must exist after extraction", binary.isFile());
        assertArrayEquals(SCRIPT, Files.readAllBytes(binary.toPath()));
    }

    @Test
    public void zipArchivesStillUnpack() throws Exception {
        File archive = folder.newFile("cbmigrate_0.1.0_linux_amd64.zip");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archive))) {
            zip.putNextEntry(new ZipEntry("cbmigrate"));
            zip.write(SCRIPT);
            zip.closeEntry();
        }
        File target = folder.newFolder("cbmigrate");

        DependenciesDownloader.extractArchive(archive, target);

        assertTrue(new File(target, "cbmigrate").isFile());
    }
}
