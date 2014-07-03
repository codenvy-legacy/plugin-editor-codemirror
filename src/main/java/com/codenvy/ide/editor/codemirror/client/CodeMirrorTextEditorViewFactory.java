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
package com.codenvy.ide.editor.codemirror.client;

import javax.inject.Inject;

import com.codenvy.ide.editor.common.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.editor.common.client.texteditor.EditorWidgetFactory;
import com.codenvy.ide.editor.common.client.texteditor.EmbeddedTextEditorPartView;
import com.codenvy.ide.editor.common.client.texteditor.EmbeddedTextEditorPartViewImpl;
import com.codenvy.ide.editor.common.client.texteditor.EmbeddedTextEditorViewFactory;

public class CodeMirrorTextEditorViewFactory implements EmbeddedTextEditorViewFactory {

    @Inject
    private EditorWidgetFactory<CodeMirrorEditorWidget> widgetFactory;
    @Inject
    private FileTypeIdentifier                          fileTypeIdentifier;


    @Override
    public EmbeddedTextEditorPartView createTextEditorPartView() {
        return new EmbeddedTextEditorPartViewImpl<CodeMirrorEditorWidget>(this.widgetFactory, this.fileTypeIdentifier);
    }
}
