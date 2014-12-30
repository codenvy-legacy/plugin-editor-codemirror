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

/**
 * Defines the codemirror resources path.
 */
public final class CodeMirrorBasePath {

    /**
     * The base path (in the source file, it should contain version placeholders, evaluated during compilation).
     */
    private final static String BASE_PATH = "codemirror-${codemirror.upstream.version}-${codemirror.plugin.revision}/";

    private CodeMirrorBasePath() {}

    /**
     * Returns the base path for codemirror resources.
     * @return the base path
     */
    public static String basePath() {
        return BASE_PATH;
    }
}