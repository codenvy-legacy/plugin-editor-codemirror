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

import java.util.logging.Logger;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.editor.codemirror.client.style.CodeMirrorResource;
import com.codenvy.ide.jseditor.client.defaulteditor.EditorBuilder;
import com.codenvy.ide.jseditor.client.editorconfig.DefaultEmbeddedTextEditorConf;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.requirejs.RequireJsLoader;
import com.codenvy.ide.jseditor.client.requirejs.RequirejsCallback;
import com.codenvy.ide.jseditor.client.requirejs.RequirejsErrorHandler;
import com.codenvy.ide.jseditor.client.requirejs.RequirejsModule;
import com.codenvy.ide.jseditor.client.requirejs.config.BundlesConfigProperty;
import com.codenvy.ide.jseditor.client.requirejs.config.PathsConfigProperty;
import com.codenvy.ide.jseditor.client.requirejs.config.RequirejsConfig;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.inject.Inject;

@Extension(title = "CodeMirror Editor.", version = "1.0.0")
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

    private final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory;

    @Inject
    public CodeMirrorEditorExtension(final EditorTypeRegistry editorTypeRegistry,
                                     final ModuleHolder moduleHolder,
                                     final RequireJsLoader requireJsLoader,
                                     final NotificationManager notificationManager,
                                     final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory,
                                     final CodeMirrorResource highlightResource) {
        this.notificationManager = notificationManager;
        this.moduleHolder = moduleHolder;
        this.requireJsLoader = requireJsLoader;
        this.editorTypeRegistry = editorTypeRegistry;
        this.codeMirrorTextEditorFactory = codeMirrorTextEditorFactory;

        highlightResource.highlightStyle().ensureInjected();
        highlightResource.editorStyle().ensureInjected();

        injectCodeMirror();
        // no need to delay
        CodeMirrorKeymaps.init();
    }

    private void injectCodeMirror() {
        /*
         * This could be simplified and optimized with a all-in-one minified js from http://codemirror.net/doc/compress.html but at least
         * while debugging, unmodified source is necessary. Another option would be to include all-in-one minified along with a source map
         */
        final String CODEMIRROR_BASE = "codemirror/";
        final String[] scripts = new String[]{

                // base script
                "lib/codemirror",

                // language modes
                "mode/xml/xml",
                "mode/htmlmixed/htmlmixed", // must be defined after xml
                "mode/htmlembedded/htmlembedded",
                "mode/dtd/dtd",

                "mode/javascript/javascript",
                "mode/coffeescript/coffeescript",

                "mode/css/css",
                "mode/sass/sass",

                "mode/sql/sql",
                "mode/properties/properties",
                "mode/yaml/yaml",
                "mode/diff/diff",
                "mode/shell/shell",

                "mode/clike/clike",
                "mode/clojure/clojure",
                "mode/groovy/groovy",

                "mode/stex/stex",
                "mode/markdown/markdown",
                "mode/gfm/gfm", // markdown extension for github

                "mode/php/php",
                "mode/python/python",
                "mode/ruby/ruby",
                "mode/go/go",
                "mode/lua/lua",
                "mode/perl/perl",
                "mode/r/r",
                "mode/rust/rust",
                "mode/tcl/tcl",

                "mode/commonlisp/commonlisp",
                "mode/haskell/haskell",
                "mode/erlang/erlang",
                "mode/scheme/scheme",
                "mode/mllike/mllike",

                "mode/cobol/cobol",
                "mode/fortran/fortran",
                "mode/pascal/pascal",
                "mode/smalltalk/smalltalk",
                "mode/vb/vb",
                "mode/vbscript/vbscript",

                CODEMIRROR_BASE + "mode/puppet/puppet",

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
                // editor keymaps
                "keymap/emacs",
                "keymap/vim",
                "keymap/sublime",
                // for search
                "addon/search/search",
                "addon/dialog/dialog",
                "addon/search/searchcursor",
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
        };

        final RequirejsConfig config = RequirejsConfig.create();
        config.setContext("codemirror");


        final PathsConfigProperty paths = PathsConfigProperty.create();
        paths.put("codemirror", CODEMIRROR_BASE);
        config.setPaths(paths);

        JsArrayString deps = JsArrayString.createArray().cast();
        deps.push("require");
        config.setDeps(deps);


        final BundlesConfigProperty bundles = BundlesConfigProperty.create();
        final JsArrayString modules = JsArrayString.createArray().cast();
        for (final String script : scripts) {
            modules.push(script);
        }

        bundles.addBundle("codemirror-all", modules);
        config.setBundles(bundles);

        final RequirejsCallback callback = new RequirejsCallback() {
            @Override
            public void onReady(final JsArray<RequirejsModule> modules) {
                registerEditor();
            }
        };
        final RequirejsErrorHandler errorHandler = new RequirejsErrorHandler() {

            @Override
            public void onError(final RequireError error) {
                Log.error(CodeMirrorEditorExtension.class, "Unable to inject CodeMirror", new JavaScriptException(error));
                initializationFailed("Unable to inject CodeMirror main script");
            }
        };
        this.requireJsLoader.require(callback, errorHandler, config, scripts, new String[]{CODEMIRROR_MODULE_KEY});

        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "lib/codemirror.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "addon/dialog/dialog.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "addon/fold/foldgutter.css");
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
            public EditorPartPresenter buildEditor() {
                final EmbeddedTextEditorPresenter editor = codeMirrorTextEditorFactory.createTextEditor();
                editor.initialize(new DefaultEmbeddedTextEditorConf(), notificationManager);
                return editor;
            }
        });
    }

    private void initializationFailed(final String errorMessage) {
        this.notificationManager.showNotification(new Notification(errorMessage, Type.ERROR));
        this.notificationManager.showNotification(new Notification("CodeMirror editor is not available", Type.WARNING));
    }
}
