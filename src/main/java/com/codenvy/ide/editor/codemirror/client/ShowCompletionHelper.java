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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMCompletionObjectOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintApplyOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintApplyOverlay.HintApplyFunction;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintCallback;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay.AsyncHintFunction;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintFunctionOverlay.HintFunction;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMHintResultsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMRenderFunctionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.hints.CMRenderFunctionOverlay.RenderFunction;
import com.codenvy.ide.jseditor.client.codeassist.Completion;
import com.codenvy.ide.jseditor.client.codeassist.CompletionProposal;
import com.codenvy.ide.jseditor.client.codeassist.CompletionProposal.CompletionCallback;
import com.codenvy.ide.jseditor.client.codeassist.CompletionReadyCallback;
import com.codenvy.ide.jseditor.client.codeassist.CompletionResources.CompletionCss;
import com.codenvy.ide.jseditor.client.codeassist.CompletionsSource;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayMixed;

import elemental.dom.Element;
import elemental.dom.Node;
import elemental.html.SpanElement;

public final class ShowCompletionHelper {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(ShowCompletionHelper.class.getName());

    private ShowCompletionHelper() {}

    public static void showCompletionProposals(final CodeMirrorEditorWidget editorWidget,
                                               final CMEditorOverlay editorOverlay,
                                               final EmbeddedDocument document,
                                               final List<CompletionProposal> proposals,
                                               final CompletionCss css) {
        if (! editorOverlay.hasShowHint() || proposals == null || proposals.isEmpty()) {
            // no support for hints or no proposals
            return;
        }

        final CMHintOptionsOverlay hintOptions = CMHintOptionsOverlay.create();
        hintOptions.setCloseOnUnfocus(true); //actually, default value
        hintOptions.setAlignWithWord(true); //default
        hintOptions.setCompleteSingle(true); //default

        final CMHintFunctionOverlay hintFunction = CMHintFunctionOverlay.createFromHintFunction(new HintFunction() {

            @Override
            public CMHintResultsOverlay getHints(final CMEditorOverlay editor,
                                                 final CMHintOptionsOverlay options) {
                final CMHintResultsOverlay result = CMHintResultsOverlay.create();
                final JsArrayMixed list = result.getList();
                for (final CompletionProposal proposal: proposals) {

                    final CMHintApplyOverlay hintApply = createApplyHintFunc(editorWidget, document, proposal);
                    final CMRenderFunctionOverlay renderFunc = createRenderHintFunc(editorWidget, proposal, css);

                    final CMCompletionObjectOverlay completionObject = JavaScriptObject.createObject().cast();

                    completionObject.setHint(hintApply);
                    completionObject.setRender(renderFunc);

                    list.push(completionObject);
                }
                result.setFrom(editor.getDoc().getCursor());
                return result;
            }


        });
        hintOptions.setHint(hintFunction);

        editorOverlay.showHint(hintOptions);
    }

    /* async version */
    public static void showCompletionProposals(final CodeMirrorEditorWidget editorWidget,
                                               final CMEditorOverlay editorOverlay,
                                               final EmbeddedDocument document,
                                               final CompletionsSource completionsSource,
                                               final CompletionCss css) {
        if (! editorOverlay.hasShowHint()) {
            // no support for hints
            return;
        }
        final CMHintOptionsOverlay hintOptions = CMHintOptionsOverlay.create();
        final CMHintFunctionOverlay hintFunction = CMHintFunctionOverlay.createFromAsyncHintFunction(new AsyncHintFunction() {

            @Override
            public void getHints(final CMEditorOverlay editor,
                                 final CMHintCallback callback,
                                 final CMHintOptionsOverlay options) {
                completionsSource.computeCompletions(new CompletionReadyCallback() {

                    @Override
                    public void onCompletionReady(final List<CompletionProposal> proposals) {
                        final CMHintResultsOverlay result = CMHintResultsOverlay.create();
                        final JsArrayMixed list = result.getList();
                        for (final CompletionProposal proposal: proposals) {

                            final CMHintApplyOverlay hintApply = createApplyHintFunc(editorWidget, document, proposal);
                            final CMRenderFunctionOverlay renderFunc = createRenderHintFunc(editorWidget, proposal, css);

                            final CMCompletionObjectOverlay completionObject = JavaScriptObject.createObject().cast();

                            completionObject.setHint(hintApply);
                            completionObject.setRender(renderFunc);

                            list.push(completionObject);
                        }
                        result.setFrom(editor.getDoc().getCursor());
                        callback.call(result);
                    }
                });
            }
        });

        // set the async hint function and trigger the delayed display of hints
        hintOptions.setHint(hintFunction);
        editorOverlay.showHint(hintOptions);
    }

    public static void showCompletionProposals(final CodeMirrorEditorWidget editorWidget,
                                               final CodeMirrorOverlay codeMirrorOverlay,
                                               final CMEditorOverlay editorOverlay,
                                               final EmbeddedDocument document,
                                               final CompletionCss css) {
         if (! editorOverlay.hasShowHint()) {
             // no support for hints
             return;
         }
         final CMHintFunctionOverlay hintAuto = CMHintFunctionOverlay.createFromName(codeMirrorOverlay, "auto");
         final CMHintResultsOverlay result = hintAuto.apply(editorOverlay);
         if (result != null) {
             final List<String> proposals = new ArrayList<>();
             final JsArrayMixed list = result.getList();
             int nonStrings = 0;
             //jsarray aren't iterable
             for (int i = 0; i < list.length(); i++) {
                 if (result.isString(i)) {
                     proposals.add(result.getCompletionItemAsString(i));
                 } else {
                     nonStrings++;
                 }
             }
             LOG.info("CM Completion returned " + list.length() + " items, of which " + nonStrings + " were not strings.");
    
             showCompletionProposals(editorWidget, editorOverlay, proposals, result.getFrom(), result.getTo(), css);
         }
    }

    private static void showCompletionProposals(final CodeMirrorEditorWidget editorWidget,
                                               final CMEditorOverlay editorOverlay,
                                               final List<String> proposals,
                                               final CMPositionOverlay from,
                                               final CMPositionOverlay to,
                                               final CompletionCss css) {
        if (! editorOverlay.hasShowHint() || proposals == null || proposals.isEmpty()) {
            // no support for hints or no proposals
            return;
        }

        final CMHintOptionsOverlay hintOptions = CMHintOptionsOverlay.create();
        hintOptions.setCloseOnUnfocus(true); //actually, default value
        hintOptions.setAlignWithWord(true); //default
        hintOptions.setCompleteSingle(true); //default

        final CMHintFunctionOverlay hintFunction = CMHintFunctionOverlay.createFromHintFunction(new HintFunction() {

            @Override
            public CMHintResultsOverlay getHints(final CMEditorOverlay editor,
                                                 final CMHintOptionsOverlay options) {
                final CMHintResultsOverlay result = CMHintResultsOverlay.create();
                final JsArrayMixed list = result.getList();
                for (final String proposal: proposals) {


                    final CMCompletionObjectOverlay completionObject = JavaScriptObject.createObject().cast();

                    completionObject.setText(proposal);
                    final CMRenderFunctionOverlay renderFunc = createRenderHintFunc(editorWidget, proposal, css);
                    completionObject.setRender(renderFunc);

                    list.push(completionObject);
                }
                result.setFrom(from);
                result.setTo(to);
                return result;
            }


        });
        hintOptions.setHint(hintFunction);

        editorOverlay.showHint(hintOptions);
    }

    private static CMHintApplyOverlay createApplyHintFunc(final CodeMirrorEditorWidget editorWidget,
                                                   final EmbeddedDocument document,
                                                   final CompletionProposal proposal) {
        return CMHintApplyOverlay.create(new HintApplyFunction() {

            @Override
            public void applyHint(final CMEditorOverlay editor, final CMHintResultsOverlay data,
                                  final JavaScriptObject completion) {
                proposal.getCompletion(new CompletionCallback() {

                    @Override
                    public void onCompletion(final Completion completion) {
                        // apply the completion
                        completion.apply(document);
                        // set the selection
                        final Region selection = completion.getSelection(document);
                        if (selection != null) {
                            editorWidget.setSelectedRange(selection, true);
                        }
                    }
                });

            }
        });
    }

    private static CMRenderFunctionOverlay createRenderHintFunc(final CodeMirrorEditorWidget editorWidget,
                                                                final CompletionProposal proposal,
                                                                final CompletionCss css) {
        return CMRenderFunctionOverlay.create(new RenderFunction() {

            @Override
            public void renderHint(final Element element, final CMHintResultsOverlay data,
                                   final JavaScriptObject completion) {
                final SpanElement icon = Elements.createSpanElement(css.proposalIcon());
                final SpanElement label = Elements.createSpanElement(css.proposalLabel());
                final SpanElement group = Elements.createSpanElement(css.proposalGroup());
                if (proposal.getIcon() != null && proposal.getIcon().getSVGImage() != null){
                    icon.appendChild((Node)proposal.getIcon().getSVGImage().getElement());
                } else if (proposal.getIcon() != null && proposal.getIcon().getImage() != null) {
                    icon.appendChild((Node)proposal.getIcon().getImage().getElement());
                }
                label.setInnerHTML(proposal.getDisplayString());
                element.appendChild(icon);
                element.appendChild(label);
                element.appendChild(group);
            }
        });
    }

    private static CMRenderFunctionOverlay createRenderHintFunc(final CodeMirrorEditorWidget editorWidget,
                                                                final String proposal,
                                                                final CompletionCss css) {
        return CMRenderFunctionOverlay.create(new RenderFunction() {

            @Override
            public void renderHint(final Element element, final CMHintResultsOverlay data,
                                   final JavaScriptObject completion) {
                final SpanElement label = Elements.createSpanElement(css.proposalLabel());
                label.setInnerHTML(proposal);
                element.appendChild(label);
            }
        });
    }
}
