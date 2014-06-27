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

import com.codenvy.ide.editor.codemirror.client.jso.CMKeymapOverlay;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Overlay class over CodeMirror's completion options objects.
 * 
 * @author "Mickaël Leduque"
 */
public class CMHintOptionsOverlay extends JavaScriptObject {

    protected CMHintOptionsOverlay() {
    }

    public final native CMHintFunctionOverlay getHint() /*-{
        return this.hint;
    }-*/;

    public final native void setHint(CMHintFunctionOverlay hintFunction) /*-{
        this.hint = hintFunction;
    }-*/;

    public final native boolean getCompleteSingle() /*-{
        return this.completeSingle;
    }-*/;

    public final native void setCompleteSingle(boolean newValue) /*-{
        this.completeSingle = newValue;
    }-*/;

    public final native boolean getAlignWithWord() /*-{
        return this.alignWithWord;
    }-*/;

    public final native void setAlignWithWord(boolean newValue) /*-{
        this.alignWithWord = newValue;
    }-*/;

    public final native boolean getCloseOnUnfocus() /*-{
        return this.closeOnUnfocus;
    }-*/;

    public final native void setCloseOnUnfocus(boolean newValue) /*-{
        this.closeOnUnfocus = newValue;
    }-*/;

    public final native CMKeymapOverlay getCustomKeys() /*-{
        return this.customKeys;
    }-*/;

    public final native void setCustomKeys(CMKeymapOverlay newValue) /*-{
        this.customKeys = newValue;
    }-*/;

    public final native CMKeymapOverlay getExtraKeys() /*-{
        return this.extraKeys;
    }-*/;

    public final native void setExtraKeys(CMKeymapOverlay newValue) /*-{
        this.extraKeys = newValue;
    }-*/;

    public static final native CMHintOptionsOverlay create() /*-{
        return {};
    }-*/;
}
