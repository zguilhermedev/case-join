import { api } from './api';
import { ProductCreationDTO, ProductResponse } from "@/types/product";
import { ResponseDTO } from '@/types/response';

export const productService = {
  async create(dto: ProductCreationDTO): Promise<ResponseDTO> {
    return api.post<ResponseDTO>('/product', dto);
  },

  async update(id: number, dto: ProductCreationDTO): Promise<ResponseDTO> {
    return api.put<ResponseDTO>(`/product/${id}`, dto);
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/product/${id}`);
  },

  async getById(id: number): Promise<ProductResponse> {
    return api.get<ProductResponse>(`/product/${id}`);
  },

  async getAll(params: {
    category?: string;
    page?: number;
    size?: number;
    sort?: string[];
  } = {}): Promise<{
    content: ProductResponse[];
    totalElements: number;
    totalPages: number;
  }> {
    const queryParams = new URLSearchParams();
    
    if (params.category) queryParams.append('category', params.category);
    if (params.page !== undefined) queryParams.append('page', params.page.toString());
    if (params.size !== undefined) queryParams.append('size', params.size.toString());
    if (params.sort) params.sort.forEach(s => queryParams.append('sort', s));
    
    return api.get(`/product?${queryParams.toString()}`);
  }
};