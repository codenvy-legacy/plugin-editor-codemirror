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
var gulp = require('gulp'),
    rjs = require('gulp-r'),
    rename = require('gulp-rename'),
    gutil = require('gulp-util'),
    del = require('del'),
    args = require('yargs').argv,
    debug = require("gulp-debug"),
    path = require('path');

var cmVersion = args.cmVersion;
if (!cmVersion) {
    throw new Error('Codemirror version is not defined');
}
console.log("Codemirror version: " + cmVersion);

var startDir = process.cwd(); // strating in projectRoot/src/main/frontend
var projectRoot = path.join(startDir, '../../..');
console.log("Project root: " + projectRoot);

var cmPath = path.join(projectRoot, '/target/codemirror-resources/com/codenvy/ide/editor/codemirror/public/codemirror/');
console.log("CM base: " + cmPath);

gulp.task('optimize', function() {
    gulp.src([path.join(cmPath, '/lib/*.js')], //, path.join(cmPath, 'addon/**/*.js'), path.join(cmPath, 'keymap/**/*.js'), path.join(cmPath, 'mode/**/*.js')],
            { base: cmPath })
        .pipe(debug())
        .pipe(rjs({
            "baseUrl": cmPath,
            "generateSourceMaps": true,
            "optimize": "uglify2",
            "preserveLicenseComments": false
        }))
        .pipe(rename({
            "extname": ".min.js"
        }))
        .pipe(gulp.dest(path.join(cmPath, 'min/')));
});


gulp.task('clean', function(cb) {
    // You can use multiple globbing patterns as you would with `gulp.src`
    del(['node_modules'], cb);
});