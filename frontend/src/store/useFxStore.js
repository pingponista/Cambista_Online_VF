import { create } from 'zustand';
import { exchangeService } from '../services/exchangeService';


export const useFxStore = create((set, get) => ({
  originCurrency: 'USD',
  targetCurrency: 'PEN',
  originAmount: '1000',
  targetAmount: '3735.00',
  operationType: 'BUY', // BUY = Compra (envías USD/EUR, recibes PEN), SELL = Venta (envías PEN, recibes USD/EUR)

  buyRate: 3.7350,
  sellRate: 3.7650,
  bankRate: 3.7100, // Tasa bancaria de referencia para cálculo de ahorro

  // Points & Breakdown State
  userPointsBalance: 320,
  redeemedPoints: 0,
  showBreakdown: true,
  baseSbsRate: 3.7565,
  spreadRate: 0.0100,
  timeAdjRate: 0.0050,
  seasonalAdjRate: -0.0010,
  promoDiscount: -0.0010,
  promoName: "Día valle: Promoción por baja demanda (-0.001)",
  earnPointsPerOrder: 10,
  isBreakdownLoaded: false,

  isRateLocked: false,
  lockedRate: null,
  timerSeconds: 300,

  fetchUserFxBreakdown: async () => {
    try {
      const savedUser = localStorage.getItem('cambista_user');
      let userEmail = null;
      if (savedUser) {
        try { userEmail = JSON.parse(savedUser).email; } catch (e) {}
      }
      const data = await exchangeService.getUserFxBreakdown(userEmail);
      set({
        userPointsBalance: data.userPointsBalance,
        baseSbsRate: data.baseSbsRate,
        spreadRate: data.spreadRate,
        timeAdjRate: data.timeAdjRate,
        seasonalAdjRate: data.seasonalAdjRate,
        promoDiscount: data.promoDiscount,
        promoName: data.promoName,
        earnPointsPerOrder: data.earnPointsPerOrder,
        isBreakdownLoaded: true,
      });
      get().recalculate();
    } catch (err) {
      console.warn('Could not fetch breakdown from server, using fallback values:', err);
    }
  },

  setRedeemedPoints: (points) => {
    set({ redeemedPoints: points });
    get().recalculate();
  },


  toggleBreakdown: () => set((state) => ({ showBreakdown: !state.showBreakdown })),

  setRates: (buy, sell) => {
    set({ buyRate: buy, sellRate: sell });
    get().recalculate();
  },

  setOperationType: (type) => {
    const isBuy = type === 'BUY';
    set({
      operationType: type,
      originCurrency: isBuy ? 'USD' : 'PEN',
      targetCurrency: isBuy ? 'PEN' : 'USD',
    });
    get().recalculate();
  },

  setOriginCurrency: (curr) => {
    set({ originCurrency: curr });
    get().recalculate();
  },

  setTargetCurrency: (curr) => {
    set({ targetCurrency: curr });
    get().recalculate();
  },

  setOriginAmount: (val) => {
    set({ originAmount: val });
    get().recalculate();
  },

  calculateEffectiveRate: () => {
    const { operationType, buyRate, sellRate, redeemedPoints } = get();
    const baseRate = operationType === 'BUY' ? buyRate : sellRate;
    const pointBonus = (redeemedPoints / 100) * 0.0010;
    return operationType === 'BUY' ? baseRate + pointBonus : baseRate - pointBonus;
  },

  recalculate: () => {
    const { originAmount, operationType } = get();
    const num = parseFloat(originAmount) || 0;
    const rate = get().calculateEffectiveRate();

    let res = 0;
    if (operationType === 'BUY') {
      res = num * rate;
    } else {
      res = num / rate;
    }

    set({ targetAmount: res.toFixed(2) });
  },

  lockRate: () => {
    const rate = get().calculateEffectiveRate();
    set({ isRateLocked: true, lockedRate: rate, timerSeconds: 300 });
  },

  decrementTimer: () => {
    const sec = get().timerSeconds;
    if (sec > 0) {
      set({ timerSeconds: sec - 1 });
    } else {
      set({ timerSeconds: 300 });
    }
  },

  calculateSavings: () => {
    const { originAmount, operationType, bankRate } = get();
    const rate = get().calculateEffectiveRate();
    const num = parseFloat(originAmount) || 0;
    if (operationType === 'BUY') {
      const cambistaTotal = num * rate;
      const bankTotal = num * bankRate;
      return Math.max(0, cambistaTotal - bankTotal).toFixed(2);
    }
    return (num * 0.025).toFixed(2);
  },
}));

