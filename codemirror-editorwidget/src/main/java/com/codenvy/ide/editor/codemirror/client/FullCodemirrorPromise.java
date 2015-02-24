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

import com.codenvy.api.promises.client.Promise;
import com.codenvy.ide.editor.codemirrorjso.client.CodeMirrorOverlay;

public class FullCodemirrorPromise {

    private Promise<CodeMirrorOverlay> fullPromise;

    public void setPromise(final Promise<CodeMirrorOverlay> fullCodemirrorPromise) {
        this.fullPromise = fullCodemirrorPromise;
    }

    public Promise<CodeMirrorOverlay> getPromise() {
        return this.fullPromise;
    }
}
