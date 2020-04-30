module.exports = {
    mongo: null,
    app: null,
    init: function (app,mongo) {
        this.mongo = mongo;
        this.app = app;
    },
    insertUser: function (user, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection('users');
                collection.insert(user, function (err, result) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(result.ops[0]._id);
                    }
                    db.close();
                });
            }
        });
    },
};