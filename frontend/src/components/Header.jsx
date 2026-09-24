import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { RefreshCw, User, LogOut } from 'lucide-react';
import { useAuthStore } from '../store/useAuthStore';
import styles from './Header.module.css';

export const Header = () => {
  const { isAuthenticated, user, profileType, logout } = useAuthStore();
  const navigate = useNavigate();

  const isNatural = (user?.profileType || profileType) === 'NATURAL';

  return (
    <header className={styles.header}>
      <div className={`container ${styles.headerContainer}`}>
        <Link to="/" className={styles.logo}>
          <div className={styles.logoBadge}>
            <RefreshCw size={20} />
          </div>
          <span>Cambista<span style={{ color: 'var(--primary-500, #10b981)' }}>Online</span></span>
        </Link>

        <nav className={styles.nav}>
          <Link to="/" className={styles.navLink}>Cotizador</Link>
          <Link to="/nosotros" className={styles.navLink}>Nosotros</Link>
          {(!isAuthenticated || !isNatural) && (
            <Link to="/empresas" className={styles.navLink}>Empresas (RUC 20)</Link>
          )}
        </nav>

        <div className={styles.userMenu}>
          {isAuthenticated ? (
            <>
              <button onClick={() => navigate('/dashboard')} className={`${styles.btnAuth} ${styles.btnLogin}`}>
                <User size={15} style={{ marginRight: '4px', verticalAlign: 'middle' }} />
                {user?.name || 'Mi Cuenta'}
              </button>
              <button onClick={logout} className={`${styles.btnAuth} ${styles.btnLogin}`} title="Cerrar Sesión">
                <LogOut size={15} />
              </button>
            </>
          ) : (
            <>
              <button onClick={() => navigate('/login')} className={`${styles.btnAuth} ${styles.btnLogin}`}>
                Iniciar Sesión
              </button>
              <button onClick={() => navigate('/register')} className={`${styles.btnAuth} ${styles.btnRegister}`}>
                Registrarse
              </button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};
