package com.jwhaha.jrouter.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {

    public static Filer filer;
    public static Types types;
    public static Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        System.out.println("============= BaseProcessor init ============");
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    public static TypeElement typeElement(String className) {
        return elements.getTypeElement(className);
    }

    public static TypeMirror typeMirror(String className) {
        return typeElement(className).asType();
    }

    public static ClassName className(String className) {
        return ClassName.get(typeElement(className));
    }

    public static TypeName typeName(String className) {
        return TypeName.get(typeMirror(className));
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    public static boolean isSubType(TypeMirror type, String className) {
        return type != null && types.isSubtype(type, typeMirror(className));
    }

    public static boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    public static boolean isSubType(Element element, TypeMirror typeMirror) {
        return element != null && types.isSubtype(element.asType(), typeMirror);
    }

    public static boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(
                Modifier.ABSTRACT);
    }

    public static boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    public static boolean isConcreteSubType(Element element, TypeMirror typeMirror) {
        return isConcreteType(element) && isSubType(element, typeMirror);
    }

    public static String randomHash() {
        return hash(UUID.randomUUID().toString());
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
}
