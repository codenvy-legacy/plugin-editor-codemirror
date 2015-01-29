/*******************************************************************************
 * Copyright (c) 2014-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.editor.codemirror.client.jso.hints;

import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
import com.google.gwt.core.client.JavaScriptObject;

public class CMHintFunctionOverlay extends JavaScriptObject {

    protected CMHintFunctionOverlay() {
    }

    public final native CMHintResultsOverlay apply(CMEditorOverlay editor, CMHintOptionsOverlay options) /*-{
        return this(editor, options);
    }-*/;

    public final native CMHintResultsOverlay apply(CMEditorOverlay editor) /*-{
        return this(editor);
    }-*/;

    public static final native CMHintFunctionOverlay createFromName(CodeMirrorOverlay codeMirror, String funcName) /*-{
        return codeMirror.hint[funcName];
    }-*/;

    public static final native CMHintFunctionOverlay createFromHintFunction(HintFunction hintFunction) /*-{
        return function(editor, options) {
            return hintFunction.@com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay.HintFunction::getHints(*)(editor, options);
        };
    }-*/;

    public static final native CMHintFunctionOverlay createFromAsyncHintFunction(AsyncHintFunction hintFunction) /*-{
        var result = function(editor, callback, options) {
            return hintFunction.@com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay.AsyncHintFunction::getHints(*)(editor, callback, options);
        };
        result.async=true;
        return result;
    }-*/;

    public interface HintFunction {
        CMHintResultsOverlay getHints(CMEditorOverlay editor, CMHintOptionsOverlay options);
    }

    public interface AsyncHintFunction {
        void getHints(CMEditorOverlay editor, CMHintCallback callback,
                                  CMHintOptionsOverlay options);
    }
}
