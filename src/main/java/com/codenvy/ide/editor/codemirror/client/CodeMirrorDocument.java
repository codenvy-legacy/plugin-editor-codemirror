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

import static com.codenvy.ide.editor.codemirror.client.EventTypes.CHANGE;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.editor.codemirror.client.jso.CMDocumentOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.event.CMChangeEventOverlay;
import com.codenvy.ide.jseditor.client.document.DocumentEventBus;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.events.CursorActivityHandler;
import com.codenvy.ide.jseditor.client.events.DocumentChangeEvent;
import com.codenvy.ide.jseditor.client.events.HasCursorActivityHandlers;
import com.codenvy.ide.jseditor.client.text.TextPosition;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * CodeMirror implementation of {@link EmbeddedDocument}.
 *
 * @author "Mickaël Leduque"
 */
public class CodeMirrorDocument implements EmbeddedDocument, DocumentHandle {

    /** The internal document representation for CodeMirror. */
    private final CMDocumentOverlay         documentOverlay;

    private final HasCursorActivityHandlers hasCursorActivityHandlers;

    private final DocumentEventBus eventBus = new DocumentEventBus();

    public CodeMirrorDocument(final CMDocumentOverlay documentOverlay,
                              final HasCursorActivityHandlers hasCursorActivityHandlers) {
        this.documentOverlay = documentOverlay;
        this.hasCursorActivityHandlers = hasCursorActivityHandlers;

        documentOverlay.getEditor().on(CHANGE, new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {
            @Override
            public void onEvent(final JsArray<JavaScriptObject> params) {

                // first parameter is editor instance, second is the change
                final CMChangeEventOverlay change = params.get(1).cast();
                fireDocumentChangeEvent(change);
            }
        });
    }

    private void fireDocumentChangeEvent(final CMChangeEventOverlay param) {
        int startOffset = 0;
        if (param.getFrom() != null) {
            startOffset = this.documentOverlay.indexFromPos(param.getFrom());
        }
        int endOffset;
        if (param.getTo() != null) {
            endOffset = this.documentOverlay.indexFromPos(param.getTo());
        } else {
            endOffset = this.documentOverlay.getValue().length();
        }
        final int length = endOffset - startOffset;
        final String text = param.getText().join("\n");

        final DocumentChangeEvent event = new DocumentChangeEvent(this, startOffset, length, text);
        this.eventBus.fireEvent(event);
    }

    @Override
    public TextPosition getPositionFromIndex(int index) {
        final CMPositionOverlay pos = this.documentOverlay.posFromIndex(index);
        return new TextPosition(pos.getLine(), pos.getCharacter());
    }

    @Override
    public int getIndexFromPosition(TextPosition position) {
        return documentOverlay.indexFromPos(CMPositionOverlay.create(position.getLine(), position.getCharacter()));
    }

    @Override
    public void setCursorPosition(TextPosition position) {
        this.documentOverlay.setCursor(position.getLine(), position.getCharacter());
    }

    @Override
    public TextPosition getCursorPosition() {
        final CMPositionOverlay pos = this.documentOverlay.getCursor();
        return new TextPosition(pos.getLine(), pos.getCharacter());
    }

    @Override
    public int getLineCount() {
        return this.documentOverlay.lineCount();
    }

    @Override
    public HandlerRegistration addCursorHandler(final CursorActivityHandler handler) {
        return this.hasCursorActivityHandlers.addCursorActivityHandler(handler);
    }

    @Override
    public String getContents() {
        return this.documentOverlay.getValue();
    }

    public void replace(final Region region, final String text) {
        final CMPositionOverlay fromPos = this.documentOverlay.posFromIndex(region.getOffset());
        final CMPositionOverlay toPos = this.documentOverlay.posFromIndex(region.getOffset() + region.getLength());

        this.documentOverlay.replaceRange(text, fromPos, toPos);
    }

    public DocumentHandle getDocumentHandle() {
        return this;
    }

    @Override
    public boolean isSameAs(final DocumentHandle document) {
        return (this.equals(document));
    }

    @Override
    public DocumentEventBus getDocEventBus() {
        return this.eventBus;
    }

    @Override
    public EmbeddedDocument getDocument() {
        return this;
    }

    @Override
    public int getContentsCharCount() {
        // same as last offset
        final int lastLine = this.documentOverlay.lastLine();
        final String lineContent = this.documentOverlay.getLine(lastLine);
        final int lineSize = lineContent.length();
        // zero based char position on the line
        return this.documentOverlay.indexFromPos(CMPositionOverlay.create(lastLine, lineSize - 1));
    }
}
