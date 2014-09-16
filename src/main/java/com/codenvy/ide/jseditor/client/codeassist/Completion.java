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
package com.codenvy.ide.jseditor.client.codeassist;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;

public interface Completion {

    /**
     * Inserts the proposed completion into the given document.
     *
     * @param document
     *         the document into which to insert the proposed completion
     */
    void apply(EmbeddedDocument document);

    /**
     * Returns the new selection after the proposal has been applied to the given document in absolute document coordinates. If it
     * returns <code>null</code>, no new selection is set.
     * <p/>
     * A document change can trigger other document changes, which have to be taken into account when calculating the new
     * selection. Typically, this would be done by installing a document listener or by using a document position during
     * {@link #apply(EmbeddedDocument)}.
     *
     * @param document
     *         the document into which the proposed completion has been inserted
     * @return the new selection in absolute document coordinates
     */
    Region getSelection(EmbeddedDocument document);
}
