'use strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const schema = new Schema({
    category_imagePath: {type: String, required: true},
    category_name: {type: String, required: true},
},{versionKey: false} );

module.exports = mongoose.model('Category', schema);