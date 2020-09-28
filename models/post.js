'use strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const schema = new Schema({
    userId: {type: Schema.Types.ObjectId, ref: 'User', required: true},
    categoryId: {type: Schema.Types.ObjectId, ref: 'Category'},
    product_imageList: [{type: String}],
    product_imagePath: {type: String},
    product_name: {type: String},
    product_description: {type: String},
    product_price: {type: Number},
    contact:{
        phone: {type: Number},
        email: {
            type: String,
            match: /[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/
        }
    },
    likes:{type: Number},
    comments: {type: Number},
    address: {type: String},
    timestamp: {type: Number, required:true},
},{versionKey: false});

module.exports = mongoose.model('Post', schema);