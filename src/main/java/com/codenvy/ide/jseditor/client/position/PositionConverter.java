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
package com.codenvy.ide.jseditor.client.position;

import com.codenvy.ide.jseditor.client.document.EmbeddedDocument.TextPosition;

/**
 * Conversion utility for text/pixel coordinates.
 * @author "Mickaël Leduque"
 *
 */
public interface PositionConverter {

    PixelCoordinates textToPixel(TextPosition textPosition);
    PixelCoordinates offsetToPixel(int textOffset);
    TextPosition pixelToText(PixelCoordinates coordinates);
    int pixelToOffset(PixelCoordinates coordinates);
    
    public static final class PixelCoordinates {
        private final int x;
        private final int y;
        
        public PixelCoordinates(final int x, final int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "PixelCoordinates [x=" + x + ", y=" + y + "]";
        }
    }
}
