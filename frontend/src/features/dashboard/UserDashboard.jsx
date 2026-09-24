import React, { useState, useEffect } from 'react';
import { useAuthStore } from '../../store/useAuthStore';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { BankAccountsManager } from './BankAccountsManager';
import { RefreshCw, History, CreditCard, User, PlusCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { exchangeService } from '../../services/exchangeService';
import { MfaSettingsCard } from '../auth/MfaSettingsCard';
import styles from './dashboard.module.css';

export const UserDashboard = () => {
  const { user } = useAuthStore();
  const [activeTab, setActiveTab] = useState('historial');
  const [filterStatus, setFilterStatus] = useState('ALL');
  const [orders, setOrders] = useState([]);
  const [loadingOrders, setLoadingOrders] = useState(false);
  const navigate = useNavigate();

  const loadUserOrders = async () => {
    try {
      setLoadingOrders(true);
      const data = await exchangeService.getUserOrders();
      if (Array.isArray(data)) {
        setOrders(data);
      }
    } catch (err) {
      console.error('Error loading orders:', err);
    } finally {
      setLoadingOrders(false);
    }
  };

  useEffect(() => {
    loadUserOrders();
  }, []);

  const filteredOrders = filterStatus === 'ALL'
    ? orders
    : orders.filter((o) => o.status === filterStatus);

  return (
    <div className={`container ${styles.dashboardGrid}`}>
      {/* Sidebar Navigation */}
      <aside className={styles.sidebar}>
        <div
          className={`${styles.navItem} ${activeTab === 'operar' ? styles.navActive : ''}`}
          onClick={() => navigate('/')}
        >
          <RefreshCw size={18} /> Nueva Operación
        </div>

        <div
          className={`${styles.navItem} ${activeTab === 'historial' ? styles.navActive : ''}`}
          onClick={() => setActiveTab('historial')}
        >
          <History size={18} /> Historial
        </div>

        <div
          className={`${styles.navItem} ${activeTab === 'cuentas' ? styles.navActive : ''}`}
          onClick={() => setActiveTab('cuentas')}
        >
          <CreditCard size={18} /> Mis Cuentas
        </div>

        <div
          className={`${styles.navItem} ${activeTab === 'perfil' ? styles.navActive : ''}`}
          onClick={() => setActiveTab('perfil')}
        >
          <User size={18} /> Mi Perfil
        </div>
      </aside>

      {/* Main Content Area */}
      <main>
        <div className={styles.profileCard}>
          <div>
            <h2 style={{ fontSize: '1.4rem', fontWeight: 800 }}>{user?.name || 'Hola, bienvenido'}</h2>
            <span style={{ fontSize: '0.85rem', color: 'var(--color-accent-green-hover)', fontWeight: 700 }}>
              Perfil {user?.profileType === 'JURIDICA' ? 'Empresarial (Persona Jurídica)' : 'Persona Natural'}
            </span>
          </div>
          <Button variant="primary" size="md" onClick={() => navigate('/')}>
            <PlusCircle size={16} /> Operar ahora
          </Button>
        </div>

        {activeTab === 'historial' && (
          <Card>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.2rem' }}>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800 }}>Historial de Operaciones</h3>

              <div className={styles.tableFilters}>
                <button 
                  onClick={loadUserOrders} 
                  style={{ background: 'transparent', border: '1px solid var(--border-color)', color: 'var(--text-main)', padding: '0.4rem 0.8rem', borderRadius: '4px', cursor: 'pointer', marginRight: '0.5rem', fontSize: '0.8rem' }}
                >
                  🔄 Actualizar
                </button>
                <select
                  value={filterStatus}
                  onChange={(e) => setFilterStatus(e.target.value)}
                  className={styles.filterSelect}
                >
                  <option value="ALL">Todos los estados</option>
                  <option value="COMPLETED">Completadas</option>
                  <option value="PENDING_PAYMENT">Pendientes de Pago</option>
                  <option value="PAYMENT_UPLOADED">En Verificación</option>
                </select>
              </div>
            </div>

            {loadingOrders ? (
              <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>Cargando tus operaciones...</div>
            ) : filteredOrders.length === 0 ? (
              <div style={{ padding: '2rem', textAlign: 'center', color: 'var(--text-muted)' }}>No tienes operaciones registradas aún.</div>
            ) : (
              <table className={styles.table}>
                <thead>
                  <tr>
                    <th>N° Orden</th>
                    <th>Operación</th>
                    <th>Monto Enviado</th>
                    <th>Monto Recibido</th>
                    <th>Tasa</th>
                    <th>Estado</th>
                    <th>Fecha</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredOrders.map((ord) => {
                    const orderId = ord.operationId || ord.orderNumber || ord.id;
                    const opType = ord.operationType === 'COMPRA' || ord.operationType === 'BUY' ? 'Compra Dólar' : 'Venta Dólar';
                    const originStr = `${ord.currencyOrigin || 'USD'} ${ord.amountSent ? Number(ord.amountSent).toFixed(2) : '0.00'}`;
                    const targetStr = `${ord.currencyDestination || 'PEN'} ${ord.amountReceived ? Number(ord.amountReceived).toFixed(2) : '0.00'}`;
                    const rateStr = ord.exchangeRate ? Number(ord.exchangeRate).toFixed(4) : '0.0000';
                    const dateStr = ord.createdAt ? new Date(ord.createdAt).toLocaleDateString() : 'Hoy';

                    return (
                      <tr key={orderId}>
                        <td><strong className="tabular-nums">{orderId}</strong></td>
                        <td>{opType}</td>
                        <td className="tabular-nums">{originStr}</td>
                        <td className="tabular-nums" style={{ color: 'var(--color-accent-green-hover)', fontWeight: 700 }}>{targetStr}</td>
                        <td className="tabular-nums">{rateStr}</td>
                        <td>
                          <span className={ord.status === 'COMPLETED' ? styles.statusCompleted : styles.statusPending}>
                            {ord.status === 'COMPLETED' ? 'Completada' : ord.status === 'PENDING_PAYMENT' ? 'Pendiente Pago' : 'En Verificación'}
                          </span>
                        </td>
                        <td className="tabular-nums">{dateStr}</td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </Card>
        )}

        {activeTab === 'cuentas' && <BankAccountsManager />}

        {activeTab === 'perfil' && (
          <div>
            <Card>
              <h3 style={{ fontSize: '1.2rem', fontWeight: 800, marginBottom: '1rem' }}>Información de mi Perfil</h3>
              <div style={{ lineHeight: '1.8', fontSize: '0.95rem' }}>
                <div><strong>Email:</strong> {user?.email || 'usuario@cambistaonline.pe'}</div>
                <div><strong>Tipo de Perfil:</strong> {user?.profileType || 'Persona Natural'}</div>
                <div><strong>Estado de Identidad:</strong> <span style={{ color: 'var(--color-accent-green-hover)', fontWeight: 700 }}>Verificado por la SBS</span></div>
              </div>
            </Card>
            <MfaSettingsCard />
          </div>
        )}
      </main>
    </div>
  );
};
