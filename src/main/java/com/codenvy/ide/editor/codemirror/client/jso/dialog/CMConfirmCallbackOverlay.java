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
package com.codenvy.ide.editor.codemirror.client.jso.dialog;

import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.google.gwt.core.client.JavaScriptObject;

public class CMConfirmCallbackOverlay extends JavaScriptObject {

    protected CMConfirmCallbackOverlay() {}

    public static final native CMConfirmCallbackOverlay create(ConfirmCallback callback) /*-{
        return function(editor) {
            callback.@com.codenvy.ide.editor.codemirror.client.jso.dialog.CMConfirmCallbackOverlay.ConfirmCallback::onConfirm(Lcom/codenvy/ide/editor/codemirror/client/jso/CMEditorOverlay;)(editor);
        };
    }-*/;

    public interface ConfirmCallback {
        void onConfirm(CMEditorOverlay editor);
    }
}
