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

import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPixelCoordinatesOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.scroll.CMPixelRangeOverlay;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument.TextPosition;
import com.codenvy.ide.jseditor.client.position.PositionConverter;

public class CodemirrorPositionConverter implements PositionConverter {

    private final CMEditorOverlay editorOverlay;

    public CodemirrorPositionConverter(final CMEditorOverlay editorOverlay) {
        this.editorOverlay = editorOverlay;
    }

    @Override
    public PixelCoordinates textToPixel(final TextPosition textPosition) {
        final CMPositionOverlay position = CMPositionOverlay.create(textPosition.getLine(),
                                                                    textPosition.getCharacter());
        return textToPixel(position);
    }

    private PixelCoordinates textToPixel(final CMPositionOverlay position) {
        final CMPixelRangeOverlay pixelPosition = this.editorOverlay.charCoords(position, "local");
        return new PixelCoordinates(pixelPosition.getLeft(),
                                    pixelPosition.getTop());
    }

    @Override
    public PixelCoordinates offsetToPixel(final int textOffset) {
        final CMPositionOverlay position = this.editorOverlay.getDoc().posFromIndex(textOffset);
        return textToPixel(position);
    }

    @Override
    public TextPosition pixelToText(final PixelCoordinates coordinates) {
        final CMPositionOverlay cmTextPos = pixelToCmText(coordinates);
        return new TextPosition(cmTextPos.getLine(), cmTextPos.getCharacter());
    }

    private CMPositionOverlay pixelToCmText(final PixelCoordinates coordinates) {
        final CMPixelCoordinatesOverlay cmPixel = CMPixelCoordinatesOverlay.create(coordinates.getX(),
                                                                                   coordinates.getY());
        return  this.editorOverlay.coordsChar(cmPixel, "local");
    }

    @Override
    public int pixelToOffset(final PixelCoordinates coordinates) {
        final CMPositionOverlay cmTextPos = pixelToCmText(coordinates);
        return this.editorOverlay.getDoc().indexFromPos(cmTextPos);
    }

}
