package com.songstock.dto;

import com.songstock.entity.PaymentMethod;
import jakarta.validation.constraints.*;
import java.util.List;

public class CreateOrderDTO {

    @NotEmpty(message = "La orden debe contener al menos un item")
    private List<OrderItemRequestDTO> items;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    // Getters y Setters
    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    @Override
    public String toString() {
        return "CreateOrderDTO{" +
                "items=" + items +
                ", paymentMethod=" + paymentMethod +
                ", shippingCity='" + shippingCity + '\'' +
                '}';
    }

    /**
     * Clase interna para items de la orden
     */
    public static class OrderItemRequestDTO {

        @NotNull(message = "El ID del producto es obligatorio")
        private Long productId;

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer quantity;

        // Getters y Setters
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        @Override
        public String toString() {
            return "OrderItemRequestDTO{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}