import React, { useEffect } from 'react'
import {
  Card,
  Form,
  Input,
  Select,
  InputNumber,
  Button,
  Typography,
  Space,
  message,
  Spin,
  Alert,
} from 'antd'
import { SaveOutlined, ArrowLeftOutlined } from '@ant-design/icons'
import { useNavigate, useParams } from 'react-router-dom'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { transactionApi } from '../services/api'
import {
  TransactionRequest,
  TransactionType,
  formatAmount,
  parseAmount,
} from '../types/transaction'

const { Title } = Typography
const { Option } = Select
const { TextArea } = Input

const TransactionForm: React.FC = () => {
  const navigate = useNavigate()
  const { id } = useParams<{ id: string }>()
  const queryClient = useQueryClient()
  const [form] = Form.useForm()
  const isEdit = Boolean(id)

  // 获取编辑数据
  const {
    data: transactionResponse,
    isLoading: isLoadingTransaction,
    error: transactionError,
  } = useQuery({
    queryKey: ['transaction', id],
    queryFn: () => transactionApi.getTransactionById(id!),
    enabled: isEdit,
  })

  // 创建操作
  const createMutation = useMutation({
    mutationFn: transactionApi.createTransaction,
    onSuccess: () => {
      message.success('Transaction created successfully')
      queryClient.invalidateQueries({ queryKey: ['transactions'] })
      queryClient.invalidateQueries({ queryKey: ['dashboard-statistics'] })
      navigate('/transactions')
    },
    onError: () => {
      message.error('Failed to create transaction')
    },
  })

  // 更新操作
  const updateMutation = useMutation({
    mutationFn: ({ transactionId, data }: { transactionId: string; data: TransactionRequest }) =>
      transactionApi.updateTransaction({ transactionId, ...data }),
    onSuccess: () => {
      message.success('Transaction updated successfully')
      queryClient.invalidateQueries({ queryKey: ['transactions'] })
      queryClient.invalidateQueries({ queryKey: ['transaction', id] })
      queryClient.invalidateQueries({ queryKey: ['dashboard-statistics'] })
      navigate('/transactions')
    },
    onError: () => {
      message.error('Failed to update transaction')
    },
  })

  // 设置表单值
  useEffect(() => {
    if (isEdit && transactionResponse) {
      form.setFieldsValue({
        transactionId: transactionResponse.transactionId,
        amount: formatAmount(transactionResponse.amount),
        transactionType: transactionResponse.transactionType,
        accountNumber: transactionResponse.accountNumber,
        counterpartyAccount: transactionResponse.counterpartyAccount || '',
        description: transactionResponse.description || '',
      } as any)
    }
  }, [isEdit, transactionResponse, form])

  const handleSubmit = (values: any) => {
    const transactionData = {
      ...values,
      amount: parseAmount(values.amount), // 转换金额
    }

    if (isEdit) {
      updateMutation.mutate({
        transactionId: id!,
        data: transactionData,
      })
    } else {
      createMutation.mutate(transactionData)
    }
  }

  const handleBack = () => {
    navigate('/transactions')
  }

  if (isEdit && isLoadingTransaction) {
    return (
      <div className="loading-container">
        <Spin size="large" />
      </div>
    )
  }

  if (isEdit && transactionError) {
    return (
      <Alert
        message="Error Loading Transaction"
        description="Failed to load transaction details. Please try again later."
        type="error"
        showIcon
        action={
          <Button size="small" onClick={handleBack}>
            Back to List
          </Button>
        }
      />
    )
  }

  const isLoading = createMutation.isPending || updateMutation.isPending

  return (
    <div>
      <div className="flex items-center mb-24">
        <Button
          type="text"
          icon={<ArrowLeftOutlined />}
          onClick={handleBack}
          className="mr-16"
        >
          Back
        </Button>
        <Title level={2} className="m-0">
          {isEdit ? 'Edit Transaction' : 'New Transaction'}
        </Title>
      </div>

      <Card>
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{
            transactionType: TransactionType.EXPENSE,  // 改为EXPENSE而不是UNKNOWN
          }}
        >
          <div className="grid grid-cols-1 md:grid-cols-2 gap-16">
            {isEdit && (
              <Form.Item
                label="Transaction ID"
                name="transactionId"
              >
                <Input
                  placeholder="Transaction ID"
                  disabled={true}
                  style={{ backgroundColor: '#f5f5f5' }}
                />
              </Form.Item>
            )}
            
            <Form.Item
              label="Account Number"
              name="accountNumber"
              rules={[
                {
                  required: true,
                  message: 'Please enter account number',
                },
                {
                  pattern: /^[0-9]{10,20}$/,
                  message: 'must be 10-20 digits',
                },
              ]}
            >
              <Input
                placeholder="1234567890"
                maxLength={20}
                disabled={isLoading}
              />
            </Form.Item>

            <Form.Item
              label="Counterparty Account"
              name="counterpartyAccount"
              rules={[
                {
                  required: true,
                  message: 'Please enter counterparty account',
                },
                {
                  pattern: /^[0-9]{10,20}$/,
                  message: 'must be 10-20 digits',
                },
              ]}
            >
              <Input
                placeholder="9876543210"
                maxLength={20}
                disabled={isLoading}
              />
            </Form.Item>

            <Form.Item
              label="Amount"
              name="amount"
              rules={[
                {
                  required: true,
                  message: 'Please enter amount',
                },
                {
                  type: 'number',
                  min: 0.01,
                  message: 'Amount must be greater than 0',
                },
                {
                  type: 'number',
                  max: 999999.99,
                  message: 'Amount cannot exceed 999,999.99',
                },
              ]}
            >
              <InputNumber
                  placeholder="Enter amount"
                  style={{ width: '100%' }}
                  precision={2}
                  min={0.01}
                  max={999999.99}
                  formatter={(value) =>
                    `$ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                  }
                  parser={(value) => parseFloat(value!.replace(/\$\s?|(,*)/g, '')) || 0 as any}
                  disabled={isLoading}
                />
            </Form.Item>

            <Form.Item
              label="Transaction Type"
              name="transactionType"
              rules={[
                {
                  required: true,
                  message: 'Please select transaction type',
                },
              ]}
            >
              <Select placeholder="Select transaction type" disabled={isLoading}>
                <Option value={TransactionType.EXPENSE}>EXPENSE</Option>
                <Option value={TransactionType.INCOME}>INCOME</Option>
              </Select>
            </Form.Item>
          </div>

          <Form.Item
            label="Description"
            name="description"
            rules={[
              {
                max: 500,
                message: 'Description cannot exceed 500 characters',
              },
            ]}
          >
            <TextArea
              placeholder="Enter transaction description (optional)"
              rows={4}
              maxLength={500}
              showCount
              disabled={isLoading}
            />
          </Form.Item>

          <div className="form-actions">
            <Space>
              <Button onClick={handleBack} disabled={isLoading}>
                Cancel
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                icon={<SaveOutlined />}
                loading={isLoading}
              >
                {isEdit ? 'Update Transaction' : 'Create Transaction'}
              </Button>
            </Space>
          </div>
        </Form>
      </Card>
    </div>
  )
}

export default TransactionForm