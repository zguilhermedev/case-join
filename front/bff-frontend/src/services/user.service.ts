import { api } from './api';
import { ResponseDTO } from '@/types/response';

interface UserCreationDTO {
  username: string;
  password: string;
  role: 'USER';
}

export const userService = {
    async create(dto: UserCreationDTO): Promise<ResponseDTO> {
    return api.post<ResponseDTO>('/user/register', dto);
    },
}