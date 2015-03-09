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
package org.eclipse.che.ide.jseditor.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for scrolls.
 */
public class ScrollEvent extends GwtEvent<ScrollHandler> {
    /** The type instance for this event. */
    public static final Type<ScrollHandler> TYPE = new Type<>();


    public ScrollEvent() {
    }

    @Override
    public Type<ScrollHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final ScrollHandler handler) {
        handler.onScroll(this);
    }

}
