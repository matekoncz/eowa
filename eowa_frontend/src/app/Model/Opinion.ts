import { User } from './User';

export interface Opinion {
  number?: number;
  id?: number;
  user: User;
  userOpinion: UserOpinion;
}

export enum UserOpinion {
  GOOD = 'GOOD',
  BAD = 'BAD',
  TOLERABLE = 'TOLERABLE',
}

export const UserOpinionLookup = {
  GOOD: UserOpinion.GOOD,
  BAD: UserOpinion.BAD,
  TOLERABLE: UserOpinion.TOLERABLE,
};
