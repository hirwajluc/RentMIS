package com.rentmis.mapper;

import com.rentmis.dto.response.InvoiceResponse;
import com.rentmis.dto.response.PaymentResponse;
import com.rentmis.model.entity.Invoice;
import com.rentmis.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, UnitMapper.class})
public interface PaymentMapper {
    @Mapping(target = "confirmedByName",
             expression = "java(payment.getConfirmedBy() != null ? payment.getConfirmedBy().getFirstName() + ' ' + payment.getConfirmedBy().getLastName() : null)")
    PaymentResponse toResponse(Payment payment);
    InvoiceResponse toResponse(Invoice invoice);
}
