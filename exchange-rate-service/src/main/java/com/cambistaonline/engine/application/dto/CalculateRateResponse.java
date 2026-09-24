package com.cambistaonline.engine.application.dto;

import java.math.BigDecimal;
import java.util.List;

public class CalculateRateResponse {
    private UserProfileData usuario;
    private TipoCambioData tipoCambio;
    private BeneficiosData beneficios;
    private List<DetalleItemData> detalle;
    private String codigo;
    private String mensaje;

    public CalculateRateResponse() {}

    public CalculateRateResponse(UserProfileData usuario, TipoCambioData tipoCambio,
                                 BeneficiosData beneficios, List<DetalleItemData> detalle,
                                 String codigo, String mensaje) {
        this.usuario = usuario;
        this.tipoCambio = tipoCambio;
        this.beneficios = beneficios;
        this.detalle = detalle;
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public UserProfileData getUsuario() { return usuario; }
    public void setUsuario(UserProfileData usuario) { this.usuario = usuario; }

    public TipoCambioData getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(TipoCambioData tipoCambio) { this.tipoCambio = tipoCambio; }

    public BeneficiosData getBeneficios() { return beneficios; }
    public void setBeneficios(BeneficiosData beneficios) { this.beneficios = beneficios; }

    public List<DetalleItemData> getDetalle() { return detalle; }
    public void setDetalle(List<DetalleItemData> detalle) { this.detalle = detalle; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public static class UserProfileData {
        private String nombre;
        private String correo;
        private String nivel;
        private String rol;

        public UserProfileData() {}
        public UserProfileData(String nombre, String correo, String nivel, String rol) {
            this.nombre = nombre;
            this.correo = correo;
            this.nivel = nivel;
            this.rol = rol;
        }

        public String getNombre() { return nombre; }
        public String getCorreo() { return correo; }
        public String getNivel() { return nivel; }
        public String getRol() { return rol; }
    }

    public static class TipoCambioData {
        private BigDecimal baseSbs;
        private BigDecimal spread;
        private BigDecimal ajusteHorario;
        private BigDecimal ajusteEstacional;
        private BigDecimal canjePuntos;
        private BigDecimal tipoCambioFinal;

        public TipoCambioData() {}
        public TipoCambioData(BigDecimal baseSbs, BigDecimal spread, BigDecimal ajusteHorario,
                              BigDecimal ajusteEstacional, BigDecimal canjePuntos, BigDecimal tipoCambioFinal) {
            this.baseSbs = baseSbs;
            this.spread = spread;
            this.ajusteHorario = ajusteHorario;
            this.ajusteEstacional = ajusteEstacional;
            this.canjePuntos = canjePuntos;
            this.tipoCambioFinal = tipoCambioFinal;
        }

        public BigDecimal getBaseSbs() { return baseSbs; }
        public BigDecimal getSpread() { return spread; }
        public BigDecimal getAjusteHorario() { return ajusteHorario; }
        public BigDecimal getAjusteEstacional() { return ajusteEstacional; }
        public BigDecimal getCanjePuntos() { return canjePuntos; }
        public BigDecimal getTipoCambioFinal() { return tipoCambioFinal; }
    }

    public static class BeneficiosData {
        private int saldoPuntos;
        private int puntosCanjeados;

        public BeneficiosData() {}
        public BeneficiosData(int saldoPuntos, int puntosCanjeados) {
            this.saldoPuntos = saldoPuntos;
            this.puntosCanjeados = puntosCanjeados;
        }

        public int getSaldoPuntos() { return saldoPuntos; }
        public int getPuntosCanjeados() { return puntosCanjeados; }
    }

    public static class DetalleItemData {
        private String concepto;
        private BigDecimal valor;

        public DetalleItemData() {}
        public DetalleItemData(String concepto, BigDecimal valor) {
            this.concepto = concepto;
            this.valor = valor;
        }

        public String getConcepto() { return concepto; }
        public BigDecimal getValor() { return valor; }
    }
}
