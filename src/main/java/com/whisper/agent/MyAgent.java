package com.whisper.agent;

import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.Date;

/**
 * 代理类
 * @author little whisper
 * @date 2021/1/5 12:18
 */
public class MyAgent {

    public static void agentmain(String args, Instrumentation inst) {
        File file = new File(args);
        byte[] classBytes = new byte[(int) file.length()];
        DataInputStream dataInputStream = null;
        try {
            dataInputStream = new DataInputStream(new FileInputStream(file));
            dataInputStream.readFully(classBytes);
            dataInputStream.close();
            ClassReader classReader = new ClassReader(classBytes);
            String s = classReader.getClassName().replaceAll("/", ".");
            Class<?> oldClass = Class.forName(s);
            ClassDefinition classDefinition = new ClassDefinition(oldClass, classBytes);
            inst.redefineClasses(classDefinition);
            System.out.println(new Date().toString() + " redefineClasses " + args + " success!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
