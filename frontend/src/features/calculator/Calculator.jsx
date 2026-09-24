import React, { useEffect } from 'react';
import { ArrowDownUp, TrendingUp, ShieldCheck, ChevronDown, ChevronUp } from 'lucide-react';
import { useFxStore } from '../../store/useFxStore';
import { useOrderStore } from '../../store/useOrderStore';
import { useAuthStore } from '../../store/useAuthStore';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { Badge } from '../../components/ui/Badge/Badge';
import { TimerBadge } from '../../components/ui/TimerBadge/TimerBadge';
import { CurrencySelector } from '../../components/ui/CurrencySelector/CurrencySelector';
import { useNavigate } from 'react-router-dom';
import styles from './calculator.module.css';

export const Calculator = () => {
  const {
    originAmount,
    targetAmount,
    originCurrency,
    targetCurrency,
    operationType,
    buyRate,
    sellRate,
    timerSeconds,
    userPointsBalance,
    redeemedPoints,
    showBreakdown,
    baseSbsRate,
    spreadRate,
    timeAdjRate,
    seasonalAdjRate,
    setOperationType,
    setOriginCurrency,
    setTargetCurrency,
    setOriginAmount,
    setRedeemedPoints,
    toggleBreakdown,
    lockRate,
    decrementTimer,
    calculateSavings,
    calculateEffectiveRate,
    fetchUserFxBreakdown,
  } = useFxStore();

  const { startOrder } = useOrderStore();
  const { isAuthenticated } = useAuthStore();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      fetchUserFxBreakdown();
    }
  }, [isAuthenticated, fetchUserFxBreakdown]);

  useEffect(() => {
    const timer = setInterval(() => {
      decrementTimer();
    }, 1000);
    return () => clearInterval(timer);
  }, [decrementTimer]);

  const effectiveRate = calculateEffectiveRate();
  const pointImprovement = (redeemedPoints / 100) * 0.0010;
  const savings = calculateSavings();

  const handleStartChange = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    lockRate();
    startOrder({
      originAmount: parseFloat(originAmount),
      originCurrency,
      targetAmount: parseFloat(targetAmount),
      targetCurrency,
      appliedRate: effectiveRate,
      operationType,
      pointsToRedeem: redeemedPoints || 0,
    });
    navigate('/exchange');
  };

  return (
    <Card className={styles.calculatorCard}>
      {/* Top Banner for Logged In Users */}
      {isAuthenticated && (
        <div className={styles.promoBanner}>
          <span>🎉</span>
          <span><strong>Día valle:</strong> Promoción por baja demanda (-0.001)</span>
        </div>
      )}

      <div className={styles.rateHeader}>
        <TimerBadge seconds={timerSeconds} />
        <div className={styles.liveRateBadge}>
          <div className={styles.pulseDot} />
          <span>Tasa en vivo: <strong className="tabular-nums">{effectiveRate.toFixed(4)}</strong></span>
        </div>
      </div>

      <div className={styles.calculatorGrid}>
        {/* Left Column: Operations & Currency Inputs */}
        <div className={styles.calcLeftCol}>
          <div className={styles.operationTabs}>
            <button
              type="button"
              className={`${styles.tabBtn} ${operationType === 'BUY' ? styles.tabActive : ''}`}
              onClick={() => setOperationType('BUY')}
            >
              <span>Comprar Dólar</span>
              <span className="tabular-nums" style={{ fontSize: '0.8rem', opacity: 0.8 }}>S/ {buyRate.toFixed(4)}</span>
            </button>

            <button
              type="button"
              className={`${styles.tabBtn} ${operationType === 'SELL' ? styles.tabActive : ''}`}
              onClick={() => setOperationType('SELL')}
            >
              <span>Vender Dólar</span>
              <span className="tabular-nums" style={{ fontSize: '0.8rem', opacity: 0.8 }}>S/ {sellRate.toFixed(4)}</span>
            </button>
          </div>

          <div className={styles.inputCard}>
            <div>
              <span className={styles.inputLabel}>Tú envías</span>
              <input
                type="number"
                className={`${styles.amountInput} tabular-nums`}
                value={originAmount}
                onChange={(e) => setOriginAmount(e.target.value)}
              />
            </div>
            <CurrencySelector
              value={originCurrency}
              onChange={setOriginCurrency}
              options={['USD', 'PEN', 'EUR']}
            />
          </div>

          <div className={styles.swapContainer}>
            <button
              type="button"
              className={styles.swapBtn}
              onClick={() => setOperationType(operationType === 'BUY' ? 'SELL' : 'BUY')}
              title="Invertir monedas"
            >
              <ArrowDownUp size={18} />
            </button>
          </div>

          <div className={styles.inputCard}>
            <div>
              <span className={styles.inputLabel}>Tú recibes</span>
              <input
                type="text"
                readOnly
                className={`${styles.amountInput} tabular-nums`}
                value={targetAmount}
                style={{ color: 'var(--color-accent-green-hover)' }}
              />
            </div>
            <CurrencySelector
              value={targetCurrency}
              onChange={setTargetCurrency}
              options={['PEN', 'USD', 'EUR']}
            />
          </div>

          <div className={styles.savingsWrapper}>
            <Badge variant="savings">
              <TrendingUp size={14} />
              <span>Ahorras aprox. <strong className="tabular-nums">S/ {savings}</strong> vs. el banco</span>
            </Badge>
          </div>

          <Button variant="primary" size="lg" onClick={handleStartChange}>
            Iniciar Cambio
          </Button>

          {isAuthenticated ? (
            <div className={styles.pointsEarnNotice}>
              Ganarás <strong>10 CambiPuntos</strong> con esta operación
            </div>
          ) : (
            <div style={{ marginTop: '1.2rem', textAlign: 'center', fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.4rem' }}>
              <ShieldCheck size={14} color="var(--color-accent-green)" />
              <span>Operación segura garantizada por la SBS</span>
            </div>
          )}
        </div>

        {/* Right Column: CambiPuntos & Breakdown Panel */}
        <div className={styles.calcRightCol}>
          {/* CambiPuntos Slider for Logged In Users */}
          {isAuthenticated && (
            <div className={styles.pointsCard}>
              <div className={styles.pointsHeader}>
                <span>Canjear CambiPuntos ⭐</span>
                <span className="tabular-nums">Saldo: {userPointsBalance} pts</span>
              </div>

              <input
                type="range"
                min="0"
                max={userPointsBalance}
                step="10"
                value={redeemedPoints}
                onChange={(e) => setRedeemedPoints(parseInt(e.target.value) || 0)}
                className={styles.pointsSlider}
              />

              <div className={styles.pointsFooter}>
                <span className="tabular-nums">{redeemedPoints} pts canjeados</span>
                <span className="tabular-nums">Mejora: +{pointImprovement.toFixed(4)}</span>
              </div>
            </div>
          )}

          {/* Exchange Rate Breakdown Section for Logged In Users */}
          {isAuthenticated && (
            <div>
              <button type="button" className={styles.breakdownToggle} onClick={toggleBreakdown}>
                {showBreakdown ? <ChevronDown size={16} /> : <ChevronUp size={16} />}
                <span>Ver desglose del tipo de cambio</span>
              </button>

              {showBreakdown && (
                <div className={styles.breakdownBox}>
                  <div className={styles.breakdownRow}>
                    <span>TC base (SBS)</span>
                    <span className="tabular-nums">{baseSbsRate.toFixed(4)}</span>
                  </div>
                  <div className={styles.breakdownRow}>
                    <span>Spread (nivel PREFERENTE)</span>
                    <span className="tabular-nums">{spreadRate.toFixed(4)}</span>
                  </div>
                  <div className={styles.breakdownRow}>
                    <span>Ajuste horario</span>
                    <span className="tabular-nums">{timeAdjRate.toFixed(4)}</span>
                  </div>
                  <div className={styles.breakdownRow}>
                    <span>Ajuste estacional</span>
                    <span className="tabular-nums">{seasonalAdjRate >= 0 ? `+${seasonalAdjRate.toFixed(4)}` : seasonalAdjRate.toFixed(4)}</span>
                  </div>
                  <div className={styles.breakdownRow} style={{ color: redeemedPoints > 0 ? 'var(--accent-purple, #a78bfa)' : 'inherit' }}>
                    <span>Canje puntos</span>
                    <span className="tabular-nums">-{pointImprovement.toFixed(4)}</span>
                  </div>

                  <div className={styles.breakdownDivider} />

                  <div className={styles.breakdownTotal}>
                    <span>TC final</span>
                    <span className="tabular-nums" style={{ color: 'var(--color-accent-green)' }}>{effectiveRate.toFixed(4)}</span>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </Card>
  );
};
