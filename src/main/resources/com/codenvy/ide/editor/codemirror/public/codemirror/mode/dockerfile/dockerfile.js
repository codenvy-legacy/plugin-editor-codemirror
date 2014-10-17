/*
 * Copyright (c) 2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
// CodeMirror, copyright (c) by Marijn Haverbeke and others
// Distributed under an MIT license: http://codemirror.net/LICENSE

(function(mod) {
  if (typeof exports == "object" && typeof module == "object") // CommonJS
    mod(require("../../lib/codemirror"));
  else if (typeof define == "function" && define.amd) // AMD
    define(["../../lib/codemirror"], mod);
  else // Plain browser env
    mod(CodeMirror);
})(function(CodeMirror) {
"use strict";

CodeMirror.defineMode("dockerfile", function(config) {

  var indentUnit = config.indentUnit;

  function keywordRegexp(words) {
    return new RegExp("^\\s*((" + words.join(")|(") + "))\\b", "i");
  }

  var keywords =  keywordRegexp([
                  "ADD",
                  "CMD",
                  "COPY",
                  "ENTRYPOINT",
                  "ENV",
                  "EXPOSE",
                  "FROM",
                  "MAINTAINER",
                  "ONBUILD",
                  "RUN",
                  "USER",
                  "VOLUME",
                  "WORKDIR",
                  ]);
  // the pattern used by the docker parser
  var lineContinuation = /^\\\s*$/;
  // pattern for double quoted strings with escapes ; single quoted string are not valid here
  var quoted = /^"[^"\\]*(?:\\.[^"\\]*)*"/;
  // pattern for start of multiline string
  var unclosedStringStart = /^"[^"\\]*(?:\\.[^"\\]*)*\\\s*$/;
  // pattern for not closing multiline string
  var notClosingStringCont = /^[^"\\]*(?:\\.[^"\\]*)*/;
  // pattern for closing multiline string
  var closingStringCont = /^[^"\\]*(?:\\.[^"\\]*)*"/;

  function tokenize(stream, state) {
    if (stream.sol()) {
      // # only mark comment start when first character on the line (i.e. no leading spaces)
      if (stream.peek() == "#") {
        stream.skipToEnd();
        state.followInstruction = false;
        return "comment";
      }
      // keywords must start line
      // keywords are case-insensitive
      if (stream.match(keywords, true)) {
        state.inMultiline = false;
        state.followInstruction = true;
        return "keyword";
      }
      // the only other allowed line start is for line continuation
      if (!state.inMultiline) {
        stream.skipToEnd();
        state.followInstruction = false;
        state.inMultiline = false;
        state.bracketedArg = false;
        return "error";
      } else {
        return tokenizeArgs(stream, state);
      }
    } else {
      return tokenizeArgs(stream, state);
    }
  }

  function tokenizeArgs(stream, state) {
    if (stream.eatSpace()) {
      state.inMultiline = false;
      return null;
    }
    if (stream.match(lineContinuation, true)) {
      state.inMultiline = true;
      return "variable-2";
    }
    if (state.followInstruction && stream.eat("[")) {
      state.bracketedArg = true;
      state.followInstruction = false;
      state.inMultiline = false;
      return "bracket";
    }
    if (state.bracketedArg && stream.eat("]")) {
      state.followInstruction = false;
      state.bracketedArg = false;
      state.inMultiline = false;
      return "bracket";
    }
    // _inside_ the brackets, either string or comma
    if (state.bracketedArg && stream.eat(",")) {
      state.followInstruction = false;
      state.inMultiline = false;
      return "atom";
    }
    if (state.bracketedArg && stream.match(quoted, true)) {
      state.followInstruction = false;
      state.inMultiline = false;
      return "string";
    }
    if (! state.bracketedArg) {
      // FIRST check if we are in a unclosed multiline string
      if (state.inMultiline && state.inString) {
        if (stream.match(notClosingStringCont, true)) {
          
        }
      }
      if (stream.match(quoted, true)) {
        state.followInstruction = false;
        state.inMultiline = false;
        return "string-2";
      }
      if (stream.match(unclosedStringStart, true)) {
        state.followInstruction = false;
        state.inMultiline = true;
        state.inString = true;
        return "string-2";
      }
    }
    if (!state.bracketedArg) {
      while (!stream.eol()) {
        if (stream.eatWhile(/^[^\\]/)) {
          if (stream.match(lineContinuation, false)) {
            state.inMultiline = false;
            state.followInstruction = false;
            return null;
          } else {
            // consume the \ and continue
            stream.next();
            state.inMultiline = false;
            state.followInstruction = false;
          }
        } else {
          // next character is a \
          if (stream.match(lineContinuation, true)) {
            state.inMultiline = true;
            state.followInstruction = false;
            return "variable-2";
          } else {
            stream.next();
            state.inMultiline = false;
            state.followInstruction = false;
          }
        }
      }
    } else {
      stream.skipToEnd();
      state.bracketedArg = false;
      state.inMultiline = false;
      state.followInstruction = false;
      return "error";
    }
  }

  return {
    startState: function () {
      var state = {};
      state.inMultiline = false;
      state.followInstruction = false;
      state.bracketedArg = false;
      state.inString = false;
      return state;
    },
    copyState: function(otherState) {
      var newState = {};
      newState.inMultiline = otherState.inMultiline;
      newState.followInstruction = otherState.followInstruction;
      newState.bracketedArg = otherState.bracketedArg;
      return newState;
    },
    token: function (stream, state) {
      return tokenize(stream, state);
    },
    indent: function(state, textAfter) {
      if (state.inMultiline) {
        return indentUnit;
      } else {
        return 0;
      }
    },
    lineComment: '#'
  };
});

CodeMirror.defineMIME("text/x-dockerfile-config", "dockerfile");

});
