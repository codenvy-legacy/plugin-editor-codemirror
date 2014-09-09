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

import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMEditorOptionsOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

/**
 * Overlay on the CodeMirror javascript object.
 */
public class CodeMirrorOverlay extends JavaScriptObject {

    protected CodeMirrorOverlay() {}

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
    public final static native CMEditorOverlay createEditorGlobal(Element element,
                                                                  JavaScriptObject options) /*-{
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

    /**
     * Version of codemirror.
     *
     * @return the version, major.minor.patch (all three are integers)
     */
    public final static native String version() /*-{
        return this.version();
    }-*/;

    /**
     * Returns the default configuration object for new codemirror editors.<br>
     * This object properties can be modified to change the default options for new editors (but will not change existing ones).
     *
     * @return the default configuration
     */
    public final native CMEditorOptionsOverlay defaults() /*-{
        return this.defaults;
    }-*/;

    /**
     * CodeMirror modes by name.
     *
     * @return a javascript object such that modes[modeName] is the mode object
     */
    public final native JavaScriptObject modes() /*-{
        return this.modes;
    }-*/;

    /**
     * Names of the modes loaded in codemirror.
     *
     * @return an array of names of modes
     */
    public final native JsArrayString modeNames() /*-{
        return Object.getOwnPropertyNames(this.modes).sort();
    }-*/;

    /**
     * Codemirror modes by mime-types.
     *
     * @return a javascript object such that mimeModes[mimeType] is the matching mode object
     */
    public final native JavaScriptObject mimeModes() /*-{
        return this.mimeModes;
    }-*/;

    /**
     * Names of the mime-types known in codemirror.
     *
     * @return an array of names of mime-types
     */
    public final native JsArrayString mimeModeNames() /*-{
        return Object.getOwnPropertyNames(this.mimeModes).sort();
    }-*/;

    /**
     * Return the registered keymaps.
     * @return the keymaps
     */
    public final native CMKeymapSetOverlay keyMap() /*-{
        return this.keyMap;
    }-*/;

    /**
     * Returns the list of key names by code.
     * @return the key names
     */
    public final native JsArrayString keyNames() /*-{
        return this.keyNames;
    }-*/;

    /**
     * Tells in the showHint method is available on the CodeMirror object.
     * @param module the CodeMirror object
     * @return true iff CodeMirror.showHint is defined
     */
    public static final native boolean hasShowHint(JavaScriptObject module) /*-{
        return (("showHint" in module) && !(typeof(module[showHint]) === 'undefined'));
    }-*/;

    /**
     * Returns the hint function matching the given name.
     * @param name the name of the function
     * @return the hint function
     */
    public final static native CMHintFunctionOverlay getHintFunction(String name) /*-{
        return this.hint[name];
    }-*/;

}