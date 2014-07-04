package com.codenvy.ide.editor.codemirror.client;

import com.google.gwt.i18n.client.Constants;

public interface KeymapDisplayConstants extends Constants {

    @DefaultStringValue("Default")
    String defaultKeymap();

    @DefaultStringValue("Emacs")
    String emacs();

    @DefaultStringValue("Vim")
    String vim();

    @DefaultStringValue("Sublime")
    String sublime();
}
