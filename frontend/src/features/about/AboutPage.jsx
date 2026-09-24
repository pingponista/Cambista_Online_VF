import React from 'react';
import { Card } from '../../components/ui/Card/Card';
import { ShieldCheck, Zap, Award, Phone, Mail, MapPin, Clock, MessageSquare, Building2, CheckCircle2 } from 'lucide-react';
import styles from './about.module.css';

export const AboutPage = () => {
  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', padding: '2rem 1rem' }}>
      {/* Hero Section */}
      <div className={styles.heroSection}>
        <div className={styles.sbsBadge}>
          <ShieldCheck size={18} color="var(--color-accent-green)" />
          <span>Regulado por la SBS (Resolución N° 03418-2021)</span>
        </div>
        <h1 className={styles.heroTitle}>
          Cambiamos la forma de cambiar tu dinero en el Perú
        </h1>
        <p className={styles.heroSubtitle}>
          Somos la plataforma fintech líder en cambio de dólares y soles. Ofrecemos la mejor tasa en tiempo real, con la máxima seguridad bancaria y transferencias acreditadas en un promedio de 15 minutos.
        </p>
      </div>

      {/* Grid Features */}
      <div className={styles.featuresGrid}>
        <Card className={styles.featureCard}>
          <div className={styles.iconCircle}>
            <ShieldCheck size={28} color="var(--accent-blue)" />
          </div>
          <h3>Seguridad Garantizada</h3>
          <p>
            Tus fondos están 100% respaldados por el sistema financiero. Cumplimos rigurosamente con los protocolos anti-lavado de activos de la UIF y la SBS.
          </p>
        </Card>

        <Card className={styles.featureCard}>
          <div className={styles.iconCircle}>
            <Zap size={28} color="var(--color-accent-green)" />
          </div>
          <h3>Transferencias en 15 minutos</h3>
          <p>
            Procesamos tus cambios de manera inmediata con cuentas directas en BCP, Interbank, BBVA y Scotiabank en soles y dólares.
          </p>
        </Card>

        <Card className={styles.featureCard}>
          <div className={styles.iconCircle}>
            <Award size={28} color="var(--accent-yellow)" />
          </div>
          <h3>El Mejor Tipo de Cambio</h3>
          <p>
            Nuestro motor dinámico analiza las variaciones del mercado interbancario de la SBS para brindarte la menor brecha entre compra y venta.
          </p>
        </Card>
      </div>

      {/* About Description */}
      <Card style={{ marginBottom: '2.5rem', padding: '2rem' }}>
        <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          <Building2 size={24} color="var(--accent-blue)" />
          Quiénes Somos
        </h2>
        <p style={{ color: 'var(--text-muted)', lineHeight: '1.7', marginBottom: '1rem' }}>
          <strong>CambistaOnline SAC</strong> nació con el objetivo de digitalizar y hacer transparente el mercado de divisas en el Perú. Eliminamos los riesgos de cambiar dinero en la calle y la altas comisiones de los bancos tradicionales.
        </p>
        <p style={{ color: 'var(--text-muted)', lineHeight: '1.7' }}>
          Contamos con una infraestructura tecnológica de grado bancario con cifrado SSL de 256 bits y alianzas estratégicas con las principales entidades financieras del país, procesando más de S/ 500 millones de soles en operaciones para clientes personas y empresas.
        </p>

        <div style={{ marginTop: '1.5rem', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', fontWeight: 600 }}>
            <CheckCircle2 size={18} color="var(--color-accent-green)" /> Cero comisiones ocultas
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', fontWeight: 600 }}>
            <CheckCircle2 size={18} color="var(--color-accent-green)" /> Programa CambiPuntos ⭐
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem', fontWeight: 600 }}>
            <CheckCircle2 size={18} color="var(--color-accent-green)" /> Comprobante y boleta legal
          </div>
        </div>
      </Card>

      {/* Support & Contact Section */}
      <Card style={{ padding: '2rem' }}>
        <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
          <Phone size={24} color="var(--accent-blue)" />
          Central de Soporte y Atención al Cliente
        </h2>

        <div className={styles.contactGrid}>
          <div className={styles.contactItem}>
            <MessageSquare size={20} color="var(--color-accent-green)" />
            <div>
              <strong>WhatsApp de Atención:</strong>
              <div>+51 987 654 321</div>
            </div>
          </div>

          <div className={styles.contactItem}>
            <Phone size={20} color="var(--accent-blue)" />
            <div>
              <strong>Central Telefónica:</strong>
              <div>+51 (01) 708-9900</div>
            </div>
          </div>

          <div className={styles.contactItem}>
            <Mail size={20} color="var(--accent-blue)" />
            <div>
              <strong>Correo Institucional:</strong>
              <div>soporte@cambistaonline.pe</div>
            </div>
          </div>

          <div className={styles.contactItem}>
            <Clock size={20} color="var(--accent-blue)" />
            <div>
              <strong>Horarios de Atención:</strong>
              <div>Lun - Vie: 8:00 AM - 7:00 PM</div>
              <div>Sábados: 9:00 AM - 2:30 PM</div>
            </div>
          </div>
        </div>

        <div style={{ marginTop: '1.8rem', paddingTop: '1.2rem', borderTop: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '0.75rem', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
          <MapPin size={20} color="var(--accent-blue)" />
          <span><strong>Oficina Principal:</strong> Av. Víctor Andrés Belaunde 147, Real Empresarial, San Isidro, Lima - Perú.</span>
        </div>
      </Card>
    </div>
  );
};
