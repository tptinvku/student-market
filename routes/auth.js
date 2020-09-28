const express = require('express')
const router = express.Router();
const userController = require('../controllers/userController');
const upload = require('../middleware/upload');

router.get('/signup', (req, res)=>{
    res.json(req.body);
});
router.post('/signin', userController.signin);
router.post('/signup', upload.single("avt"), userController.signup);

module.exports = router;