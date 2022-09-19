// Imports
var mongodb = require('mongodb');
var ID = mongodb.ObjectId;
var crypto = require('crypto');
var express = require('express');
var bodyParser = require('body-parser');

// Use this function to generate salt to be used further.
var generateRandomString = function(length) {
    return crypto.randomBytes(Math.ceil(length/2)).toString('hex').slice(0,length);
}

// Return an encoded password data
var sha512 = function(password, salt) {
    var hash = crypto.createHmac('sha512', salt);
    hash.update(password);
    var value  = hash.digest('hex');
    return {
        salt: salt,
        passwordHash: value
    };
}

function saltHashPassword(userPassword) {
    var salt = generateRandomString(16);
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

function checkHashPassword(userPassword, salt) {
    var passwordData = sha512(userPassword, salt);
    return passwordData;
}

// API services
var api = express();
api.use(bodyParser.json());
api.use(bodyParser.urlencoded({extended: true}));

// create database client
var Monclient = mongodb.MongoClient;

// baseURL
var url = 'mongodb://localhost:27017';
Monclient.connect(url, function(err, client) {
    if (err) {
        console.log('Failed to connect to mongoDB Server.', err);
    } else {

        // register endpoints we need.
        api.post('/register', (request, response) => {
            var data_to_post = request.body;

            var original_password = data_to_post.password;
            var hash_data = saltHashPassword(original_password);
            var password = hash_data.passwordHash;
            var salt = hash_data.salt;
            var name = data_to_post.name;
            var email = data_to_post.email;

            var json = {
                'name': name,
                'email': email,
                'password': password,
                'salt': salt
            };

            var db = client.db('LoginApp');

            db.collection('user').find({'email': email}).count(function(err, count){
                if (count != 0) {
                    response.json('Email already exists.');
                    console.log('Email already exists.');
                } else {
                    db.collection('user').insertOne(json, function(err, res) {
                        response.json('Success.');
                        console.log('Success.');
                    })
                }
            })
        })

        api.post('/login', (request, response) => {
            var data_to_post = request.body;
            var userPassword = data_to_post.password;
            var email = data_to_post.email;

            var db = client.db('LoginApp');

            db.collection('user').find({'email': email}).count(function(err, count){
                if (count == null) {
                    response.json('Email does not exist.');
                    console.log('Email does not exist.');
                } else {
                    db.collection('user').findOne({'email': email}, function(err, user) {
                        var salt = user.salt;
                        var hashed_password = checkHashPassword(userPassword, salt).passwordHash; // input password + db salt -> new hash.
                        var encrypted_password = user.password; // old db hash.
                        if (hashed_password == encrypted_password) {
                            response.json('Login success.');
                    console.log('Login success.');
                        } else {
                            response.json('Wrong password.');
                    console.log('Wrong password.');
                        }
                    })
                }
            })
        })


        api.listen(3000, () => {
            console.log('Connected successfully. Running services on port 3000.');
        })
    }
})
