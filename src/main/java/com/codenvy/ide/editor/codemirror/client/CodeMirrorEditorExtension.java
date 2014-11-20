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
package com.codenvy.ide.editor.codemirror.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.editor.codemirror.client.style.CodeMirrorResource;
import com.codenvy.ide.jseditor.client.codeassist.CompletionResources;
import com.codenvy.ide.jseditor.client.defaulteditor.EditorBuilder;
import com.codenvy.ide.jseditor.client.editorconfig.DefaultTextEditorConfiguration;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.requirejs.RequireJsLoader;
import com.codenvy.ide.jseditor.client.requirejs.RequirejsErrorHandler.RequireError;
import com.codenvy.ide.jseditor.client.texteditor.AbstractEditorModule.EditorInitializer;
import com.codenvy.ide.jseditor.client.texteditor.AbstractEditorModule.InitializerCallback;
import com.codenvy.ide.jseditor.client.texteditor.ConfigurableTextEditor;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.inject.Inject;

@Extension(title = "CodeMirror Editor", version = "1.1.0")
public class CodeMirrorEditorExtension {

    /** The logger. */
    private static final Logger  LOG = Logger.getLogger(CodeMirrorEditorExtension.class.getSimpleName());
    /** The editor type key. */
    public static final String                CODEMIRROR_EDITOR_KEY = "codemirror";

    /** The codemirror javascript module key. */
    public static final String                CODEMIRROR_MODULE_KEY = "CodeMirror";

    private final NotificationManager         notificationManager;
    private final ModuleHolder                moduleHolder;
    private final RequireJsLoader             requireJsLoader;
    private final EditorTypeRegistry          editorTypeRegistry;
    private final CodeMirrorEditorModule      editorModule;

    private final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory;

    private boolean initFailedWarnedOnce = false;

    @Inject
    public CodeMirrorEditorExtension(final EditorTypeRegistry editorTypeRegistry,
                                     final ModuleHolder moduleHolder,
                                     final RequireJsLoader requireJsLoader,
                                     final NotificationManager notificationManager,
                                     final CodeMirrorEditorModule editorModule,
                                     final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory,
                                     final CodeMirrorResource highlightResource,
                                     final CompletionResources completionResources) {
        this.notificationManager = notificationManager;
        this.moduleHolder = moduleHolder;
        this.requireJsLoader = requireJsLoader;
        this.editorModule = editorModule;
        this.editorTypeRegistry = editorTypeRegistry;
        this.codeMirrorTextEditorFactory = codeMirrorTextEditorFactory;

        highlightResource.highlightStyle().ensureInjected();
        highlightResource.editorStyle().ensureInjected();
        highlightResource.dockerfileModeStyle().ensureInjected();
        highlightResource.gutterStyle().ensureInjected();

        completionResources.completionCss().ensureInjected();

        Log.info(CodeMirrorEditorExtension.class, "Codemirror extension module=" + editorModule);
        editorModule.setEditorInitializer(new EditorInitializer() {
            @Override
            public void initialize(final InitializerCallback callback) {
                // add code-splitting of the whole orion editor
                GWT.runAsync(new RunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        injectCodeMirror(callback);
                    }
                    @Override
                    public void onFailure(final Throwable reason) {
                        callback.onFailure(reason);
                    }
                });
            }
        });
        // must not delay
        registerEditor();
        CodeMirrorKeymaps.init();
    }

    private void injectCodeMirror(final InitializerCallback callback) {
        /*
         * This could be simplified and optimized with a all-in-one minified js from http://codemirror.net/doc/compress.html but at least
         * while debugging, unmodified source is necessary. Another option would be to include all-in-one minified along with a source map
         */
        final String CODEMIRROR_BASE = "codemirror/";
        final String[] scripts = new String[]{

                // base script
                CODEMIRROR_BASE + "lib/codemirror",

                // language modes
                CODEMIRROR_BASE + "mode/xml/xml",
                CODEMIRROR_BASE + "mode/htmlmixed/htmlmixed", // must be defined after xml
                CODEMIRROR_BASE + "mode/htmlembedded/htmlembedded",
                CODEMIRROR_BASE + "mode/dtd/dtd",

                CODEMIRROR_BASE + "mode/javascript/javascript",
                CODEMIRROR_BASE + "mode/coffeescript/coffeescript",

                CODEMIRROR_BASE + "mode/css/css",
                CODEMIRROR_BASE + "mode/sass/sass",

                CODEMIRROR_BASE + "mode/sql/sql",
                CODEMIRROR_BASE + "mode/properties/properties",
                CODEMIRROR_BASE + "mode/yaml/yaml",
                CODEMIRROR_BASE + "mode/diff/diff",
                CODEMIRROR_BASE + "mode/shell/shell",

                CODEMIRROR_BASE + "mode/clike/clike",
                CODEMIRROR_BASE + "mode/clojure/clojure",
                CODEMIRROR_BASE + "mode/groovy/groovy",
                CODEMIRROR_BASE + "mode/kotlin/kotlin",

                CODEMIRROR_BASE + "mode/stex/stex",
                CODEMIRROR_BASE + "mode/markdown/markdown",
                CODEMIRROR_BASE + "mode/gfm/gfm", // markdown extension for github

                CODEMIRROR_BASE + "mode/slim/slim",

                CODEMIRROR_BASE + "mode/php/php",
                CODEMIRROR_BASE + "mode/python/python",
                CODEMIRROR_BASE + "mode/ruby/ruby",
                CODEMIRROR_BASE + "mode/go/go",
                CODEMIRROR_BASE + "mode/lua/lua",
                CODEMIRROR_BASE + "mode/perl/perl",
                CODEMIRROR_BASE + "mode/r/r",
                CODEMIRROR_BASE + "mode/rust/rust",
                CODEMIRROR_BASE + "mode/tcl/tcl",

                CODEMIRROR_BASE + "mode/commonlisp/commonlisp",
                CODEMIRROR_BASE + "mode/haskell/haskell",
                CODEMIRROR_BASE + "mode/erlang/erlang",
                CODEMIRROR_BASE + "mode/scheme/scheme",
                CODEMIRROR_BASE + "mode/mllike/mllike",

                CODEMIRROR_BASE + "mode/cobol/cobol",
                CODEMIRROR_BASE + "mode/fortran/fortran",
                CODEMIRROR_BASE + "mode/pascal/pascal",
                CODEMIRROR_BASE + "mode/smalltalk/smalltalk",
                CODEMIRROR_BASE + "mode/vb/vb",
                CODEMIRROR_BASE + "mode/vbscript/vbscript",

                CODEMIRROR_BASE + "mode/puppet/puppet",
                CODEMIRROR_BASE + "mode/dockerfile/dockerfile",

                // hints
                CODEMIRROR_BASE + "addon/hint/show-hint",
                CODEMIRROR_BASE + "addon/hint/xml-hint",
                CODEMIRROR_BASE + "addon/hint/html-hint",
                CODEMIRROR_BASE + "addon/hint/javascript-hint",
                CODEMIRROR_BASE + "addon/hint/css-hint",
                CODEMIRROR_BASE + "addon/hint/anyword-hint",
                CODEMIRROR_BASE + "addon/hint/sql-hint",

                // pair matching
                CODEMIRROR_BASE + "addon/edit/closebrackets",
                CODEMIRROR_BASE + "addon/edit/closetag",
                CODEMIRROR_BASE + "addon/edit/matchbrackets",
                CODEMIRROR_BASE + "addon/edit/matchtags",
                // the two following are added to repair actual functionality in 'classic' editor
                CODEMIRROR_BASE + "addon/selection/mark-selection",
                CODEMIRROR_BASE + "addon/selection/active-line",
                // editor keymaps
                CODEMIRROR_BASE + "keymap/emacs",
                CODEMIRROR_BASE + "keymap/vim",
                CODEMIRROR_BASE + "keymap/sublime",
                // for search
                CODEMIRROR_BASE + "addon/search/search",
                CODEMIRROR_BASE + "addon/dialog/dialog",
                CODEMIRROR_BASE + "addon/search/searchcursor",
                CODEMIRROR_BASE + "addon/search/match-highlighter",
                // comment management
                CODEMIRROR_BASE + "addon/comment/comment",
                CODEMIRROR_BASE + "addon/comment/continuecomment",
                // folding
                CODEMIRROR_BASE + "addon/fold/foldcode",
                CODEMIRROR_BASE + "addon/fold/foldgutter",
                CODEMIRROR_BASE + "addon/fold/brace-fold",
                CODEMIRROR_BASE + "addon/fold/xml-fold", // also required by matchbrackets and closebrackets
                CODEMIRROR_BASE + "addon/fold/comment-fold",
                CODEMIRROR_BASE + "addon/fold/indent-fold",
                CODEMIRROR_BASE + "addon/fold/markdown-fold",
        };


        this.requireJsLoader.require(new Callback<Void, Throwable>() {
            @Override
            public void onSuccess(final Void result) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(final Throwable e) {
                if (e instanceof JavaScriptException) {
                    final JavaScriptException jsException = (JavaScriptException)e;
                    final Object nativeException = jsException.getThrown();
                    if (nativeException instanceof RequireError) {
                        final RequireError requireError = (RequireError)nativeException;
                        final String errorType = requireError.getRequireType();
                        String message = "Codemirror injection failed: " + errorType + " ";
                        final JsArrayString modules = requireError.getRequireModules();
                        if (modules != null) {
                            message += modules.join(",");
                        }
                        Log.debug(CodeMirrorEditorExtension.class, message);
                    }
                }
                initializationFailed(callback, "Unable to initialize CodeMirror", e);
            }
        }, scripts, new String[]{CODEMIRROR_MODULE_KEY});

        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "lib/codemirror.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "addon/dialog/dialog.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "addon/fold/foldgutter.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "addon/hint/show-hint.css");

    }

    private static void injectCssLink(final String url) {
        LinkElement link = Document.get().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachToHead(link);
    }

    /**
     * Attach an element to document head.
     * 
     * @param scriptElement the element to attach
     */
    private static native void nativeAttachToHead(JavaScriptObject scriptElement) /*-{
        $doc.getElementsByTagName("head")[0].appendChild(scriptElement);
    }-*/;

    private void registerEditor() {
        LOG.fine("Registering CodeMirror editor type.");
        this.editorTypeRegistry.registerEditorType(EditorType.fromKey(CODEMIRROR_EDITOR_KEY), "CodeMirror", new EditorBuilder() {

            @Override
            public ConfigurableTextEditor buildEditor() {
                final EmbeddedTextEditorPresenter<CodeMirrorEditorWidget> editor = codeMirrorTextEditorFactory.createTextEditor();
                editor.initialize(new DefaultTextEditorConfiguration(), notificationManager);
                return editor;
            }
        });
    }

    private void initializationFailed(final InitializerCallback callback, final String errorMessage, Throwable e) {
        if (this.initFailedWarnedOnce) {
            return;
        }
        this.initFailedWarnedOnce = true;

        this.notificationManager.showNotification(new Notification(errorMessage, Type.ERROR));
        this.notificationManager.showNotification(new Notification("CodeMirror editor is not available", Type.WARNING));
        LOG.log(Level.SEVERE, errorMessage + " - ", e);
        callback.onFailure(e);
    }
}
