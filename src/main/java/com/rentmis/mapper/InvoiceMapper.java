package com.rentmis.mapper;

import com.rentmis.dto.response.InvoiceResponse;
import com.rentmis.model.entity.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    InvoiceResponse toResponse(Invoice invoice);
}
