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
package com.codenvy.api.promises.client.callback;

import com.codenvy.api.promises.client.Promise;
import com.codenvy.api.promises.client.js.Executor;
import com.codenvy.api.promises.client.js.Executor.ExecutorBody;
import com.codenvy.api.promises.client.js.JsPromiseError;
import com.codenvy.api.promises.client.js.Promises;
import com.codenvy.api.promises.client.js.RejectFunction;
import com.codenvy.api.promises.client.js.ResolveFunction;
import com.google.gwt.core.client.Callback;

public final class CallbackPromiseHelper {

    private CallbackPromiseHelper() {
    }

    public static <T> Promise<T> createFromCallback(final Call<T, Throwable> call) {
        final ExecutorBody<T> body = new Executor.ExecutorBody<T>() {
            @Override
            public void apply(final ResolveFunction<T> resolve, final RejectFunction reject) {
                call.makeCall(new LocalCallback<T>(resolve, reject));
            }
        };
        final Executor<T> executor = Executor.create(body);
        final Promise<T> result = Promises.create(executor);
        return result;
    }

    public interface Call<T, F> {
        void makeCall(Callback<T, F> callback);
    }

    private static class LocalCallback<T> implements Callback<T, Throwable> {

        private ResolveFunction<T> resolve;
        private RejectFunction reject;

        private LocalCallback(final ResolveFunction<T> resolve, final RejectFunction reject) {
            this.resolve = resolve;
            this.reject = reject;
        }

        @Override
        public void onSuccess(final T result) {
            this.resolve.apply(result);
        }

        @Override
        public void onFailure(final Throwable reason) {
            this.reject.apply(JsPromiseError.create(reason));
        }

    }
}
