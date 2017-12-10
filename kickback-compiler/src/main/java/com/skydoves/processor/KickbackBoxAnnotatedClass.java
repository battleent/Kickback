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

import com.google.common.base.Strings;
import com.google.common.base.VerifyException;
import com.skydoves.kickback.KickbackBox;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class KickbackBoxAnnotatedClass {

    public final TypeElement annotatedElement;
    public final String packageName;
    public final String clazzName;
    public final String boxName;

    public final List<KickbackElementClass> kickbackElementList;
    public final List<TypeElement> kickbackInjectionList;

    public KickbackBoxAnnotatedClass(TypeElement annotatedElement, Elements elementUtils) throws VerifyException {
        KickbackBox kickbackBox = annotatedElement.getAnnotation(KickbackBox.class);
        PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
        this.packageName = packageElement.isUnnamed() ? null : packageElement.getQualifiedName().toString();
        this.annotatedElement = annotatedElement;
        this.clazzName = annotatedElement.getSimpleName().toString();
        this.boxName = Strings.isNullOrEmpty(kickbackBox.name()) ? StringUtils.toUpperCamel(this.clazzName) : kickbackBox.name();
        this.kickbackElementList =  new ArrayList<>();
        this.kickbackInjectionList = new ArrayList<>();

        Map<String, String> checkMap = new HashMap<>();
        annotatedElement.getEnclosedElements().stream()
                .filter(element -> element instanceof VariableElement)
                .map(element -> (VariableElement) element)
                .forEach(variable -> {
                    KickbackElementClass kickbackElement = new KickbackElementClass(variable, elementUtils);

                    if(checkMap.get(kickbackElement.elementName) != null) {
                        throw new VerifyException(String.format("\'%s\' key is already used in class.", kickbackElement.elementName));
                    }

                    checkMap.put(kickbackElement.elementName, kickbackElement.clazzName);
                    kickbackElementList.add(kickbackElement);
                });

        annotatedElement.getEnclosedElements().stream()
                .filter(element -> element instanceof ExecutableElement)
                .filter(element -> !element.getSimpleName().equals(annotatedElement.getSimpleName()))
                .skip(1)
                .map(element -> (ExecutableElement) element)
                .forEach(method -> {
                    MethodSpec methodSpec = MethodSpec.overriding(method).build();
                    if(methodSpec.returnType != TypeName.get(Void.TYPE)) {
                        throw new VerifyException(String.format("return type must be void : '%s' method with return type '%s'", methodSpec.name, methodSpec.returnType));
                    } else if(methodSpec.parameters.size() > 1 || methodSpec.parameters.size() == 0) {
                        throw new VerifyException(String.format("length of parameter must be 1 : '%s' method with parameters '%s'", methodSpec.name, methodSpec.parameters.toString()));
                    }

                    ParameterSpec parameterSpec = methodSpec.parameters.get(0);
                    TypeElement injectedElement = elementUtils.getTypeElement(parameterSpec.type.toString());
                    kickbackInjectionList.add(injectedElement);
                });
    }
}
