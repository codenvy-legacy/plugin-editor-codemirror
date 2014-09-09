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


import java.util.logging.Level;
import java.util.logging.Logger;

import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.text.RegionImpl;
import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMKeymapOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMKeymapSetOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMRangeOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMSetSelectionOptions;
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.event.BeforeSelectionEventParamOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.event.CMChangeEventOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMEditorOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMMatchTagsConfig;
import com.codenvy.ide.jseditor.client.document.EmbeddedDocument;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.events.BeforeSelectionChangeEvent;
import com.codenvy.ide.jseditor.client.events.BeforeSelectionChangeHandler;
import com.codenvy.ide.jseditor.client.events.CursorActivityEvent;
import com.codenvy.ide.jseditor.client.events.CursorActivityHandler;
import com.codenvy.ide.jseditor.client.events.GutterClickEvent;
import com.codenvy.ide.jseditor.client.events.GutterClickHandler;
import com.codenvy.ide.jseditor.client.events.HasBeforeSelectionChangeHandlers;
import com.codenvy.ide.jseditor.client.events.HasCursorActivityHandlers;
import com.codenvy.ide.jseditor.client.events.HasGutterClickHandlers;
import com.codenvy.ide.jseditor.client.events.HasScrollHandlers;
import com.codenvy.ide.jseditor.client.events.HasViewPortChangeHandlers;
import com.codenvy.ide.jseditor.client.events.ScrollEvent;
import com.codenvy.ide.jseditor.client.events.ScrollHandler;
import com.codenvy.ide.jseditor.client.events.ViewPortChangeEvent;
import com.codenvy.ide.jseditor.client.events.ViewPortChangeHandler;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeEvent;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeHandler;
import com.codenvy.ide.jseditor.client.keymap.KeymapPrefReader;
import com.codenvy.ide.jseditor.client.position.PositionConverter;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.texteditor.EditorWidget;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The CodeMirror implementation of {@link EditorWidget}.
 *
 * @author "Mickaël Leduque"
 */
public class CodeMirrorEditorWidget extends Composite implements EditorWidget, HasChangeHandlers, HasFocusHandlers, HasBlurHandlers,
                                                     HasCursorActivityHandlers, HasBeforeSelectionChangeHandlers,
                                                     HasViewPortChangeHandlers, HasGutterClickHandlers, HasScrollHandlers {

    private static final String                         TAB_SIZE_OPTION             = "tabSize";

    /** The UI binder instance. */
    private static final CodeMirrorEditorWidgetUiBinder UIBINDER = GWT.create(CodeMirrorEditorWidgetUiBinder.class);

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(CodeMirrorEditorWidget.class.getSimpleName());

    @UiField
    SimplePanel                                         panel;

    /** The native editor object. */
    private final CMEditorOverlay                       editorOverlay;

    private final PreferencesManager                    preferencesManager;

    /** The EmbeddededDocument instance. */
    private CodeMirrorDocument                          embeddedDocument;
    /** The position converter instance. */
    private final PositionConverter                           positionConverter;

    private final CodeMirrorOverlay                      codeMirror;

    // flags to know if an event type has already be added to the native editor
    private boolean                                     changeHandlerAdded          = false;
    private boolean                                     focusHandlerAdded           = false;
    private boolean                                     blurHandlerAdded            = false;
    private boolean                                     scrollHandlerAdded          = false;
    private boolean                                     cursorHandlerAdded          = false;
    private boolean                                     beforeSelectionHandlerAdded = false;
    private boolean                                     viewPortHandlerAdded        = false;
    private boolean                                     gutterClickHandlerAdded     = false;

    /** The 'generation', marker to ask if changes where done since if was set. */
    private int generationMarker;

    private Keymap                                      keymap;

    private CMKeymapOverlay keyBindings;

    @AssistedInject
    public CodeMirrorEditorWidget(final ModuleHolder moduleHolder,
                                  final PreferencesManager preferencesManager,
                                  final EventBus eventBus,
                                  @Assisted final String editorMode) {
        initWidget(UIBINDER.createAndBindUi(this));

        this.preferencesManager = preferencesManager;


        this.codeMirror = moduleHolder.getModule(CodeMirrorEditorExtension.CODEMIRROR_MODULE_KEY).cast();

        this.editorOverlay = this.codeMirror.createEditor(this.panel.getElement(), getConfiguration());
        this.editorOverlay.setSize("100%", "100%");
        this.editorOverlay.refresh();

        this.positionConverter = new CodemirrorPositionConverter(this.editorOverlay);

        setMode(editorMode);

        initKeyBindings();

        setupKeymap();
        eventBus.addHandler(KeymapChangeEvent.TYPE, new KeymapChangeHandler() {

            @Override
            public void onKeymapChanged(final KeymapChangeEvent event) {
                final String editorTypeKey = event.getEditorTypeKey();
                if (CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY.equals(editorTypeKey)) {
                    setupKeymap();
                }
            }
        });
        this.generationMarker = this.editorOverlay.getDoc().changeGeneration(true);

        buildKeybindingInfo();
    }

    private void initKeyBindings() {

        this.keyBindings = CMKeymapOverlay.create();

        this.keyBindings.addBinding("Shift-Ctrl-K", this,  new CodeMirrorKeyBindingAction<CodeMirrorEditorWidget>() {

            public void action(final CodeMirrorEditorWidget editorWidget) {
                LOG.fine("Keybindings help binding used.");
                editorWidget.keybindingHelp();
            }
        });

        this.editorOverlay.addKeyMap(this.keyBindings);
    }

    @Override
    public String getValue() {
        return this.editorOverlay.getValue();
    }

    @Override
    public void setValue(final String newValue) {
        this.editorOverlay.setValue(newValue);
        // reset history, else the setValue is undo-able
        this.editorOverlay.getDoc().clearHistory();
        this.generationMarker = this.editorOverlay.getDoc().changeGeneration(true);
        LOG.fine("Set value - state clean=" + editorOverlay.getDoc().isClean(getGenerationMarker())
                 + " (generation=" + getGenerationMarker() + ").");
    }

    private CMEditorOptionsOverlay getConfiguration() {
        final CMEditorOptionsOverlay options = CMEditorOptionsOverlay.create();

        // show line numbers
        options.setLineNumbers(true);

        // set a theme
        options.setTheme("codenvy");

        // autoclose brackets/tags, match brackets/tags
        options.setProperty("autoCloseBrackets", true);
        options.setProperty("matchBrackets", true);
        options.setProperty("autoCloseTags", true);

        // folding
        options.setProperty("foldGutter", true);

        // gutters - define 2 : line and fold
        final JsArrayString gutters = JsArray.createArray(2).cast();
        gutters.push("CodeMirror-linenumbers");
        gutters.push("CodeMirror-foldgutter");
        options.setGutters(gutters);

        // highlight matching tags
        final CMMatchTagsConfig matchTagsConfig = CMMatchTagsConfig.create();
        matchTagsConfig.setBothTags(true);
        options.setProperty("matchTags", matchTagsConfig);

        // highlight active line
        options.setProperty("styleActiveLine", true);

        return options;
    }

    protected void autoComplete() {
        this.editorOverlay.showHint();
    }

    @Override
    public void setMode(final String modeName) {
        LOG.fine("Setting editor mode : " + modeName);
        if (!modeName.equals("html")) {
            this.editorOverlay.setOption("mode", modeName);
        } else {
            LOG.fine("... actually, changing to text/html.");
            this.editorOverlay.setOption("mode", "text/html");
        }
    }

    public void selectVimKeymap() {
        this.editorOverlay.setOption("keyMap", CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.VIM));
        this.editorOverlay.setOption("showCursorWhenSelecting", true);
    }

    public void selectEmacsKeymap() {
        this.editorOverlay.setOption("keyMap", CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.EMACS));
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    public void selectSublimeKeymap() {
        this.editorOverlay.setOption("keyMap", CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.SUBLIME));
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    public void selectDefaultKeymap() {
        this.editorOverlay.setOption("keyMap", CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.DEFAULT));
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    private void selectKeymap(final Keymap keymap) {
        if (keymap == null || CodeMirrorKeymaps.DEFAULT.equals(keymap)) {
            selectDefaultKeymap();
        } else if (CodeMirrorKeymaps.EMACS.equals(keymap)) {
            selectEmacsKeymap();
        } else if (CodeMirrorKeymaps.VIM.equals(keymap)) {
            selectVimKeymap();
        } else if (CodeMirrorKeymaps.SUBLIME.equals(keymap)) {
            selectSublimeKeymap();
        } else {
            this.keymap = null;
            throw new RuntimeException("Unknown keymap type: " + keymap);
        }
        this.keymap = keymap;
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        if (!changeHandlerAdded) {
            changeHandlerAdded = true;
            this.editorOverlay.on("change", new CMEditorOverlay.EventHandlerOneParameter<CMChangeEventOverlay>() {

                @Override
                public void onEvent(final CMChangeEventOverlay param) {
                    LOG.fine("Change event - state clean=" + editorOverlay.getDoc().isClean(getGenerationMarker())
                                  + " (generation=" + getGenerationMarker() + ").");
                    fireChangeEvent();
                }
            });
        }
        return addHandler(handler, ChangeEvent.getType());
    }

    private void fireChangeEvent() {
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
    }

    @Override
    public HandlerRegistration addFocusHandler(final FocusHandler handler) {
        if (!focusHandlerAdded) {
            focusHandlerAdded = true;
            this.editorOverlay.on("focus", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireFocusEvent();
                }
            });
        }
        return addHandler(handler, FocusEvent.getType());
    }

    private void fireFocusEvent() {
        DomEvent.fireNativeEvent(Document.get().createFocusEvent(), this);
    }

    @Override
    public HandlerRegistration addBlurHandler(final BlurHandler handler) {
        if (!blurHandlerAdded) {
            blurHandlerAdded = true;
            this.editorOverlay.on("blur", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireBlurEvent();
                }
            });
        }
        return addHandler(handler, BlurEvent.getType());
    }

    private void fireBlurEvent() {
        DomEvent.fireNativeEvent(Document.get().createBlurEvent(), this);
    }

    @Override
    public HandlerRegistration addScrollHandler(final ScrollHandler handler) {
        if (!scrollHandlerAdded) {
            scrollHandlerAdded = true;
            this.editorOverlay.on("scroll", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireScrollEvent();
                }
            });
        }
        return addHandler(handler, ScrollEvent.TYPE);
    }

    private void fireScrollEvent() {
        fireEvent(new ScrollEvent());
    }

    @Override
    public HandlerRegistration addCursorActivityHandler(final CursorActivityHandler handler) {
        if (!cursorHandlerAdded) {
            cursorHandlerAdded = true;
            this.editorOverlay.on("cursorActivity", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireCursorActivityEvent();
                }
            });
        }
        return addHandler(handler, CursorActivityEvent.TYPE);
    }

    private void fireCursorActivityEvent() {
        fireEvent(new CursorActivityEvent());
    }

    @Override
    public HandlerRegistration addBeforeSelectionChangeHandler(final BeforeSelectionChangeHandler handler) {
        if (!beforeSelectionHandlerAdded) {
            beforeSelectionHandlerAdded = true;
            this.editorOverlay.on("beforeSelectionChange",
                                  new CMEditorOverlay.EventHandlerOneParameter<BeforeSelectionEventParamOverlay>() {

                                      @Override
                                      public void onEvent(final BeforeSelectionEventParamOverlay param) {
                                          fireBeforeSelectionChangeEvent();
                                      }
                                  });
        }
        return addHandler(handler, BeforeSelectionChangeEvent.TYPE);
    }

    private void fireBeforeSelectionChangeEvent() {
        fireEvent(new BeforeSelectionChangeEvent());
    }

    @Override
    public HandlerRegistration addViewPortChangeHandler(ViewPortChangeHandler handler) {
        if (!viewPortHandlerAdded) {
            viewPortHandlerAdded = true;
            this.editorOverlay.on("viewportChange", new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

                @Override
                public void onEvent(final JsArray<JavaScriptObject> param) {
                    final JsArrayInteger asIntegers = param.cast();
                    final int from = asIntegers.get(0);
                    final int to = asIntegers.get(1);
                    fireViewPortChangeEvent(from, to);
                }
            });
        }
        return addHandler(handler, ViewPortChangeEvent.TYPE);
    }

    private void fireViewPortChangeEvent(final int from, final int to) {
        fireEvent(new ViewPortChangeEvent(from, to));
    }

    @Override
    public HandlerRegistration addGutterClickHandler(GutterClickHandler handler) {
        if (!gutterClickHandlerAdded) {
            gutterClickHandlerAdded = true;
            this.editorOverlay.on("gutterClick", new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

                @Override
                public void onEvent(final JsArray<JavaScriptObject> params) {
                    fireGutterClickEvent();
                }
            });
        }
        return addHandler(handler, GutterClickEvent.TYPE);
    }

    private void fireGutterClickEvent() {
        fireEvent(new GutterClickEvent());
    }

    @Override
    public void setReadOnly(final boolean isReadOnly) {
        this.editorOverlay.setOption("readOnly", isReadOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.editorOverlay.getBooleanOption("readOnly");
    }

    @Override
    public boolean isDirty() {
        return !this.editorOverlay.getDoc().isClean(this.generationMarker);
    }

    @Override
    public void markClean() {
        boolean beforeDirty = false;
        int beforeGeneration = 0;
        if (LOG.isLoggable(Level.FINE)) {
            beforeDirty = isDirty();
            beforeGeneration = this.generationMarker;
        }

        // Use changeGeneration instead of markClean: codemirror author's recommandation
        this.generationMarker = this.editorOverlay.getDoc().changeGeneration(true);

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("markClean - Before dirty=" + beforeDirty + " gen=" + beforeGeneration
                     + " After dirty=" + isDirty() + " gen=" + this.generationMarker);
        }
    }


    @Override
    public int getTabSize() {
        return this.editorOverlay.getIntOption(TAB_SIZE_OPTION);
    }

    @Override
    public void setTabSize(int tabSize) {
        this.editorOverlay.setOption(TAB_SIZE_OPTION, tabSize);
    }

    @Override
    public EmbeddedDocument getDocument() {
        if (this.embeddedDocument == null) {
            this.embeddedDocument = new CodeMirrorDocument(this.editorOverlay.getDoc(), this);
        }
        return this.embeddedDocument;
    }

    @Override
    public Region getSelectedRange() {
        // will only support a single selection here

        /* multiple selection support would use listSelections() */
        final CMPositionOverlay from = this.editorOverlay.getDoc().getCursorFrom();
        final CMPositionOverlay to = this.editorOverlay.getDoc().getCursorTo();

        final int startOffset = this.editorOverlay.getDoc().indexFromPos(from);
        final int endOffset = this.editorOverlay.getDoc().indexFromPos(to);

        final int lastLine = this.editorOverlay.getDoc().lastLine();
        final int lastPosition = this.editorOverlay.getDoc().getLine(lastLine).length();

        if (startOffset < 0 || endOffset > lastPosition || startOffset > endOffset) {
            throw new RuntimeException("Invalid selection");
        }
        return new RegionImpl(startOffset, endOffset - startOffset);
    }

    public void setSelectedRange(final Region selection, final boolean show) {
        final CMPositionOverlay anchor = this.editorOverlay.getDoc().posFromIndex(selection.getOffset());
        final int headOffset = selection.getOffset() + selection.getLength();
        final CMPositionOverlay head = this.editorOverlay.getDoc().posFromIndex(headOffset);

        if (show) {
            this.editorOverlay.getDoc().setSelection(anchor, head);
        } else {
            this.editorOverlay.getDoc().setSelection(anchor, head, CMSetSelectionOptions.createNoScroll());
        }
    }

    public void setDisplayRange(final Region range) {
        final CMPositionOverlay from = this.editorOverlay.getDoc().posFromIndex(range.getOffset());
        final CMPositionOverlay to = this.editorOverlay.getDoc().posFromIndex(range.getOffset() + range.getLength());
        final CMRangeOverlay nativeRange = CMRangeOverlay.create(from, to);
        this.editorOverlay.scrollIntoView(nativeRange);
    }

    private void setupKeymap() {
        final String propertyValue = KeymapPrefReader.readPref(this.preferencesManager,
                                                               CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY);
        Keymap keymap;
        try {
            keymap = Keymap.fromKey(propertyValue);
        } catch (final IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Unknown value in keymap preference.", e);
            return;
        }
        selectKeymap(keymap);
    }

    /**
     * Returns the generation marker.<br>
     * As the field is not final, this is needed so the non-static inner class can see the value changes.
     *
     * @return the generation marker
     */
    private int getGenerationMarker() {
        return this.generationMarker;
    }

    /**
     * Generate a key bindings list for all keymaps.
     */
    public void keybindingHelp() {
        insertAtCursor(buildKeybindingInfo());
    }

    private String buildKeybindingInfo() {
        final StringBuilder sb = new StringBuilder();
        final CMKeymapSetOverlay keymapsObject = codeMirror.keyMap();
        for (final String keymapKey : keymapsObject.getKeys()) {
            if (keymapKey.startsWith("emacs") || keymapKey.startsWith("vim")) {
                continue;
            }
            sb.append("# ").append(keymapKey).append("\n\n");
            final CMKeymapOverlay keymap = keymapsObject.get(keymapKey);
            for (final String binding : keymap.getKeys()) {
                if (binding.equals("fallthrough")
                    || binding.equals("nofallthrough")
                    || binding.equals("disableInput")
                    || binding.equals("auto")) {
                    continue;
                }
                switch (keymap.getType(binding)) {
                    case COMMAND_NAME:
                        sb.append("**")
                          .append(binding)
                          .append("** ")
                          .append(keymap.getCommandName(binding))
                          .append("\n\n");
                        break;
                    case FUNCTION:
                        sb.append("**")
                          .append(binding)
                          .append("** ")
                          .append(keymap.getFunctionSource(binding))
                          .append("\n\n");
                        break;
                    default:
                        break;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void insertAtCursor(final String insertedText) {
        final CMPositionOverlay cursor = this.editorOverlay.getDoc().getCursor();
        this.editorOverlay.getDoc().replaceRange(insertedText, cursor);
    }

    @Override
    public EditorType getEditorType() {
        return EditorType.fromKey(CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY);
    }

    @Override
    public Keymap getKeymap() {
        return this.keymap;
    }

    public PositionConverter getPositionConverter() {
        return this.positionConverter;
    }

    public void setFocus() {
        this.editorOverlay.focus();
    }

    interface CodeMirrorEditorWidgetUiBinder extends UiBinder<SimplePanel, CodeMirrorEditorWidget> {
    }
}
