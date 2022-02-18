import { Server } from "socket.io";

const io = new Server().listen(3000);
const games: Game = {};
const lobbys: Lobby = {};

io.on("connection", function (socket) {
  console.log(
    `[${socket.handshake.query.CustomId}] User connected from ${socket.request.socket.remoteAddress}`
  );

  socket.on("newGame", function (name) {
    if (games[name]) {
      console.log(`[${socket.handshake.query.CustomId}] Join Game ${name}`);
      socket.emit("newGame", {});
    } else {
      console.log(
        `[${socket.handshake.query.CustomId}] Created a new Game ${name}`
      );
      const username = socket.handshake.query.CustomId as string;
      newGame(name, username);
      socket.emit("newGame", {});
    }
  });

  socket.on("getLobbys", function () {
    socket.emit("getLobbys", getLobbys());
  });
});

function newGame(gameId: string, username: string) {
  lobbys[gameId] = {
    owner: username,
    currentUsers: 0,
    maxUsers: 48,
  };

  games[gameId] = {};
  const nsp = io.of("/game/" + gameId);
  nsp.on("connection", function (socket) {
    const username = socket.handshake.query.CustomId as string;
    console.log(
      `[${gameId} - ${username}] User connected from ${socket.request.socket.remoteAddress}`
    );
    addScore(gameId, username, 0);
    nsp.emit("score", getScore(gameId));

    socket.on("score", function (score) {
      addScore(gameId, username, score);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("won", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });

    socket.on("over", function (score) {
      addScore(gameId, username, score, false);
      nsp.emit("score", getScore(gameId));
    });
  });
}

function addScore(gameId, username, score, alive = true) {
  console.log(`[${gameId} - ${username}] ${alive ? "Alive" : "Dead"} ${score}`);

  if (!games[gameId]) {
    games[gameId] = {};
  }
  if (!games[gameId][username]) {
    games[gameId][username] = {
      score: 0,
      alive: true,
    };
  }

  games[gameId][username].score = score;
  games[gameId][username].alive = alive;
}

function getScore(gameId: string) {
  var score = Object.keys(games[gameId]).map((key) => {
    return {
      ...games[gameId][key],
      username: key,
    };
  });

  score.sort((a, b) => b.score - a.score);

  return score;
}

function getLobbys() {
  var lobbyList = Object.keys(lobbys).map((gameId) => {
    return {
      ...lobbys[gameId],
      currentUsers: Object.keys(lobbys[gameId]).length,
      id: gameId,
    };
  });

  lobbyList.sort((a, b) => b.currentUsers - a.currentUsers);

  return lobbyList;
}
