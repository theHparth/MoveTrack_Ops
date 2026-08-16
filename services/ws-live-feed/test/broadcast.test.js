const test = require("node:test");
const assert = require("node:assert");

test("broadcast only sends to open clients", () => {
  // build a fake client object with readyState/send, assert send() was called
  // exactly for OPEN clients and never for closed ones
});
