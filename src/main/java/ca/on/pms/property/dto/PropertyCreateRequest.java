package ca.on.pms.property.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class PropertyCreateRequest {

    private UUID orgId;

    private String address;
    private String city;
    private String province;
    private String postalCode;

    private String propertyType;

    private BigDecimal ownershipPercent;
    private BigDecimal selfUsePercent;

    private String managementCompany;
}
