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
package com.codenvy.ide.editor.codemirror.client;

import java.util.logging.Logger;

import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.EventHandlers.EventHandlerMixedParameters;
import com.codenvy.ide.editor.codemirror.client.jso.event.CMChangeEventOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.line.CMGutterMarkersOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.line.CMLineInfoOverlay;
import com.codenvy.ide.jseditor.client.gutter.Gutter;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;

import elemental.dom.Element;

/**
 * Gutter for codemirror editors.
 */
public class CodemirrorGutter implements Gutter {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(CodemirrorGutter.class.getSimpleName());

    /**
     * The editor instance
     */
    private final CMEditorOverlay editorOverlay;

    /**
     * The codemirror object.
     */
    private final CodeMirrorOverlay codeMirror;

    public CodemirrorGutter(final CodeMirrorOverlay codemirror, final CMEditorOverlay editorOverlay) {
        this.editorOverlay = editorOverlay;
        this.codeMirror = codemirror;
    }

    @Override
    public void addGutterItem(final int line, final String gutterId, final Element element,
                              final LineNumberingChangeCallback lineCallback) {
        this.editorOverlay.setGutterMarker(line, gutterId, element);
        this.codeMirror.on(editorOverlay, EventTypes.CHANGE,
                           new EventHandlerMixedParameters() {
                               @Override
                               public void onEvent(final JsArrayMixed params) {
                                   // 0->editor, 1->event object
                                   final CMChangeEventOverlay event = params.getObject(1);
                                   final JsArrayString newText = event.getText();
                                   final CMPositionOverlay from = event.getFrom();
                                   final CMPositionOverlay to = event.getTo();

                                   // if the first character of the line is not included, the (potential) line
                                   // numbering change only starts at the following line.
                                   int changeStart = from.getLine() + 1;

                                   int removedCount = 0;
                                   if (from.getLine() != to.getLine()) {
                                       // no lines were removed
                                       // don't count first line yet
                                       removedCount = Math.abs(from.getLine() - to.getLine()) - 1;
                                       if (from.getCharacter() == 0) {
                                           // start of first line is included, 'to' is on another line, so the line is deleted
                                           removedCount = removedCount + 1;
                                           changeStart = changeStart - 1;
                                       }
                                       // if 'to' is at the end of the line, the line is _not_ removed, just emptied
                                   }
                                   // else no lines were removed

                                   final int addedCount = newText.length() - 1;

                                   // only call back if there is a change in the lines
                                   if (removedCount > 0 || addedCount > 0) {
                                       LOG.fine("Line change from l." + changeStart + " removed " + removedCount + " added " + addedCount);
                                       lineCallback.onLineNumberingChange(changeStart,
                                                                          removedCount,
                                                                          addedCount);
                                   }
                               }

                           });
    }


    @Override
    public Element getGutterItem(final int line, final String gutterId) {
        final CMLineInfoOverlay lineInfo = this.editorOverlay.lineInfo(line);
        if (lineInfo == null) {
            LOG.fine("No lineInfo for line" + line);
            return null;
        }
        if (lineInfo.getGutterMarkers() == null) {
            LOG.fine("No gutter markers for line" + line);
            return null;
        }
        final CMGutterMarkersOverlay markers = lineInfo.getGutterMarkers();
        if (markers.hasMarker(gutterId)) {
            return markers.getMarker(gutterId);
        } else {
            LOG.fine("No markers found for gutter " + gutterId + "on line " + line);
            return null;
        }
    }

    @Override
    public void clearGutter(final String gutterId) {
        this.editorOverlay.clearGutter(gutterId);
    }

    public void addGutterItem(final int line, final String gutterId, final com.google.gwt.dom.client.Element element) {
        this.editorOverlay.setGutterMarker(line, gutterId, element);
    }

    public void removeGutterItem(final int line, final String gutterId) {
        this.editorOverlay.setGutterMarker(line, gutterId, (Element)null);
    }

    public void addGutterItem(final int line, final String gutterId, final elemental.dom.Element element) {
        this.editorOverlay.setGutterMarker(line, gutterId, element);
    }
}
