package com.cambistaonline.auth.application.dto;

public class RegisterUserCommand {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String dni;
    private String companyName;
    private String ruc;
    private String legalRepresentativeName;
    private String role; // "N" o "J"

    public RegisterUserCommand() {}

    public RegisterUserCommand(String email, String password, String firstName, String lastName, String dni, String companyName, String ruc, String legalRepresentativeName, String role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dni = dni;
        this.companyName = companyName;
        this.ruc = ruc;
        this.legalRepresentativeName = legalRepresentativeName;
        this.role = role;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getLegalRepresentativeName() { return legalRepresentativeName; }
    public void setLegalRepresentativeName(String legalRepresentativeName) { this.legalRepresentativeName = legalRepresentativeName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
