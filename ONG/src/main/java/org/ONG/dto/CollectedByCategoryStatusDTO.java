package org.ONG.dto;

import java.math.BigDecimal;

public class CollectedByCategoryStatusDTO {
    private String category;
    private long quantityReceived;
    private long quantityAssigned;
    private BigDecimal total;

    public CollectedByCategoryStatusDTO(String category, long quantityReceived, long quantityAssigned, BigDecimal total) {
        this.category = category;
        this.quantityReceived = quantityReceived;
        this.quantityAssigned = quantityAssigned;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public long getQuantityReceived() {
        return quantityReceived;
    }

    public long getQuantityAssigned() {
        return quantityAssigned;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
