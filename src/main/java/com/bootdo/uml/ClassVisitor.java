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

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * The simplest of class visitors, invokes the method visitor class for each
 * method found.
 */
public class ClassVisitor extends EmptyVisitor {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    //注解类型
    public static String ANNOTATION_TYPE_NAME_POSTMAPPING = "Lorg/springframework/web/bind/annotation/PostMapping;";

    public static String ANNOTATION_TYPE_NAME_GETMAPPING = "Lorg/springframework/web/bind/annotation/GetMapping;";

    public static String ANNOTATION_TYPE_NAME_REQUESTMAPPING = "Lorg/springframework/web/bind/annotation/RequestMapping;";

    public JavaClass clazz;
    public ConstantPoolGen constants;
    public String classReferenceFormat;
    public Class<?> jvmClass;
    /**
     * 类依赖关系集合
     */
    public List<Map<String,Object>> classReferences = new ArrayList<>();
    
    public ClassVisitor(JavaClass jc,Class<?> jvmClass) {
        clazz = jc;
        constants = new ConstantPoolGen(clazz.getConstantPool());
        classReferenceFormat = clazz.getClassName();
        this.jvmClass = jvmClass;
    }

    public void visitJavaClass(JavaClass jc) {
        if (jc.isEnum() || jc.isInterface() || jc.isAbstract() || jc.isAnnotation()){

        }else{
            jc.getConstantPool().accept(this);
            Method[] methods = jc.getMethods();
            for (int i = 0; i < methods.length; i++)
                methods[i].accept(this);
        }
    }
    


    public void visitConstantPool(ConstantPool constantPool) {
    	List<Map<String,Object>> classReference = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < constantPool.getLength(); i++) {
        	Map<String,Object> map = new HashMap<String,Object>();
            Constant constant = constantPool.getConstant(i);
            if (constant == null)
                continue;
            if (constant.getTag() == 7) {
                String referencedClass = 
                  constantPool.constantToString(constant);
                map.put(classReferenceFormat, referencedClass);
                classReference.add(map);
            }
        }
    }

    public void visitMethod(Method method){
        MethodGen mg = new MethodGen(method, clazz.getClassName(), constants);
        MethodVisitor visitor = new MethodVisitor(mg, clazz);
 
        visitor.start();
        
        if ("<init>".equals(method.getName())) {

        }else {
            //获取调用方法

            Map<String,Object> call_class_name = new HashMap<String,Object>(); //class
            
            Map<String,Object> call_method_name = new HashMap<String,Object>();//方法名称

            Map<String,Object> call_method_param = new HashMap<String,Object>();//方法参数类型

            Map<String,Object> byte_code = new HashMap<String,Object>();//方法参数类型

            Map<String,Object> return_type = new HashMap<String,Object>();//返回类型

            Map<String,Object> sub = new HashMap<String,Object>();//方法map对象

            call_class_name.put("call_class_name", clazz.getClassName());
            call_method_name.put("call_method_name", method.getName());
            call_method_param.put("call_method_param", argumentList(method.getArgumentTypes()));
            return_type.put("return_type",method.getReturnType().toString());
            try {
                if (method.getCode() != null){
                    String byte_code_str = method.getCode().toString();
                    byte_code.put("byte_code",byte_code_str.substring(0, byte_code_str.indexOf("Attribute")).trim());//存储字节码指令
                }else{
                    byte_code.put("byte_code","");//存储字节码指令
                }
            } catch (Exception e) {
                byte_code.put("byte_code","");//存储字节码指令
                e.printStackTrace();
            }

            sub.put("call_class_name", call_class_name);
            sub.put("call_method_name", call_method_name);
            sub.put("call_method_param", call_method_param);
            sub.put("return_type", return_type);
            sub.put("byte_code", byte_code);
            sub.put("sub", visitor.totalList);
            Map<String,Object> pairMap = new HashMap<>();
            Annotation restControllerAnnotation = null;
            Annotation controllerAnnotation = null;
            Annotation requestMappingAnnotation = null;
            if (jvmClass != null){
                restControllerAnnotation = jvmClass.getAnnotation(RestController.class);
                controllerAnnotation = jvmClass.getAnnotation(Controller.class);
                requestMappingAnnotation = jvmClass.getAnnotation(RequestMapping.class);
            }
            StringBuffer stringBuffer  = new StringBuffer();
            boolean isAnnotationController = false;
            boolean isAnnotationService = false;
            /**
             * RestController注解
             */
            if (restControllerAnnotation != null){
                stringBuffer.append(((RestController) restControllerAnnotation).value());
                isAnnotationController = true;
            }
            /**
             * Controller注解
             */
            if (controllerAnnotation != null){
                stringBuffer.append(((Controller) controllerAnnotation).value());
                isAnnotationController = true;
            }
            /**
             * RequestMapping注解
             */
            if (requestMappingAnnotation != null){
                String[]  s = ((RequestMapping) requestMappingAnnotation).value();
                if (s!= null && s.length > 0){
                    for (int x = 0; x< s.length ;x++){
                        stringBuffer.append(s[x].toString());
                    }
                }
                isAnnotationController = true;
            }
            AnnotationEntry[] annotationEntries = method.getAnnotationEntries();//获取method注解信息

            //controller层注解解析，获取url
            if (annotationEntries != null && isAnnotationController == true){
            	//判断method有无注解
                for (int x = 0 ; x < annotationEntries.length; x++){
                    AnnotationEntry annotationEntry = annotationEntries[x];
                    String annotationType = annotationEntry.getAnnotationType().toString();
                    //注解类型判断
                    if (ANNOTATION_TYPE_NAME_POSTMAPPING.equals(annotationType)){
                        isAnnotationService = true;
                    }else if(ANNOTATION_TYPE_NAME_GETMAPPING.equals(annotationType)){
                        isAnnotationService = true;
                    }else if(ANNOTATION_TYPE_NAME_REQUESTMAPPING.equals(annotationType)){
                        isAnnotationService = true;
                    }else {
                        continue;
                    }
                    StringBuffer  pair = new StringBuffer();;
                    if (isAnnotationService){
                        ElementValuePair[] elementValuePairs = annotationEntry.getElementValuePairs();//
                        for (int j = 0 ; j < elementValuePairs.length; j++){
                            ElementValuePair elementValuePair = elementValuePairs[j];
                            if ("value".equals(elementValuePair.getNameString())){
                                pair.append(elementValuePair.getValue().toString());//方法url信息
                            }
                        }
                        //stringBuffer为controller的url
                        if (elementValuePairs != null && elementValuePairs.length == 0){
                            pairMap.put("pair",stringBuffer.toString());
                        }else {
                            String temp = pair.toString();
                            temp = temp.replace("{","");
                            temp = temp.replace("}","");
                            pairMap.put("pair",stringBuffer.append(temp.replaceAll("^\\{", "").replaceAll("\\}$", "")));
                        }
                        break;
                    }

                }

            }
            logger.info("stringBuffer:"+stringBuffer);
            logger.info("method:"+method.getName());
            sub.put("pair",pairMap);
            classReferences.add(sub);
        }
        
    }
    
    public void start() {
        visitJavaClass(clazz);
    }
    
    
    private String argumentList(Type[] arguments) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(arguments[i].toString());
        }
        return sb.toString();
    }
}
