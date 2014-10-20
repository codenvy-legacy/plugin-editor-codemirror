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

CodeMirror.defineMode('dockerfile', function(config) {

  var indentUnit = config.indentUnit;

  function keywordRegexp(words) {
    return new RegExp('^\\s*((' + words.join(')|(') + '))\\b', 'i');
  }

  var keywords =  keywordRegexp([
                  'ADD',
                  'CMD',
                  'COPY',
                  'ENTRYPOINT',
                  'ENV',
                  'EXPOSE',
                  'FROM',
                  'MAINTAINER',
                  'ONBUILD',
                  'RUN',
                  'USER',
                  'VOLUME',
                  'WORKDIR',
                  ]);

  // token type constants
  var INSTRUCTION = 'keyword';
  var COMMENT = 'comment';
  var CONT_MARKER = 'variable-2';
  var BRACKET = 'bracket';
  var EXEC_FORM_ARG = 'string';
  var COMMA = 'atom';
  var ERROR = 'error';


  // the pattern used by the docker parser
  var lineContinuation = /^\\\s*$/;
  // pattern for double quoted strings with escapes ; single quoted string are not valid here
  var quoted = /^"[^"\\]*(?:\\.[^"\\]*)*"/;

  function tokenize(stream, state) {
    if (stream.sol()) {
      // # only mark comment start when first character on the line (i.e. no leading spaces)
      if (stream.peek() == '#') {
        stream.skipToEnd();
        state.followInstruction = false;
        return COMMENT;
      }
      // keywords must start line
      // keywords are case-insensitive
      if (stream.match(keywords, true)) {
        state.inMultiline = false;
        state.followInstruction = true;
        return INSTRUCTION;
      }
      // the only other allowed line start is for line continuation
      if (!state.inMultiline) {
        stream.skipToEnd();
        state.followInstruction = false;
        state.inMultiline = false;
        state.bracketedArg = false;
        return ERROR;
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
      return CONT_MARKER;
    }
    if (state.followInstruction && stream.eat('[')) {
      state.bracketedArg = true;
      state.followInstruction = false;
      state.inMultiline = false;
      return BRACKET;
    }
    if (state.bracketedArg && stream.eat(']')) {
      state.followInstruction = false;
      state.bracketedArg = false;
      state.inMultiline = false;
      return BRACKET;
    }
    // _inside_ the brackets, either string or comma
    if (state.bracketedArg && stream.eat(',')) {
      state.followInstruction = false;
      state.inMultiline = false;
      return COMMA;
    }
    if (state.bracketedArg && stream.match(quoted, true)) {
      state.followInstruction = false;
      state.inMultiline = false;
      return EXEC_FORM_ARG;
    }
    if (!state.bracketedArg) {
      while (!stream.eol()) {
        if (stream.eatWhile(/^[^\\\s]/)) { // eats until next whitespace or \
          if (stream.peek() === '\\') {
            if (stream.match(lineContinuation, false)) {
              state.inMultiline = true;
              state.followInstruction = false;
              return null;
            } else { // consume the \ and continue
              stream.next();
              state.inMultiline = false;
              state.followInstruction = false;
            }
          } else {
            // it was a whitespace
            return null;
          }
        } else {
          // next character is a \
          if (stream.match(lineContinuation, true)) {
            state.inMultiline = true;
            state.followInstruction = false;
            return CONT_MARKER;
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
      return ERROR;
    }
  }

  return {
    startState: function () {
      var state = {};
      state.inMultiline = false;
      state.followInstruction = false;
      state.bracketedArg = false;
      return state;
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

CodeMirror.defineMIME('text/x-dockerfile-config', 'dockerfile');

});
