'user strict'

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const schema = new Schema({
    userId: {type: Schema.Types.ObjectId, ref: 'User'},
    contents: [
        {   
            postId: {type: Schema.Types.ObjectId, ref: 'Post'},
            senderId: {type: Schema.Types.ObjectId, ref: 'User'},
            content: {type: String},
            timeStamp: {type: Number}
        }
    ]
}, {versionKey: false});

module.exports = mongoose.model('Notification', schema);