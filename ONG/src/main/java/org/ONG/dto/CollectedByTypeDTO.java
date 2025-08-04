package org.ONG.dto;

import org.ONG.enums.DonorType;

import java.math.BigDecimal;

public class CollectedByTypeDTO {
    private DonorType donorType;
    private long quantity;
    private BigDecimal total;

    public CollectedByTypeDTO(DonorType donorType, long quantity, BigDecimal total) {
        this.donorType = donorType;
        this.quantity = quantity;
        this.total = total;
    }

    public DonorType getDonorType() {
        return donorType;
    }

    public long getQuantity() {
        return quantity;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
