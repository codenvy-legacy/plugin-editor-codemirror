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
package com.codenvy.api.promises.client;

public class FunctionException extends Exception {

    private static final long serialVersionUID = 1L;

    public FunctionException() {
    }

    public FunctionException(String message) {
        super(message);
    }

    public FunctionException(Throwable cause) {
        super(cause);
    }

    public FunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
