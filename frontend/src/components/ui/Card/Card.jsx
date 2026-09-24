import React from 'react';
import styles from './Card.module.css';

export const Card = ({ children, hoverable = false, className = '', style = {} }) => {
  return (
    <div
      className={`${styles.card} ${hoverable ? styles.hoverable : ''} ${className}`}
      style={style}
    >
      {children}
    </div>
  );
};
