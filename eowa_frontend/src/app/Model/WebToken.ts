import { User } from "./User";

export interface WebToken {
    user: User;
    jsessionid: string;
    timestamp: number;
}