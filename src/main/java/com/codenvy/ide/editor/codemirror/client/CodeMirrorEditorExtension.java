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
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
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
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.inject.Inject;

import elemental.client.Browser;
import elemental.dom.Element;
import elemental.dom.NodeList;
import elemental.html.HeadElement;
import elemental.html.LinkElement;

@Extension(title = "CodeMirror Editor", version = "1.1.0")
public class CodeMirrorEditorExtension {

    /** The logger. */
    private static final Logger  LOG = Logger.getLogger(CodeMirrorEditorExtension.class.getSimpleName());
    /** The editor type key. */
    public static final String                CODEMIRROR_EDITOR_KEY = "codemirror";

    /** The codemirror javascript module key. */
    public static final String                CODEMIRROR_MODULE_KEY = "CodeMirror";

    /** The base path for codemirror resources. */
    private final String codemirrorBase;

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
                                     final CodeMirrorBasePath codemirrorBasePath,
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
        this.codemirrorBase = codemirrorBasePath.basePath();

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
                        injectCodeMirror(callback, completionResources);
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

    private void injectCodeMirror(final InitializerCallback callback, final CompletionResources completionResources) {
        /*
         * This could be simplified and optimized with a all-in-one minified js from http://codemirror.net/doc/compress.html but at least
         * while debugging, unmodified source is necessary. Another option would be to include all-in-one minified along with a source map
         */
        final String[] scripts = new String[]{

                // base script
                codemirrorBase + "lib/codemirror",

                // library of modes
                codemirrorBase + "mode/meta",
                // mode autoloading
                codemirrorBase + "addon/mode/loadmode",


                /* We will preload modes that have extensions */
                // language modes
                codemirrorBase + "mode/xml/xml",
                codemirrorBase + "mode/htmlmixed/htmlmixed", // must be defined after xml

                codemirrorBase + "mode/javascript/javascript",
                codemirrorBase + "mode/coffeescript/coffeescript",

                codemirrorBase + "mode/css/css",

                codemirrorBase + "mode/sql/sql",

                codemirrorBase + "mode/clike/clike",

                codemirrorBase + "mode/markdown/markdown",
                codemirrorBase + "mode/gfm/gfm", // markdown extension for github

                codemirrorBase + "mode/dockerfile/dockerfile_codenvy",

                // hints
                codemirrorBase + "addon/hint/show-hint",
                codemirrorBase + "addon/hint/xml-hint",
                codemirrorBase + "addon/hint/html-hint",
                codemirrorBase + "addon/hint/javascript-hint",
                codemirrorBase + "addon/hint/css-hint",
                codemirrorBase + "addon/hint/anyword-hint",
                codemirrorBase + "addon/hint/sql-hint",

                // pair matching
                codemirrorBase + "addon/edit/closebrackets",
                codemirrorBase + "addon/edit/closetag",
                codemirrorBase + "addon/edit/matchbrackets",
                codemirrorBase + "addon/edit/matchtags",
                // the two following are added to repair actual functionality in 'classic' editor
                codemirrorBase + "addon/selection/mark-selection",
                codemirrorBase + "addon/selection/active-line",

                // for search
                codemirrorBase + "addon/search/search",
                codemirrorBase + "addon/dialog/dialog",
                codemirrorBase + "addon/search/searchcursor",
                codemirrorBase + "addon/search/match-highlighter",
                // comment management
                codemirrorBase + "addon/comment/comment",
                codemirrorBase + "addon/comment/continuecomment",
                // folding
                codemirrorBase + "addon/fold/foldcode",
                codemirrorBase + "addon/fold/foldgutter",
                codemirrorBase + "addon/fold/brace-fold",
                codemirrorBase + "addon/fold/xml-fold", // also required by matchbrackets and closebrackets
                codemirrorBase + "addon/fold/comment-fold",
                codemirrorBase + "addon/fold/indent-fold",
                codemirrorBase + "addon/fold/markdown-fold",
        };


        this.requireJsLoader.require(new Callback<JavaScriptObject[], Throwable>() {
            @Override
            public void onSuccess(final JavaScriptObject[] result) {
                finishInit(callback);
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

        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "lib/codemirror.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/dialog/dialog.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/fold/foldgutter.css");
        injectCssLinkAtTop(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/hint/show-hint.css");

    }

    private void finishInit(final InitializerCallback callback) {
        final CodeMirrorOverlay codeMirror = moduleHolder.getModule(CodeMirrorEditorExtension.CODEMIRROR_MODULE_KEY).cast();
        codeMirror.setModeURL(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "mode/%N/%N.js");
        callback.onSuccess();
    }


    private static void injectCssLink(final String url) {
        LinkElement link = Browser.getDocument().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachToHead(link);
    }

    private static void injectCssLinkAtTop(final String url) {
        LinkElement link = Browser.getDocument().createLinkElement();
        link.setRel("stylesheet");
        link.setHref(url);
        nativeAttachFirstLink(link);
    }

    /**
     * Attach an element to document head.
     * 
     * @param newElement the element to attach
     */
    private static void nativeAttachToHead(Element newElement) {
        Elements.getDocument().getHead().appendChild(newElement);
    }

    private static void nativeAttachFirstLink(Element styleElement) {
        final HeadElement head = Elements.getDocument().getHead();
        final NodeList nodes = head.getElementsByTagName("link");
        if (nodes.length() > 0) {
            head.insertBefore(styleElement, nodes.item(0));
        } else {
            head.appendChild(styleElement);
        }
    }

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
