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

public class CMCompletionOverlay extends JavaScriptObject {

    protected CMCompletionOverlay() {
    }

    public native final String getText() /*-{
        return this.text;
    }-*/;

    public native final void setText(String text) /*-{
        this.text = text;
    }-*/;

    public native final String getDisplayText() /*-{
        return this.displayText;
    }-*/;

    public native final void setDisplayText(String displayText) /*-{
        this.displayText = displayText;
    }-*/;

    public native final String getClassName() /*-{
        return this.className;
    }-*/;

    public native final void setClassName(String className) /*-{
        this.className = className;
    }-*/;

    public native final CMRenderFunctionOverlay getRender() /*-{
        return this.render;
    }-*/;

    public native final void setRender(CMRenderFunctionOverlay render) /*-{
        this.render = render;
    }-*/;

    public native final CMHintApplyOverlay getHint() /*-{
        return this.hint;
    }-*/;

    public native final void setHint(CMHintApplyOverlay hint) /*-{
        this.hint = hint;
    }-*/;

    public native final CMPositionOverlay getFrom() /*-{
        return this.from;
    }-*/;

    public native final void setFrom(CMPositionOverlay from) /*-{
        this.from = from;
    }-*/;

    public native final CMPositionOverlay getTo() /*-{
        return this.to;
    }-*/;

    public native final void setTo(CMPositionOverlay to) /*-{
        this.to = to;
    }-*/;
}
