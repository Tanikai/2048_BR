import { Namespace } from "socket.io";
import { DefaultEventsMap } from "socket.io/dist/typed-events";

export type Game = {
  [gameId: string]: {
    [userId: string]: {
      username: string;
      score: number;
      alive: boolean;
      position?: number;
    };
  };
};

export type Score = {
  userId: string;
  username: string;
  score: number;
  alive: boolean;
  position?: number;
};

export type Lobby = {
  [gameId: string]: {
    owner: string;
    currentUsers: number;
    maxUsers: number;
    running: boolean;
    round: number;
    roundDurations: Array<number>;
    duration: number;
  };
};

export type SocketNamespace = Namespace<
  DefaultEventsMap,
  DefaultEventsMap,
  DefaultEventsMap,
  any
>;
