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
package com.codenvy.api.promises.client.js;

import com.codenvy.api.promises.client.PromiseError;
import com.google.gwt.core.client.JavaScriptObject;

public class RejectFunction extends JavaScriptObject {

    protected RejectFunction() {
    }

    public final native void apply(PromiseError error) /*-{
        this(error);
    }-*/;
}
