export interface CategoryCreationDTO {
    name: string;
}
  
export interface CategoryDTO {
    id: number;
    name: string;
}
  
export interface CategoryResponse {
    content: CategoryDTO[];
    totalElements: number;
    totalPages: number;
}