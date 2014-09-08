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
import java.util.logging.Logger;

import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.google.gwt.core.shared.GWT;

/**
 * Keymaps supported by CodeMirror.
 *
 * @author "Mickaël Leduque"
 */
public final class CodeMirrorKeymaps {

    /** The logger. */
    private static final Logger  LOG = Logger.getLogger(CodeMirrorKeymaps.class.getSimpleName());

    public static Keymap                     DEFAULT;
    public static Keymap                     EMACS;
    public static Keymap                     VIM;
    public static Keymap                     SUBLIME;

    private static final Map<Keymap, String> nativeMapping = new HashMap<>();

    public final static void init() {
        LOG.fine("Initializing codemirror keymaps.");
        final KeymapDisplayConstants constants = GWT.create(KeymapDisplayConstants.class);
        final EditorType cmEditor = EditorType.fromKey(CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY);
        DEFAULT = Keymap.newKeymap("CM_default", constants.defaultKeymap(), cmEditor);
        EMACS = Keymap.newKeymap("CM_emacs", constants.emacs(), cmEditor);
        VIM = Keymap.newKeymap("CM_vim", constants.vim(), cmEditor);
        SUBLIME = Keymap.newKeymap("CM_sublime", constants.sublime(), cmEditor);


        nativeMapping.put(DEFAULT, "default");
        nativeMapping.put(EMACS, "emacs");
        nativeMapping.put(VIM, "vim");
        nativeMapping.put(SUBLIME, "sublime");
    }

    public static final String getNativeMapping(final Keymap keymap) {
        return nativeMapping.get(keymap);
    }
}
