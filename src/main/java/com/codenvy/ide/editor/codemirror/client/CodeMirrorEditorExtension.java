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

import java.util.Stack;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.core.editor.EditorType;
import com.codenvy.ide.core.editor.EditorTypeRegistry;
import com.codenvy.ide.editor.common.client.requirejs.ModuleHolder;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LinkElement;
import com.google.inject.Inject;

@Extension(title = "CodeMirror Editor.", version = "1.0.0")
public class CodeMirrorEditorExtension {

    /** The editor type key. */
    private static final String               CODEMIRROR_EDITOR_KEY = "codemirror";

    private final NotificationManager         notificationManager;
    private final ModuleHolder                moduleHolder;
    private final EditorTypeRegistry          editorTypeRegistry;

    private final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory;

    @Inject
    public CodeMirrorEditorExtension(final EditorTypeRegistry editorTypeRegistry,
                                     final ModuleHolder moduleHolder,
                                     final NotificationManager notificationManager,
                                     final CodeMirrorTextEditorFactory codeMirrorTextEditorFactory) {
        this.notificationManager = notificationManager;
        this.moduleHolder = moduleHolder;
        this.editorTypeRegistry = editorTypeRegistry;
        this.codeMirrorTextEditorFactory = codeMirrorTextEditorFactory;

        injectCodeMirror();
    }

    private void injectCodeMirror() {
        /*
         * This could be simplified and optimized with a all-in-one minified js from http://codemirror.net/doc/compress.html but at least
         * while debugging, unmodified source is necessary. Another option would be to include all-in-one minified along with a source map
         */
        final Stack<String> scripts = new Stack<String>();
        final String CODEMIRROR_BASE = "codemirror-4.2/";
        final String[] scriptsNames = new String[]{

                // language modes
                CODEMIRROR_BASE + "mode/htmlmixed/htmlmixed.js", // must be defined after xml - stack so reverse order
                CODEMIRROR_BASE + "mode/xml/xml.js",
                CODEMIRROR_BASE + "mode/javascript/javascript.js",
                CODEMIRROR_BASE + "mode/css/css.js",
                CODEMIRROR_BASE + "mode/sql/sql.js",
                CODEMIRROR_BASE + "mode/clike/clike.js",
                CODEMIRROR_BASE + "mode/markdown/markdown.js",
                CODEMIRROR_BASE + "mode/gfm/gfm.js", // markdown extension for github
                CODEMIRROR_BASE + "mode/properties/properties.js",
                CODEMIRROR_BASE + "mode/php/php.js",
                CODEMIRROR_BASE + "mode/htmlembedded/htmlembedded.js",
                CODEMIRROR_BASE + "mode/python/python.js",
                CODEMIRROR_BASE + "mode/yaml/yaml.js",

                // hints
                CODEMIRROR_BASE + "addon/hint/show-hint.js",
                CODEMIRROR_BASE + "addon/hint/xml-hint.js",
                CODEMIRROR_BASE + "addon/hint/html-hint.js",
                CODEMIRROR_BASE + "addon/hint/javascript-hint.js",
                CODEMIRROR_BASE + "addon/hint/css-hint.js",
                CODEMIRROR_BASE + "addon/hint/anyword-hint.js",
                CODEMIRROR_BASE + "addon/hint/sql-hint.js",

                // pair matching
                CODEMIRROR_BASE + "addon/edit/closebrackets.js",
                CODEMIRROR_BASE + "addon/edit/closetag.js",
                CODEMIRROR_BASE + "addon/edit/matchbrackets.js",
                CODEMIRROR_BASE + "addon/edit/matchtags.js",
                // the two following are added to repair actual functionality in 'classic' editor
                CODEMIRROR_BASE + "addon/selection/mark-selection.js",
                CODEMIRROR_BASE + "addon/selection/active-line.js",
                // editor keymaps
                CODEMIRROR_BASE + "keymap/emacs.js",
                CODEMIRROR_BASE + "keymap/vim.js",
                CODEMIRROR_BASE + "keymap/sublime.js",
                // for search
                CODEMIRROR_BASE + "addon/search/search.js",
                CODEMIRROR_BASE + "addon/dialog/dialog.js",
                CODEMIRROR_BASE + "addon/search/searchcursor.js",
                // comment management
                CODEMIRROR_BASE + "addon/comment/comment.js",
                CODEMIRROR_BASE + "addon/comment/continuecomment.js",
                // folding
                CODEMIRROR_BASE + "addon/fold/foldcode.js",
                CODEMIRROR_BASE + "addon/fold/foldgutter.js",
                CODEMIRROR_BASE + "addon/fold/brace-fold.js",
                CODEMIRROR_BASE + "addon/fold/xml-fold.js", // also required by matchbrackets and closebrackets
                CODEMIRROR_BASE + "addon/fold/comment-fold.js",
        };
        for (final String script : scriptsNames) {
            scripts.add(script); // not push, it would need to be fed in reverse order
        }

        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "lib/codemirror.js")
                      .setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onSuccess(final Void result) {
                              injectCodeMirrorExtensions(scripts);
                          }

                          @Override
                          public void onFailure(final Exception e) {
                              Log.error(CodeMirrorEditorExtension.class, "Unable to inject CodeMirror", e);
                              initializationFailed("Unable to inject CodeMirror main script");
                          }
                      }).inject();
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "lib/codemirror.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + CODEMIRROR_BASE + "theme/solarized-mod.css");
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

    private void injectCodeMirrorExtensions(final Stack<String> scripts) {
        if (scripts.isEmpty()) {
            Log.info(CodeMirrorEditorExtension.class, "Finished loading CodeMirror scripts.");
            initialize();
        } else {
            final String script = scripts.pop();
            ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + script)
                          .setWindow(ScriptInjector.TOP_WINDOW)
                          .setCallback(new Callback<Void, Exception>() {
                              @Override
                              public void onSuccess(final Void aVoid) {
                                  injectCodeMirrorExtensions(scripts);
                              }

                              @Override
                              public void onFailure(final Exception e) {
                                  Log.error(CodeMirrorEditorExtension.class, "Unable to inject CodeMirror script " + script, e);
                                  initializationFailed("Unable to inject CodeMirror script " + script);
                              }
                          }).inject();
        }
    }

    private void initialize() {
        this.editorTypeRegistry.registerEditorType(EditorType.fromKey(CODEMIRROR_EDITOR_KEY), "CodeMirror", new EditorProvider() {

            @Override
            public EditorPartPresenter getEditor() {
                return codeMirrorTextEditorFactory.createTextEditor();
            }
        });
    }

    private void initializationFailed(final String errorMessage) {
        this.notificationManager.showNotification(new Notification(errorMessage, Type.ERROR));
        this.notificationManager.showNotification(new Notification("CodeMirror editor is not available", Type.WARNING));
    }
}
