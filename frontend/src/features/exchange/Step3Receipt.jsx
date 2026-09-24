import React, { useState, useEffect } from 'react';
import { useOrderStore } from '../../store/useOrderStore';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { CheckCircle2, Clock, Check, ArrowRight } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import styles from './exchange.module.css';

export const Step3Receipt = () => {
  const { generatedOrderId, orderData, transactionNumber, resetOrder } = useOrderStore();
  const [status, setStatus] = useState('En Verificación');
  const navigate = useNavigate();

  useEffect(() => {
    // Simulación reactiva de cambio de estado en vivo
    const t1 = setTimeout(() => setStatus('Procesando'), 5000);
    const t2 = setTimeout(() => setStatus('Completada'), 12000);
    return () => { clearTimeout(t1); clearTimeout(t2); };
  }, []);

  const handleFinish = () => {
    resetOrder();
    navigate('/dashboard');
  };

  return (
    <Card className={styles.flowCard} style={{ textAlign: 'center' }}>
      <CheckCircle2 size={60} color="var(--color-accent-green)" style={{ marginBottom: '1rem' }} />
      
      <h2 style={{ fontSize: '1.5rem', fontWeight: 800, marginBottom: '0.2rem' }}>¡Operación Registrada!</h2>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
        Orden <strong className="tabular-nums">{generatedOrderId}</strong> | N° Operación: <strong className="tabular-nums">{transactionNumber}</strong>
      </p>

      {/* Tracker Timeline */}
      <div className={styles.trackerTimeline}>
        <div className={`${styles.timelineItem} ${styles.timelineActive}`}>
          <div className={styles.timelineDot}><Check size={14} /></div>
          <span>Registrada</span>
        </div>

        <div className={`${styles.timelineItem} ${status === 'Procesando' || status === 'Completada' ? styles.timelineActive : ''}`}>
          <div className={styles.timelineDot}>
            {status === 'Procesando' || status === 'Completada' ? <Check size={14} /> : <Clock size={12} />}
          </div>
          <span>En Verificación</span>
        </div>

        <div className={`${styles.timelineItem} ${status === 'Completada' ? styles.timelineActive : ''}`}>
          <div className={styles.timelineDot}>
            {status === 'Completada' ? <Check size={14} /> : '3'}
          </div>
          <span>Completada</span>
        </div>
      </div>

      <div style={{ background: 'var(--bg-main)', padding: '1.2rem', borderRadius: 'var(--border-radius)', textAlign: 'left', marginBottom: '1.8rem', fontSize: '0.9rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
          <span style={{ color: 'var(--text-muted)' }}>Monto Transferido:</span>
          <strong className="tabular-nums">{orderData?.originCurrency} {orderData?.originAmount?.toFixed(2)}</strong>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
          <span style={{ color: 'var(--text-muted)' }}>Monto a Recibir:</span>
          <strong style={{ color: 'var(--color-accent-green-hover)' }} className="tabular-nums">{orderData?.targetCurrency} {orderData?.targetAmount?.toFixed(2)}</strong>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span style={{ color: 'var(--text-muted)' }}>Estado Actual:</span>
          <strong style={{ color: status === 'Completada' ? 'var(--color-accent-green)' : 'var(--color-accent-yellow)' }}>{status}</strong>
        </div>
      </div>

      <Button variant="primary" size="lg" onClick={handleFinish}>
        Ver mis operaciones en el Dashboard <ArrowRight size={16} />
      </Button>
    </Card>
  );
};
