/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.editor.codemirror.client.inject;

import javax.inject.Named;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorExtension;
import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorWidget;
import com.codenvy.ide.editor.codemirror.client.CodeMirrorTextEditorViewFactory;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.texteditor.EditorWidgetFactory;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

@ExtensionGinModule
public class CodeMirrorEditorGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // Bind the CodeMirror EditorWidget factory
        install(new GinFactoryModuleBuilder().build(new TypeLiteral<EditorWidgetFactory<CodeMirrorEditorWidget>>() {
        }));
        // and the view factory
        bind(CodeMirrorTextEditorViewFactory.class);
    }

    @Provides
    @Singleton
    @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INJECT_NAME)
    protected String defaultEditorTypeKey() {
        return CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY;
    }
}
