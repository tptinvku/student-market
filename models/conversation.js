"user strict";

const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const schema = new Schema({
  members: {
    user_st: { type: Schema.Types.ObjectId, ref: "User" },
    user_nd: { type: Schema.Types.ObjectId, ref: "User" },
  },
  contents: [
    {
      userId: { type: Schema.Types.ObjectId, ref: "User" },
      message: { type: String },
      timestamp: { type: Number },
    },
  ],
}, {versionKey: false});

module.exports = mongoose.model("Conversation", schema);
