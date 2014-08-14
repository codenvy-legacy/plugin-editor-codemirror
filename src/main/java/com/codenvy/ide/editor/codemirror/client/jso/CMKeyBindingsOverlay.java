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
package com.codenvy.ide.editor.codemirror.client.jso;

import com.codenvy.ide.editor.codemirror.client.CodeMirrorEditorWidget;
import com.codenvy.ide.editor.codemirror.client.KeyBindingAction;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * A list of key bindings.
 * 
 * @author "Mickaël Leduque"
 */
public class CMKeyBindingsOverlay extends JavaScriptObject {

    protected CMKeyBindingsOverlay() {
    }

    public final native void addBinding(String keySpec, KeyBindingAction keyBindingAction, CodeMirrorEditorWidget editorWidget) /*-{
        this[keySpec] = function(editor) {
            keyBindingAction.@com.codenvy.ide.editor.codemirror.client.KeyBindingAction::action(*)(editorWidget);
        }
    }-*/;

    public final static native CMKeyBindingsOverlay create() /*-{
        return {};
    }-*/;
}
