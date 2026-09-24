import React, { useState } from 'react';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../store/useAuthStore';
import { useFxStore } from '../../store/useFxStore';
import { useNavigate } from 'react-router-dom';
import styles from './auth.module.css';

export const SocialAuthButtons = ({ onMfaRequired, onError }) => {
  const [loadingProvider, setLoadingProvider] = useState(null);
  const [showConfigHelp, setShowConfigHelp] = useState(null);
  const setAuth = useAuthStore((state) => state.setAuth);
  const navigate = useNavigate();

  const getClientId = (provider) => {
    switch (provider) {
      case 'GOOGLE':
        return import.meta.env.VITE_OAUTH_GOOGLE_CLIENT_ID || '';
      case 'GITHUB':
        return import.meta.env.VITE_OAUTH_GITHUB_CLIENT_ID || '';
      case 'FACEBOOK':
        return import.meta.env.VITE_OAUTH_FACEBOOK_APP_ID || '';
      default:
        return '';
    }
  };

  const executeMockLogin = async (provider) => {
    setShowConfigHelp(null);
    try {
      setLoadingProvider(provider);
      if (onError) onError('');

      const mockCode = `mock_usuario.${provider.toLowerCase()}@${provider.toLowerCase()}.com`;
      const redirectUri = `${window.location.origin}/login`;

      const res = await authService.loginOAuth(provider, mockCode, redirectUri);

      if (res?.mfaRequired) {
        if (onMfaRequired) onMfaRequired(res.mfaSessionToken);
        return;
      }

      setAuth(res);
      useFxStore.getState().fetchUserFxBreakdown();
      navigate('/');
    } catch (err) {
      const msg = err.response?.data?.message || `Error al autenticar con ${provider}`;
      if (onError) onError(msg);
    } finally {
      setLoadingProvider(null);
    }
  };

  const handleSocialClick = (provider) => {
    const clientId = getClientId(provider);
    const redirectUri = `${window.location.origin}/login`;

    // Si NO está configurado el Client ID en .env, ofrecemos la ayuda y opción de prueba
    if (!clientId || clientId.trim() === '' || clientId.includes('placeholder')) {
      setShowConfigHelp(provider);
      return;
    }

    // Si está configurado, redireccionamos a la ventana oficial del proveedor
    sessionStorage.setItem('oauth_provider', provider);
    sessionStorage.setItem('oauth_redirect_uri', redirectUri);

    let authUrl = '';
    if (provider === 'GOOGLE') {
      authUrl = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&response_type=code&scope=openid%20profile%20email&access_type=offline&prompt=select_account`;
    } else if (provider === 'GITHUB') {
      authUrl = `https://github.com/login/oauth/authorize?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=user:email`;
    } else if (provider === 'FACEBOOK') {
      authUrl = `https://www.facebook.com/v19.0/dialog/oauth?client_id=${clientId}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=email,public_profile&response_type=code`;
    }

    if (authUrl) {
      window.location.href = authUrl;
    }
  };

  return (
    <div style={{ marginTop: '1.2rem', marginBottom: '1.2rem' }}>
      {/* Modal de Ayuda cuando falta Client ID en .env */}
      {showConfigHelp && (
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
          <div style={{
            background: '#1e293b',
            border: '1px solid rgba(255, 255, 255, 0.15)',
            borderRadius: '12px',
            padding: '1.5rem',
            maxWidth: '440px',
            width: '100%',
            color: '#f8fafc',
          }}>
            <h3 style={{ fontSize: '1.15rem', fontWeight: 700, margin: '0 0 0.75rem 0', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              ⚙️ Configurar {showConfigHelp} OAuth
            </h3>
            <p style={{ fontSize: '0.85rem', color: '#94a3b8', lineHeight: 1.5, margin: '0 0 1rem 0' }}>
              Para conectar con tu cuenta real de <strong>{showConfigHelp}</strong>, agrega tu credencial en el archivo <code>frontend/.env</code>:
            </p>

            <div style={{
              background: '#0f172a',
              padding: '0.75rem',
              borderRadius: '6px',
              fontFamily: 'monospace',
              fontSize: '0.8rem',
              color: '#38bdf8',
              marginBottom: '1.2rem',
              wordBreak: 'break-all',
            }}>
              {showConfigHelp === 'GOOGLE' && 'VITE_OAUTH_GOOGLE_CLIENT_ID=tu-client-id.apps.googleusercontent.com'}
              {showConfigHelp === 'GITHUB' && 'VITE_OAUTH_GITHUB_CLIENT_ID=tu-github-client-id'}
              {showConfigHelp === 'FACEBOOK' && 'VITE_OAUTH_FACEBOOK_APP_ID=tu-facebook-app-id'}
            </div>

            <p style={{ fontSize: '0.82rem', color: '#cbd5e1', marginBottom: '1.2rem' }}>
              ¿Aún no tienes las claves? Puedes ingresar de inmediato con una cuenta de prueba para verificar el flujo:
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              <button
                type="button"
                onClick={() => executeMockLogin(showConfigHelp)}
                style={{
                  background: '#2563eb',
                  color: '#ffffff',
                  padding: '0.65rem',
                  borderRadius: '6px',
                  border: 'none',
                  fontWeight: 600,
                  fontSize: '0.88rem',
                  cursor: 'pointer',
                }}
              >
                ⚡ Probar con cuenta simulada de {showConfigHelp}
              </button>
              <button
                type="button"
                onClick={() => setShowConfigHelp(null)}
                style={{
                  background: 'transparent',
                  color: '#94a3b8',
                  padding: '0.5rem',
                  borderRadius: '6px',
                  border: '1px solid rgba(255, 255, 255, 0.1)',
                  cursor: 'pointer',
                  fontSize: '0.82rem',
                }}
              >
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}

      <div className={styles.socialDivider}>
        <div className={styles.socialDividerLine} />
        <span className={styles.socialDividerText}>
          O continúa con
        </span>
        <div className={styles.socialDividerLine} />
      </div>

      <div className={styles.socialGrid}>
        {/* Google */}
        <button
          type="button"
          onClick={() => handleSocialClick('GOOGLE')}
          disabled={!!loadingProvider}
          title="Iniciar sesión oficial con Google"
          className={`${styles.socialBtn} ${styles.socialBtnGoogle}`}
        >
          <div className={styles.socialIconBox}>
            <svg width="20" height="20" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
            </svg>
          </div>
          <span>{loadingProvider === 'GOOGLE' ? '...' : 'Google'}</span>
        </button>

        {/* GitHub */}
        <button
          type="button"
          onClick={() => handleSocialClick('GITHUB')}
          disabled={!!loadingProvider}
          title="Iniciar sesión oficial con GitHub"
          className={`${styles.socialBtn} ${styles.socialBtnGithub}`}
        >
          <div className={styles.socialIconBox}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="#ffffff">
              <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
            </svg>
          </div>
          <span>{loadingProvider === 'GITHUB' ? '...' : 'GitHub'}</span>
        </button>

        {/* Facebook */}
        <button
          type="button"
          onClick={() => handleSocialClick('FACEBOOK')}
          disabled={!!loadingProvider}
          title="Iniciar sesión oficial con Facebook"
          className={`${styles.socialBtn} ${styles.socialBtnFacebook}`}
        >
          <div className={styles.socialIconBox}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="#1877F2">
              <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
            </svg>
          </div>
          <span>{loadingProvider === 'FACEBOOK' ? '...' : 'Facebook'}</span>
        </button>
      </div>
    </div>
  );
};

export default SocialAuthButtons;
