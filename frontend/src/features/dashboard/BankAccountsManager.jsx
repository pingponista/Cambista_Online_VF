import React, { useState } from 'react';
import { Card } from '../../components/ui/Card/Card';
import { Button } from '../../components/ui/Button/Button';
import { CreditCard, Plus, Trash2 } from 'lucide-react';
import styles from './dashboard.module.css';

export const BankAccountsManager = () => {
  const [accounts, setAccounts] = useState([
    { id: 1, bank: 'BCP', type: 'Ahorros', currency: 'USD', number: '193-98129381-0-12', cci: '002193009812938101214' },
    { id: 2, bank: 'Interbank', type: 'Corriente', currency: 'PEN', number: '200-30018293-1', cci: '0032000030018293188' },
  ]);

  const [showAddForm, setShowAddForm] = useState(false);
  const [newAcc, setNewAcc] = useState({
    bank: 'BCP',
    type: 'Ahorros',
    currency: 'USD',
    number: '',
    cci: '',
  });

  const handleAddAccount = (e) => {
    e.preventDefault();
    if (!newAcc.number || !newAcc.cci) return;
    setAccounts([...accounts, { ...newAcc, id: Date.now() }]);
    setShowAddForm(false);
    setNewAcc({ bank: 'BCP', type: 'Ahorros', currency: 'USD', number: '', cci: '' });
  };

  const handleDelete = (id) => {
    setAccounts(accounts.filter((a) => a.id !== id));
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
        <h3 style={{ fontSize: '1.3rem', fontWeight: 800 }}>Mis Cuentas Bancarias</h3>
        <Button variant="primary" size="sm" onClick={() => setShowAddForm(!showAddForm)}>
          <Plus size={16} /> Registrar Cuenta
        </Button>
      </div>

      {showAddForm && (
        <Card style={{ marginBottom: '2rem', background: 'var(--bg-dark-card)', border: '1px solid var(--border-color)' }}>
          <h4 style={{ marginBottom: '1rem', fontWeight: 700 }}>Agregar Nueva Cuenta Bancaria</h4>
          <form onSubmit={handleAddAccount} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>Banco</label>
              <select
                value={newAcc.bank}
                onChange={(e) => setNewAcc({ ...newAcc, bank: e.target.value })}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-surface)', color: 'var(--text-main)' }}
              >
                <option value="BCP">BCP</option>
                <option value="Interbank">Interbank</option>
                <option value="BBVA">BBVA</option>
                <option value="Scotiabank">Scotiabank</option>
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>Moneda</label>
              <select
                value={newAcc.currency}
                onChange={(e) => setNewAcc({ ...newAcc, currency: e.target.value })}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-surface)', color: 'var(--text-main)' }}
              >
                <option value="USD">Dólares (USD)</option>
                <option value="PEN">Soles (PEN)</option>
                <option value="EUR">Euros (EUR)</option>
              </select>
            </div>

            <div>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>Número de Cuenta</label>
              <input
                type="text"
                required
                placeholder="Ej: 193-98129381-0-12"
                value={newAcc.number}
                onChange={(e) => setNewAcc({ ...newAcc, number: e.target.value })}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-surface)', color: 'var(--text-main)' }}
                className="tabular-nums"
              />
            </div>

            <div>
              <label style={{ fontSize: '0.8rem', fontWeight: 600, display: 'block', marginBottom: '0.3rem' }}>CCI Interbancario (20 dígitos)</label>
              <input
                type="text"
                required
                placeholder="Ej: 002193009812938101214"
                value={newAcc.cci}
                onChange={(e) => setNewAcc({ ...newAcc, cci: e.target.value })}
                style={{ width: '100%', padding: '0.75rem', borderRadius: 'var(--border-radius-sm)', border: '1px solid var(--border-color)', background: 'var(--bg-surface)', color: 'var(--text-main)' }}
                className="tabular-nums"
              />
            </div>

            <div style={{ gridColumn: '1 / -1', display: 'flex', gap: '0.5rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
              <Button variant="outline" size="sm" onClick={() => setShowAddForm(false)}>Cancelar</Button>
              <Button variant="primary" size="sm" type="submit">Guardar Cuenta</Button>
            </div>
          </form>
        </Card>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '1.2rem' }}>
        {accounts.map((acc) => (
          <Card key={acc.id} hoverable style={{ position: 'relative' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.8rem' }}>
              <strong style={{ fontSize: '1.1rem', color: 'var(--text-main)' }}>{acc.bank} ({acc.currency})</strong>
              <button onClick={() => handleDelete(acc.id)} style={{ color: 'var(--color-accent-red)', opacity: 0.7 }}>
                <Trash2 size={16} />
              </button>
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.4rem' }}>
              Cuenta: <span className="tabular-nums" style={{ color: 'var(--text-main)', fontWeight: 700 }}>{acc.number}</span>
            </div>
            <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              CCI: <span className="tabular-nums" style={{ color: 'var(--text-main)' }}>{acc.cci}</span>
            </div>
          </Card>
        ))}
      </div>
    </div>
  );
};
