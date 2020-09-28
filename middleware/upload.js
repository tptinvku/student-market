const multer = require('multer');
const fs = require('fs');
const storage = multer.diskStorage({
  destination: (req, file, cb)=>{
    const dir = 'uploads';
    if(!fs.existsSync(dir)){
      fs.mkdirSync(dir);
    }
    cb(null, dir);
  },
  filename: (req, file, cb)=>{
    // console.log(file);
    cb(null, Date.now() +'-vku-'+ file.originalname.replace(/\s+/g, ''));
  }
});

const fileFilter = (req, file, cb)=>{
  // reject a file
  if (file.mimetype === 'image/jpg' || file.mimetype === 'image/jpeg' || file.mimetype === 'image/png') {
    cb(null, true);
  } else {
    cb(null, false);
  }
}

const upload = multer({
  storage: storage,
  limits: {
    fileSize: 1024 * 1024 * 5
  },
  fileFilter: fileFilter
});


module.exports = upload;