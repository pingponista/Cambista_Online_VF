import React from 'react';
import { useOrderStore } from '../../store/useOrderStore';
import { Step1Accounts } from './Step1Accounts';
import { Step2Transfer } from './Step2Transfer';
import { Step3Receipt } from './Step3Receipt';
import styles from './exchange.module.css';

export const ExchangeFlow = () => {
  const { currentStep } = useOrderStore();

  const steps = [
    { number: 1, label: '1. Cotiza y Selecciona' },
    { number: 2, label: '2. Transfiere' },
    { number: 3, label: '3. Recibe tu dinero' },
  ];

  return (
    <div className="container" style={{ padding: '3rem 0' }}>
      <div className={styles.stepperBar}>
        {steps.map((step, idx) => (
          <React.Fragment key={step.number}>
            <div className={`${styles.stepItem} ${currentStep >= step.number ? styles.stepActive : ''}`}>
              <div className={styles.stepBadge}>{step.number}</div>
              <span className={styles.stepText}>{step.label}</span>
            </div>
            {idx < steps.length - 1 && <div className={styles.stepDivider} />}
          </React.Fragment>
        ))}
      </div>

      {currentStep === 1 && <Step1Accounts />}
      {currentStep === 2 && <Step2Transfer />}
      {currentStep === 3 && <Step3Receipt />}
    </div>
  );
};
