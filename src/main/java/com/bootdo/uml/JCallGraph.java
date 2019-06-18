/*
 * Copyright (c) 2011 - Georgios Gousios <gousiosg@gmail.com>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bootdo.uml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.utils.FileUtil;
import com.bootdo.common.utils.ZIPUtil;
import net.sf.json.JSONArray;
import org.apache.bcel.classfile.ClassParser;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructs a callgraph out of a JAR archive. Can combine multiple archives
 * into a single call graph.
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 * 
 */
public class JCallGraph {
    static Logger  logger = LoggerFactory.getLogger(JCallGraph.class);
    public static void main(String[] args) {



        ClassParser cp;
        
        try {
        	List<Map<String,Object>> classReferences = new ArrayList<>();//method集合
    		//根据路径获取war对象
            JarFile jar = new JarFile("C:\\Users\\EDZ\\Desktop\\shiro.war");//根据路径获取zip,jar,
            //FileUtil.unzip("C:\\Users\\EDZ\\Desktop\\precisiontest-2.0.0.war");
            //迭代jar目录对象的元素
            Enumeration<JarEntry> entries = jar.entries();
            String s = "";
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                //判断是否是目录，是目录返回true
                if (entry.isDirectory())
                    continue;

                if (!entry.getName().endsWith(".class")){
                    continue;
                }
                logger.info("entryName:"+entry.getName());
                String path = "C:\\Users\\EDZ\\Desktop\\shiro";
                String classRoot = "\\WEB-INF\\classes\\";
                DynamicClassLoader precisionTestClassLoader = null;
                if (entry.getName().startsWith("WEB-INF")){
                    cp = new ClassParser("C:\\Users\\EDZ\\Desktop\\shiro.war",entry.getName());
                    String className =  entry.getName();
                    className = className.replace("/",".");
                    className = className.replace("WEB-INF.classes.","");
                    className = className.substring(0,className.lastIndexOf(".class"));
                    logger.info("className:"+className);
                    ZIPUtil.uncompress("C:\\Users\\EDZ\\Desktop\\shiro.war");
                    precisionTestClassLoader = new DynamicClassLoader(path,classRoot);
                    //precisionTestClassLoader.loadClazz("C:\\Users\\EDZ\\Desktop\\shiro\\WEB-INF\\classes\\com\\example\\shiro\\LoginController.class");

                    Class<?> jvmClass = precisionTestClassLoader.findClass(className);
                    ClassVisitor visitor = new ClassVisitor(cp.parse(),jvmClass);
                    visitor.start();
                    classReferences.addAll(visitor.classReferences);
                    System.out.println("list:"+classReferences);
                }else if(entry.getName().startsWith("BOOT-INF")){
                    cp = new ClassParser("C:\\Users\\EDZ\\Desktop\\branch.war",entry.getName());
                    String className =  entry.getName();
                    className = className.replace("/",".");
                    className = className.replace("WEB-INF.classes.","");
                    className = className.substring(0,className.lastIndexOf(".class"));
                    logger.info("className:"+className);
                    precisionTestClassLoader.loadClazz("C:\\Users\\EDZ\\Desktop\\branch\\WEB-INF\\classes\\reman\\MyTest.class");
                    ZIPUtil.uncompress("C:\\Users\\EDZ\\Desktop\\branch.war");                    ;
                    Class<?> jvmClass = precisionTestClassLoader.findClass(className);
                    ClassVisitor visitor = new ClassVisitor(cp.parse(),jvmClass);
                    visitor.start();
                    classReferences.addAll(visitor.classReferences);
                }
                logger.info("classReferences:"+classReferences);
            }
                
        } catch (Exception e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
