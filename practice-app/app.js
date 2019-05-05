const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser')
const session = require('express-session');

var app = express();
app.use(cors()); // for all routes

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ extended: false }))

// parse application/json
app.use(bodyParser.json())

var port = process.env.PORT || 8080;

app.use(express.static(__dirname + "./front-end"));
app.use('/login',require('./back-end/routes/login'));
app.use('/signup',require('./back-end/routes/signup'));
app.use(session({ secret: '23jfDer94b3re6ibk3d1mw8ac', resave: false, saveUninitialized: false }));

app.listen(port, () => console.log(`Example app listening on port ${port}!`));