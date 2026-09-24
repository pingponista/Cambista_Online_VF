import React, { useEffect } from 'react';
import { Clock } from 'lucide-react';
import { useExchangeStore } from '../store/useExchangeStore';
import styles from './TimerPill.module.css';

export const TimerPill = () => {
  const { timerSeconds, decrementTimer } = useExchangeStore();

  useEffect(() => {
    const interval = setInterval(() => {
      decrementTimer();
    }, 1000);
    return () => clearInterval(interval);
  }, [decrementTimer]);

  const minutes = Math.floor(timerSeconds / 60);
  const seconds = timerSeconds % 60;
  const formattedTime = `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

  return (
    <div className={styles.timerPill}>
      <div className={styles.pulseDot} />
      <Clock size={14} />
      <span>Tasa garantizada por: {formattedTime}</span>
    </div>
  );
};
