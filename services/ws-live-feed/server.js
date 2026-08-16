const { WebSocketServer } = require("ws");

const wss = new WebSocketServer({ port: 8095 });
const clients = new Set();

wss.on("connection", (socket) => {
  clients.add(socket);
  socket.on("close", () => clients.delete(socket));
});

function broadcast(message) {
  const payload = JSON.stringify(message);
  for (const client of clients) {
    if (client.readyState === client.OPEN) {
      client.send(payload);
    }
  }
}

module.exports = { broadcast };
