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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;

/**
 * Overlay on the CodeMirror javascript object.
 */
public class CodeMirrorOverlay extends JavaScriptObject {

    /**
     * Creates an editor instance using the global CodeMirror object.
     * @param element the element backing the editor
     * @return an editor instance
     */
    public final static native CMEditorOverlay createEditorGlobal(final Element element) /*-{
        return $wnd.CodeMirror(element, {});
    }-*/;

    /**
     * Creates an editor instance using the global CodeMirror object.
     * @param element the element backing the editor
     * @param options the editor options
     * @return an editor instance
     */
    public final static native CMEditorOverlay createEditorGlobal(final Element element,
                                                            final JavaScriptObject options) /*-{
        return $wnd.CodeMirror(element, options);
    }-*/;

    /**
     * Creates an editor instance.
     * @param element the element backing the editor
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(Element element) /*-{
        return this(element, options);
    }-*/;

    /**
     * Creates an editor instance using the given CodeMirror object.
     * @param element the element backing the editor
     * @param options the editor options
     * @return an editor instance
     */
    public final native CMEditorOverlay createEditor(Element element,
                                                            JavaScriptObject options) /*-{
        return this(element, options);
    }-*/;
}
