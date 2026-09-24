import React from 'react';
import { Clock } from 'lucide-react';
import styles from './TimerBadge.module.css';

export const TimerBadge = ({ seconds = 300 }) => {
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  const timeStr = `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;

  return (
    <div className={styles.timerContainer}>
      <div className={styles.pulse} />
      <Clock size={14} />
      <span>Tasa congelada: <strong className="tabular-nums">{timeStr}</strong></span>
    </div>
  );
};
