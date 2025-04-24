export interface ProductCreationDTO {
    name: string;
    value: number;
    categoryId: number;
}
  
  export interface ProductResponse {
    id: number;
    name: string;
    description: string;
    value: number;
    category: {
      id: number;
      name: string;
    };
  }
