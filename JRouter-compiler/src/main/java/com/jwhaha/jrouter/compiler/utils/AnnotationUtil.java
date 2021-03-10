package com.jwhaha.jrouter.compiler.utils;

import com.squareup.javapoet.ClassName;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class AnnotationUtil {
    public static ClassName className(String className, Elements elements) {
        return ClassName.get(typeElement(className, elements));
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    public static boolean isConcreteSubType(Element element, String className, Types types, Elements elements) {
        return isConcreteType(element) && isSubType(element, className, types, elements);
    }

    public static boolean isConcreteSubType(Element element, TypeMirror typeMirror, Types types) {
        return isConcreteType(element) && isSubType(element, typeMirror, types);
    }

    public static boolean isSubType(TypeMirror type, String className, Types types, Elements elements) {
        return type != null && types.isSubtype(type, typeMirror(className, elements));
    }

    public static boolean isSubType(Element element, String className, Types types, Elements elements) {
        return element != null && isSubType(element.asType(), className, types, elements);
    }

    public static boolean isSubType(Element element, TypeMirror typeMirror, Types types) {
        return element != null && types.isSubtype(element.asType(), typeMirror);
    }

    public static boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(
                Modifier.ABSTRACT);
    }

    public static TypeMirror typeMirror(String className, Elements elements) {
        return typeElement(className, elements).asType();
    }

    public static String hash(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(str.hashCode());
        }
    }

    private static TypeElement typeElement(String className, Elements elements) {
        return elements.getTypeElement(className);
    }
}
