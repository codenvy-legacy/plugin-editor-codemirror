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
package com.codenvy.ide.jseditor.client.text;


/**
 * Oriented range of text.<br>
 */
public class TextRange {
    /** The start position of the range. */
    private final TextPosition from;

    /** The end position of the range. */
    private final TextPosition to;

    public TextRange(final TextPosition from, final TextPosition to) {
        this.from = from;
        this.to = to;
    }

    public TextPosition getFrom() {
        return this.from;
    }

    public TextPosition getTo() {
        return this.to;
    }
}
