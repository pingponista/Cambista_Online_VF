import React, { useState } from 'react';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../store/useAuthStore';
import { useFxStore } from '../../store/useFxStore';
import styles from './auth.module.css';

export const MfaVerificationModal = ({ mfaSessionToken, onCancel, onSuccess }) => {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const setAuth = useAuthStore((state) => state.setAuth);

  const handleVerify = async (e) => {
    e.preventDefault();
    if (!code || code.trim().length !== 6) {
      setError('Por favor ingresa un código de 6 dígitos.');
      return;
    }

    try {
      setLoading(true);
      setError('');
      const data = await authService.verifyMfa(mfaSessionToken, code.trim());
      setAuth(data);
      useFxStore.getState().fetchUserFxBreakdown();
      if (onSuccess) onSuccess();
    } catch (err) {
      setError(err.response?.data?.message || 'Código de autenticación inválido o expirado.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      position: 'fixed',
      inset: 0,
      background: 'rgba(0, 0, 0, 0.75)',
      backdropFilter: 'blur(4px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 9999,
      padding: '1rem',
    }}>
      <div className={`glass-panel ${styles.authCard}`} style={{ maxWidth: '420px', width: '100%' }}>
        <div style={{ textAlign: 'center', marginBottom: '1.2rem' }}>
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🔐</div>
          <h2 className={styles.authTitle} style={{ fontSize: '1.4rem' }}>Verificación en 2 Pasos</h2>
          <p className={styles.authSubtitle} style={{ fontSize: '0.88rem' }}>
            Ingresa el código de 6 dígitos generado por tu aplicación <strong>Google Authenticator</strong>
          </p>
        </div>

        {error && (
          <div style={{
            background: 'rgba(239, 68, 68, 0.15)',
            border: '1px solid #ef4444',
            color: '#fca5a5',
            padding: '0.75rem',
            borderRadius: '6px',
            marginBottom: '1rem',
            fontSize: '0.85rem',
            textAlign: 'center',
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleVerify}>
          <div className={styles.formGroup} style={{ textAlign: 'center' }}>
            <input
              type="text"
              maxLength={6}
              autoFocus
              placeholder="000000"
              value={code}
              onChange={(e) => setCode(e.target.value.replace(/\D/g, ''))}
              style={{
                width: '100%',
                maxWidth: '240px',
                textAlign: 'center',
                fontSize: '2rem',
                letterSpacing: '0.4rem',
                fontWeight: 700,
                padding: '0.6rem 0.8rem',
                borderRadius: '8px',
                border: '2px solid #3b82f6',
                background: 'rgba(15, 23, 42, 0.6)',
                color: '#ffffff',
                margin: '0 auto',
                display: 'block',
              }}
            />
          </div>

          <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.5rem' }}>
            <button
              type="button"
              onClick={onCancel}
              style={{
                flex: 1,
                padding: '0.75rem',
                borderRadius: '6px',
                border: '1px solid rgba(255, 255, 255, 0.2)',
                background: 'transparent',
                color: '#94a3b8',
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className={styles.btnSubmit}
              disabled={loading || code.length !== 6}
              style={{ flex: 1, marginTop: 0 }}
            >
              {loading ? 'Verificando...' : 'Verificar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
