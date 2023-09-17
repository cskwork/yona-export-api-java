package com.export.yona.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.cglib.proxy.Enhancer;

public class PropertyFileController {
    static File file;

    public static void main(String... args)throws IOException
    {
        file = new File("application.properties");
        Properties table = new Properties();
        table.setProperty("Shivam","Bane");
        table.setProperty("CS","Maverick");
        System.out.println("Properties has been set in HashTable: " + table);
        // saving the properties in file
        saveProperties(table);
        // changing the property
        table.setProperty("Shivam", "Swagger");
        System.out.println("After the change in HashTable: " + table);
        // saving the properties in file
        saveProperties(table);
        // loading the saved properties
        loadProperties(table);
    }

    static void saveProperties(Properties p) throws IOException
    {
        try (FileOutputStream fr = new FileOutputStream(file)) {
            p.store(fr, "Properties");
            fr.close();
        }
        System.out.println("After saving properties: " + p);
    }

    static void loadProperties(Properties p)throws IOException
    {
        try (FileInputStream fi = new FileInputStream(file)) {
            p.load(fi);
            fi.close();
        }
        System.out.println("After Loading properties: " + p);
    }
}
