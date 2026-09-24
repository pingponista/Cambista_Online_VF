package com.cambistaonline.auth.adapters.outbound.mfa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotpAdapterTest {

    private TotpAdapter totpAdapter;

    @BeforeEach
    void setUp() {
        totpAdapter = new TotpAdapter();
    }

    @Test
    @DisplayName("Debe generar un secreto Base32 válido de 32 caracteres")
    void shouldGenerateValidBase32Secret() {
        String secret = totpAdapter.generateSecret();

        assertNotNull(secret);
        assertEquals(32, secret.length());
        assertTrue(secret.matches("^[A-Z2-7]+$"), "El secreto debe cumplir con la especificación Base32 RFC 4648");
    }

    @Test
    @DisplayName("Debe generar URI compatible con Google Authenticator")
    void shouldGenerateOtpAuthUri() {
        String secret = "JBSWY3DPEHPK3PXPJBSWY3DPEHPK3PXP";
        String uri = totpAdapter.getOtpAuthUri(secret, "usuario@correo.com", "CambistaOnline");

        assertNotNull(uri);
        assertTrue(uri.startsWith("otpauth://totp/CambistaOnline:usuario@correo.com"));
        assertTrue(uri.contains("secret=JBSWY3DPEHPK3PXPJBSWY3DPEHPK3PXP"));
        assertTrue(uri.contains("issuer=CambistaOnline"));
    }

    @Test
    @DisplayName("Debe rechazar códigos nulos, vacíos o con longitud incorrecta")
    void shouldRejectInvalidCodes() {
        String secret = totpAdapter.generateSecret();

        assertFalse(totpAdapter.verifyCode(secret, null));
        assertFalse(totpAdapter.verifyCode(secret, ""));
        assertFalse(totpAdapter.verifyCode(secret, "12345"));
        assertFalse(totpAdapter.verifyCode(secret, "1234567"));
        assertFalse(totpAdapter.verifyCode(null, "123456"));
    }

    @Test
    @DisplayName("Debe cumplir al 100% con los vectores de prueba oficiales de RFC 6238")
    void shouldComplyWithRfc6238OfficialVectors() {
        // Clave de prueba oficial RFC 6238: "12345678901234567890" (20 bytes ASCII)
        byte[] rfcKey = "12345678901234567890".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        String rfcBase32 = totpAdapter.base32Encode(rfcKey);
        assertEquals("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", rfcBase32);

        // Vector 1: Time = 59s -> step = 1 -> código "287082"
        assertEquals("287082", totpAdapter.generateTotpCode(rfcKey, 1));

        // Vector 2: Time = 1111111109s -> step = 37037036 -> código "081804"
        assertEquals("081804", totpAdapter.generateTotpCode(rfcKey, 37037036));

        // Vector 3: Time = 1234567890s -> step = 41152263 -> código "005924"
        assertEquals("005924", totpAdapter.generateTotpCode(rfcKey, 41152263));

        // Vector 4: Time = 2000000000s -> step = 66666666 -> código "279037"
        assertEquals("279037", totpAdapter.generateTotpCode(rfcKey, 66666666));

        // Verificación en tiempo de ejecución del método verifyCode
        String secret = totpAdapter.generateSecret();
        long currentInterval = System.currentTimeMillis() / 1000 / 30;
        String currentCode = totpAdapter.generateTotpCode(totpAdapter.base32Decode(secret), currentInterval);
        assertTrue(totpAdapter.verifyCode(secret, currentCode));
    }
}
