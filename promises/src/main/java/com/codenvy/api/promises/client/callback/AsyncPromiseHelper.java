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
import com.google.gwt.user.client.rpc.AsyncCallback;

public final class AsyncPromiseHelper {

    private AsyncPromiseHelper() {
    }

    public static <V> Promise<V> createFromAsyncRequest(final RequestCall<V> call) {
        final ExecutorBody<V> body = new Executor.ExecutorBody<V>() {
            @Override
            public void apply(final ResolveFunction<V> resolve, final RejectFunction reject) {
                call.makeCall(new AsyncCallback<V>() {
                    @Override
                    public void onSuccess(final V result) {
                        resolve.apply(result);
                    }

                    @Override
                    public void onFailure(final Throwable exception) {
                        reject.apply(JsPromiseError.create(exception));
                    }
                });
            }
        };
        final Executor<V> executor = Executor.create(body);
        return Promises.create(executor);
    }

    public interface RequestCall<V> {
        void makeCall(AsyncCallback<V> callback);
    }
}
