/*******************************************************************************
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.editor.codemirror.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Defines the codemirror resources path.
 * Note: uses {@link Constants} interface for proactical reason. Should not be translated.
 */
public interface CodeMirrorBasePath extends Constants {

    String basePath();
}
