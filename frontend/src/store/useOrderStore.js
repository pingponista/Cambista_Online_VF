import { create } from 'zustand';

export const useOrderStore = create((set, get) => ({
  currentStep: 1, // 1. Cotiza y Selecciona, 2. Transfiere, 3. Recibe
  orderData: null,
  originBank: 'BCP',
  destinationBank: 'BCP',
  originAccount: '',
  destinationAccount: '',
  voucherFile: null,
  transactionNumber: '',
  orderStatus: 'En Verificación', // 'En Verificación', 'Procesando', 'Completada'
  generatedOrderId: null,

  businessAccounts: {
    BCP: { bank: 'BCP', account: '193-9821839-0-12', cci: '002-193009821839012-14', holder: 'CambistaOnline SAC' },
    INTERBANK: { bank: 'Interbank', account: '200-30018293-1', cci: '003-20000300182931-88', holder: 'CambistaOnline SAC' },
    BBVA: { bank: 'BBVA', account: '0011-0182-0100098213', cci: '011-182-000100098213-91', holder: 'CambistaOnline SAC' },
    SCOTIABANK: { bank: 'Scotiabank', account: '000-8891283', cci: '057-000-00008891283-42', holder: 'CambistaOnline SAC' },
  },

  setStep: (step) => set({ currentStep: step }),

  startOrder: (payload) => {
    set({
      orderData: payload,
      currentStep: 1,
      generatedOrderId: 'ORD-' + Math.floor(100000 + Math.random() * 900000),
    });
  },

  setBankSelection: (originBank, destinationBank, originAccount, destinationAccount) => {
    set({ originBank, destinationBank, originAccount, destinationAccount });
  },

  submitTransferProof: (txNum, file) => {
    set({
      transactionNumber: txNum,
      voucherFile: file,
      orderStatus: 'En Verificación',
      currentStep: 3,
    });
  },

  resetOrder: () => {
    set({
      currentStep: 1,
      orderData: null,
      transactionNumber: '',
      voucherFile: null,
      orderStatus: 'En Verificación',
      generatedOrderId: null,
    });
  },
}));
