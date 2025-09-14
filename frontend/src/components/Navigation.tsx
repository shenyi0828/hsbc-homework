import React from 'react'
import { Layout, Menu } from 'antd'
import { useNavigate, useLocation } from 'react-router-dom'
import {
  TransactionOutlined,
  PlusOutlined,
} from '@ant-design/icons'

const { Sider } = Layout

const Navigation: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()

  const menuItems = [
    {
      key: '/transactions',
      icon: <TransactionOutlined />,
      label: 'Transactions',
    },
    {
      key: '/transactions/new',
      icon: <PlusOutlined />,
      label: 'New Transaction',
    },
  ]

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key)
  }

  const getSelectedKey = () => {
    const path = location.pathname
    if (path.startsWith('/transactions/edit/')) {
      return '/transactions/new'
    }
    return path
  }

  return (
    <Sider
      width={250}
      style={{
        background: '#fff',
        borderRight: '1px solid #f0f0f0',
      }}
    >
      <Menu
        mode="inline"
        selectedKeys={[getSelectedKey()]}
        style={{
          height: '100%',
          borderRight: 0,
          paddingTop: 16,
        }}
        items={menuItems}
        onClick={handleMenuClick}
      />
    </Sider>
  )
}

export default Navigation