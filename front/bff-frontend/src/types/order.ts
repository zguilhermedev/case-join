export interface CategoryDTO {
    id: number;
    name: string;
}
  
export interface ProductResponse {
    id: number;
    name: string;
    category: CategoryDTO;
    value: number; // BigDecimal convertido para number
}
  
export interface ProductWithQuantityDTO {
    product: ProductResponse;
    quantity: number;
}
  
export interface OrderDTO {
    id: number;
    userId: number;
    amount: number; // BigDecimal convertido para number
    products: ProductWithQuantityDTO[];
}
  
export interface OrderCreationDTO {
    userId: number;
    amount: number;
    products: Array<{
        productId: number;
        quantity: number;
    }>;
}
  
export interface ResponseDTO {
    code: string;
    message: string;
    data?: any;
}