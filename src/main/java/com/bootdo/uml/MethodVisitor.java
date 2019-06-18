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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;

/**
 * The simplest of method visitors, prints any invoked method
 * signature for all method invocations.
 * 
 * Class copied with modifications from CJKM: http://www.spinellis.gr/sw/ckjm/
 */

/**
 * 方法访问器
 */
public class MethodVisitor extends EmptyVisitor {

    JavaClass visitedClass;
    public MethodGen mg;
    public ConstantPoolGen cp;
    public String format;
    
    
    
    
    

    
    List<Map<String,Object>> totalList = new ArrayList<Map<String,Object>>();//被调用方法列表
    
    public MethodVisitor(MethodGen m, JavaClass jc) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        format = "M:" + visitedClass.getClassName() + ":" + mg.getName() + "(" + argumentList(mg.getArgumentTypes()) + ")"
            + " " + "(%s)%s:%s(%s)";
        if ("<init>".equals(mg.getName().toString())) {

        }
        
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

    public void start() {
        if (mg.isAbstract() || mg.isNative())
            return;
        for (InstructionHandle ih = mg.getInstructionList().getStart(); 
                ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            
            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConst.getInstruction(opcode) != null)
                && !(i instanceof ConstantPushInstruction) 
                && !(i instanceof ReturnInstruction));
    }

    /**
     * 被调用方法访问
     * @param i
     */
    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
    	Map<String,Object> called_class_name = new HashMap<String,Object>(); //被调用class

    	Map<String,Object> called_method_name = new HashMap<String,Object>();//被调用方法名称

        Map<String,Object> called_method_param = new HashMap<String,Object>();//被调用方法参数类型

        Map<String,Object> return_type = new HashMap<String,Object>();//被调用方法返回类型

        called_method_name.put("called_method_name", i.getMethodName(cp));

    	called_method_param.put("called_method_param", argumentList(i.getArgumentTypes(cp)));    	
    	  
    	called_class_name.put("called_class_name", i.getReferenceType(cp));

        return_type.put("return_type",i.getReturnType(cp).toString());
        Map<String,Object> sub = new HashMap<String,Object>();//被调用方法map对象
    	sub.put("called_class_name", called_class_name);
    	sub.put("called_method_name", called_method_name);
    	sub.put("called_method_param", called_method_param);
        sub.put("return_type", return_type);
    	totalList.add(sub);
    }

//    @Override
//    public void visitINVOKEINTERFACE(INVOKEINTERFACE i) {
//    	//System.out.println("I");
//        System.out.println(String.format(format,"I",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
//    }

//    @Override
//    public void visitINVOKESPECIAL(INVOKESPECIAL i) {
//    	//System.out.println("O");
//        System.out.println(String.format(format,"O",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
//    }

//    @Override
//    public void visitINVOKESTATIC(INVOKESTATIC i) {
//    	//System.out.println("S");
//        System.out.println(String.format(format,"S",i.getReferenceType(cp),i.getMethodName(cp),argumentList(i.getArgumentTypes(cp))));
//    }

//    @Override
//    public void visitINVOKEDYNAMIC(INVOKEDYNAMIC i) {
//    	//System.out.println("D");
//        System.out.println(String.format(format,"D",i.getType(cp),i.getMethodName(cp),
//                argumentList(i.getArgumentTypes(cp))));
//    }
}
