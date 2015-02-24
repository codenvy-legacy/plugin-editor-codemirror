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
package com.codenvy.ide.editor.codemirror.client;

import java.util.Arrays;
import java.util.List;

import com.codenvy.api.promises.client.Operation;
import com.codenvy.api.promises.client.OperationException;
import com.codenvy.api.promises.client.Promise;
import com.codenvy.ide.editor.codemirror.base.client.AbstractCodemirrorInitializer;
import com.codenvy.ide.editor.codemirror.base.client.BaseCodemirrorInitializer;
import com.codenvy.ide.editor.codemirror.resources.client.BasePathConstant;
import com.codenvy.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import com.codenvy.ide.jseditor.client.requirejs.RequireJsLoader;
import com.google.inject.Inject;

public class FullCodemirrorInitializer extends AbstractCodemirrorInitializer {

    private final FullCodemirrorPromise fullCodemirrorPromise;
    private final BaseCodemirrorInitializer baseCodemirrorInitializer;
    private Initializer initializer;

    @Inject
    public FullCodemirrorInitializer(final RequireJsLoader requireJsLoader,
                                     final BasePathConstant basePathConstant,
                                     final FullCodemirrorPromise fullCodemirrorPromise,
                                     final BaseCodemirrorInitializer baseCodemirrorInitializer) {
        super(requireJsLoader, basePathConstant);

        this.fullCodemirrorPromise = fullCodemirrorPromise;
        this.baseCodemirrorInitializer = baseCodemirrorInitializer;
    }

    public Promise<CodeMirrorOverlay> init() {
        if (this.fullCodemirrorPromise == null) {
            this.baseCodemirrorInitializer.init();
            // can be done separately, if the scripts are already loading, requirejs will take that into account
            final Promise<CodeMirrorOverlay> result = initScripts(getScripts());
            result.then(new Operation<CodeMirrorOverlay>() {
                @Override
                public void apply(final CodeMirrorOverlay codemirror) throws OperationException {
                    initializer.init();
                }
            });
            this.fullCodemirrorPromise.setPromise(result);
        }
        return this.fullCodemirrorPromise.getPromise();
    }

    @Override
    protected List<String> getScripts() {
        return Arrays.asList(new String[]{
                                          // /lib/codemirror added elsewhere
                                          // library of modes
                                          "mode/meta",


                                          /* We will preload modes that have extensions */
                                          // language modes
                                          "mode/xml/xml",
                                          "mode/htmlmixed/htmlmixed", // must be defined after xml

                                          "mode/javascript/javascript",
                                          "mode/coffeescript/coffeescript",

                                          "mode/css/css",

                                          "mode/sql/sql",

                                          "mode/clike/clike",

                                          "mode/markdown/markdown",
                                          "mode/gfm/gfm", // markdown extension for github

                                          // hints
                                          "addon/hint/show-hint",
                                          "addon/hint/xml-hint",
                                          "addon/hint/html-hint",
                                          "addon/hint/javascript-hint",
                                          "addon/hint/css-hint",
                                          "addon/hint/anyword-hint",
                                          "addon/hint/sql-hint",

                                          // pair matching
                                          "addon/edit/closebrackets",
                                          "addon/edit/closetag",
                                          "addon/edit/matchbrackets",
                                          "addon/edit/matchtags",
                                          // the two following are added to repair actual functionality in 'classic' editor
                                          "addon/selection/mark-selection",
                                          "addon/selection/active-line",

                                          // for search
                                          "addon/search/search",
                                          "addon/dialog/dialog",
                                          "addon/search/searchcursor",
                                          "addon/search/match-highlighter",
                                          // comment management
                                          "addon/comment/comment",
                                          "addon/comment/continuecomment",
                                          // folding
                                          "addon/fold/foldcode",
                                          "addon/fold/foldgutter",
                                          "addon/fold/brace-fold",
                                          "addon/fold/xml-fold", // also required by matchbrackets and closebrackets
                                          "addon/fold/comment-fold",
                                          "addon/fold/indent-fold",
                                          "addon/fold/markdown-fold",

                                          "addon/scroll/simplescrollbars",
                                          "addon/scroll/annotatescrollbar",
                                          "addon/scroll/scrollpastend",
                                          "addon/search/matchesonscrollbar",
        });
    }

    public void setInitializer(final Initializer initializer) {
        this.initializer = initializer;
    }

    public interface Initializer {
        void init();
    }
}
