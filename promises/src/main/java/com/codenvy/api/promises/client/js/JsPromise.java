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

import com.codenvy.api.promises.client.Function;
import com.codenvy.api.promises.client.Operation;
import com.codenvy.api.promises.client.Promise;
import com.codenvy.api.promises.client.PromiseError;
import com.codenvy.api.promises.client.Thenable;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Implementation of {@link Promise} around ES6 promises.
 * @param <V> the type of the promised value
 */
public class JsPromise<V> extends JavaScriptObject implements Promise<V> {

    /** JSO mandated protected constructor. */
    protected JsPromise() {}

    @Override
    public final native <B> Thenable<B> then(V arg) /*-{
        return this.then(arg);
    }-*/;

    @Override
    public final native <B> Promise<B> then(Function<V, B> onFullfilled) /*-{
        return this.then(function(value) {
            return onFullfilled.@com.codenvy.api.promises.client.Function::apply(*)(value);
        });
    }-*/;

    @Override
    public final <B> Promise<B> then(Function<V, B> onFullfilled, Function<PromiseError, B> onRejected) {
        if (onFullfilled != null) {
            return this.internalThen(onFullfilled, onRejected);
        } else {
            return this.catchError(onRejected);
        }
    }

    public final native <B> Promise<B> internalThen(Function<V, B> onFullfilled, Function<PromiseError, B> onRejected) /*-{
        return this.then(function(value) {
            return onFullfilled.@com.codenvy.api.promises.client.Function::apply(*)(value);
        }, function(reason) {
            return onRejected.@com.codenvy.api.promises.client.Function::apply(*)(reason);
        });
    }-*/;

    @Override
    public final native <B> Promise<B> catchError(Function<PromiseError, B> onRejected) /*-{
        return this.then(undefined, function(reason) {
            return onRejected.@com.codenvy.api.promises.client.Function::apply(*)(reason);
        });
    }-*/;

    @Override
    public final native Promise<V> then(Operation<V> onFullfilled) /*-{
        return this.then(function(value) {
            onFullfilled.@com.codenvy.api.promises.client.Operation::apply(*)(value);
        });
    }-*/;

    @Override
    public final Promise<V> then(Operation<V> onFullfilled, Function<PromiseError, V> onRejected) {
        if (onFullfilled != null) {
            return this.internalThen(onFullfilled, onRejected);
        } else {
            return this.catchError(onRejected);
        }
    }

    public final native <B> Promise<B> internalThen(Operation<V> onFullfilled, Function<PromiseError, V> onRejected) /*-{
        return this.then(function(value) {
            onFullfilled.@com.codenvy.api.promises.client.Operation::apply(*)(value);
        }, function(reason) {
            return onRejected.@com.codenvy.api.promises.client.Function::apply(*)(reason);
        });
    }-*/;

    @Override
    public final Promise<V> then(Operation<V> onFullfilled, Operation<PromiseError> onRejected) {
        if (onFullfilled != null) {
            return this.internalThen(onFullfilled, onRejected);
        } else {
            return this.catchError(onRejected);
        }
    }

    public final native Promise<V> internalThen(Operation<V> onFullfilled, Operation<PromiseError> onRejected) /*-{
        return this.then(function(value) {
            onFullfilled.@com.codenvy.api.promises.client.Operation::apply(*)(value);
        }, function(reason) {
            onRejected.@com.codenvy.api.promises.client.Operation::apply(*)(reason);
        });
    }-*/;

    @Override
    public final native Promise<V> catchError(Operation<PromiseError> onRejected) /*-{
        return this.then(undefined, function(reason) {
            onRejected.@com.codenvy.api.promises.client.Operation::apply(*)(reason);
        });
    }-*/;

    @Override
    public final <B> Promise<B> then(final Thenable<B> thenable) {
        if (thenable instanceof JavaScriptObject) {
            return this.thenJs((JavaScriptObject)thenable);
        } else {
            return this.thenJava(thenable);
        }
    }

    private final native <B> Promise<B> thenJs(JavaScriptObject thenable) /*-{
        return this.then(thenable);
    }-*/;

    private final native <B> Promise<B> thenJava(Thenable<B> thenable) /*-{
        return this.then(new Object() {
            then: function(arg) {
                return @com.codenvy.api.promises.client.js.JsPromise::staticThen(*)(thenable, arg);
            }
        });
    }-*/;

    private final static <B> Thenable<B> staticThen(Thenable<B> thenable, B arg) {
        return thenable.then(arg);
    }
}
