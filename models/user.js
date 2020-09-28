'use strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcrypt-nodejs');

const schema = new Schema({
    username: {type: String, required: true},
    address: {type: String},
    phone: {type: Number, required: true, unique: true},
    gender: {type: Boolean, required: true},
    email: {
      type: String,
      required: true,
      unique: true,
      match: /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/
    },
    password: {type: String, required: true}
},{versionKey: false});

schema.methods.encryptPassword = function(password) {
  return bcrypt.hashSync(password, bcrypt.genSaltSync(5), null);  
};

schema.methods.validPassword = function(password) {
  return bcrypt.compareSync(password, this.password);  
};

module.exports = mongoose.model('User', schema);

