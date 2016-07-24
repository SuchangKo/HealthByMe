var express = require('express');
var router = express.Router();

//For SQLite
var file = "healthbyme.db";
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(file);

/* GET users Info. */
router.get('/', function (req, res, next) {
    /*
     Request
     user_id
     Response
     user_id,fb_id,name,nickname,yesterday_kcal
     */
    db.get("SELECT COUNT(*) AS count FROM User WHERE user_id=" + req.query.user_id, function (err, row) {
        if (row.count == 0) { //is Not Exist
            res.send({
                'result': false
            });
        } else { //is Exist
            db.get("SELECT user_id,fb_id,name,nickname,yesterday_kcal from User WHERE user_id =" + req.query.user_id, function (err, row) {
                if (row.user_id != null) {
                    console.log(req.query.user_id + " is Exist");
                    res.send({
                        'result': true,
                        'user_id': row.user_id,
                        'fb_id': row.fb_id,
                        'name': row.name,
                        'nickname': row.nickname,
                        'yesterday_kcal': row.yesterday_kcal
                    });
                } else {
                    console.log(req.query.user_id + " is not Exist");
                    res.send({
                        'result': false
                    });
                }
            });
        }
    });


});

/* POST users join */
router.post('/', function (req, res, next) {
    /*
     Request
     user_id
     Response
     user_id,fb_id,name,nickname,yesterday_kcal,gcm
     */

    db.get("SELECT COUNT(*) AS count FROM User WHERE fb_id LIKE '" + req.body.fb_id + "'", function (err, row) {
        console.log(row.count);
        if (row.count == 0) { // New user
            var stmt = db.prepare("INSERT INTO User (fb_id,name,nickname,gcm) VALUES (?,?,?,?)");
            stmt.run(req.body.fb_id, req.body.name, req.body.nickname, req.body.gcm);
            db.get("SELECT * FROM User WHERE fb_id LIKE '" + req.body.fb_id + "'", function (err, row) {
                console.log("[LOG] USER/POST/NEW_USER");
                if (err) {
                    res.send({
                        'result': false
                    });
                } else {
                    res.send({
                        'result': true,
                        'user_id': row.user_id
                    });
                }
            });
        } else {  // Exist User & Update GCM
            db.run("UPDATE User SET gcm = '" + req.body.gcm + "' WHERE fb_id LIKE '" + req.body.fb_id + "'", function (err, row) {
                db.get("SELECT * FROM User WHERE fb_id LIKE '" + req.body.fb_id + "'", function (err, row) {
                    console.log("[LOG] USER/POST/EXIST_USER");
                    if (err) {
                        res.send({
                            'result': false
                        });
                    } else {
                        res.send({
                            'result': true,
                            'user_id': row.user_id
                        });
                    }
                });
            });

        }

    });
});

/* PUT users update nickname */
router.put('/', function (req, res, next) {
    /*
     Request
     user_id
     Response
     user_id,fb_id,name,nickname,yesterday_kcal,gcm
     */
    db.get("SELECT COUNT(*) AS count FROM User WHERE user_id=" + req.body.user_id, function (err, row) {
        if (row.count == 0) { //is Not Exist
            res.send({
                'result': false
            });
        } else { //is Exist
            db.run("UPDATE User SET nickname = '" + req.body.nickname + "' WHERE user_id=" + req.body.user_id, function (err, row) {
                db.get("SELECT user_id,fb_id,name,nickname,yesterday_kcal FROM User WHERE user_id=" + req.body.user_id, function (err, row) {
                    console.log("[LOG] USER/POST/EXIST_USER");
                    if (err) {
                        console.log(err);
                        res.send({
                            'result': false
                        });
                    } else {
                        res.send({
                            'result': true,
                            'user_id': row.user_id,
                            'fb_id': row.fb_id,
                            'name': row.name,
                            'nickname': row.nickname,
                            'yesterday_kcal': row.yesterday_kcal
                        });
                    }
                });
            });
        }
    });
});

/* DELETE users Info */
router.delete('/', function (req, res, next) {
    /*
     Request
     user_id
     Response

     */
    db.get("SELECT COUNT(*) AS count FROM User WHERE user_id=" + req.body.user_id, function (err, row) {
        if (row.count == 0) { //is Not Exist
            console.log("[LOG] USER/DELETE/ISNOTEXIST_USER");
            res.send({
                'result': false
            });
        } else { //is Exist
            console.log("[LOG] USER/DELETE/EXIST_USER");
            db.run("DELETE From User WHERE user_id="+req.body.user_id, function (err, row) {
                db.get("SELECT COUNT(*) AS count FROM User WHERE user_id=" + req.body.user_id, function (err, row) {
                    if(row.count == 0){
                        res.send({
                            'result': true
                        });
                    }else{
                        res.send({
                            'result': false
                        });
                    }
                });
            });
        }
    });
});

module.exports = router;