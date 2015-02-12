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
package com.codenvy.ide.editor.codemirror.client.jso;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A codemirror command function.
 */
public class CMCommandOverlay extends JavaScriptObject {

    /** JSO mandated construcotr. */
    protected CMCommandOverlay() {
    }

    /** Creates a command function instance from a {@link CommandFunction} java instance. */
    public static final native CMCommandOverlay create(CommandFunction commandFunc) /*-{
        return $entry(function(editor) {
            commandFunc.@com.codenvy.ide.editor.codemirror.client.jso.CMCommandOverlay.CommandFunction::execCommand(Lcom/codenvy/ide/editor/codemirror/client/jso/CMEditorOverlay;)(editor);
        });
    }-*/;

    /** Interface describing a command function for the java side. */
    public interface CommandFunction {
        void execCommand(CMEditorOverlay editor);
    }
}
