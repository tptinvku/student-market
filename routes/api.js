const express = require('express')
const router = express.Router();
const categoryController = require('../controllers/categoryController');
const postController = require('../controllers/postController');

router.get('/categories', categoryController.get);
router.get('/get-category/:category_name', categoryController.getByName);
router.get('/post/:postId', postController.getByPostId);
router.get('/posts/new', postController.new);
router.get('/posts', postController.get);
router.get('/posts/:categoryId', postController.getByCategoryId);
router.get('/post/react/likes/:postId', postController.getReacts);
router.get('/search/posts/:postName', postController.getPostsByName)
module.exports = router;