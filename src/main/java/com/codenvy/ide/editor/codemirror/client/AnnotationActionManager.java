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

import java.util.HashMap;
import java.util.Map;

import com.codenvy.ide.jseditor.client.annotation.AnnotationAction;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Manages actions on gutter annotations.
 */
public class AnnotationActionManager {

    /** All registered actions. */
    private final Map<AnnotationKey, AnnotationAction> actions = new HashMap<>();

    /**
     * Registers an action on a gutter position.
     * @param line the line
     * @param gutter the gutter id
     * @param action the action
     */
    public void registerAnnotation(final int line, final String gutter, final AnnotationAction action) {
        this.actions.put(new AnnotationKey(line, gutter), action);
    }

    /**
     * Removes an action from a gutter position.
     * @param line the line of the action to remove
     * @param gutter the gutter id
     */
    public void removeAnnotation(final int line, final String gutter) {
        this.actions.remove(new AnnotationKey(line, gutter));
    }

    public void onClick(final int line, final String gutterId, final ClickEvent event) {
        final AnnotationAction action = this.actions.get(new AnnotationKey(line, gutterId));
        if (action == null) {
            return;
        }
        action.onAnnotationClick();
    }

    /**
     * A (line, gutterId) key couple.
     */
    private static class AnnotationKey {

        /** The line of the position. */
        private final int line;
        /** The gutter id . */
        private final String gutter;

        public AnnotationKey(final int line, final String gutterId) {
            this.line = line;
            this.gutter = gutterId;
        }

        public int getLine() {
            return line;
        }

        public String getGutter() {
            return gutter;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((gutter == null) ? 0 : gutter.hashCode());
            result = prime * result + line;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof AnnotationKey)) {
                return false;
            }
            final AnnotationKey other = (AnnotationKey)obj;
            if (gutter == null) {
                if (other.gutter != null) {
                    return false;
                }
            } else if (!gutter.equals(other.gutter)) {
                return false;
            }
            if (line != other.line) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "AnnotationKey [line=" + line + ", gutter=" + gutter + "]";
        }
    }
}
