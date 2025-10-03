package org.baldzhiyski.payment.mapper;


import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.baldzhiyski.payment.payment.res.PaymentRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orderRef",source = "orderReference")
    Payment toEntity(PaymentReq payment);

    PaymentRes toPaymentRes(Payment payment);
}
