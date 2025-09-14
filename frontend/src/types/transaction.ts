// 交易类型定义

// 交易类型枚举
export enum TransactionType {
  UNKNOWN = 0,
  EXPENSE = 1,  // 支出
  INCOME = 2    // 收入
}

export interface Transaction {
  id: number
  transactionId: string
  amount: number
  transactionType: number
  accountNumber: string
  counterpartyAccount?: string
  description?: string
  createdAt: string
  updatedAt: string
}

export interface TransactionRequest {
  transactionId?: string
  amount: number
  transactionType: number
  accountNumber: string
  counterpartyAccount?: string
  description?: string
}

export interface TransactionDeleteRequest {
  transactionId: string
}

export interface PageRequest {
  page: number
  size: number
  sortBy?: string
  sortDirection?: 'ASC' | 'DESC'
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

// 更新ApiResponse接口
export interface ApiResponse<T> {
  success: boolean
  code?: number  // 添加错误码字段
  message: string
  data: T
  timestamp: string
}

// 工具函数
export const getTransactionTypeName = (type: number): string => {
  switch (type) {
    case 1:
      return 'Expense'
    case 2:
      return 'Income'
    default:
      return 'Unknown'
  }
}

export const getTransactionTypeColor = (type: number): string => {
  switch (type) {
    case 1:
      return 'red'     // 支出用红色
    case 2:
      return 'green'   // 收入用绿色
    default:
      return 'default'
  }
}

// 金额格式化
export const formatAmount = (amount: number): string => {
  return (amount / 100).toFixed(2)
}

// 金额转换
export const parseAmount = (amount: number | string): number => {
  const numAmount = typeof amount === 'string' ? parseFloat(amount) : amount
  return Math.round(numAmount * 100)
}