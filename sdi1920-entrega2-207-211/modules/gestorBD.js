module.exports = {
    mongo: null,
    app: null,
    init: function (app,mongo) {
        this.mongo = mongo;
        this.app = app;
    },

    obtainUsersPg: function (criterio, pg, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                console.log('AAAAAAAAAAAAAAAA');
                funcionCallback(null);
            } else {
                let collection = db.collection('users');
                let totalResults = collection.find(criterio);

                totalResults.count(function(err, count){
                    totalResults.skip( (pg-1)*5 ).limit( 5 )
                        .toArray(function(err, usuarios) {
                            if (err) {
                                funcionCallback(null);
                                console.log('BBBBBBBBBBB');
                                console.log(err);
                            } else {
                                funcionCallback(usuarios, count);
                            }
                            db.close();
                        });
                });
            }
        });
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
    getUsers: function (criteria, funcionCallback) {
        this.mongo.MongoClient.connect(this.app.get('db'), function (err, db) {
            if (err) {
                funcionCallback(null);
            } else {
                let collection = db.collection('users');
                collection.find(criteria).toArray(function (err, usuarios) {
                    if (err) {
                        funcionCallback(null);
                    } else {
                        funcionCallback(usuarios);
                    }
                    db.close();
                });
            }
        });
    }
};