export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;      // current page index (0-based)
  size: number;        // page size
}
