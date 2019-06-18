package com.bootdo.common.utils;


import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/*
 * 解压war生成class文件
 * */
public class ZIPUtil {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) throws Exception {
        String fileName="D:\\precisiontest-2.0.0.war";
        uncompress(fileName);
    }

    public static void uncompress(String inFileName) throws Exception{
        if(inFileName == null){
            return;
        }
        String inFileName2 = inFileName.replace("\\", "/");
        int index = inFileName2.lastIndexOf(".");
        String outputDir = inFileName2.substring(0,index);
        File outputFile = new File(outputDir);
        if(!outputFile.exists()){
            outputFile.mkdirs();
        }
        outputFile = null;

        File file = new File(inFileName2);
        ZipFile zipFile = new ZipFile(file);
        Enumeration<?> en = zipFile.entries();
        while(en.hasMoreElements()){
            ZipEntry zipEntry = (ZipEntry) en.nextElement();
            System.out.println();
            if(zipEntry.isDirectory()){
                File file2 = new File(outputDir + File.separator + zipEntry.getName());
                if(!file2.exists()){
                    file2.mkdirs();
                }
                continue;
            }else{
                String name = zipEntry.getName();
                index = name.lastIndexOf("/");
                if(index >= 0){
                    name = name.substring(0,index);
                    File file2 = new File(outputDir + File.separator + name);
                    if(!file2.exists()){
                        file2.mkdirs();
                    }
                }
            }
            InputStream in = zipFile.getInputStream(zipEntry);
            OutputStream os = new FileOutputStream(outputDir + File.separator + zipEntry.getName());
            copyTo(in, os, zipEntry.getSize());
            in.close();
            os.close();
        }
        zipFile.close();
    }

    private static void copyTo(InputStream is,OutputStream os,long size) throws Exception{
        if(size <= 0){
            return;
        }
        int readSize = -1;
        int hasRead = 0;
        byte[] data = new byte[1024];
        while(hasRead < size){
            readSize = is.read(data);
            os.write(data, 0,readSize);
            os.flush();
            hasRead += readSize;
        }
    }
}
