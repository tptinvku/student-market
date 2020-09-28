'use strict';

const category = require('../models/category');

module.exports = {
    get: (req, res)=>{
        category.find({},(err, result)=>{
            if(err) return console.error(err);
            res.json(result);
        })
    },
    getByName: (req, res)=>{
        category.find({'category_name': [req.params.category_name]}, (err, result)=>{
            if(err) return console.error(err);
            res.json(result);
        })
    }
}