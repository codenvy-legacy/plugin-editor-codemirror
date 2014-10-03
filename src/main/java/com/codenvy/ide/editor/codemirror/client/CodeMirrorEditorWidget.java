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


import static com.codenvy.ide.editor.codemirror.client.EventTypes.BEFORE_SELECTION_CHANGE;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.BLUR;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.CHANGE;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.CURSOR_ACTIVITY;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.FOCUS;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.GUTTER_CLICK;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.SCROLL;
import static com.codenvy.ide.editor.codemirror.client.EventTypes.VIEWPORT_CHANGE;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.AUTOCLOSE_BRACKETS;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.AUTOCLOSE_TAGS;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.FOLD_GUTTER;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.KEYMAP;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.MATCH_BRACKETS;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.MODE;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.READONLY;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.SHOW_CURSOR_WHEN_SELECTING;
import static com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey.STYLE_ACTIVE_LINE;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.codenvy.ide.api.text.Region;
import com.codenvy.ide.api.text.RegionImpl;
import com.codenvy.ide.api.texteditor.HandlesUndoRedo;
import com.codenvy.ide.editor.codemirror.client.jso.CMEditorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMKeymapOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMKeymapSetOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMPositionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMRangeOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.CMSetSelectionOptions;
import com.codenvy.ide.editor.codemirror.client.jso.CodeMirrorOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.EventHandlers.EventHandlerMixedParameters;
import com.codenvy.ide.editor.codemirror.client.jso.dialog.CMDialogOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.dialog.CMDialogOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.event.BeforeSelectionEventParamOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.event.CMChangeEventOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.line.CMGutterMarkersOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.line.CMLineInfoOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.marks.CMTextMarkerOptionOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.marks.CMTextMarkerOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMEditorOptionsOverlay;
import com.codenvy.ide.editor.codemirror.client.jso.options.CMMatchTagsConfig;
import com.codenvy.ide.editor.codemirror.client.jso.options.OptionKey;
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
import com.codenvy.ide.jseditor.client.keymap.KeyBindingAction;
import com.codenvy.ide.jseditor.client.keymap.Keybinding;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeEvent;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeHandler;
import com.codenvy.ide.jseditor.client.position.PositionConverter;
import com.codenvy.ide.jseditor.client.prefmodel.KeymapPrefReader;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.text.TextRange;
import com.codenvy.ide.jseditor.client.texteditor.EditorWidget;
import com.codenvy.ide.jseditor.client.texteditor.HasGutter.LineNumberingChangeCallback;
import com.codenvy.ide.jseditor.client.texteditor.HasTextMarkers.MarkerRegistration;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayMixed;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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


    /** The EmbeddededDocument instance. */
    private CodeMirrorDocument                          embeddedDocument;
    /** The position converter instance. */
    private final PositionConverter                           positionConverter;

    private final CodeMirrorOverlay                      codeMirror;

    /** Component that handles undo/redo. */
    private final HandlesUndoRedo undoRedo;

    /** Component to read the keymap preference. */
    private final KeymapPrefReader keymapPrefReader;

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
                                  final EventBus eventBus,
                                  final KeymapPrefReader keymapPrefReader,
                                  @Assisted final String editorMode) {
        initWidget(UIBINDER.createAndBindUi(this));

        this.keymapPrefReader = keymapPrefReader;


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
        this.undoRedo = new CodeMirrorUndoRedo(this.editorOverlay.getDoc());
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
        options.setProperty(AUTOCLOSE_BRACKETS, true);
        options.setProperty(MATCH_BRACKETS, true);
        options.setProperty(AUTOCLOSE_TAGS, true);

        // folding
        options.setProperty(FOLD_GUTTER, true);

        // gutters - define 2 : line and fold
        final JsArrayString gutters = JsArray.createArray(2).cast();
        gutters.push("CodeMirror-linenumbers");
        gutters.push("CodeMirror-foldgutter");
        gutters.push("annotation");
        options.setGutters(gutters);

        // highlight matching tags
        final CMMatchTagsConfig matchTagsConfig = CMMatchTagsConfig.create();
        matchTagsConfig.setBothTags(true);
        options.setProperty(OptionKey.MATCH_TAGS, matchTagsConfig);

        // highlight active line
        options.setProperty(STYLE_ACTIVE_LINE, true);

        return options;
    }

    protected void autoComplete() {
        this.editorOverlay.showHint();
    }

    @Override
    public void setMode(final String modeName) {
        LOG.fine("Setting editor mode : " + modeName);
        this.editorOverlay.setOption(MODE, modeName);
    }

    public void selectVimKeymap() {
        this.editorOverlay.setOption(KEYMAP, CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.VIM));
        this.editorOverlay.setOption(SHOW_CURSOR_WHEN_SELECTING, true);
    }

    public void selectEmacsKeymap() {
        this.editorOverlay.setOption(KEYMAP, CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.EMACS));
        this.editorOverlay.setOption("SHOW_CURSOR_WHEN_SELECTING", false);
    }

    public void selectSublimeKeymap() {
        this.editorOverlay.setOption(KEYMAP, CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.SUBLIME));
        this.editorOverlay.setOption("SHOW_CURSOR_WHEN_SELECTING", false);
    }

    public void selectDefaultKeymap() {
        this.editorOverlay.setOption(KEYMAP, CodeMirrorKeymaps.getNativeMapping(CodeMirrorKeymaps.DEFAULT));
        this.editorOverlay.setOption("SHOW_CURSOR_WHEN_SELECTING", false);
    }

    private void selectKeymap(final Keymap keymap) {
        Keymap usedKeymap = keymap;
        if (usedKeymap == null) {
            usedKeymap = CodeMirrorKeymaps.DEFAULT;
            selectDefaultKeymap();
        } else if (CodeMirrorKeymaps.DEFAULT.equals(usedKeymap)) {
            selectDefaultKeymap();
        } else if (CodeMirrorKeymaps.EMACS.equals(usedKeymap)) {
            selectEmacsKeymap();
        } else if (CodeMirrorKeymaps.VIM.equals(usedKeymap)) {
            selectVimKeymap();
        } else if (CodeMirrorKeymaps.SUBLIME.equals(usedKeymap)) {
            selectSublimeKeymap();
        } else {
            usedKeymap = CodeMirrorKeymaps.DEFAULT;
            selectDefaultKeymap();
            Log.error(CodeMirrorEditorWidget.class, "Unknown keymap: " + keymap + " - replacing by default one.");
        }
        this.keymap = usedKeymap;
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        if (!changeHandlerAdded) {
            changeHandlerAdded = true;
            this.editorOverlay.on(CHANGE, new CMEditorOverlay.EventHandlerOneParameter<CMChangeEventOverlay>() {

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
            this.editorOverlay.on(FOCUS, new CMEditorOverlay.EventHandlerNoParameters() {

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
            this.editorOverlay.on(BLUR, new CMEditorOverlay.EventHandlerNoParameters() {

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
            this.editorOverlay.on(SCROLL, new CMEditorOverlay.EventHandlerNoParameters() {

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
            this.editorOverlay.on(CURSOR_ACTIVITY, new CMEditorOverlay.EventHandlerNoParameters() {

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
            this.editorOverlay.on(BEFORE_SELECTION_CHANGE,
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
            this.editorOverlay.on(VIEWPORT_CHANGE, new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

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
            this.editorOverlay.on(GUTTER_CLICK, new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

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
        this.editorOverlay.setOption(READONLY, isReadOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.editorOverlay.getBooleanOption(READONLY);
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
        final String propertyValue = this.keymapPrefReader.readPref(CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY);
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
            if (keymapKey == null || keymapKey.startsWith("emacs") || keymapKey.startsWith("vim")) {
                continue;
            }
            sb.append("# ").append(keymapKey).append("\n\n");
            final CMKeymapOverlay keymap = keymapsObject.get(keymapKey);
            for (final String binding : keymap.getKeys()) {
                if ("fallthrough".equals(binding)
                    || "nofallthrough".equals(binding)
                    || "disableInput".equals(binding)
                    || "auto".equals(binding)) {
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
        return EditorType.getInstance(CodeMirrorEditorExtension.CODEMIRROR_EDITOR_KEY);
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

    @Override
    public void onResize() {
        this.editorOverlay.refresh();
    }

    @Override
    public HandlesUndoRedo getUndoRedo() {
        return this.undoRedo;
    }

    public void showMessage(final String message) {
        final CMDialogOptionsOverlay options = JavaScriptObject.createObject().cast();
        options.setBottom(true);
        final CMDialogOverlay dialog = this.editorOverlay.getDialog();
        if (dialog != null) {
            dialog.openNotification(message, options);
        } else {
            Log.info(CodeMirrorEditorWidget.class, message);
        }
    }

    public void addKeybinding(final Keybinding keybinding) {
        final String keySpec = KeybindingTranslator.translateKeyBinding(keybinding, this.codeMirror);
        if (keySpec == null) {
            LOG.warning("Couldn't bind key, keycode is unknown.");
            return;
        }
        final KeyBindingAction bindingAction = keybinding.getAction();
        if (bindingAction == null) {
            LOG.warning("Cannot bind null action on "+ keySpec +".");
            return;
        }
        LOG.info("Binding action on " + keySpec + ".");
        this.keyBindings.addBinding(keySpec, bindingAction, new CodeMirrorKeyBindingAction<KeyBindingAction>() {

            @Override
            public void action(final KeyBindingAction action) {
                action.action();
            }
        });
    }

    public void addGutterItem(final int line, final String gutterId, final Element element){
        this.editorOverlay.setGutterMarker(line, gutterId, element);
    }

    public void removeGutterItem(final int line, final String gutterId) {
        this.editorOverlay.setGutterMarker(line, gutterId, (Element)null);
    }

    public void addGutterItem(final int line, final String gutterId, final elemental.dom.Element element) {
        this.editorOverlay.setGutterMarker(line, gutterId, element);
    }

    public void addGutterItem(final int line, final String gutterId,
                              final elemental.dom.Element element,
                              final LineNumberingChangeCallback lineCallback) {
        this.editorOverlay.setGutterMarker(line, gutterId, element);
        this.codeMirror.on(editorOverlay, EventTypes.CHANGE,
                           new EventHandlerMixedParameters() {
                @Override
                public void onEvent(final JsArrayMixed params) {
                    // 0->editor, 1->event object
                    final CMChangeEventOverlay event = params.getObject(1);
                    final JsArrayString newText = event.getText();
                    final CMPositionOverlay from = event.getFrom();
                    final CMPositionOverlay to = event.getTo();

                    // if the first character of the line is not included, the (potential) line
                    // numbering change only starts at the following line.
                    int changeStart = from.getLine() + 1;

                    int removedCount = 0;
                    if (from.getLine() != to.getLine()) {
                        // no lines were removed
                        // don't count first line yet
                        removedCount = Math.abs(from.getLine() - to.getLine()) - 1;
                        if (from.getCharacter() == 0) {
                            // start of first line is included, 'to' is on another line, so the line is deleted
                            removedCount = removedCount + 1;
                            changeStart = changeStart - 1;
                        }
                        // if 'to' is at the end of the line, the line is _not_ removed, just emptied
                    }
                    // else no lines were removed

                    final int addedCount = newText.length() - 1;

                    // only call back if there is a change in the lines
                    if (removedCount > 0 || addedCount > 0) {
                        LOG.fine("Line change from l." + changeStart + " removed " + removedCount + " added " + addedCount);
                        lineCallback.onLineNumberingChange(changeStart,
                                                           removedCount,
                                                           addedCount);
                    }
                }

        });
    }

    public elemental.dom.Element getGutterItem(final int line, final String gutterId) {
        final CMLineInfoOverlay lineInfo = this.editorOverlay.lineInfo(line);
        if (lineInfo == null) {
            LOG.fine("No lineInfo for line" + line);
            return null;
        }
        if (lineInfo.getGutterMarkers() == null) {
            LOG.fine("No gutter markers for line" + line);
            return null;
        }
        final CMGutterMarkersOverlay markers = lineInfo.getGutterMarkers();
        if (markers.hasMarker(gutterId)) {
            return markers.getMarker(gutterId);
        } else {
            LOG.fine("No markers found for gutter " + gutterId + "on line " + line);
            return null;
        }
    }

    public void clearGutter(final String gutterId) {
        this.editorOverlay.clearGutter(gutterId);
    }

    public MarkerRegistration addMarker(final TextRange range, final String className) {
        final CMPositionOverlay from = CMPositionOverlay.create(range.getFrom().getLine(), range.getFrom().getCharacter());
        final CMPositionOverlay to = CMPositionOverlay.create(range.getTo().getLine(), range.getTo().getCharacter());
        final CMTextMarkerOptionOverlay options = JavaScriptObject.createObject().cast();
        options.setClassName(className);

        final CMTextMarkerOverlay textMark = this.editorOverlay.asMarksManager().markText(from, to, options);
        if (textMark == null) {
            LOG.warning("addMarker: markText returned a undefined TextMarker - range=" + range);
            return null;
        }
        return new MarkerRegistration() {
            @Override
            public void clearMark() {
                textMark.clear();
            }
        };
    }

    interface CodeMirrorEditorWidgetUiBinder extends UiBinder<SimplePanel, CodeMirrorEditorWidget> {
    }
}
