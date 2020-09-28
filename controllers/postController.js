'use strict'

const Post = require('../models/post');
const React = require('../models/react');
const Notification = require('../models/notification');
const Converstion = require('../models/conversation');
const conversation = require('../models/conversation');
module.exports = {
    get: async (req, res) => {
        await Post.find({}, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result);
        })
    },

    new: async (req, res) => {
        await Post.find({}, (err, result) => {
            if (err) return console.error(err);
            if (result) {
                return res.status(200).json(result);
            }
        }).sort({ _id: -1 }).limit(4);
    },
    getByPostId: async(req, res)=>{
        await Post.find({ '_id': [req.params.postId] }, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result);
        })
    },
    getByCategoryId: async (req, res) => {
        await Post.find({ 'categoryId': [req.params.categoryId] }, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result);
        })
    },
    getReacts: async (req, res) => {
        await React.find({ 'postId': [req.params.postId] }, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result);
        })
    },
    getPostsByName: async (req, res) => {
        let key_word = req.params.postName;
        await Post.find({ product_name: { $regex: key_word, $options: 'm' } }, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result);
        })
    },
    getNotifications: async (req, res) => {
        await Notification.find({ userId: [req.params.userId] }, (err, result) => {
            if (err) return console.error(err);
            return res.status(200).json(result)
        });
    },
    getListConversation: async (req, res) =>{
        await conversation.find({}, (err, result)=>{
            if(err) return console.error(result);
            if(result){
                
            }
        })
    }
}