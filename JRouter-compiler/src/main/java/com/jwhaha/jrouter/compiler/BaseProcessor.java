package com.jwhaha.jrouter.compiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public abstract class BaseProcessor extends AbstractProcessor {

    Filer filer;
    Types types;
    Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        System.out.println("============= BaseProcessor init ============");
        filer = processingEnvironment.getFiler();
        types = processingEnvironment.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
    }
}
