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
package com.codenvy.ide.editor.codemirror.client.jso.event;

import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Argument of a (codemirror) native change event.
 * 
 * @author "Mickaël Leduque"
 */
public class CMBeforeChangeEventOverlay extends JavaScriptObject {

    protected CMBeforeChangeEventOverlay() {
    }

    public final native CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    public final native CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    public final native JsArrayString getText() /*-{
        return this.text;
    }-*/;

    public final native void cancel() /*-{
        this.cancel();
    }-*/;


    public final native boolean hasUpdate() /*-{
        return (typeof this.update === "function");
    }-*/;

    public final native String update() /*-{
        return this.update();
    }-*/;

    public final native String update(CMPositionOverlay from) /*-{
        return this.update(from);
    }-*/;

    public final native String update(CMPositionOverlay from, CMPositionOverlay to) /*-{
        return this.update(from, to);
    }-*/;

    public final native String update(CMPositionOverlay from, CMPositionOverlay to, JsArrayString text) /*-{
        return this.update(from, to, text);
    }-*/;
}
