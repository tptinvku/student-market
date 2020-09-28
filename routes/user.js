const express = require('express')
const router = express.Router();
const upload = require('../middleware/upload');
const userController = require('../controllers/userController');
const postController = require('../controllers/postController');
router.get('/', userController.default);

router.get('/profile/:userId', userController.profile);

router.get('/cart');

router.get('/notifications/:userId', postController.getNotifications);

router.post('/create/post', upload.array('productImages', 12), userController.post.create);

router.get('/posts/:userId', userController.getUserPosts);

router.put('/react/like', userController.reacts.likes);

router.put('/react/comment', userController.reacts.comments);

router.put('/update/post/:postId', userController.post.update);

router.delete('/delete/post/:postId', userController.post.delete);

router.delete('/delete/cmtId/:cmtId/postId/:postId', userController.reacts.removeComment);

module.exports = router;
