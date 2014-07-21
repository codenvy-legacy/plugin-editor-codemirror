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
package com.codenvy.ide.editor.codemirror.client.jso.options;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Configuration object for the matchTag addon.<br>
 * 
 * @see https://codemirror.net/doc/manual.html#addon_matchtags
 * @see https://codemirror.net/addon/edit/matchbrackets.js
 * @author "Mickaël Leduque"
 */
public class CMMatchTagsConfig extends JavaScriptObject {

    protected CMMatchTagsConfig() {
    }

    public static native final CMMatchTagsConfig create() /*-{
        return {};
    }-*/;

    public final native void setBothTags(boolean bothTags) /*-{
        this.bothTags = bothTags;
    }-*/;

    public final native boolean getBothTags() /*-{
        return this.bothTags;
    }-*/;
}
