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

import com.google.gwt.core.client.JavaScriptObject;

public class CMDialogCallbackOverlay extends JavaScriptObject {

    protected CMDialogCallbackOverlay() {}

    public static final native CMDialogCallbackOverlay create(DialogCallback callback) /*-{
        return function(value) {
            callback.@com.codenvy.ide.editor.codemirror.client.jso.dialog.CMDialogCallbackOverlay.DialogCallback::onInputDone(Ljava/lang/String;)(value);
        };
    }-*/;

    public interface DialogCallback {
        void onInputDone(String value);
    }
}
