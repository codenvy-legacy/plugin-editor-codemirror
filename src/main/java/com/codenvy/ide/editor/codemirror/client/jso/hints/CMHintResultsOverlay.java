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

import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

public class CMHintResultsOverlay extends JavaScriptObject {

    protected CMHintResultsOverlay() {
    }

    public final native CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    public final native CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    public final native JsArrayMixed getList() /*-{
        return this.list;
    }-*/;

    public final native boolean completionItemIsString(int i) /*-{
        return (this.list[i] instanceof String);
    }-*/;

    public final native String getCompletionItemAsString(int i) /*-{
        return this.list[i];
    }-*/;

    public final native CMCompletionOverlay getCompletionItemAsObject(int i) /*-{
        return this.list[i];
    }-*/;

}