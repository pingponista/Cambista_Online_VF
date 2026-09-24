import React, { useState } from 'react';
import { useOrderStore } from '../../store/useOrderStore';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { Copy, Check, Upload, ArrowLeft } from 'lucide-react';
import { exchangeService } from '../../services/exchangeService';
import styles from './exchange.module.css';

export const Step2Transfer = () => {
  const { orderData, originBank, businessAccounts, submitTransferProof, setStep, generatedOrderId } = useOrderStore();
  const [txNum, setTxNum] = useState('');
  const [file, setFile] = useState(null);
  const [toastMsg, setToastMsg] = useState('');
  const [copiedKey, setCopiedKey] = useState('');
  const [loading, setLoading] = useState(false);

  if (!orderData) return null;

  const bankInfo = businessAccounts[originBank] || businessAccounts.BCP;

  const copyToClipboard = (text, key) => {
    navigator.clipboard.writeText(text);
    setCopiedKey(key);
    setToastMsg(`¡Copiado al portapapeles: ${text}!`);
    setTimeout(() => {
      setToastMsg('');
      setCopiedKey('');
    }, 2500);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!txNum.trim()) {
      setToastMsg('Por favor ingresa el número de operación bancaria');
      setTimeout(() => setToastMsg(''), 2500);
      return;
    }
    try {
      setLoading(true);
      const orderNumber = generatedOrderId || 'TRX-DEFAULT';
      await exchangeService.confirmTransfer(orderNumber, txNum);
      submitTransferProof(txNum, file);
    } catch (err) {
      console.error('Error confirming transfer in Step2Transfer:', err);
      // Advance to step 3 anyway to maintain user flow
      submitTransferProof(txNum, file);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card className={styles.flowCard}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '1rem', cursor: 'pointer', color: 'var(--text-muted)' }} onClick={() => setStep(1)}>
        <ArrowLeft size={16} /> <span style={{ fontSize: '0.85rem' }}>Volver al Paso 1</span>
      </div>

      <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '0.5rem' }}>2. Transfiere a CambistaOnline</h2>
      <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
        Transfiere desde tu app bancaria la suma de <strong style={{ color: 'var(--text-main)' }} className="tabular-nums">{orderData.originCurrency} {orderData.originAmount?.toFixed(2)}</strong> a la siguiente cuenta:
      </p>

      <div className={styles.bankBox}>
        <div style={{ fontWeight: 800, fontSize: '1.1rem', marginBottom: '0.75rem', color: 'var(--text-main)' }}>
          Cuentas {bankInfo.bank} - {bankInfo.holder}
        </div>

        <div className={styles.bankRow}>
          <span>Número de Cuenta ({orderData.originCurrency}):</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <strong className="tabular-nums">{bankInfo.account}</strong>
            <button type="button" className={styles.copyBtn} onClick={() => copyToClipboard(bankInfo.account, 'acc')}>
              {copiedKey === 'acc' ? <Check size={12} /> : <Copy size={12} />}
              {copiedKey === 'acc' ? 'Copiado' : 'Copiar'}
            </button>
          </div>
        </div>

        <div className={styles.bankRow}>
          <span>CCI Interbancario:</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <strong className="tabular-nums">{bankInfo.cci}</strong>
            <button type="button" className={styles.copyBtn} onClick={() => copyToClipboard(bankInfo.cci, 'cci')}>
              {copiedKey === 'cci' ? <Check size={12} /> : <Copy size={12} />}
              {copiedKey === 'cci' ? 'Copiado' : 'Copiar CCI'}
            </button>
          </div>
        </div>
      </div>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '1.2rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.4rem' }}>
            Número de Operación Bancaria *
          </label>
          <input
            type="text"
            required
            placeholder="Ej: 00981293"
            value={txNum}
            onChange={(e) => setTxNum(e.target.value)}
            style={{ width: '100%', padding: '0.85rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', fontWeight: 600 }}
            className="tabular-nums"
          />
        </div>

        <div style={{ marginBottom: '1.8rem' }}>
          <label style={{ display: 'block', fontSize: '0.85rem', fontWeight: 700, marginBottom: '0.4rem' }}>
            Adjuntar Voucher / Comprobante (Opcional - JPG, PNG, PDF)
          </label>
          <div style={{ border: '2px dashed var(--border-color)', padding: '1.2rem', borderRadius: 'var(--border-radius-sm)', textAlign: 'center', cursor: 'pointer' }}>
            <Upload size={24} color="var(--color-accent-green)" style={{ marginBottom: '0.3rem' }} />
            <input
              type="file"
              accept="image/jpeg,image/png,application/pdf"
              onChange={(e) => setFile(e.target.files[0])}
              style={{ display: 'none' }}
              id="voucherUpload"
            />
            <label htmlFor="voucherUpload" style={{ cursor: 'pointer', display: 'block', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              {file ? <strong>{file.name}</strong> : 'Haz clic para seleccionar comprobante'}
            </label>
          </div>
        </div>

        <Button variant="primary" size="lg" type="submit" disabled={loading}>
          {loading ? 'Enviando...' : 'Enviar Constancia de Transferencia'}
        </Button>
      </form>

      {toastMsg && (
        <div className={styles.toast}>
          <Check size={18} color="var(--color-accent-green)" />
          <span>{toastMsg}</span>
        </div>
      )}
    </Card>
  );
};
