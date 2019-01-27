package me.markoutte.libs.rpc.utils;

import java.io.*;

/**
 * Модифицированный ObjectInputStream, который умеет загружать объекты через
 * заданный класслоудер.
 */
public class ClassLoaderObjectInputStream extends ObjectInputStream {

    private ClassLoader classLoader;

    public ClassLoaderObjectInputStream(ClassLoader classLoader, InputStream inputStream) throws IOException {
        super(inputStream);
        this.classLoader = classLoader;
    }

    protected Class resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        Class clazz = null;
        try {
            clazz = Class.forName(objectStreamClass.getName(), false, classLoader);
        } catch (Throwable e) { }

        if (clazz != null) {
            return clazz;
        } else {
            return super.resolveClass(objectStreamClass);
        }
    }
}
