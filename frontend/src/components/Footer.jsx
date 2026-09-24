import React from 'react';
import { ShieldCheck, Lock } from 'lucide-react';
import styles from './Footer.module.css';

export const Footer = () => {
  return (
    <footer className={styles.footer}>
      <div className="container">
        <div className={styles.footerGrid}>
          <div>
            <h4 className={styles.footerTitle}>Cambista Online</h4>
            <p style={{ maxWidth: '300px', lineHeight: '1.6' }}>
              Plataforma fintech regulada por la SBS para el intercambio seguro y transparente de dólares, euros y soles.
            </p>
          </div>

          <div>
            <h4 className={styles.footerTitle}>Servicios</h4>
            <ul className={styles.footerLinks}>
              <li><a href="#">Cambio Persona Natural</a></li>
              <li><a href="#">Cambio Persona Jurídica</a></li>
              <li><a href="#">API de Cotización</a></li>
            </ul>
          </div>

          <div>
            <h4 className={styles.footerTitle}>Legal</h4>
            <ul className={styles.footerLinks}>
              <li><a href="#">Términos y Condiciones</a></li>
              <li><a href="#">Política de Privacidad</a></li>
              <li><a href="#">Libro de Reclamaciones</a></li>
            </ul>
          </div>

          <div>
            <h4 className={styles.footerTitle}>Seguridad Garantizada</h4>
            <div style={{ display: 'flex', gap: '0.8rem', alignItems: 'center', marginTop: '0.5rem' }}>
              <ShieldCheck color="var(--primary-500)" size={28} />
              <span>Registrado en la SBS - Res. N° 0231-2022</span>
            </div>
          </div>
        </div>

        <div className={styles.footerBottom}>
          <p>© 2026 Cambista Online. Todos los derechos reservados.</p>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Lock size={14} color="var(--primary-500)" />
            <span>Encriptación Bancaria 256-bit SSL</span>
          </div>
        </div>
      </div>
    </footer>
  );
};
