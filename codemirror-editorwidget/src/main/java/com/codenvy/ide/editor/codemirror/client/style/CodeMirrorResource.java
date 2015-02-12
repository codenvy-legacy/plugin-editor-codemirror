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
package com.codenvy.ide.editor.codemirror.client.style;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface CodeMirrorResource extends ClientBundle {

    @Source({"cm_highlight.css", "com/codenvy/ide/api/ui/style.css"})
    CssResource highlightStyle();

    @Source({"cm_theme.css", "com/codenvy/ide/api/ui/style.css"})
    CssResource editorStyle();

    @Source({"cm_gutters.css", "com/codenvy/ide/api/ui/style.css"})
    CssResource gutterStyle();

    @Source({"cm_simplescrollbars.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
    CssResource scrollStyle();

    @Source("com/codenvy/ide/texteditor/squiggle.gif")
    ImageResource squiggle();

    @Source({"cm_highlight_dockerfile.css", "com/codenvy/ide/api/ui/style.css"})
    CssResource dockerfileModeStyle();
}
