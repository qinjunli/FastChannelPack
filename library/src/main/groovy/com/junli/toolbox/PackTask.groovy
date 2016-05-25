package com.junli.toolbox

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class PackTask extends DefaultTask{

    ChannelConfig channelConfig

    //android buildVariant
    Object variant;


    @TaskAction
    def doPack() throws IOException{

        File originalFile = variant.outputs[0].outputFile

        channelConfig.getConfiguredChannelList().forEach{ channelStr ->
            //1. copy a apk file to destination path

            File newApkFile = new File(project.rootDir,
                    "build/apks/$channelStr-${originalFile.getName()}")
            if(newApkFile.exists()){
                newApkFile.delete()
            }

            File parent = newApkFile.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            if(!newApkFile.exists()){
                println("creating new file ${newApkFile.getAbsolutePath()}")
                newApkFile.createNewFile()
            }
            newApkFile << originalFile.bytes

            addFileToExistingZip(newApkFile, "META-INF/bmbchannel_$channelStr")

        }
    }


    public static ZipFile addFileToExistingZip(File zipFile, String tag) throws IOException{
        // get a temp file
        File tempFile = File.createTempFile(zipFile.getName(), null);
        // delete it, otherwise you cannot rename your existing zip to it.
        tempFile.delete();

        boolean renameOk=zipFile.renameTo(tempFile);
        if (!renameOk)
        {
            throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
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
            if(!toBeDeleted){
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
        out.putNextEntry(new ZipEntry(tag));
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
}