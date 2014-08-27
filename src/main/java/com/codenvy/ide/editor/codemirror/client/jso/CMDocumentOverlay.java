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
package com.codenvy.ide.editor.codemirror.client.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class CMDocumentOverlay extends JavaScriptObject {

    protected CMDocumentOverlay() {
    }

    // history

    public final native void undo() /*-{
        return this.undo();
    }-*/;

    public final native void redo() /*-{
        return this.redo();
    }-*/;

    public final native void clearHistory() /*-{
        return this.clearHistory();
    }-*/;

    public final native void undoSelection() /*-{
        return this.undoSelection();
    }-*/;

    public final native void redoSelection() /*-{
        return this.redoSelection();
    }-*/;

    public final native int historyUndoSize() /*-{
        return this.historySize().undo;
    }-*/;

    public final native int historyRedoSize() /*-{
        return this.historySize().redo;
    }-*/;

    // content

    public final native String getValue() /*-{
        return this.getValue();
    }-*/;

    public final native String getValue(String separator) /*-{
        return this.getValue(separator);
    }-*/;

    public final native void setValue(String newContent) /*-{
        this.setValue(newContent);
    }-*/;

    public final native String getRange(int fromLine, int fromChar, int toLine, int toChar) /*-{
        return this.getRange({ line : fromLine, ch : fromChar }, { line : toLine, ch : toChar });
    }-*/;

    public final native String getRange(int fromLine, int fromChar, int toLine, int toChar, String separator) /*-{
        return this.getRange({ line : fromLine, ch : fromChar }, { line : toLine, ch : toChar }, separator);
    }-*/;

    /**
     * Replace the text range with the new text.
     * 
     * @param replacement the new text
     * @param fromLine the line of the beginning of the range
     * @param fromChar the char of the beginning of the range
     * @param toLine the line of the end of the range
     * @param toChar the char of the end of the range
     */
    public final native void replaceRange(String replacement, int fromLine, int fromChar, int toLine, int toChar) /*-{
        return this.replaceRange(replacement, { line : fromLine, ch : fromChar }, { line : toLine, ch : toChar });
    }-*/;

    /**
     * insert the text at the given position.
     * 
     * @param replacement the text to insert
     * @param fromLine the line of the insertion position
     * @param fromChar the char of the insertion position
     */
    public final native void replaceRange(String replacement, int fromLine, int fromChar) /*-{
        return this.replaceRange(replacement, { line : fromLine, ch : fromChar });
    }-*/;

    public final native String getLine(int index) /*-{
        return this.getLine(index);
    }-*/;

    public final native int firstLine() /*-{
        return this.firstLine();
    }-*/;

    public final native int lastLine() /*-{
        return this.lastLine();
    }-*/;


    public final native int lineCount() /*-{
        return this.lineCount();
    }-*/;

    // position

    public final native CMPositionOverlay getCursor() /*-{
        return this.getCursor();
    }-*/;

    /**
     * Returns the position one end of the primary selection.
     * 
     * @param whichEndOfSelection "from", "to", "head" or "anchor"
     * @return one end
     */
    public final native CMPositionOverlay getCursor(String whichEndOfSelection) /*-{
        return this.getCursor(whichEndOfSelection);
    }-*/;

    public final CMPositionOverlay getCursorFrom() {
        return this.getCursor("from");
    }

    public final CMPositionOverlay getCursorTo() {
        return this.getCursor("to");
    }

    public final CMPositionOverlay getCursorHead() {
        return this.getCursor("head");
    }

    public final CMPositionOverlay getCursorAnchor() {
        return this.getCursor("anchor");
    }

    public final native void setCursor(CMPositionOverlay position)/*-{
        this.setCursor(position);
    }-*/;

    public final native void setCursor(int line, int ch)/*-{
        this.setCursor(line, ch);
    }-*/;

    public final native void setCursor(CMPositionOverlay position, CMSetSelectionOptions options)/*-{
        this.setCursor(position, options);
    }-*/;

    public final native void setCursor(int line, int ch, CMSetSelectionOptions options)/*-{
        this.setCursor(line, ch, options);
    }-*/;

    public final native CMPositionOverlay posFromIndex(int index) /*-{
        return this.posFromIndex(index);
    }-*/;

    public final native int indexFromPos(CMPositionOverlay position) /*-{
        return this.indexFromPos(position);
    }-*/;

    // editor

    public final native CMEditorOverlay getEditor() /*-{
        return this.getEditor();
    }-*/;

    // selection

    public final native String getSelection() /*-{
        return this.getSelection();
    }-*/;

    public final native String getSelection(String lineSep) /*-{
        return this.getSelection(lineSep);
    }-*/;

    public final native JsArrayString getSelections(String lineSep) /*-{
        return this.getSelections(lineSep);
    }-*/;

    public final native boolean somethingSelected() /*-{
        return this.somethingSelected();
    }-*/;

    public final native JsArray<CMSelectionOverlay> listSelections() /*-{
        return this.listSelection();
    }-*/;

    public final native void replaceSelection(String replacement) /*-{
        this.replaceSelection(replacement);
    }-*/;

    public final native void setSelection(CMPositionOverlay anchor) /*-{
        this.setSelection(anchor);
    }-*/;

    public final native void setSelection(CMPositionOverlay anchor, CMPositionOverlay head) /*-{
        this.setSelection(anchor, head);
    }-*/;

    public final native void setSelection(CMPositionOverlay anchor, CMPositionOverlay head,
                                          CMSetSelectionOptions options) /*-{
        this.setSelection(anchor, head, options);
    }-*/;

    /**
     * Sets a new set of selections. There must be at least one selection in the given array.<br>
     * The primary index is taken from the previous selection, or set to the last range if the
     * previous selection had less ranges than the new one.
     * @param ranges the ranges of the new selection
     */
    public final native void setSelections(JsArray<CMSelectionOverlay> ranges) /*-{
        this.setSelections(ranges);
    }-*/;

    /**
     * Sets a new set of selections. There must be at least one selection in the given array.
     * @param ranges the ranges of the new selection
     * @param primary determines which selection is the primary one
     */
    public final native void setSelections(JsArray<CMSelectionOverlay> ranges, int primary) /*-{
        this.setSelections(ranges, primary);
    }-*/;

    /**
     * Sets a new set of selections. There must be at least one selection in the given array.
     * @param ranges the ranges of the new selection
     * @param primary determines which selection is the primary one
     * @param options selection options
     */
    public final native void setSelections(JsArray<CMSelectionOverlay> ranges, int primary,
                                           CMSetSelectionOptions options) /*-{
        this.setSelections(ranges, primary, options);
    }-*/;

    /**
     * Sets a new set of selections. There must be at least one selection in the given array.
     * @param ranges the ranges of the new selection
     * @param options selection options
     */
    public final native void setSelections(JsArray<CMSelectionOverlay> ranges,
                                           CMSetSelectionOptions options) /*-{
        this.setSelections(ranges, options);
    }-*/;

    /**
     * Add a new selection to the existing selections and makes it the primary selection.
     * @param anchor the anchor of the selection
     * @param head the head of the selection
     */
    public final native void addSelection(CMPositionOverlay anchor, CMPositionOverlay head) /*-{
        this.addSelection(anchor, head);
    }-*/;

    /**
     * Similar to setSelection, but will, if shift is held or the extending flag is set, move the head
     * of the selection while leaving the anchor at its current place.<br>
     * When multiple selections are present, all but the primary selection will be dropped by this method.
     * @param from origin of extension
     */
    public final native void extendSelection(CMPositionOverlay from) /*-{
        this.extendSelection(from);
    }-*/;

    /**
     * Same as {@link #extendSelection(CMPositionOverlay)}, the 'to' argument ensurse a region
     * (for example a word or paragraph) will end up selected (in addition to whatever lies
     * between that region and the current anchor). 
     * @param from origin of extension
     * @param to mandatory position that must be included in selection 
     */
    public final native void extendSelection(CMPositionOverlay from, CMPositionOverlay to) /*-{
        this.extendSelection(from, to);
    }-*/;

    public final native void extendSelection(CMPositionOverlay from,
                                             CMSetSelectionOptions options) /*-{
        this.extendSelection(from, options);
    }-*/;

    public final native void extendSelection(CMPositionOverlay from, CMPositionOverlay to,
                                             CMSetSelectionOptions options) /*-{
        this.extendSelection(from, to, options);
    }-*/;

    /**
     * An equivalent of {@link #extendSelection(CMPositionOverlay)} that acts on all selections at once.
     * @param heads
     */
    public final native void extendSelections(JsArray<CMPositionOverlay> heads) /*-{
        this.extendSelections(heads);
    }-*/;

    /**
     * An equivalent of {@link #extendSelection(CMPositionOverlay, CMSetSelectionOptions)} that acts on all selections at once.
     * @param heads
     * @param options
     */
    public final native void extendSelections(JsArray<CMPositionOverlay> heads,
                                             CMSetSelectionOptions options) /*-{
        this.extendSelections(heads, options);
    }-*/;

    /**
     * Sets or clears the 'extending' flag, which acts similar to the shift key, in that it will cause cursor movement
     * and calls to extendSelection to leave the selection anchor in place.
     * @param extending the extending flag (true to extend, falsse to clear)
     */
    public final native void setExtending(boolean extending) /*-{
        this.setExtending(extending);
    }-*/;

    /**
     * Get the value of the 'extending' flag.
     * @return the flag
     */
    public final native boolean getExtending() /*-{
        return this.getExtending();
    }-*/;

    /**
     * Replace selection(s) with replacement.
     * 
     * @param replacement the replacement string
     * @param selectAfter if "around", the new text is selected, if "start", the selection is at the beginning of the new text
     * @return
     */
    public final native void replaceSelection(String replacement, String selectAfter) /*-{
        this.replaceSelection(replacement, selectAfter);
    }-*/;

    // line operations

    public final native CMLineHandleOverlay getLineHandle(int lineIndex) /*-{
        return this.getLineHandle(lineIndex);
    }-*/;

    public final native int getLineNumber(CMLineHandleOverlay linehandle) /*-{
        return this.getLineNumber(lineHandle);
    }-*/;

    public final native void eachline(LineOperation lineOperation) /*-{
        this.eachLine(
            lineOperation.@com.codenvy.ide.editor.codemirror.client.jso.CMDocumentOverlay.LineOperation::processLine(Lcom/codenvy/ide/editor/codemirror/client/jso/CMLineHandleOverlay;)
        );
    }-*/;

    public final native void eachline(int start, int end, LineOperation lineOperation) /*-{
        this.eachLine(
            start, end,
            lineOperation.@com.codenvy.ide.editor.codemirror.client.jso.CMDocumentOverlay.LineOperation::processLine(Lcom/codenvy/ide/editor/codemirror/client/jso/CMLineHandleOverlay;)
        );
    }-*/;

    public interface LineOperation {
        void processLine(CMLineHandleOverlay lineHandle);
    }

    // clean/dirty state

    public final native boolean isClean() /*-{
        return this.isClean();
    }-*/;

    public final native boolean isClean(int generation) /*-{
        return this.isClean(generation);
    }-*/;

    /**
     * @deprecated by upstream - use {@link #changeGeneration()} or {@link #changeGeneration(boolean)} instead<br>
     *             Kept for API completeness
     */
    @Deprecated
    public final native void markClean() /*-{
        this.markClean();
    }-*/;

    public final native int changeGeneration() /*-{
        return this.changeGeneration();
    }-*/;

    public final native int changeGeneration(boolean closeEvent) /*-{
        return this.changeGeneration(closeEvent);
    }-*/;
}
