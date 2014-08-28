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
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMEditorOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.scroll.CMPixelRangeOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.scroll.CMScrollInfoOverlay;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;

public class CMEditorOverlay extends JavaScriptObject {

    protected CMEditorOverlay() {
    }

    public final native String getValue() /*-{
        return this.getValue();
    }-*/;

    public final native CMDocumentOverlay getDoc() /*-{
        return this.getDoc();
    }-*/;

    public final native void setValue(final String newValue) /*-{
        this.setValue(newValue);
    }-*/;

    public final native void setSize(final String newWidth, final String newHeight) /*-{
        this.setSize(newWidth, newHeight);
    }-*/;

    public final native void refresh() /*-{
        this.refresh();
    }-*/;

    public final native CMModeOverlay getModeAt(final CMPositionOverlay position) /*-{
        return this.getModeAt(position);
    }-*/;

    public final native CMModeOverlay getMode() /*-{
        return this.getMode();
    }-*/;

    public final native void showHint(CMHintOptionsOverlay options) /*-{
        this.showHint(options);
    }-*/;

    public final native void showHint() /*-{
        this.showHint();
    }-*/;

    /**
     * Change option value for the editor. Obviously only works for options that take a string value.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final String value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    /**
     * Change option value for the editor.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final JavaScriptObject value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    /**
     * Change option value for the editor.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final boolean value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    /**
     * Change option value for the editor.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final int value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    public final native String getStringOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final native boolean getBooleanOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final native int getIntOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final native CMEditorOptionsOverlay getOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final static native CMHintFunctionOverlay getHintFunction(String name) /*-{
        return $wnd.CodeMirror.hint.html;
    }-*/;

    public final static native CMEditorOverlay createEditor(final Element element) /*-{
        return $wnd.CodeMirror(element, {});
    }-*/;

    public final static native CMEditorOverlay createEditor(final Element element,
                                                            final JavaScriptObject options) /*-{
        return $wnd.CodeMirror(element, options);
    }-*/;

    public final static native CMEditorOverlay createEditor(Element element,
                                                            JavaScriptObject options,
                                                            JavaScriptObject module) /*-{
        return module(element, options);
    }-*/;

    // events handling - the cm.off(...) method is not that easy to do...

    public final native <T extends JavaScriptObject> void on(String eventType, EventHandlerMultipleParameters<T> handler) /*-{
        this
                .on(
                        eventType,
                        function() {
                            var params = [];
                            for (var i = 0; i < arguments.length; i++) {
                                params.push(arguments[i]);
                            }
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerMultipleParameters::onEvent(*)(params);
                        });
    }-*/;

    public final native void on(String eventType, EventHandlerNoParameters handler) /*-{
        this
                .on(
                        eventType,
                        function() {
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerNoParameters::onEvent()();
                        });
    }-*/;

    public final native <T extends JavaScriptObject> void on(String eventType, EventHandlerOneParameter<T> handler) /*-{
        this
                .on(
                        eventType,
                        function(param) {
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerOneParameter::onEvent(*)(param);
                        });
    }-*/;

    public interface EventHandlerNoParameters {
        void onEvent();
    }

    public interface EventHandlerOneParameter<T extends JavaScriptObject> {
        void onEvent(T param);
    }

    public interface EventHandlerMultipleParameters<T extends JavaScriptObject> {
        void onEvent(JsArray<T> param);
    }

    // editor element

    public final native Element getWrapperElement() /*-{
        return this.getWrapperElement();
    }-*/;
    
    public final static native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                                    String eventType,
                                                                    EventHandlerOneParameter<T> handler,
                                                                    JavaScriptObject module) /*-{
        module
                .on(
                        eventType,
                        function(param) {
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerOneParameter::onEvent(*)(param);
                        });
    }-*/;
    
    public final static native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                                    String eventType,
                                                                    EventHandlerNoParameters handler,
                                                                    JavaScriptObject module) /*-{
        module
                .on(
                        eventType,
                        function(param) {
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerNoParameters::onEvent()();
                        });
    }-*/;
    
    public final static native <T extends JavaScriptObject> void on(JavaScriptObject instance,
                                                                    String eventType,
                                                                    EventHandlerMultipleParameters<T> handler,
                                                                    JavaScriptObject module) /*-{
        module
                .on(
                        eventType,
                        function() {
                            var params = [];
                            for (var i = 0; i < arguments.length; i++) {
                                params.push(arguments[i]);
                            }
                            handler.@com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay.EventHandlerMultipleParameters::onEvent(*)();
                        });
    }-*/;

    /**
     * Version of codemirror.
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return the version, major.minor.patch (all three are integers)
     */
    public final static native String version(JavaScriptObject module) /*-{
        return module.version();
    }-*/;

    /**
     * CodeMirror modes by name.
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return a javascript object such that modes[modeName] is the mode object
     */
    public final static native JavaScriptObject modes(JavaScriptObject module) /*-{
        return module.modes;
    }-*/;

    /**
     * Names of the modes loaded in codemirror.
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return an array of names of modes
     */
    public final static native JsArrayString modeNames(JavaScriptObject module) /*-{
        return Object.getOwnPropertyNames(module.modes).sort();
    }-*/;

    /**
     * Codemirror modes by mime-types.
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return a javascript object such that mimeModes[mimeType] is the matching mode object
     */
    public final static native JavaScriptObject mimeModes(JavaScriptObject module) /*-{
        return module.mimeModes;
    }-*/;

    /**
     * Names of the mime-types known in codemirror.
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return an array of names of mime-types
     */
    public final static native JsArrayString mimeModeNames(JavaScriptObject module) /*-{
        return Object.getOwnPropertyNames(module.mimeModes).sort();
    }-*/;

    /**
     * Returns the default configuration object for new codemirror editors.<br>
     * This object properties can be modified to change the default options for new editors (but will not change existing ones).
     * 
     * @param module the CodeMirror module (loaded by requirejs)
     * @return the default configuration
     */
    public final static native CMEditorOptionsOverlay defaults(JavaScriptObject module) /*-{
        return module.defaults;
    }-*/;

    public final static native CMKeymapSetOverlay keyMap(JavaScriptObject module) /*-{
        return module.keyMap;
    }-*/;

    public final native void addKeyMap(CMKeymapOverlay keymap) /*-{
        this.addKeyMap(keymap);
    }-*/;

    public final native void addKeyMap(CMKeymapOverlay keymap, boolean bottom) /*-{
        this.addKeyMap(keymap, bottom);
    }-*/;

    public final native CMScrollInfoOverlay getScrollInfo() /*-{
        return this.getScrollInfo();
    }-*/;

    /**
     * Scroll the cursor into view.
     */
    public final native void scrollIntoView() /*-{
        this.scrollIntoView(null);
    }-*/;

    /**
     * Scroll the cursor into view.
     * @param margin the amount of vertical pixel around the area to make visible as well.
     */
    public final native void scrollIntoView(double margin) /*-{
        this.scrollIntoView(null, margin);
    }-*/;

    /**
     * Scroll the given line, character position into view.
     */
    public final native void scrollIntoView(CMPositionOverlay what) /*-{
        this.scrollIntoView(what);
    }-*/;

    /**
     * Scroll the given line, character position into view.
     * @param margin the amount of vertical pixel around the area to make visible as well.
     */
    public final native void scrollIntoView(CMPositionOverlay what, double margin) /*-{
        this.scrollIntoView(what, margin);
    }-*/;

    /**
     * Scroll the given rectangular zone into view.
     */
    public final native void scrollIntoView(CMPixelRangeOverlay what) /*-{
        this.scrollIntoView(what);
    }-*/;

    /**
     * Scroll the given rectangular zone into view.
     * @param margin the amount of vertical pixel around the area to make visible as well.
     */
    public final native void scrollIntoView(CMPixelRangeOverlay what, double margin) /*-{
        this.scrollIntoView(what, margin);
    }-*/;

    /**
     * Scroll the given text range into view.
     */
    public final native void scrollIntoView(CMRangeOverlay what) /*-{
        this.scrollIntoView(what);
    }-*/;

    /**
     * Scroll the given text range into view.
     * @param margin the amount of vertical pixel around the area to make visible as well.
     */
    public final native void scrollIntoView(CMRangeOverlay what, double margin) /*-{
        this.scrollIntoView(what, margin);
    }-*/;

    /**
     * Returns the position and dimensions of an arbitrary character.
     * @param position the character position
     * @return the position and dimensions of the character
     */
    public final native CMPixelRangeOverlay charCoords(CMPositionOverlay position) /*-{
        return this.charCoords(position);
    }-*/;

    /**
     * Returns the position and dimensions of an arbitrary character.
     * @param position the character position
     * @param mode context of the coordinates; window, page (default) or local(top-left corner of document)
     * @return the position and dimensions of the character
     */
    public final native CMPixelRangeOverlay charCoords(CMPositionOverlay position, String mode) /*-{
        return this.charCoords(position, mode);
    }-*/;

    /**
     * Given an {left, top} object, returns the {line, ch} position that corresponds to it. 
     * @param coordinates the pixel coordinates
     * @return the {line, char} position 
     */
    public final native CMPositionOverlay coordsChar(CMPixelCoordinatesOverlay coordinates) /*-{
        return this.coordChar(coordinates);
    }-*/;

    /**
     * Given an {left, top} object, returns the {line, ch} position that corresponds to it, relative to the mode. 
     * @param coordinates the pixel coordinates
     * @param mode window,page or local
     * @return the {line, char} position 
     */
    public final native CMPositionOverlay coordsChar(CMPixelCoordinatesOverlay coordinates, String mode) /*-{
        return this.coordChar(coordinates, mode);
    }-*/;
}
