var express = require('express');
var router = express.Router();
var gcm = require('node-gcm');

//For SQLite
var file = "healthbyme.db";
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(file);


/* POST users join */
router.post('/', function (req, res, next) {
    db.each("SELECT * FROM User",function (err,row) {

        var message = new gcm.Message({
            collapseKey: "demo",
            delayWhileIdle: true,
            timeToLive: 3,
            data: {
                title: req.body.title,
                message: req.body.message,
                custom_key1: req.body.key1,
                custom_key2: req.body.key2
            }
        });

        var server_api_key = "AIzaSyBcM6fBDmTCVB6iK_havo2Do9IQw764ZDo";
        var sender = new gcm.Sender(server_api_key);
        var registrationIds = [];

        var token = row.gcm;
        registrationIds.push(token);

        sender.send(message, registrationIds, 4, function (err, result) {
            console.log(result);
        });
    });
    res.send("Success");
});

module.exports = router;