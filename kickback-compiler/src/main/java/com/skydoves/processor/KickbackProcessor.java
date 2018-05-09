/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.processor;

import com.google.auto.service.AutoService;
import com.google.common.base.VerifyException;
import com.skydoves.kickback.KickbackBox;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.skydoves.kickback.KickbackBox"})
@AutoService(Processor.class)
public class KickbackProcessor extends AbstractProcessor {

    private Map<String, KickbackBoxAnnotatedClass> annotatedBoxMap;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotatedBoxMap = new HashMap<>();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(NOTE, "start Kickback-Processor");
        if(annotations.isEmpty()) {
            return true;
        }

        roundEnv.getElementsAnnotatedWith(KickbackBox.class).stream()
                .map(annotatedType -> (TypeElement) annotatedType)
                .forEach(annotatedType -> {
                    try {
                        checkValidBoxType(annotatedType);
                        processKickbackBox(annotatedType);
                    } catch (IllegalAccessException e) {
                        showErrorLog(e.getMessage(), annotatedType);
                    }
                });

        return true;
    }

    private void processKickbackBox(TypeElement annotatedType) {
        try {
            KickbackBoxAnnotatedClass annotatedClazz = new KickbackBoxAnnotatedClass(annotatedType, processingEnv.getElementUtils());
            checkDuplicatedKickbackBox(annotatedClazz);
            generateProcessKickbackBox(annotatedClazz);
            generatePreferencesFactoryImpl(annotatedClazz);
        } catch (VerifyException e) {
            showErrorLog(e.getMessage(), annotatedType);
        }
    }

    private void generateProcessKickbackBox(KickbackBoxAnnotatedClass annotatedClazz) {
        try {
            TypeSpec generatedSpec = (new KickbackBoxGenerator(annotatedClazz)).generate();
            JavaFile.builder(annotatedClazz.packageName, generatedSpec).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            // ignore ;)
        }
    }

    private void generatePreferencesFactoryImpl(KickbackBoxAnnotatedClass annotatedClazz) {
        try {
            TypeSpec generatedSpec = (new PreferencesFactoryImplGenerator(annotatedClazz)).generate();
            JavaFile.builder(annotatedClazz.packageName, generatedSpec).build().writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            // ignore :)
        }
    }

    private void checkValidBoxType(TypeElement annotatedType) throws IllegalAccessException {
        if(!annotatedType.getKind().isClass()) {
            throw new IllegalAccessException("Only classes can be annotated with @KickbackBox");
        } else if(annotatedType.getModifiers().contains(Modifier.FINAL)) {
            throw new IllegalAccessException("class modifier can not be final");
        } else if(annotatedType.getModifiers().contains(Modifier.PRIVATE)) {
            throw new IllegalAccessException("class modifier can not be private");
        }
    }

    private void checkDuplicatedKickbackBox(KickbackBoxAnnotatedClass annotatedClazz) throws VerifyException {
        String keyName = annotatedClazz.boxName;
        if(annotatedBoxMap.containsKey(keyName)) {
            throw new VerifyException("@KickbackBox key name is duplicated.");
        } else {
            annotatedBoxMap.put(keyName, annotatedClazz);
        }
    }

    private void showErrorLog(String message, Element element) {
        messager.printMessage(ERROR, StringUtils.getErrorMessagePrefix() + message, element);
    }
}
