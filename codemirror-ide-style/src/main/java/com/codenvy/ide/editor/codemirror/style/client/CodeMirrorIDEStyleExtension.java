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
package com.codenvy.ide.editor.codemirror.style.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.editor.codemirror.resources.client.CodeMirrorBasePath;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;

import elemental.client.Browser;
import elemental.dom.Element;
import elemental.dom.NodeList;
import elemental.html.HeadElement;
import elemental.html.LinkElement;

@Extension(title = "CodeMirror Editor IDE Style", version = "1.1.0")
public class CodeMirrorIDEStyleExtension {

    @Inject
    public CodeMirrorIDEStyleExtension(final CodeMirrorResource highlightResource) {


        highlightResource.highlightStyle().ensureInjected();
        highlightResource.editorStyle().ensureInjected();
        highlightResource.dockerfileModeStyle().ensureInjected();
        highlightResource.gutterStyle().ensureInjected();
        highlightResource.scrollStyle().ensureInjected();

        injectCodeMirrorIDEStyle();
    }

    private void injectCodeMirrorIDEStyle() {
        final String codemirrorBase = CodeMirrorBasePath.basePath();

        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "lib/codemirror.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/dialog/dialog.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/fold/foldgutter.css");
        injectCssLinkAtTop(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/hint/show-hint.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/search/matchesonscrollbar.css");
        injectCssLink(GWT.getModuleBaseForStaticFiles() + codemirrorBase + "addon/scroll/simplescrollbars.css");

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
}
