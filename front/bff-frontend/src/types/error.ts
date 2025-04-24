// src/types/error.ts
export interface ApiError {
    message: string;
    status?: number;
    data?: any;
}