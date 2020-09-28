const User = require("../models/user");
const Post = require("../models/post");
const React = require("../models/react");
const jwt = require("jsonwebtoken");
const fs = require("fs");
const sharp = require("sharp");

module.exports = {
  reacts: {
    likes: async (req, res) => {
      let postId = req.body.postId;
      let userId = req.body.userId;
      let query = { postId: postId };
      let like = {
        userId: userId,
      };
      let numberLike = 0;
      await React.findOne(query, (err, react) => {
        if (err) {
          return res.json({
            error: err,
          });
        }
        if (react) {
          let exists = false;
          numberLike = react.likes.length;
          for (var i = 0; i < numberLike; i++) {
            if (userId == react.likes[i].userId) {
              exists = true;
              break;
            }
          }
          if (exists == true) {
            React.updateOne(
              query,
              {
                $pull: { likes: like },
              },
              (err) => {
                if (err) console.error(err);
                console.log("Successfully dislike");
              }
            );
            numberLike -= 1;
          } else if (exists == false) {
            React.updateOne(
              query,
              {
                $push: { likes: like },
              },
              (err) => {
                if (err) console.error(err);
                console.log("Successfully like");
              }
            );
            numberLike += 1;
          }
        }
        if (!react) {
          numberLike = 1;
          var react = new React();
          react.postId = postId;
          react.likes.push(like);
          react.save();
        }
        Post.updateOne(
          { _id: postId },
          {
            $set: { likes: numberLike },
          },
          (err) => {
            if (err) return console.error(err);
            return res.status(200).json("Successfully reaction");
          }
        );
      });
    },
    comments: async (req, res) => {
      let postId = req.body.postId;
      let query = { postId: postId };
      let cmts = {
        userId: req.body.userId,
        content: req.body.content,
      };
      let numberCmt = 0;
      await React.findOne(query, (err, react) => {
        if (err) {
          return res.json({
            error: err,
          });
        }
        if (react) {
          numberCmt = react.comments.length;
          React.updateOne(
            query,
            {
              $push: { comments: cmts },
            },
            (err) => {
              if (err) console.error(err);
              console.log("Successfully comment");
            }
          );
          numberCmt += 1;
        }
        if (!react) {
          numberCmt = 1;
          var react = new React();
          react.postId = postId;
          react.comments.push(cmts);
          react.save();
        }
        Post.updateOne(
          { _id: postId },
          {
            $set: { comments: numberCmt },
          },
          (err) => {
            if (err) return console.error(err);
            return res.status(200).json("Successfully reaction");
          }
        );
      });
    },
    removeComment: async (req, res) => {
      let postId = req.params.postId;
      let cmtId = req.params.cmtId;
      let query = { postId: postId };
      let cmt = {
        _id: cmtId,
      };
      await React.updateOne(
        query,
        {
          $pull: { comments: cmt },
        },
        (err) => {
          if (err) return console.error(err);
          return res.status(200).json("Successfully removed a Comment");
        }
      );
    },
  },
  
  post: {
    create: async (req, res) => {
      const files = req.files;
      console.log(files);
      let userId = req.body.userId;
      let categoryId = req.body.categoryId;
      let product_name = req.body.product_name;
      let product_price = req.body.product_price;
      let product_description = req.body.product_description;
      let product_imageList = files.map((file) => {
        let filepath =
          "uploads/" +
          Date.now() +
          "-250x250-vku-" +
          file.originalname.replace(/\s+/g, "");
        sharp(file.path)
          .resize(250, 250)
          .toFile(filepath, (err) => {
            if (err) console.error(err);
            if (fs.existsSync(file.path)) {
              fs.unlinkSync(file.path);
            }
          });
        return filepath;
      });
      let product_imagePath = product_imageList[0];
      let address = req.body.address;
      let email = req.body.email;
      let phone = req.body.phone;
      var newPost = await new Post();
      newPost.userId = userId;
      newPost.categoryId = categoryId;
      newPost.product_imageList = product_imageList;
      newPost.product_imagePath = product_imagePath;
      newPost.product_name = product_name;
      newPost.product_description = product_description;
      newPost.product_price = product_price;
      newPost.address = address;
      newPost.contact.phone = phone;
      newPost.contact.email = email;
      newPost.timestamp = Date.now();
      newPost.save((err, result) => {
        if (err) return console.error(err);
        return res.status(200).json(result);
      });
    },
    update: async (req, res) => {
      let postId = req.params.postId;
      let categoryId = req.body.categoryId;
      let product_name = req.body.product_name;
      let product_price = req.body.product_price;
      let product_description = req.body.product_description;
      let address = req.body.address;
      let email = req.body.email;
      let phone = req.body.phone;
      Post.updateOne(
        { _id: postId },
        {
            contact: {
              phone: phone,
              email: email,
            },
            categoryId: categoryId,
            product_name: product_name,
            product_description: product_description,
            product_price: product_price,
            address: address,
            timestamp: Date.now()
        },
        (err) => {
          if (err) return console.error(err);
          return res.send('Post is updated');
        }
      );
    },
    delete: async (req, res) => {
      let postId = req.params.postId;
      let images = [];
      await Post.findOne({ _id: postId }, (err, docs) => {
        images = docs.product_imageList;
        images.forEach((element) => {
          if (fs.existsSync(element)) {
            fs.unlinkSync(element);
          }
        });
      });
      await Post.deleteOne({ _id: postId });
      await React.deleteOne({ postId: postId }, (err) => {
        if (err) console.log(err);
        return res.status(200).json("Successful deletion");
      });
    },
  },
  default: (req, res) => {
    res.status(200).json(req.userData);
  },

  getUserPosts: (req, res) => {
    Post.find({ userId: [req.params.userId] }, (err, posts) => {
      if (err) return console.error(err);
      return res.json(posts);
    });
  },
  profile: (req, res) => {
    User.findOne({ _id: [req.params.userId] }, (err, user) => {
      if (err) return console.error(err);
      return res.json(user);
    });
  },

  signin: (req, res) => {
    let email = req.body.email;
    let password = req.body.password;
    User.findOne({ email: email }, (err, user) => {
      if (err) {
        return res.json({
          message: "Auth failed",
        });
      }
      if (!user) {
        return res.json({
          message: "No user found.",
        });
      }
      if (!user.validPassword(password)) {
        return res.json({
          accessToken: null,
          message: "Wrong password.",
        });
      }

      let token = jwt.sign(
        {
          _id: user._id,
        },
        process.env.KEY_ACCESS_TOKEN_SECRET,
        {
          expiresIn: "1d",
        }
      );
      return res.json({
        message: "Auth successful",
        user: user,
        token: token,
      });
    });
  },
  signup: (req, res) => {
    let username = req.body.username;
    let phone = req.body.phone;
    let email = req.body.email;
    let password = req.body.password;
    let gender = req.body.gender;
    User.findOne({ email: email }, (err, user) => {
      if (err) {
        return res.json({
          error: err,
        });
      }
      if (user) {
        return res.json({
          message: "Email is already in use.",
        });
      }
      var newUser = new User();
      newUser.username = username;
      newUser.phone = phone;
      newUser.gender = gender;
      newUser.email = email;
      newUser.password = newUser.encryptPassword(password);
      newUser.save((err, result) => {
        if (err) {
          return res.json({
            message: "Sign Up Failed",
          });
        }
        let token = jwt.sign(
          {
            _id: result._id,
          },
          process.env.KEY_ACCESS_TOKEN_SECRET,
          {
            expiresIn: "1d",
          }
        );
        return res.json({
          message: "Sign Up Success",
          user: result,
          token: token,
        });
      });
    });
  },
  logout: (req, res) => {},
};
