import axios from 'axios';

// API配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 交易相关类型
export interface Transaction {
  id: number;
  transactionId: string;
  amount: number;
  transactionType: number;
  accountNumber: string;
  counterpartyAccount?: string;
  description?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TransactionRequest {
  transactionId?: string;
  amount: number;
  transactionType: number;
  accountNumber: string;
  counterpartyAccount?: string;
  description?: string;
}

export interface TransactionDeleteRequest {
  transactionId: string;
}

export interface PageRequest {
  page: number;
  size: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

// 更新ApiResponse接口，添加code字段
export interface ApiResponse<T> {
  success: boolean;
  code?: number;  // 添加错误码字段
  data: T;
  message: string;
  timestamp: string;
}

// 添加错误处理函数
const handleApiResponse = <T>(apiResponse: ApiResponse<T>): T => {
  if (!apiResponse.success) {
    throw new Error(apiResponse.message || 'API request failed');
  }
  return apiResponse.data;
};

// 交易API
export const transactionApi = {
  // 分页查询
  getTransactions: async (params: PageRequest): Promise<PageResponse<Transaction>> => {
    const response = await apiClient.get('/transactions', { params });
    const apiResponse: ApiResponse<PageResponse<Transaction>> = response.data;
    return handleApiResponse(apiResponse);
  },

  // 根据ID查询
  getTransactionById: async (transactionId: string): Promise<Transaction> => {
    const response = await apiClient.get(`/transactions/${transactionId}`);
    const apiResponse: ApiResponse<Transaction> = response.data;
    return handleApiResponse(apiResponse);
  },

  // 新建交易
  createTransaction: async (request: TransactionRequest): Promise<Transaction> => {
    const response = await apiClient.post('/transactions/create', request);
    const apiResponse: ApiResponse<Transaction> = response.data;
    return handleApiResponse(apiResponse);
  },

  // 更新交易
  updateTransaction: async (request: TransactionRequest): Promise<Transaction> => {
    const response = await apiClient.post('/transactions/update', request);
    const apiResponse: ApiResponse<Transaction> = response.data;
    return handleApiResponse(apiResponse);
  },

  // 删除交易
  deleteTransaction: async (request: TransactionDeleteRequest): Promise<{success: boolean; message: string}> => {
    const response = await apiClient.post('/transactions/delete', request);
    const apiResponse: ApiResponse<boolean> = response.data;
    
    if (!apiResponse.success) {
      throw new Error(apiResponse.message || 'Delete failed');
    }
    
    return {
      success: apiResponse.success && apiResponse.data,
      message: apiResponse.message
    };
  },
};

export default transactionApi;