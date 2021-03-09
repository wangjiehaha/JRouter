package com.jwhaha.jrouter.compiler;

import com.google.auto.service.AutoService;
import com.jwhaha.jrouter.annotation.Const;
import com.jwhaha.jrouter.annotation.RouterService;
import com.jwhaha.jrouter.annotation.ServiceImpl;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ServiceAnnotationProcessor extends BaseProcessor {

    private final HashMap<String, Entity> mEntityMap = new HashMap<>();
    private String mHash = null;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        if (env.processingOver()) {
            generateInitClass();
        } else {
            processAnnotations(env);
        }
        return true;
    }

    private void processAnnotations(RoundEnvironment env) {
        System.out.println("=========== Begin collect Annotations ==========");
        for (Element element : env.getElementsAnnotatedWith(RouterService.class)) {
            if (mHash == null) {
                mHash = hash(getClassName(element.asType()));
            }

            RouterService service = element.getAnnotation(RouterService.class);
            if (service == null) {
                continue;
            }

            List<? extends TypeMirror> typeMirrors = getInterface(service);
            String[] keys = service.key();

            String implementationName = getClassName(element.asType());
            boolean singleton = service.singleton();
            final boolean defaultImpl = service.defaultImpl();
            final boolean canRegister = service.register();

            if (typeMirrors != null && !typeMirrors.isEmpty()) {
                for (TypeMirror mirror : typeMirrors) {
                    if (mirror == null) {
                        continue;
                    }
                    if (!isConcreteSubType(element, mirror)) {
                        String msg = getClassName(element.asType()) + " no implementation annotations " + RouterService.class.getName()
                                + " annotated interface " + mirror.toString();
                        throw new RuntimeException(msg);
                    }
                    String interfaceName = getClassName(mirror);

                    Entity entity = mEntityMap.get(interfaceName);
                    if (entity == null) {
                        entity = new Entity(interfaceName);
                        mEntityMap.put(interfaceName, entity);
                    }

                    if (defaultImpl) {
                        //如果设置为默认实现，则手动添加一个内部标识默认实现的key
                        entity.put(ServiceImpl.DEFAULT_IMPL_KEY, implementationName, singleton, canRegister);
                    }

                    if (keys.length > 0) {
                        for (String key : keys) {
                            if (key.contains(":")) {
                                String msg = String.format("%s: 注解%s的key参数不可包含冒号",
                                        implementationName, RouterService.class.getName());
                                throw new RuntimeException(msg);
                            }
                            entity.put(key, implementationName, singleton, canRegister);
                        }
                    } else {
                        entity.put(null, implementationName, singleton, canRegister);
                    }
                }
            }
        }
        System.out.println("=========== End collect Annotations ==========");
    }

    private void generateInitClass() {
        System.out.println("mEntityMap size " + mEntityMap.size() + ", mHash " + mHash);
        if (mEntityMap.isEmpty() || mHash == null) {
            return;
        }
        ServiceInitClassBuilder generator = new ServiceInitClassBuilder("ServiceInit" + Const.SPLITTER + mHash);
        for (Map.Entry<String, Entity> entry : mEntityMap.entrySet()) {
            for (ServiceImpl service : entry.getValue().getMap().values()) {
                boolean canRegister = entry.getValue().getCanRegisterMap().get(service);
                generator.put(entry.getKey(), service.getKey(), service.getImplementation(), service.isSingleton(), canRegister);

                System.out.println("entry.getKey() = " + entry.getKey() + ", service.getKey() = " + service.getKey()
                        + ", service.getImplementation() = " + service.getImplementation()
                        + ", service.getSingleton() = " + service.isSingleton()
                        + ", canRegister = " + canRegister);
            }
        }
        generator.build();
    }

    private static List<? extends TypeMirror> getInterface(RouterService service) {
        try {
            service.interfaces();
        } catch (MirroredTypesException mte) {
            return mte.getTypeMirrors();
        }
        return null;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(RouterService.class.getName()));
    }

    public static class Entity {

        private final String mInterfaceName;

        private final Map<String, ServiceImpl> mMap = new HashMap<>();
        private final Map<ServiceImpl, Boolean> mCanRegisterMap = new HashMap<>();

        Entity(String interfaceName) {
            mInterfaceName = interfaceName;
        }

        Map<String, ServiceImpl> getMap() {
            return mMap;
        }

        Map<ServiceImpl, Boolean> getCanRegisterMap() {
            return mCanRegisterMap;
        }

        void put(String key, String implementationName, boolean singleton, boolean canRegister) {
            if (implementationName == null) {
                return;
            }
            ServiceImpl impl = new ServiceImpl(key, implementationName, singleton);
            ServiceImpl prev = mMap.put(impl.getKey(), impl);
            String errorMsg = ServiceImpl.checkConflict(mInterfaceName, prev, impl);
            mCanRegisterMap.put(impl, canRegister);
            if (errorMsg != null) {
                throw new RuntimeException(errorMsg);
            }
        }

        public List<String> getContents() {
            List<String> list = new ArrayList<>();
            for (ServiceImpl impl : mMap.values()) {
                list.add(impl.toConfig());
            }
            return list;
        }
    }

    public static class ServiceInitClassBuilder {

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
