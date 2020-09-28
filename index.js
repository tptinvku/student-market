"use strict";

require('dotenv').config();
const express = require('express');
const app = express();
const server = require('http').createServer(app);
const port = process.env.PORT || 3000;
const logger = require('morgan');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const session = require('express-session');
const flash = require('connect-flash');
const assert = require('assert');
const apiRoutes = require('./routes/api');
const authRoute = require('./routes/auth');
const userRoutes = require('./routes/user');
const cookieParser = require('cookie-parser');
const MongoStore = require('connect-mongo')(session);
const configDB = require('./config/database');
const checkAuth = require('./middleware/check-auth');
const io = require('socket.io')(server)
const uri = configDB.database;

require('./io/socket.io').socketIo(io)

mongoose.connect(uri, { useCreateIndex: true, useNewUrlParser: true, useUnifiedTopology: true }, (err) => {
    assert.equal(null, err)
    console.log('connected')
})

app.use(logger('dev'));
app.use('/uploads', express.static('uploads'));
app.use(express.static('public'));
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());


app.use(cookieParser());
app.use(session({
    resave: false,
    saveUninitialized: false,
    secret: 'sict',
    cookie: { maxAge: 180 * 60 * 1000 },
    store: new MongoStore({ mongooseConnection: mongoose.connection }),
}));

app.use('/api', apiRoutes);
app.use('/user', checkAuth, userRoutes);
app.use('/auth', authRoute);
app.use((req, res) => {
    res.status(404).send({ url: req.originalUrl + ' not found' });
});




server.listen(port, () => {
    console.log('RESTful API server started *:', port);
});



