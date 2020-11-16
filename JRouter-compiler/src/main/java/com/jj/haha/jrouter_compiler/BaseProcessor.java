package com.jj.haha.jrouter_compiler;

import com.jj.haha.jrouter.annotation.Const;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;

import java.io.IOException;
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

    protected Filer filer;
    protected Types types;
    protected Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        System.out.println("============= BaseProcessor init ============");
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }

    public TypeElement typeElement(String className) {
        return elements.getTypeElement(className);
    }

    public TypeMirror typeMirror(String className) {
        return typeElement(className).asType();
    }

    public ClassName className(String className) {
        return ClassName.get(typeElement(className));
    }

    public TypeName typeName(String className) {
        return TypeName.get(typeMirror(className));
    }

    public static String getClassName(TypeMirror typeMirror) {
        return typeMirror == null ? "" : typeMirror.toString();
    }

    public boolean isSubType(TypeMirror type, String className) {
        return type != null && types.isSubtype(type, typeMirror(className));
    }

    public boolean isSubType(Element element, String className) {
        return element != null && isSubType(element.asType(), className);
    }

    public boolean isSubType(Element element, TypeMirror typeMirror) {
        return element != null && types.isSubtype(element.asType(), typeMirror);
    }

    public boolean isConcreteType(Element element) {
        return element instanceof TypeElement && !element.getModifiers().contains(
                Modifier.ABSTRACT);
    }

    public boolean isConcreteSubType(Element element, String className) {
        return isConcreteType(element) && isSubType(element, className);
    }

    public boolean isConcreteSubType(Element element, TypeMirror typeMirror) {
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

    public CodeBlock buildHandler(boolean isActivity, Symbol.ClassSymbol cls) {
        CodeBlock.Builder b = CodeBlock.builder();
        if (isActivity) {
            b.add("$S", cls.className());
        } else {
            b.add("new $T()", cls);
        }
        return b.build();
    }

    public class ServiceInitClassBuilder {

        private final String className;
        private final CodeBlock.Builder builder;
        private final ClassName serviceLoaderClass;

        public ServiceInitClassBuilder(String className) {
            this.className = className;
            this.builder = CodeBlock.builder();
            System.out.println("typeElement(className) = " + typeElement(Const.SERVICE_LOADER_CLASS));
            this.serviceLoaderClass = className(Const.SERVICE_LOADER_CLASS);
        }

        public ServiceInitClassBuilder put(String interfaceName, String key, String implementName, boolean singleton, boolean canRegister) {
            builder.addStatement("$T.put($T.class, $S, $T.class, $L, $L)",
                    serviceLoaderClass,
                    className(interfaceName),
                    key,
                    className(implementName),
                    singleton,
                    canRegister);
            return this;
        }

        public ServiceInitClassBuilder putDirectly(String interfaceName, String key, String implementName, boolean singleton, boolean canRegister) {
            builder.addStatement("$T.put($T.class, $S, $L.class, $L, $L)",
                    serviceLoaderClass,
                    className(interfaceName),
                    key,
                    implementName,
                    singleton,
                    canRegister);
            return this;
        }

        public void build() {
            MethodSpec methodSpec = MethodSpec.methodBuilder(Const.INIT_METHOD)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID)
                    .addCode(this.builder.build())
                    .build();

            TypeSpec typeSpec = TypeSpec.classBuilder(this.className)
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodSpec)
                    .build();
            try {
                JavaFile.builder(Const.GEN_PKG_SERVICE, typeSpec)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
