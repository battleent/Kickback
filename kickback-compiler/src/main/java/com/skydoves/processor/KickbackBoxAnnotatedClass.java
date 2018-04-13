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
import com.skydoves.kickback.KickbackFunction;
import com.squareup.javapoet.MethodSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
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

    public final List<String> keyNameFields;
    public final Map<String, KickbackElementClass> keyFieldMap;
    public final Map<String, Element> setterFunctionsList;
    public final Map<String, Element> getterFunctionsList;

    private static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";

    public KickbackBoxAnnotatedClass(TypeElement annotatedElement, Elements elementUtils) throws VerifyException {
        KickbackBox kickbackBox = annotatedElement.getAnnotation(KickbackBox.class);
        PackageElement packageElement = elementUtils.getPackageOf(annotatedElement);
        this.packageName = packageElement.isUnnamed() ? null : packageElement.getQualifiedName().toString();
        this.annotatedElement = annotatedElement;
        this.clazzName = annotatedElement.getSimpleName().toString();
        this.boxName = Strings.isNullOrEmpty(kickbackBox.name()) ? StringUtils.toUpperCamel(this.clazzName) : kickbackBox.name();
        this.kickbackElementList =  new ArrayList<>();
        this.keyNameFields = new ArrayList<>();
        this.keyFieldMap = new HashMap<>();
        this.setterFunctionsList = new HashMap<>();
        this.getterFunctionsList = new HashMap<>();

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
                    keyNameFields.add(kickbackElement.elementName);
                    keyFieldMap.put(kickbackElement.elementName, kickbackElement);
                });

        annotatedElement.getEnclosedElements().stream()
                .filter(function -> !function.getKind().isField() && function.getModifiers().contains(Modifier.PUBLIC) &&
                        function.getAnnotation(KickbackFunction.class) != null).forEach(function -> {
            KickbackFunction annotation = function.getAnnotation(KickbackFunction.class);
            String keyName = StringUtils.toUpperCamel(annotation.keyname());
            if(keyNameFields.contains(keyName)) {
                keyFieldMap.get(keyName).isObjectField = true;
                if(function.getSimpleName().toString().startsWith(SETTER_PREFIX)) {
                    setterFunctionsList.put(keyName, function);
                } else if(function.getSimpleName().toString().startsWith(GETTER_PREFIX)) {
                    getterFunctionsList.put(keyName, function);
                } else {
                    throw new VerifyException(String.format("PreferenceFunction's prefix should startWith 'get' or 'put' : %s", function.getSimpleName()));
                }
            } else {
                throw new VerifyException(String.format("keyName '%s' is not exist in entity.", keyName));
            }

            MethodSpec methodSpec = MethodSpec.overriding((ExecutableElement) function).build();
            if(methodSpec.parameters.size() > 1) {
                throw new VerifyException("PreferenceFunction should has one parameter");
            } else if(!methodSpec.parameters.get(0).type.equals(keyFieldMap.get(keyName).typeName)) {
                throw new VerifyException(String.format("parameter '%s''s type should be %s.", methodSpec.parameters.get(0).name, keyFieldMap.get(keyName).typeName));
            } else if(!methodSpec.returnType.equals(keyFieldMap.get(keyName).typeName)) {
                throw new VerifyException(String.format("method '%s''s return type should be %s.", methodSpec.name, keyFieldMap.get(keyName).typeName));
            }
        });
    }
}
