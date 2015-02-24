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
package com.codenvy.ide.editor.codemirror.base.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.editor.codemirror.resources.client.BasePathConstant;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.requirejs.RequireJsLoader;
import com.google.inject.Inject;

@Extension(title = "CodeMirror Base", version = "1.1.0")
public class BaseCodeMirrorExtension {

    private MinimalCodemirrorPromise minimalCodemirrorPromise;

    @Inject
    public BaseCodeMirrorExtension(final EditorTypeRegistry editorTypeRegistry,
                                   final RequireJsLoader requireJsLoader,
                                   final NotificationManager notificationManager,
                                   final BasePathConstant basePathConstant,
                                   final MinimalCodemirrorPromise minimalCodemirrorPromise,
                                   final MinimalCodemirrorInitializer minimalCodemirrorInitializer) {

        this.minimalCodemirrorPromise = minimalCodemirrorPromise;

        if (this.minimalCodemirrorPromise.getPromise() == null) {
            minimalCodemirrorInitializer.init();
        }
    }
}
