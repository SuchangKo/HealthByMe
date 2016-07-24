var express = require('express');
var router = express.Router();

//For SQLite
var file = "healthbyme.db";
var sqlite3 = require("sqlite3").verbose();
var db = new sqlite3.Database(file);

/* GET Food listing. */
router.get('/', function (req, res, next) {
    console.log("SELECT * FROM Food WHERE user_id=" + req.query.user_id + " AND eat_time = date('now');");


    db.get("SELECT COUNT(*) AS count FROM Food WHERE user_id=" + req.query.user_id + " AND date(eat_time) = date('now')", function (err, row) {
        console.log(row);
        var row_count = row.count;
        var tmp_count = 0;
        var result_json = [];

        if(err){
            console.log(err);
        }
        if(row.count == 0){
            res.send([]);
        }
        db.each("SELECT * FROM Food WHERE user_id=" + req.query.user_id + " AND date(eat_time) = date(\'now\')", function (err, row) {
            result_json.push({
                'food_id': row.food_id,
                'user_id': row.user_id,
                'food': row.food,
                'kcal': row.kcal,
                'description': row.description,
                'eat_time': row.eat_time,
            });
            tmp_count++;
            if (tmp_count == row_count) {
                res.send(result_json);
            }
        });
    });

});

/* POST food Writing */
router.post('/', function (req, res, next) {
    db.get("SELECT COUNT(*) AS count FROM User WHERE user_id=" + req.body.user_id, function (err, row) {
        if (row.count == 0) { //NOT Exist User
            res.send({
                'result': false
            });
        } else {
            var stmt = db.prepare("INSERT INTO Food (user_id,food,kcal,description,eat_time) VALUES (?,?,?,?,DATETIME('now','localtime'))");
            stmt.run(req.body.user_id, req.body.food, req.body.kcal, req.body.description);
            res.send({
                'result': true
            });
        }
    });
});


/* PUT food fix */
router.put('/', function (req, res, next) {
    /*
     Request
     food_id,user_id,food,kcal,eat_Time,description
     Response
     result:T/F
     user_id,food,kcal,eat_Time,description
     */
    db.get("SELECT COUNT(*) AS count FROM Food WHERE food_id=" + req.body.food_id, function (err, row) {
        if (row.count == 0) { //is Not Exist
            console.log("[LOG] FOOD/DELETE/ISNOTEXIST_FOOD");
            res.send({
                'result': false
            });
        } else { //is Exist
            console.log("[LOG] FOOD/DELETE/EXIST_FOOD");
            db.run("UPDATE Food SET food ='" + req.body.food + "',kcal=" + req.body.kcal + ",description='" + req.body.description + "' WHERE food_id=" + req.body.food_id, function (err, row) {
                db.get("SELECT * FROM Food WHERE food_id=" + req.body.food_id, function (err, row) {
                    res.send({
                        'result':true,
                        'food_id': row.food_id,
                        'user_id': row.user_id,
                        'food': row.food,
                        'kcal': row.kcal,
                        'description': row.description,
                        'eat_time': row.eat_time,
                    });
                });
            });
        }
    });
});


/* DELETE food post */
router.delete('/', function (req, res, next) {
    /*
     Request
     food_id
     Response
     result:T/F
     */
    db.get("SELECT COUNT(*) AS count FROM Food WHERE food_id=" + req.body.food_id, function (err, row) {
        if (row.count == 0) { //is Not Exist
            console.log("[LOG] FOOD/DELETE/ISNOTEXIST_FOOD");
            res.send({
                'result': false
            });
        } else { //is Exist
            console.log("[LOG] FOOD/DELETE/EXIST_FOOD");
            db.run("DELETE From Food WHERE food_id=" + req.body.food_id, function (err, row) {
                db.get("SELECT COUNT(*) AS count FROM Food WHERE food_id=" + req.body.food_id, function (err, row) {
                    if (row.count == 0) {
                        res.send({
                            'result': true
                        });
                    } else {
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
