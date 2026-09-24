import React, { useState, useEffect } from 'react';
import { authService } from '../../services/authService';
import apiAuth from '../../services/apiAuth';

export const MfaSettingsCard = () => {
  const [mfaEnabled, setMfaEnabled] = useState(false);
  const [loading, setLoading] = useState(false);
  const [setupData, setSetupData] = useState(null);
  const [verifyCode, setVerifyCode] = useState('');
  const [disableCode, setDisableCode] = useState('');
  const [showDisableForm, setShowDisableForm] = useState(false);
  const [message, setMessage] = useState({ text: '', type: '' });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const res = await apiAuth.getMe();
      if (res?.data) {
        setMfaEnabled(res.data.mfaEnabled || false);
      }
    } catch (err) {
      console.warn('No se pudo obtener el estado MFA actual', err);
    }
  };

  const handleStartSetup = async () => {
    try {
      setLoading(true);
      setMessage({ text: '', type: '' });
      const data = await authService.setupMfa();
      setSetupData(data);
    } catch (err) {
      setMessage({ text: 'Error al iniciar la configuración de MFA.', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmEnable = async (e) => {
    e.preventDefault();
    if (!verifyCode || verifyCode.length !== 6) {
      setMessage({ text: 'Ingresa un código de 6 dígitos.', type: 'error' });
      return;
    }

    try {
      setLoading(true);
      setMessage({ text: '', type: '' });
      await authService.enableMfa(verifyCode.trim());
      setMfaEnabled(true);
      setSetupData(null);
      setVerifyCode('');
      setMessage({ text: '✅ ¡Autenticación en 2 pasos activada con éxito!', type: 'success' });
    } catch (err) {
      setMessage({ text: err.response?.data?.message || 'Código inválido o expirado.', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const handleDisable = async (e) => {
    e.preventDefault();
    if (!disableCode || disableCode.length !== 6) {
      setMessage({ text: 'Ingresa el código actual de 6 dígitos.', type: 'error' });
      return;
    }

    try {
      setLoading(true);
      setMessage({ text: '', type: '' });
      await authService.disableMfa(disableCode.trim());
      setMfaEnabled(false);
      setShowDisableForm(false);
      setDisableCode('');
      setMessage({ text: 'Doble factor (2FA) desactivado correctamente.', type: 'success' });
    } catch (err) {
      setMessage({ text: err.response?.data?.message || 'Código inválido.', type: 'error' });
    } finally {
      setLoading(false);
    }
  };

  const qrImageUrl = setupData?.qrCodeUri
    ? `https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=${encodeURIComponent(setupData.qrCodeUri)}`
    : null;

  return (
    <div style={{
      background: 'rgba(15, 23, 42, 0.65)',
      border: '1px solid rgba(255, 255, 255, 0.1)',
      borderRadius: '12px',
      padding: '1.5rem',
      marginTop: '1.5rem',
      color: '#f8fafc',
    }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <span style={{ fontSize: '1.8rem' }}>🛡️</span>
          <div>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: 0 }}>Autenticación en Dos Pasos (2FA)</h3>
            <p style={{ fontSize: '0.85rem', color: '#94a3b8', margin: '0.2rem 0 0 0' }}>
              Protege tus operaciones y fondos con Google Authenticator
            </p>
          </div>
        </div>
        <span style={{
          padding: '0.35rem 0.85rem',
          borderRadius: '20px',
          fontSize: '0.8rem',
          fontWeight: 700,
          background: mfaEnabled ? 'rgba(34, 197, 94, 0.15)' : 'rgba(239, 68, 68, 0.15)',
          color: mfaEnabled ? '#4ade80' : '#f87171',
          border: `1px solid ${mfaEnabled ? 'rgba(34, 197, 94, 0.3)' : 'rgba(239, 68, 68, 0.3)'}`,
        }}>
          {mfaEnabled ? 'ACTIVO' : 'INACTIVO'}
        </span>
      </div>

      {message.text && (
        <div style={{
          padding: '0.75rem',
          borderRadius: '6px',
          marginBottom: '1rem',
          fontSize: '0.85rem',
          background: message.type === 'success' ? 'rgba(34, 197, 94, 0.15)' : 'rgba(239, 68, 68, 0.15)',
          color: message.type === 'success' ? '#4ade80' : '#f87171',
          border: `1px solid ${message.type === 'success' ? '#22c55e' : '#ef4444'}`,
        }}>
          {message.text}
        </div>
      )}

      {/* Si NO está activo y NO ha iniciado setup */}
      {!mfaEnabled && !setupData && (
        <div>
          <p style={{ fontSize: '0.88rem', color: '#cbd5e1', marginBottom: '1.2rem' }}>
            Al activar 2FA, cada vez que inicies sesión se te solicitará un código temporal de 6 dígitos generado por tu teléfono celular.
          </p>
          <button
            type="button"
            onClick={handleStartSetup}
            disabled={loading}
            style={{
              background: '#2563eb',
              color: '#fff',
              padding: '0.65rem 1.25rem',
              borderRadius: '6px',
              border: 'none',
              fontWeight: 600,
              cursor: 'pointer',
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
            }}
          >
            📲 Configurar Google Authenticator
          </button>
        </div>
      )}

      {/* Paso de Setup con Código QR */}
      {!mfaEnabled && setupData && (
        <div style={{ background: 'rgba(30, 41, 59, 0.5)', padding: '1.2rem', borderRadius: '8px', border: '1px solid rgba(255, 255, 255, 0.08)' }}>
          <h4 style={{ margin: '0 0 0.8rem 0', fontSize: '1rem', color: '#38bdf8' }}>
            Paso 1: Escanea este código QR con Google Authenticator
          </h4>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '1.5rem', alignItems: 'center' }}>
            {qrImageUrl && (
              <div style={{ background: '#ffffff', padding: '0.5rem', borderRadius: '8px' }}>
                <img src={qrImageUrl} alt="QR Code TOTP" style={{ width: '160px', height: '160px', display: 'block' }} />
              </div>
            )}
            <div style={{ flex: 1, minWidth: '220px' }}>
              <p style={{ fontSize: '0.85rem', color: '#94a3b8', margin: '0 0 0.5rem 0' }}>
                ¿No puedes escanear el código? Ingresa esta clave manual en tu app:
              </p>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.8rem' }}>
                <code style={{
                  background: 'rgba(0, 0, 0, 0.4)',
                  padding: '0.4rem 0.8rem',
                  borderRadius: '4px',
                  color: '#facc15',
                  fontSize: '0.95rem',
                  fontWeight: 700,
                  display: 'inline-block',
                  letterSpacing: '0.1rem',
                }}>
                  {setupData.manualKey}
                </code>
                <button
                  type="button"
                  onClick={() => {
                    navigator.clipboard.writeText(setupData.manualKey);
                    setMessage({ text: '📋 Clave manual copiada al portapapeles', type: 'success' });
                  }}
                  title="Copiar clave"
                  style={{
                    background: 'rgba(255, 255, 255, 0.1)',
                    border: '1px solid rgba(255, 255, 255, 0.2)',
                    color: '#e2e8f0',
                    padding: '0.35rem 0.65rem',
                    borderRadius: '4px',
                    fontSize: '0.8rem',
                    cursor: 'pointer',
                  }}
                >
                  📋 Copiar
                </button>
              </div>

              <div style={{
                fontSize: '0.78rem',
                color: '#94a3b8',
                background: 'rgba(15, 23, 42, 0.7)',
                padding: '0.6rem 0.8rem',
                borderRadius: '6px',
                marginBottom: '1rem',
                lineHeight: 1.4,
                borderLeft: '3px solid #38bdf8',
              }}>
                ℹ️ <strong>Consejo:</strong> Si tenías una cuenta previa de <em>CambistaOnline</em> en tu app de autenticación, bórrala antes de escanear este código. Asegúrate de ingresar el código antes de que termine el círculo de 30 segundos.
              </div>

              <h4 style={{ margin: '0.8rem 0 0.5rem 0', fontSize: '1rem', color: '#38bdf8' }}>
                Paso 2: Ingresa el código de 6 dígitos que muestra tu app
              </h4>
              <form onSubmit={handleConfirmEnable} style={{ display: 'flex', gap: '0.5rem' }}>
                <input
                  type="text"
                  maxLength={6}
                  placeholder="000000"
                  value={verifyCode}
                  onChange={(e) => setVerifyCode(e.target.value.replace(/\D/g, ''))}
                  style={{
                    width: '120px',
                    textAlign: 'center',
                    fontSize: '1.2rem',
                    letterSpacing: '0.2rem',
                    fontWeight: 700,
                    padding: '0.4rem',
                    borderRadius: '6px',
                    border: '1px solid #38bdf8',
                    background: '#0f172a',
                    color: '#fff',
                  }}
                />
                <button
                  type="submit"
                  disabled={loading || verifyCode.length !== 6}
                  style={{
                    background: '#16a34a',
                    color: '#fff',
                    padding: '0.5rem 1rem',
                    borderRadius: '6px',
                    border: 'none',
                    fontWeight: 600,
                    cursor: 'pointer',
                  }}
                >
                  {loading ? 'Activando...' : 'Confirmar y Activar'}
                </button>
                <button
                  type="button"
                  onClick={() => setSetupData(null)}
                  style={{
                    background: 'transparent',
                    color: '#94a3b8',
                    border: 'none',
                    cursor: 'pointer',
                  }}
                >
                  Cancelar
                </button>
              </form>
            </div>
          </div>
        </div>
      )}

      {/* Si YA está activo */}
      {mfaEnabled && (
        <div>
          <p style={{ fontSize: '0.88rem', color: '#cbd5e1', marginBottom: '1rem' }}>
            Tu cuenta está protegida con verificación en dos pasos. Se requerirá un código TOTP cada vez que inicies sesión.
          </p>

          {!showDisableForm ? (
            <button
              type="button"
              onClick={() => setShowDisableForm(true)}
              style={{
                background: 'rgba(239, 68, 68, 0.2)',
                color: '#f87171',
                border: '1px solid rgba(239, 68, 68, 0.4)',
                padding: '0.5rem 1rem',
                borderRadius: '6px',
                fontWeight: 600,
                cursor: 'pointer',
              }}
            >
              Desactivar 2FA
            </button>
          ) : (
            <form onSubmit={handleDisable} style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginTop: '0.5rem' }}>
              <input
                type="text"
                maxLength={6}
                placeholder="Código de 6 dígitos"
                value={disableCode}
                onChange={(e) => setDisableCode(e.target.value.replace(/\D/g, ''))}
                style={{
                  width: '160px',
                  textAlign: 'center',
                  padding: '0.4rem',
                  borderRadius: '6px',
                  border: '1px solid #ef4444',
                  background: '#0f172a',
                  color: '#fff',
                  fontWeight: 600,
                }}
              />
              <button
                type="submit"
                disabled={loading || disableCode.length !== 6}
                style={{
                  background: '#dc2626',
                  color: '#fff',
                  padding: '0.45rem 0.9rem',
                  borderRadius: '6px',
                  border: 'none',
                  fontWeight: 600,
                  cursor: 'pointer',
                }}
              >
                Confirmar Desactivación
              </button>
              <button
                type="button"
                onClick={() => setShowDisableForm(false)}
                style={{
                  background: 'transparent',
                  color: '#94a3b8',
                  border: 'none',
                  cursor: 'pointer',
                }}
              >
                Cancelar
              </button>
            </form>
          )}
        </div>
      )}
    </div>
  );
};

export default MfaSettingsCard;
