import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService, DEMO_CREDENTIALS } from '../../services/authService';
import { useAuthStore } from '../../store/useAuthStore';
import { useFxStore } from '../../store/useFxStore';
import { SocialAuthButtons } from './SocialAuthButtons';
import { MfaVerificationModal } from './MfaVerificationModal';
import styles from './auth.module.css';

export const LoginForm = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [oauthLoadingMsg, setOauthLoadingMsg] = useState('');
  const [mfaSessionToken, setMfaSessionToken] = useState(null);

  const setAuth = useAuthStore((state) => state.setAuth);
  const navigate = useNavigate();

  // Escuchar el retorno oficial del proveedor OAuth con ?code=...
  useEffect(() => {
    const handleOAuthCallback = async () => {
      const params = new URLSearchParams(window.location.search);
      const code = params.get('code');
      const oauthError = params.get('error');

      if (oauthError) {
        setError(`Error en la autorización social: ${oauthError}`);
        window.history.replaceState({}, document.title, window.location.pathname);
        return;
      }

      if (code) {
        const provider = sessionStorage.getItem('oauth_provider') || 'GOOGLE';
        const redirectUri = sessionStorage.getItem('oauth_redirect_uri') || `${window.location.origin}/login`;

        // Limpiamos los parámetros de la URL visible
        window.history.replaceState({}, document.title, window.location.pathname);

        try {
          setLoading(true);
          setOauthLoadingMsg(`Autenticando tu cuenta con ${provider}...`);
          setError('');

          const data = await authService.loginOAuth(provider, code, redirectUri);

          if (data?.mfaRequired) {
            setMfaSessionToken(data.mfaSessionToken);
            return;
          }

          setAuth(data);
          useFxStore.getState().fetchUserFxBreakdown();
          navigate('/');
        } catch (err) {
          setError(err.response?.data?.message || `Error al completar el acceso con ${provider}`);
        } finally {
          setLoading(false);
          setOauthLoadingMsg('');
          sessionStorage.removeItem('oauth_provider');
          sessionStorage.removeItem('oauth_redirect_uri');
        }
      }
    };

    handleOAuthCallback();
  }, [navigate, setAuth]);

  const handleDemoLogin = async () => {
    setEmail(DEMO_CREDENTIALS.email);
    setPassword(DEMO_CREDENTIALS.password);
    try {
      setLoading(true);
      setError('');
      const data = await authService.login(DEMO_CREDENTIALS.email, DEMO_CREDENTIALS.password);
      if (data?.mfaRequired) {
        setMfaSessionToken(data.mfaSessionToken);
        return;
      }
      setAuth(data);
      useFxStore.getState().fetchUserFxBreakdown();
      navigate('/');
    } catch (err) {
      setError('Error al ingresar con la cuenta Demo');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      const data = await authService.login(email, password);
      if (data?.mfaRequired) {
        setMfaSessionToken(data.mfaSessionToken);
        return;
      }
      setAuth(data);
      useFxStore.getState().fetchUserFxBreakdown();
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {mfaSessionToken && (
        <MfaVerificationModal
          mfaSessionToken={mfaSessionToken}
          onCancel={() => setMfaSessionToken(null)}
          onSuccess={() => navigate('/')}
        />
      )}

      <div className={`glass-panel ${styles.authCard}`}>
        <h2 className={styles.authTitle}>Iniciar Sesión</h2>
        <p className={styles.authSubtitle}>Bienvenido a Cambista Online</p>

        {oauthLoadingMsg && (
          <div style={{
            background: 'rgba(59, 130, 246, 0.2)',
            border: '1px solid #3b82f6',
            color: '#93c5fd',
            padding: '0.85rem',
            borderRadius: 'var(--radius-sm, 8px)',
            marginBottom: '1rem',
            fontSize: '0.9rem',
            textAlign: 'center',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '0.5rem',
          }}>
            <span style={{ animation: 'spin 1s linear infinite' }}>🔄</span> {oauthLoadingMsg}
          </div>
        )}

        {/* Demo Credentials Helper Box */}
        <div style={{
          background: 'rgba(30, 58, 138, 0.25)',
          border: '1px solid rgba(59, 130, 246, 0.4)',
          padding: '0.9rem 1.1rem',
          borderRadius: 'var(--border-radius-sm, 8px)',
          marginBottom: '1.2rem',
          fontSize: '0.86rem',
          color: '#cbd5e1',
        }}>
          <div style={{ fontWeight: 700, color: '#60a5fa', marginBottom: '0.35rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
            <span>💡</span> Credenciales Demo (Acceso Rápido):
          </div>
          <div style={{ marginBottom: '0.15rem' }}>Email: <strong style={{ color: '#ffffff' }}>demo@cambistaonline.pe</strong></div>
          <div>Contraseña: <strong style={{ color: '#ffffff' }}>demo1234</strong></div>
          <button
            type="button"
            onClick={handleDemoLogin}
            style={{
              marginTop: '0.75rem',
              width: '100%',
              background: '#2563eb',
              color: '#ffffff',
              padding: '0.55rem',
              borderRadius: '6px',
              fontWeight: 700,
              fontSize: '0.85rem',
              border: 'none',
              cursor: 'pointer',
              boxShadow: '0 2px 8px rgba(37, 99, 235, 0.3)',
            }}
          >
            ⚡ Ingresar directamente con Demo
          </button>
        </div>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#fca5a5', padding: '0.8rem', borderRadius: 'var(--radius-sm)', marginBottom: '1rem', fontSize: '0.85rem' }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label}>Correo Electrónico</label>
            <input
              type="email"
              className={styles.input}
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label}>Contraseña</label>
            <input
              type="password"
              className={styles.input}
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          <button type="submit" className={styles.btnSubmit} disabled={loading}>
            {loading ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>

        {/* 3 Redes Sociales: Google, GitHub, Facebook */}
        <SocialAuthButtons
          onMfaRequired={(token) => setMfaSessionToken(token)}
          onError={(msg) => setError(msg)}
        />

        <p style={{ marginTop: '1rem', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          ¿No tienes una cuenta? <Link to="/register" style={{ color: 'var(--primary-500)', fontWeight: '600' }}>Regístrate gratis</Link>
        </p>
      </div>
    </>
  );
};

export default LoginForm;
