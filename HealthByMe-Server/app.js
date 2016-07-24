var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');

var routes = require('./routes/index');
var users = require('./routes/users');
var foods = require('./routes/foods');

var app = express();

// set SQLite setup
var fs = require('fs');
var file = "healthbyme.db";
var exists = fs.existsSync(file);
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(file);

/*
 * User Table Schema
 * - user_id int
 * - fb_id text
 * - name text
 * - nickname text
 * - yesterday_kcal int
 * - gcm text
 *db.run("CREATE TABLE Food (food_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id TEXT,food TEXT,kcal INTEGER,description TEXT,eat_time DATETIME)");
 * Food Table Schema
 * - food_id int
 * - user_id int
 * - food text
 * - kcal int
 * - description text
 * - eat_time datetime
 *
 * */

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(path.join(__dirname, 'public', 'favicon.ico')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', routes);
app.use('/users', users);
app.use('/foods', foods);


// catch 404 and forward to error handler
app.use(function (req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function (err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function (err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;
