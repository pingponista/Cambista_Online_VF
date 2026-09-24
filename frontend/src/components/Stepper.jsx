import React from 'react';
import styles from './Stepper.module.css';

export const Stepper = ({ currentStep = 1 }) => {
  const steps = [
    { number: 1, label: '1. Cotiza' },
    { number: 2, label: '2. Transfiere' },
    { number: 3, label: '3. Recibe' },
  ];

  return (
    <div className={styles.stepperContainer}>
      {steps.map((step, idx) => (
        <React.Fragment key={step.number}>
          <div className={`${styles.stepItem} ${currentStep >= step.number ? styles.stepActive : ''}`}>
            <div className={styles.stepNumber}>{step.number}</div>
            <span className={styles.stepLabel}>{step.label}</span>
          </div>
          {idx < steps.length - 1 && <div className={styles.stepDivider} />}
        </React.Fragment>
      ))}
    </div>
  );
};
