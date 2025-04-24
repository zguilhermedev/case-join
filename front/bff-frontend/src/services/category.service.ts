import { ResponseDTO } from '@/types/response';
import { api } from './api';
import { 
  CategoryCreationDTO, 
  CategoryDTO, 
  CategoryResponse 
} from "@/types/category";

export const categoryService = {
  /**
   * Cria uma nova categoria
   * @param dto Dados da categoria a ser criada
   * @returns Promise com a resposta do servidor
   */
  async create(dto: CategoryCreationDTO): Promise<ResponseDTO> {
    return api.post<ResponseDTO>('/category', dto);
  },

  /**
   * Atualiza uma categoria existente
   * @param dto Dados atualizados da categoria (deve incluir o ID)
   * @returns Promise com a resposta do servidor
   */
  async update(id: number, dto: CategoryDTO): Promise<ResponseDTO> {
    return api.put<ResponseDTO>(`/category/${id}`, dto);
  },

  /**
   * Remove uma categoria
   * @param id ID da categoria a ser removida
   * @returns Promise vazia em caso de sucesso
   */
  async delete(id: number): Promise<void> {
    await api.delete(`/category/${id}`);
  },

  /**
   * Busca uma categoria pelo ID
   * @param id ID da categoria
   * @returns Promise com os dados da categoria
   */
  async getById(id: number): Promise<CategoryDTO> {
    return api.get<CategoryDTO>(`/category/${id}`);
  },

  /**
   * Lista todas as categorias com paginação e filtros
   * @param params Parâmetros de busca (opcionais)
   * @returns Promise com a lista paginada de categorias
   */
  async getAll(params: {
    name?: string;
    page?: number;
    size?: number;
    sort?: string[];
  } = {}): Promise<CategoryResponse> {
    const queryParams = new URLSearchParams();
    
    if (params.name) queryParams.append('name', params.name);
    if (params.page !== undefined) queryParams.append('page', params.page.toString());
    if (params.size !== undefined) queryParams.append('size', params.size.toString());
    if (params.sort) params.sort.forEach(s => queryParams.append('sort', s));
    
    return api.get<CategoryResponse>(`/category?${queryParams.toString()}`);
  }
};