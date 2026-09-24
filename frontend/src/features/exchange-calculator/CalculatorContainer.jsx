import React, { useState } from 'react';
import { ArrowDownUp, ShieldAlert, CheckCircle2 } from 'lucide-react';
import { useExchangeStore } from '../../store/useExchangeStore';
import { useAuthStore } from '../../store/useAuthStore';
import { Stepper } from '../../components/Stepper';
import { TimerPill } from '../../components/TimerPill';
import { exchangeService } from '../../services/exchangeService';
import { useNavigate } from 'react-router-dom';
import styles from './exchangeCalculator.module.css';

export const CalculatorContainer = () => {
  const {
    originAmount,
    targetAmount,
    originCurrency,
    targetCurrency,
    operationType,
    buyRate,
    sellRate,
    setOperationType,
    setOriginAmount,
    currentStep,
    setStep,
    activeOrder,
    setActiveOrder,
  } = useExchangeStore();

  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();
  const [txNumber, setTxNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const currentRate = operationType === 'BUY' ? buyRate : sellRate;

  const handleStartOperation = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      setLoading(true);
      setErrorMsg('');
      const payload = {
        currencyOrigin: originCurrency || 'USD',
        currencyDestination: targetCurrency || 'PEN',
        operationType: operationType === 'BUY' ? 'COMPRA' : 'VENTA',
        amountSent: parseFloat(originAmount) || 100.00,
        pointsToRedeem: 0,
      };
      console.log('Sending order payload from frontend:', payload);
      const order = await exchangeService.createOrder(payload);
      setActiveOrder(order);
      setStep(2);
    } catch (err) {
      console.error("Order creation error:", err);
      if (err.response?.status === 401) {
        useAuthStore.getState().logout();
        navigate('/login');
        setErrorMsg('Tu sesión ha expirado. Por favor ingresa nuevamente.');
      } else {
        setErrorMsg(err.response?.data?.message || 'Error al iniciar la operación.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmTransfer = async () => {
    if (!txNumber.trim()) {
      setErrorMsg('Ingresa el número de operación bancaria');
      return;
    }
    try {
      setLoading(true);
      setErrorMsg('');
      const orderId = activeOrder?.operationId || activeOrder?.orderNumber || activeOrder?.id || 'TRX-DEFAULT';
      const updated = await exchangeService.confirmTransfer(orderId, txNumber);
      setActiveOrder(updated);
      setStep(3);
    } catch (err) {
      console.error("Confirm transfer error:", err);
      setStep(3);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ padding: '3rem 0' }}>
      <Stepper currentStep={currentStep} />

      <div className={`glass-panel ${styles.calcCard}`}>
        <div className={styles.rateHeader}>
          <TimerPill />
          <div className={styles.rateBadge}>
            Tasa: <span className={styles.rateValue}>{currentRate.toFixed(4)}</span>
          </div>
        </div>

        {errorMsg && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#fca5a5', padding: '0.8rem', borderRadius: 'var(--radius-sm)', marginBottom: '1rem', fontSize: '0.9rem' }}>
            {errorMsg}
          </div>
        )}

        {currentStep === 1 && (
          <>
            <div className={styles.toggleContainer}>
              <button
                className={`${styles.toggleBtn} ${operationType === 'BUY' ? styles.toggleActive : ''}`}
                onClick={() => setOperationType('BUY')}
              >
                Dólar Compra ({buyRate.toFixed(4)})
              </button>
              <button
                className={`${styles.toggleBtn} ${operationType === 'SELL' ? styles.toggleActive : ''}`}
                onClick={() => setOperationType('SELL')}
              >
                Dólar Venta ({sellRate.toFixed(4)})
              </button>
            </div>

            <div className={styles.inputGroup}>
              <div>
                <span className={styles.inputLabel}>Tú envías</span>
                <input
                  type="number"
                  className={styles.numberInput}
                  value={originAmount}
                  onChange={(e) => setOriginAmount(e.target.value)}
                />
              </div>
              <span className={styles.currencySelect}>{originCurrency}</span>
            </div>

            <button className={styles.swapBtn} onClick={() => setOperationType(operationType === 'BUY' ? 'SELL' : 'BUY')}>
              <ArrowDownUp size={18} />
            </button>

            <div className={styles.inputGroup}>
              <div>
                <span className={styles.inputLabel}>Tú recibes (aprox)</span>
                <input
                  type="text"
                  readOnly
                  className={styles.numberInput}
                  value={targetAmount}
                  style={{ color: 'var(--primary-500)' }}
                />
              </div>
              <span className={styles.currencySelect}>{targetCurrency}</span>
            </div>

            <button className={styles.btnAction} onClick={handleStartOperation} disabled={loading}>
              {loading ? 'Procesando...' : 'Iniciar Cambio'}
            </button>
          </>
        )}

        {currentStep === 2 && activeOrder && (
          <div style={{ textAlign: 'center' }}>
            <h3 style={{ marginBottom: '1rem', color: 'var(--text-main)' }}>Transfiere desde tu Banco</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
              Transfiere <strong style={{ color: 'var(--primary-500)' }}>{activeOrder.currencyOrigin || activeOrder.originCurrency || originCurrency} {activeOrder.amountSent || activeOrder.originAmount || originAmount}</strong> a la cuenta de Cambista Online (BCP / Interbank):
            </p>


            <div style={{ background: 'var(--bg-card)', padding: '1.2rem', borderRadius: 'var(--radius-md)', marginBottom: '1.5rem', textAlign: 'left', fontSize: '0.9rem' }}>
              <div><strong>BCP Corriente:</strong> 193-9821839-0-12</div>
              <div><strong>CCI:</strong> 002-193009821839012-14</div>
              <div><strong>Titular:</strong> Cambista Online SAC</div>
            </div>

            <div className={styles.inputGroup}>
              <div style={{ width: '100%' }}>
                <span className={styles.inputLabel}>Número de Operación Bancaria</span>
                <input
                  type="text"
                  className={styles.numberInput}
                  placeholder="Ej: 009821"
                  value={txNumber}
                  onChange={(e) => setTxNumber(e.target.value)}
                />
              </div>
            </div>

            <button className={styles.btnAction} onClick={handleConfirmTransfer} disabled={loading}>
              {loading ? 'Verificando...' : 'Ya transferí'}
            </button>
          </div>
        )}

        {currentStep === 3 && activeOrder && (
          <div style={{ textAlign: 'center', padding: '1rem 0' }}>
            <CheckCircle2 color="var(--primary-500)" size={64} style={{ marginBottom: '1rem' }} />
            <h3 style={{ color: 'var(--text-main)', marginBottom: '0.5rem' }}>¡Transferencia Registrada!</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem', marginBottom: '1.5rem' }}>
              Nuestros operadores están validando tu abono. Recibirás tu cambio en tu cuenta bancaria en un promedio de 15 minutos.
            </p>
            <button className={styles.btnAction} onClick={() => { setStep(1); setActiveOrder(null); }}>
              Hacer otra operación
            </button>
          </div>
        )}
      </div>
    </div>
  );
};
