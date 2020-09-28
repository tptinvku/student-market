const React = require("../models/react");
const Post = require("../models/post");
const Conversation = require("../models/conversation");
const Notification = require("../models/notification");
module.exports.socketIo = (io) => {
  io.on("connection", async (socket) => {
    console.log("a user connected:" + socket.id);
    socket.on("client-create-room", (action) => {
      console.log(action);
      let cvstId;
      let data = JSON.parse(action);
      let customerId = data.userId;
      let sellerId = data.sellerId;
      let members = {
        user_st: customerId,
        user_nd: sellerId,
      };
      Conversation.find({}, (err, result) => {
        if (err) console.error(err);
        if (result) {
          let exists = false;
          for (var i = 0; i < result.length; i++) {
            // console.log(result[i].members.sellerId);
            if (sellerId == result[i].members.user_st || customerId == result[i].members.user_st ) {
              if (sellerId == result[i].members.user_nd || customerId == result[i].members.user_nd) {
                cvstId = result[i]._id;
                exists = true;
                break;
              }
            }
          }
          if (exists == true) {
            console.log("room: " + cvstId);
            io.emit("server-send-cvstId", { _id: cvstId });
          } else if (exists == false) {
            var cvst = new Conversation();
            cvst.members = members;
            cvst.save((err, docs) => {
              if (err) console.error(err);
              cvstId = docs._id;
              console.log("room: " + cvstId);
            });
            io.emit("server-send-cvstId", { _id: cvstId });
          }
        }
      });
      socket.join(cvstId);
    });

    socket.on("client-send-message", (action) => {
      let data = JSON.parse(action);
      let content = {
        userId: data.senderId,
        message: data.message,
        timestamp: Date.now()
      }
      Conversation.find({})
      console.log(action);
      socket.broadcast.to(data.cvstId).emit("server-send-message", { message: data.message});
    });

    socket.on("like-post", (action) => {
      let data = JSON.parse(action);
      console.log(data);
      let postId = data.postId;
      let userId = data.userId;
      let query = { postId: postId };
      let like = {
        userId: userId,
      };
      let numberLike = 0;
      React.findOne(query, (err, react) => {
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
                console.log(userId + " dislike ");
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
                console.log(userId + " like");
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
            if (err) console.error(err);
          }
        );
        io.emit("emit-numLike", { postId: postId, num: numberLike });
        numberLike = 0;
      });
    });

    socket.on("notification", (action) => {
      console.log(action);
      let notify = JSON.parse(action);
      let postId = notify.contents[0].postId;
      let userId = notify.contents[0].senderId;
      let sellerId = notify.sellerId;
      let content = notify.contents[0].content;
      let item = {
        postId: postId,
        senderId: userId,
        content: content,
        timeStamp: Date.now(),
      };
      let query = { userId: sellerId };
      Notification.findOne(query, (err, docs) => {
        if (err) console.error(err);
        if (docs) {
          let exists = false;
          let numContent = docs.contents.length;
          for (var i = 0; i < numContent; i++) {
            if (postId == docs.contents[i].postId) {
              if (userId == docs.contents[i].senderId) {
                exists = true;
                break;
              }
              exists = false;
            }
          }
          if (exists == true) {
            Notification.updateOne(
              query,
              {
                $pull: {
                  contents: {
                    postId: postId,
                    senderId: userId,
                  },
                },
              },
              (err) => {
                if (err) console.error(err);
                console.log(userId + " dislike ");
              }
            );
          } else if (exists == false) {
            Notification.updateOne(
              query,
              {
                $push: { contents: item },
              },
              (err) => {
                if (err) console.error(err);
                console.log(userId + " like ");
                socket.broadcast.emit("emit-notify", {
                  userId: docs.userId,
                  content: content,
                });
              }
            );
          }
        }
        if (!docs) {
          let notification = new Notification();
          notification.userId = sellerId;
          notification.contents.push(item);
          notification.save((err, result) => {
            if (err) console.error(err);
            socket.broadcast.emit("emit-notify", {
              userId: userId,
              content: content,
            });
          });
        }
      });
    });

    socket.on("search", (action) => {
      let data = JSON.parse(action);
      let regex = data.keyword;
      let userId = data.userId;
      console.log(action)
      // action: postname || username
      Post.find(
        { product_name: { $regex: regex, $options: "m" } },
        (err, result) => {
          if (err) console.error(err);
          io.emit("emit-search", {userId: userId, result: result});
          console.log(result);
        }
      );
    });
    socket.on("disconnect", () => {
      console.log(socket.id + " a user disconnected:");
    });
  });
};
