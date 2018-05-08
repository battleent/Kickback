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
import com.skydoves.kickback.Keep;
import com.skydoves.kickback.KickbackElement;
import com.skydoves.kickback.Soft;
import com.skydoves.kickback.Weak;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

public class KickbackElementClass {

    public final VariableElement variableElement;
    public final String packageName;
    public TypeName typeName;
    public final String clazzName;
    public final String elementName;
    public final Object value;
    public boolean isPrimitive = false;
    public final boolean isWeak;
    public final boolean isSoft;
    public final boolean keep;
    public boolean isObjectField = false;

    public KickbackElementClass(VariableElement variableElement, Elements elementUtils) throws VerifyException {
        KickbackElement kickbackElement = variableElement.getAnnotation(KickbackElement.class);
        Weak weak = variableElement.getAnnotation(Weak.class);
        Soft soft = variableElement.getAnnotation(Soft.class);
        Keep keep = variableElement.getAnnotation(Keep.class);
        PackageElement packageElement = elementUtils.getPackageOf(variableElement);
        this.variableElement = variableElement;
        this.packageName = packageElement.isUnnamed() ? null : packageElement.getQualifiedName().toString();
        this.typeName = TypeName.get(variableElement.asType());
        this.clazzName = variableElement.getSimpleName().toString();
        this.value = variableElement.getConstantValue();
        if(weak != null) this.isWeak = true;
        else this.isWeak = false;
        if(soft != null) this.isSoft = true;
        else this.isSoft = false;
        if(keep != null) this.keep = true;
        else this.keep = false;

        if(kickbackElement != null) {
            this.elementName =  StringUtils.toUpperCamel(Strings.isNullOrEmpty(kickbackElement.name()) ? this.clazzName : kickbackElement.name());
        } else {
            this.elementName = StringUtils.toUpperCamel(this.clazzName);
        }

        checkPrimitiveType();
        checkModifierValidate();
        checkAnnotationValidate();
    }

    private void checkPrimitiveType() {
        if(typeName.equals(TypeName.BOOLEAN) || typeName.equals(TypeName.BYTE) || typeName.equals(TypeName.SHORT) ||
                typeName.equals(TypeName.SHORT) ||  typeName.equals(TypeName.INT) || typeName.equals(TypeName.LONG) || typeName.equals(TypeName.CHAR) ||
                typeName.equals(TypeName.FLOAT) || typeName.equals(TypeName.DOUBLE)) {
            this.typeName = this.typeName.box();
            this.isPrimitive = true;
        }
    }

    private void checkModifierValidate() throws VerifyException {
        if(this.value == null && !this.isPrimitive && this.variableElement.getModifiers().contains(Modifier.FINAL)) {
            throw new VerifyException(String.format("Object field \'%s\' can not be final.", this.variableElement.getSimpleName()));
        }
    }

    private void checkAnnotationValidate() throws VerifyException {
        if(this.isWeak && this.isSoft) {
            throw new VerifyException(String.format("Object field \'%s\' can has only one reference type. Weak or Soft.", this.variableElement.getSimpleName()));
        }
    }
}
