/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
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
import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorModule;
import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorPresenter;
import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorWidget;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.texteditor.EditorModule;
import com.codenvy.ide.jseditor.client.texteditor.EditorWidgetFactory;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenterFactory;
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
        install(new GinFactoryModuleBuilder().build(new TypeLiteral<EditorWidgetFactory<CodeMirrorEditorWidget>>() {}));
        bind(new TypeLiteral<EditorModule<CodeMirrorEditorWidget>>() {}).to(CodeMirrorEditorModule.class);

        install(new GinFactoryModuleBuilder()
            .implement(new TypeLiteral<EmbeddedTextEditorPresenter<CodeMirrorEditorWidget>>() {}, CodeMirrorEditorPresenter.class)
            .build(new TypeLiteral<EmbeddedTextEditorPresenterFactory<CodeMirrorEditorWidget>>() {}));
    }

    @Provides
    @Singleton
    @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INJECT_NAME)
    protected String defaultEditorTypeKey() {
        return CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY;
    }
}
