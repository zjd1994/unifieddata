package com.bootdo.uml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * 动态加载war包或者jar包里面的class
 */
public class DynamicClassLoader extends ClassLoader{
    private static final String SUFFIX = ".class";
    public String path;//解压目录
    public String classRoot;//class文件根目录（WEB-INF或者BOOT-INF）

    public DynamicClassLoader(String path,String classRoot) {
        this.path = path;
        this.classRoot = classRoot;
    }

    public DynamicClassLoader(ClassLoader parent,String path){
        //super(parent);
        this.path = path;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        String classPath = getClassPath(path,className,classRoot);
        if(classPath != null){
            byte[] clazz = loadClazz(classPath);
            return defineClass(clazz, 0, clazz.length);
        }else{
            System.out.println("class is not found !");
            return null;
        }
    }

    public byte[] loadClazz(String classPath) {
        try {
            FileInputStream in = new FileInputStream(new File(classPath));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while((b = in.read()) != -1){
                baos.write(b);
            }
            in.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public String getClassPath(String path,String className,String classRoot){
        String resource = className.replace(".","\\");
        String classPath = path + classRoot+resource + SUFFIX;
        File classFile = new File(classPath);
        if(classFile.exists()){
            return classPath;
        }
        return null;
    }
}
