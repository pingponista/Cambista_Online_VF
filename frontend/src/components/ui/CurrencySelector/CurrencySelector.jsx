import React from 'react';
import styles from './CurrencySelector.module.css';

const FLAG_EMOJIS = {
  USD: '🇺🇸',
  EUR: '🇪🇺',
  PEN: '🇵🇪',
};

export const CurrencySelector = ({ value = 'USD', onChange, options = ['USD', 'PEN', 'EUR'] }) => {
  return (
    <div className={styles.selectWrapper}>
      <span style={{ fontSize: '1.2rem' }}>{FLAG_EMOJIS[value] || '🌐'}</span>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className={styles.selectInput}
      >
        {options.map((opt) => (
          <option key={opt} value={opt}>
            {opt}
          </option>
        ))}
      </select>
    </div>
  );
};
