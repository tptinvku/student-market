'use strict'

const mongoose = require('mongoose')
const Schema = mongoose.Schema;

const schema = new Schema({
    postId: {type: Schema.Types.ObjectId, ref: 'Post'},
    likes:[
        {
            userId: {type: Schema.Types.ObjectId, ref: 'User'}
        }
    ],
    comments: [
        {
            userId: {type: Schema.Types.ObjectId, ref: 'User'},
            content: {type: String}
        }
    ],
}, {versionKey: false});


module.exports = mongoose.model('React', schema);