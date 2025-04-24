import { ApiError } from '@/types/error';
import { api } from './api';
import { 
  OrderDTO,
  OrderCreationDTO,
  ResponseDTO,
} from "@/types/order";

const BASE_URL = "/order";

export const orderService = {
  /**
   * Cria um novo pedido
   * @param dto Dados do pedido a ser criado
   * @returns Promise com a resposta do servidor
   */
  async create(dto: OrderCreationDTO): Promise<ResponseDTO> {
    try {

      const productsMap = Object.fromEntries(
        dto.products.map(item => [item.productId, item.quantity])
      );

      const backendFormat = {
        ...dto,
        products: productsMap
      };

      return await api.post(BASE_URL, backendFormat);
    } catch (error: unknown) {
      const apiError: ApiError = {
        message: 'Erro ao criar pedido'
      };  
      throw apiError;
    }
  },

  /**
   * Atualiza um pedido existente
   * @param id ID do pedido a ser atualizado
   * @param dto Dados atualizados do pedido
   * @returns Promise com a resposta do servidor
   */
  async update(id: number, dto: OrderCreationDTO): Promise<ResponseDTO> {
    const productsMap = Object.fromEntries(
      dto.products.map(item => [item.productId, item.quantity])
    );

    const backendFormat = {
      ...dto,
      products: productsMap
    };

    return api.put<ResponseDTO>(`${BASE_URL}/${id}`, backendFormat);
  },

  /**
   * Remove um pedido
   * @param id ID do pedido a ser removido
   * @returns Promise vazia em caso de sucesso
   */
  async delete(id: number): Promise<void> {
    await api.delete(`${BASE_URL}/${id}`);
  },

  /**
   * Busca um pedido pelo ID com seus produtos
   * @param id ID do pedido
   * @returns Promise com os dados do pedido e produtos
   */
  async getById(id: number): Promise<OrderDTO> {
    return api.get<OrderDTO>(`${BASE_URL}/${id}`);
  },

  /**
   * Lista todos os pedidos com paginação e filtros
   * @param params Parâmetros de busca (opcionais)
   * @returns Promise com a lista paginada de pedidos
   */
  async getAll(params: {
    userId?: number;
    page?: number;
    size?: number;
  } = {}): Promise<{
    content: OrderDTO[];
    totalElements: number;
    totalPages: number;
  }> {
    const queryParams = new URLSearchParams();
    
    if (params.userId) queryParams.append('userId', params.userId.toString());
    if (params.page !== undefined) queryParams.append('page', params.page.toString());
    if (params.size !== undefined) queryParams.append('size', params.size.toString());
    
    return api.get(`${BASE_URL}?${queryParams.toString()}`);
  }
};