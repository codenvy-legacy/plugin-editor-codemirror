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

/**
 * Interface for an 'operation', as a function without a return value, only side-effects, but without the burden of having a callback with
 * Void parameter.
 * 
 * @param <A> the type of the argument
 */
public interface Operation<A> {
    void apply(A arg) throws OperationException;
}
