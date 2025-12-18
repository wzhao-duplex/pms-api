package ca.on.pms.property.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PropertyUpdateRequest {

    private String address;
    private String city;
    private String province;
    private String postalCode;

    private String propertyType;

    private BigDecimal ownershipPercent;
    private BigDecimal selfUsePercent;

    private String managementCompany;
}
