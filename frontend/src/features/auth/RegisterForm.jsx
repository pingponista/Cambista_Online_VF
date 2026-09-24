import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../store/useAuthStore';
import { useFxStore } from '../../store/useFxStore';
import { SocialAuthButtons } from './SocialAuthButtons';
import styles from './auth.module.css';

export const RegisterForm = () => {
  const [profileType, setProfileType] = useState('NATURAL');
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    dni: '',
    companyName: '',
    ruc: '',
    legalRepresentativeName: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const setAuth = useAuthStore((state) => state.setAuth);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      const role = profileType === 'NATURAL' ? 'N' : 'J';
      const payload = {
        ...formData,
        profileType,
        role,
        rol: role,
      };
      const data = await authService.register(payload);
      setAuth(data);
      useFxStore.getState().fetchUserFxBreakdown();
      navigate('/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Error al registrar la cuenta');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`glass-panel ${styles.authCard}`}>
      <h2 className={styles.authTitle}>Crear Cuenta</h2>
      <p className={styles.authSubtitle}>Elige tu tipo de perfil para empezar</p>

      <div className={styles.typeTabs}>
        <button
          className={`${styles.tabBtn} ${profileType === 'NATURAL' ? styles.tabActive : ''}`}
          onClick={() => setProfileType('NATURAL')}
        >
          Persona Natural
        </button>
        <button
          className={`${styles.tabBtn} ${profileType === 'JURIDICA' ? styles.tabActive : ''}`}
          onClick={() => setProfileType('JURIDICA')}
        >
          Persona Jurídica (Empresas)
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
            name="email"
            className={styles.input}
            required
            value={formData.email}
            onChange={handleChange}
          />
        </div>

        <div className={styles.formGroup}>
          <label className={styles.label}>Contraseña</label>
          <input
            type="password"
            name="password"
            className={styles.input}
            required
            value={formData.password}
            onChange={handleChange}
          />
        </div>

        {profileType === 'NATURAL' ? (
          <>
            <div className={styles.formGroup}>
              <label className={styles.label}>Nombres</label>
              <input
                type="text"
                name="firstName"
                className={styles.input}
                required
                value={formData.firstName}
                onChange={handleChange}
              />
            </div>
            <div className={styles.formGroup}>
              <label className={styles.label}>Apellidos</label>
              <input
                type="text"
                name="lastName"
                className={styles.input}
                required
                value={formData.lastName}
                onChange={handleChange}
              />
            </div>
            <div className={styles.formGroup}>
              <label className={styles.label}>DNI (8 dígitos)</label>
              <input
                type="text"
                name="dni"
                maxLength={8}
                className={styles.input}
                required
                value={formData.dni}
                onChange={handleChange}
              />
            </div>
          </>
        ) : (
          <>
            <div className={styles.formGroup}>
              <label className={styles.label}>Razón Social</label>
              <input
                type="text"
                name="companyName"
                className={styles.input}
                required
                value={formData.companyName}
                onChange={handleChange}
              />
            </div>
            <div className={styles.formGroup}>
              <label className={styles.label}>RUC (11 dígitos - 20...)</label>
              <input
                type="text"
                name="ruc"
                maxLength={11}
                className={styles.input}
                required
                value={formData.ruc}
                onChange={handleChange}
              />
            </div>
            <div className={styles.formGroup}>
              <label className={styles.label}>Representante Legal</label>
              <input
                type="text"
                name="legalRepresentativeName"
                className={styles.input}
                required
                value={formData.legalRepresentativeName}
                onChange={handleChange}
              />
            </div>
          </>
        )}

        <button type="submit" className={styles.btnSubmit} disabled={loading}>
          {loading ? 'Creando cuenta...' : 'Registrarme'}
        </button>
      </form>

      {/* 3 Redes Sociales: Google, GitHub, Facebook */}
      <SocialAuthButtons onError={(msg) => setError(msg)} />

      <p style={{ marginTop: '1rem', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
        ¿Ya tienes cuenta? <Link to="/login" style={{ color: 'var(--primary-500)', fontWeight: '600' }}>Inicia sesión</Link>
      </p>
    </div>
  );
};
