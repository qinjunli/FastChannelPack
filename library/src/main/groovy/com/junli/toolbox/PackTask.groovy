package com.junli.toolbox

import groovy.util.logging.Log
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class PackTask extends DefaultTask {

    String channelPrefix

    ChannelConfig channelConfig

    //android buildVariant
    Object variant;


    @TaskAction
    def doPack() throws IOException {

        File originalFile = variant.outputs[0].outputFile

        channelConfig.getConfiguredChannelList().forEach { channelStr ->
            //1. copy a apk file to destination path

            def new_apk_name = "${variant.name}-$channelStr-${variant.versionName}-${variant.versionCode}.apk"
            def apk_target_path = "build/apks/";
//            File newApkFile = new File(project.rootDir,
//                    "build/apks/$channelStr-${originalFile.getName()}")

            File newApkFile = new File(project.rootDir,
                    apk_target_path + new_apk_name)
            if (newApkFile.exists()) {
//                println("delete exsiting file ${newApkFile.getAbsolutePath()}")
                newApkFile.delete()
            }

            File parent = newApkFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            if (!newApkFile.exists()) {
                println("Generate channel package: ${newApkFile.getAbsolutePath()}")
                newApkFile.createNewFile()
            }
            newApkFile << originalFile.bytes

//            addFileToExistingZip_3(newApkFile, "META-INF/${channelPrefix}_${channelStr}")
            addFileToExistingZip_3(newApkFile, "${channelPrefix}_${channelStr}")

            println("Done!")
        }
    }


    public static ZipFile addFileToExistingZip(File zipFile, String tag) throws IOException {
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk = zipFile.renameTo(tempFile);
        if (!renameOk) {
            throw new RuntimeException("could not rename the file " + zipFile.getAbsolutePath() + " to " + tempFile.getAbsolutePath());
        }
        byte[] buf = new byte[4096 * 1024];

        ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry entry = zin.getNextEntry();
        while (entry != null) {
            String name = entry.getName();
            boolean toBeDeleted = false;
            if (tag.indexOf(name) != -1) {
                toBeDeleted = true;
            }
            if (!toBeDeleted) {
                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(name));
                // Transfer bytes from the ZIP file to the output file
                int len;
                while ((len = zin.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            entry = zin.getNextEntry();
        }
        // Close the streams
        zin.close();
        // Compress the files
//        FileInputStream input = new FileInputStream(versionFile);
//        String fName = versionFile.getName();
        // Add ZIP entry to output stream.
        ZipEntry channelEntry = new ZipEntry(tag);
        channelEntry.setMethod(ZipEntry.STORED)
        out.putNextEntry(channelEntry);
        // Transfer bytes from the file to the ZIP file
//        int len;
//        while ((len = input.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
        out.write("42\n".getBytes());
        // Complete the entry
        out.closeEntry();
//        input.close();
        // Complete the ZIP file
        out.close();
        tempFile.delete();

        return new ZipFile(zipFile);
    }

    public static void addFileToExistingZip_3(File file, String channel) throws Exception {
        net.lingala.zip4j.core.ZipFile zipFile = new net.lingala.zip4j.core.ZipFile(file);

        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(Zip4jConstants.COMP_STORE);


        File folder = new File(file.getParentFile(), "META-INF");
        if(folder.exists()){
            folder.delete();
        }
        folder.mkdirs();
        File channelFile = new File(folder, channel);

        if (channelFile.exists()) {
            channelFile.deleteOnExit();
        }

        channelFile.createNewFile();
//        println("create new file " + channelFile.getName());
//        println("add new file: " + folder.getName());
        zipFile.addFolder(folder, parameters)
    }
}