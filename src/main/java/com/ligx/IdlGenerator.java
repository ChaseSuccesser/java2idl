package com.ligx;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.*;

/**
 * Author: ligongxing.
 * Date: 2017年09月27日.
 */
public class IdlGenerator {

    /**
     * 生成idl文件
     *
     * @param savePath     idl文件的存储位置(绝对路径).形如:/Users/lgx/demo.thrift
     * @param packagePaths 要扫描的包. 形如:com.ligx
     */
    public static void generateIdl(String savePath, String... packagePaths) {
        if (packagePaths == null || packagePaths.length == 0) {
            return;
        }

        // 扫描packages下所有的class
        Set<Class> classSet = new HashSet<>();
        for (String packagePath : packagePaths) {
            List<Class> classList = getClassNameFromPackage(packagePath);
            if (classList == null || classList.size() == 0) {
                continue;
            }
            classSet.addAll(classList);
        }
        if (classSet.size() == 0) {
            return;
        }

        StringBuilder allIdl = new StringBuilder(500);
        for (Class clazz : classSet) {
            boolean isIdlPresent = clazz.isAnnotationPresent(Idl.class);
            if (!isIdlPresent) {
                continue;
            }
            String thriftStruct = generateStruct(clazz);
            allIdl.append(thriftStruct);
        }

        try {
            File file = new File(savePath);
            OutputStream out = new FileOutputStream(file);
            IOUtils.write(allIdl.toString(), out, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * java POJO 转换为 thrift struct
     *
     * @param clazz
     * @return
     */
    private static String generateStruct(Class clazz) {
        StringBuilder sb = new StringBuilder(100);

        sb.append("struct").append(" ").append(clazz.getSimpleName()).append(" ").append("{").append("\n");

        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return null;
        }
        int i = 1;
        for (Field field : fields) {
            Type fieldType = field.getGenericType();
            String thriftType = parseType(fieldType);
            sb.append("    ").append(i).append(": ").append("optional ").append(thriftType).append(" ").append(field.getName()).append(";").append("\n");
            i += 1;
        }
        sb.append("}").append("\n");
        return sb.toString();
    }

    /**
     * 解析POJO的字段类型
     * 将字段的java类型映射为thrift类型
     * <p>
     * int --> i32
     * Map<String, String> --> map<string, string>
     *
     * @param type
     * @return
     */
    private static String parseType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;

            StringBuilder sb = new StringBuilder();

            Type rawType = pType.getRawType();
            sb.append(mapType(rawType)).append("<");

            Type[] typeArgs = pType.getActualTypeArguments();
            for (Type typeArg : typeArgs) {
                if(typeArg instanceof ParameterizedType){
                    sb.append(parseType(typeArg));
                } else {
                    String thriftType = mapType(typeArg);
                    sb.append(thriftType).append(",");
                }
            }
            return sb.toString().substring(0, sb.length() - 1) + ">";
        } else {
            Class fieldClass = (Class) type;
            return mapType(fieldClass);
        }
    }

    private static String mapType(Type type) {
        Class clazz = (Class) type;
        if (clazz == byte.class) {
            return "byte";
        } else if (clazz == short.class) {
            return "i16";
        } else if (clazz == int.class) {
            return "i32";
        } else if (clazz == long.class) {
            return "i64";
        } else if (clazz == double.class) {
            return "double";
        } else if (clazz == boolean.class) {
            return "bool";
        } else if (clazz == String.class) {
            return "string";
        } else if (clazz == Map.class) {
            return "map";
        } else if (clazz == List.class) {
            return "list";
        } else if (clazz == Set.class) {
            return "set";
        } else {
            return clazz.getSimpleName();
        }
    }

    /**
     * 获取给定package下所有的class
     *
     * @param packagePath 形如:com.ligx
     * @return
     */
    private static List<Class> getClassNameFromPackage(final String packagePath) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String newPackagePath = packagePath.replace(".", "/");
            URL packageUrl = classLoader.getResource(newPackagePath);
            if (packageUrl == null) {
                System.err.println("packageUrl is null!");
                return null;
            }

            String absolutePackagePath = packageUrl.getPath();

            File packageFile = new File(absolutePackagePath);
            File[] files = packageFile.listFiles();
            if (files == null || files.length == 0) {
                System.err.println("file list is empty!");
                return null;
            }
            List<Class> classList = new ArrayList<>(files.length);
            for (File file : files) {
                String fileName = file.getName();
                if (!fileName.contains(".class")) {
                    continue;
                }
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                String entireClassReference = packagePath + "." + className;
                Class clazz = Class.forName(entireClassReference);
                classList.add(clazz);
            }
            return classList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        IdlGenerator.generateIdl("/Users/lgx/dev/idl-generator-test/demo.thrift", "com.ligx");
    }
}

