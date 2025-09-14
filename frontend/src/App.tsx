import { Routes, Route, Navigate } from 'react-router-dom'
import { Layout, Typography, theme } from 'antd'
import { BankOutlined } from '@ant-design/icons'
import TransactionList from './pages/TransactionList'
import TransactionForm from './pages/TransactionForm'
import Navigation from './components/Navigation'

const { Header, Content } = Layout
const { Title } = Typography

function App() {
  const {
    token: { colorBgContainer },
  } = theme.useToken()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 24px' }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <BankOutlined style={{ color: 'white', fontSize: '24px', marginRight: '12px' }} />
          <Title level={3} style={{ color: 'white', margin: 0 }}>
            Transaction Management System
          </Title>
        </div>
      </Header>
      
      <Layout>
        <Navigation />
        
        <Content
          className="p-6"
          style={{
            background: colorBgContainer,
            minHeight: 'calc(100vh - 64px)',
          }}
        >
          <div className="fade-in">
            <Routes>
              <Route path="/" element={<Navigate to="/transactions" replace />} />
              <Route path="/transactions" element={<TransactionList />} />
              <Route path="/transactions/new" element={<TransactionForm />} />
              <Route path="/transactions/edit/:id" element={<TransactionForm />} />
              <Route path="*" element={<Navigate to="/transactions" replace />} />
            </Routes>
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default App