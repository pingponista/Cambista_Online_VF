import React, { useState } from 'react';
import { useOrderStore } from '../../store/useOrderStore';
import { useFxStore } from '../../store/useFxStore';

import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { Building2, CreditCard } from 'lucide-react';
import { exchangeService } from '../../services/exchangeService';
import styles from './exchange.module.css';

export const Step1Accounts = () => {
  const { orderData, setBankSelection, setStep } = useOrderStore();
  const [originBank, setOriginBank] = useState('BCP');
  const [destBank, setDestBank] = useState('BCP');
  const [destAccount, setDestAccount] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  if (!orderData) return null;

  const handleNext = async () => {
    try {
      setLoading(true);
      setErrorMsg('');
      const pointsToRedeem = orderData.pointsToRedeem || useFxStore.getState().redeemedPoints || 0;
      const payload = {
        currencyOrigin: orderData.originCurrency || 'USD',
        currencyDestination: orderData.targetCurrency || 'PEN',
        operationType: (orderData.operationType === 'BUY' || orderData.operationType === 'COMPRA') ? 'COMPRA' : 'VENTA',
        amountSent: orderData.originAmount || 100.00,
        pointsToRedeem: pointsToRedeem,
      };

      const createdOrder = await exchangeService.createOrder(payload);

      // Refresh points in store
      if (useFxStore.getState().fetchUserFxBreakdown) {
        useFxStore.getState().fetchUserFxBreakdown();
      }

      setBankSelection(originBank, destBank, '', destAccount);
      useOrderStore.setState({
        generatedOrderId: createdOrder.operationId || createdOrder.orderNumber,
        realOrderData: createdOrder
      });
      setStep(2);
    } catch (err) {
      console.error('Error creating order in Step1Accounts:', err);
      setErrorMsg(err.response?.data?.message || 'Error al registrar la orden en la base de datos.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className={styles.flowCard}>
      <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '0.5rem' }}>1. Selecciona tus Cuentas</h2>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
        Indica desde qué banco transferirás y dónde deseas recibir tu dinero.
      </p>

      {errorMsg && (
        <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#fca5a5', padding: '0.8rem', borderRadius: 'var(--radius-sm)', marginBottom: '1rem', fontSize: '0.9rem' }}>
          {errorMsg}
        </div>
      )}

      <div className={styles.summaryBox}>
        <div>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Envías</span>
          <div style={{ fontWeight: 800, fontSize: '1.2rem' }} className="tabular-nums">
            {orderData.originCurrency} {orderData.originAmount?.toFixed(2)}
          </div>
        </div>

        <div style={{ textAlign: 'right' }}>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Recibes</span>
          <div style={{ fontWeight: 800, fontSize: '1.2rem', color: 'var(--color-accent-green-hover)' }} className="tabular-nums">
            {orderData.targetCurrency} {orderData.targetAmount?.toFixed(2)}
          </div>
        </div>
      </div>

      <div style={{ marginBottom: '1.5rem' }}>
        <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.5rem' }}>
          <Building2 size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />
          ¿Desde qué banco nos transferirás?
        </label>
        <select
          value={originBank}
          onChange={(e) => setOriginBank(e.target.value)}
          style={{ width: '100%', padding: '0.85rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', fontWeight: 600 }}
        >
          <option value="BCP">Banco de Crédito (BCP)</option>
          <option value="INTERBANK">Interbank</option>
          <option value="BBVA">BBVA Continental</option>
          <option value="SCOTIABANK">Scotiabank</option>
        </select>
      </div>

      <div style={{ marginBottom: '2rem' }}>
        <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.5rem' }}>
          <CreditCard size={16} style={{ verticalAlign: 'middle', marginRight: '6px' }} />
          ¿En qué cuenta bancaria recibirás el dinero?
        </label>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '0.75rem' }}>
          <select
            value={destBank}
            onChange={(e) => setDestBank(e.target.value)}
            style={{ padding: '0.85rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', fontWeight: 600 }}
          >
            <option value="BCP">BCP</option>
            <option value="INTERBANK">Interbank</option>
            <option value="BBVA">BBVA</option>
            <option value="SCOTIABANK">Scotiabank</option>
          </select>

          <input
            type="text"
            placeholder="Número de cuenta o CCI (20 dígitos)"
            value={destAccount}
            onChange={(e) => setDestAccount(e.target.value)}
            style={{ padding: '0.85rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', fontWeight: 600 }}
            className="tabular-nums"
          />
        </div>
      </div>

      <Button variant="primary" size="lg" onClick={handleNext} disabled={loading}>
        {loading ? 'Creando Orden...' : 'Continuar al Paso 2'}
      </Button>
    </Card>
  );
};
