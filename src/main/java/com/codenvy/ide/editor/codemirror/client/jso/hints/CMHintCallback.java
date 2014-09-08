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
package com.codenvy.ide.editor.codemirror.client.jso.hints;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Callback for asynchronous completion proposal retrieval.
 */
public class CMHintCallback extends JavaScriptObject {

    protected CMHintCallback() {}

    public static final native CMHintCallback create(HintCallbackFunction callbackFun) /*-{
        return $entry(function(data) {
            callbackFun.@com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintCallback.HintCallbackFunction::callback(Lcom/codenvy/ide/editor/codemirror/client/jso/hints/CMHintResultsOverlay;)(data);
        });
    }-*/;

    public final native void call(CMHintResultsOverlay data) /*-{
        this(data);
    }-*/;

    public interface HintCallbackFunction {
        void callback(CMHintResultsOverlay data);
    }
}
