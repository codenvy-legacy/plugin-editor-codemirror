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

import com.codenvy.api.promises.client.Promise;
import com.codenvy.ide.editor.codemirrorjso.client.CodeMirrorOverlay;
import com.codenvy.ide.util.loging.Log;

public class BaseCodemirrorPromise {

    private Promise<CodeMirrorOverlay> basePromise;

    public BaseCodemirrorPromise() {
        Log.info(BaseCodemirrorPromise.class, "new instance!");
    }

    public void setPromise(final Promise<CodeMirrorOverlay> baseCodemirrorPromise) {
        this.basePromise = baseCodemirrorPromise;
    }

    public Promise<CodeMirrorOverlay> getPromise() {
        return this.basePromise;
    }
}
