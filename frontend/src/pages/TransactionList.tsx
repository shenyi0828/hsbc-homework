import React, { useState } from 'react'
import {
  Table,
  Card,
  Button,
  Space,
  Tag,
  Typography,
  Popconfirm,
  message,
  Alert,
} from 'antd'
import type { TablePaginationConfig } from 'antd'
import {
  EditOutlined,
  DeleteOutlined,
  SearchOutlined,
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import dayjs from 'dayjs'
import { transactionApi } from '../services/api'
import {
  Transaction,
  PageRequest,
  getTransactionTypeName,
  getTransactionTypeColor,
  formatAmount,
} from '../types/transaction'

const { Title } = Typography

const TransactionList: React.FC = () => {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [query, setQuery] = useState<PageRequest>({
    page: 0,
    size: 10,
  })

  // 查询交易列表
  const {
    data: response,
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['transactions', query],
    queryFn: () => transactionApi.getTransactions(query),
  })

  // 删除操作
  const deleteMutation = useMutation({
    mutationFn: transactionApi.deleteTransaction,
    onSuccess: () => {
      message.success('Transaction deleted successfully')
      queryClient.invalidateQueries({ queryKey: ['transactions'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard-statistics'] })
    },
    onError: () => {
      message.error('Failed to delete transaction')
    },
  })

  const transactions = response?.content || []
  const pagination = response || {
    totalElements: 0,
    totalPages: 0,
    size: 10,
    number: 0,
  }

  const handleSearch = () => {
    refetch()
  }

  const handleTableChange = (pagination: TablePaginationConfig) => {
    setQuery({
      ...query,
      page: (pagination.current || 1) - 1,
      size: pagination.pageSize || 10,
    })
  }

  const handleDelete = (transactionId: string) => {
    deleteMutation.mutate({ transactionId })
  }

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'Transaction ID',
      dataIndex: 'transactionId',
      key: 'transactionId',
      width: 120,
      ellipsis: true,
    },
    {
      title: 'Account Number',
      dataIndex: 'accountNumber',
      key: 'accountNumber',
      width: 150,
    },
    {
      title: 'Amount',
      dataIndex: 'amount',
      key: 'amount',
      width: 120,
      render: (amount: number) => (
        <span className="font-medium">${formatAmount(amount)}</span>
      ),
    },
    {
      title: 'Type',
      dataIndex: 'transactionType',
      key: 'transactionType',
      width: 100,
      render: (type: number) => (
        <Tag color={getTransactionTypeColor(type)}>
          {getTransactionTypeName(type)}
        </Tag>
      ),
    },

    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
      ellipsis: true,
    },
    {
      title: 'Created At',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: 'Actions',
      key: 'actions',
      width: 120,
      render: (_: unknown, record: Transaction) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/transactions/edit/${record.transactionId}`)}
          >
            Edit
          </Button>
          <Popconfirm
            title="Are you sure you want to delete this transaction?"
            onConfirm={() => handleDelete(record.transactionId)}
            okText="Yes"
            cancelText="No"
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              loading={deleteMutation.isPending}
            >
              Delete
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  if (error) {
    return (
      <Alert
        message="Error Loading Transactions"
        description="Failed to load transactions. Please try again later."
        type="error"
        showIcon
        action={
          <Button size="small" onClick={() => refetch()}>
            Retry
          </Button>
        }
      />
    )
  }

  return (
    <div>
      <Card>
        <div style={{ marginBottom: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Title level={4} style={{ margin: 0 }}>Transactions</Title>
          <Button
            type="primary"
            icon={<SearchOutlined />}
            onClick={handleSearch}
            loading={isLoading}
          >
            Refresh
          </Button>
        </div>
        <Table
          columns={columns}
          dataSource={transactions}
          rowKey="id"
          loading={isLoading}
          pagination={{
            current: pagination.number + 1,
            pageSize: pagination.size,
            total: pagination.totalElements,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total, range) =>
              `${range[0]}-${range[1]} of ${total} items`,
            pageSizeOptions: ['10', '20', '50', '100'],
          }}
          onChange={handleTableChange}
          scroll={{ x: 1200 }}
        />
      </Card>
    </div>
  )
}

export default TransactionList