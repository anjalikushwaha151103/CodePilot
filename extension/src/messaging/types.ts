import { ProblemContext } from "../models/ProblemContext";

export enum MessageType {
  GET_PLATFORM = "GET_PLATFORM",
  GET_PROBLEM_CONTEXT = "GET_PROBLEM_CONTEXT",
  PROBLEM_CONTEXT_UPDATED = "PROBLEM_CONTEXT_UPDATED",
  PING = "PING",
  ERROR = "ERROR",
}

export interface BaseMessage {
  type: MessageType;
  payload?: any;
}

export interface GetPlatformMessage extends BaseMessage {
  type: MessageType.GET_PLATFORM;
}

export interface GetProblemContextMessage extends BaseMessage {
  type: MessageType.GET_PROBLEM_CONTEXT;
}

export interface ProblemContextUpdatedMessage extends BaseMessage {
  type: MessageType.PROBLEM_CONTEXT_UPDATED;
  payload: {
    context: ProblemContext | null;
  };
}

export interface PingMessage extends BaseMessage {
  type: MessageType.PING;
}

export interface ErrorMessage extends BaseMessage {
  type: MessageType.ERROR;
  payload: {
    message: string;
  };
}

export type ExtensionMessage =
  | GetPlatformMessage
  | GetProblemContextMessage
  | ProblemContextUpdatedMessage
  | PingMessage
  | ErrorMessage;
