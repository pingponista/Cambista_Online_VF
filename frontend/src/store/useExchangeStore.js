import { create } from 'zustand';

export const useExchangeStore = create((set, get) => ({
  originCurrency: 'USD',
  targetCurrency: 'PEN',
  originAmount: '1000',
  targetAmount: '3735.00',
  operationType: 'BUY', // BUY = Envías USD/EUR, Recibes PEN. SELL = Envías PEN, Recibes USD/EUR.
  
  buyRate: 3.7350,
  sellRate: 3.7650,
  
  timerSeconds: 300,
  isTimerActive: true,
  currentStep: 1, // 1: Cotiza, 2: Transfiere, 3: Recibe
  activeOrder: null,

  setOperationType: (type) => {
    const isBuy = type === 'BUY';
    set({
      operationType: type,
      originCurrency: isBuy ? 'USD' : 'PEN',
      targetCurrency: isBuy ? 'PEN' : 'USD',
    });
    get().recalculate();
  },

  setOriginAmount: (amount) => {
    set({ originAmount: amount });
    get().recalculate();
  },

  setRates: (buy, sell) => {
    set({ buyRate: buy, sellRate: sell });
    get().recalculate();
  },

  setStep: (step) => set({ currentStep: step }),
  setActiveOrder: (order) => set({ activeOrder: order }),

  recalculate: () => {
    const { originAmount, operationType, buyRate, sellRate } = get();
    const numAmount = parseFloat(originAmount) || 0;

    let calculated = 0;
    if (operationType === 'BUY') {
      calculated = numAmount * buyRate;
    } else {
      calculated = numAmount / sellRate;
    }

    set({ targetAmount: calculated.toFixed(2) });
  },

  decrementTimer: () => {
    const current = get().timerSeconds;
    if (current > 0) {
      set({ timerSeconds: current - 1 });
    } else {
      set({ timerSeconds: 300 }); // reset cycle
    }
  },
}));
