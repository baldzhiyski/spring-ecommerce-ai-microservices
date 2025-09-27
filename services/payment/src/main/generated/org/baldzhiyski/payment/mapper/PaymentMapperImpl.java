package org.baldzhiyski.payment.mapper;

import javax.annotation.processing.Generated;
import org.baldzhiyski.payment.payment.Payment;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-27T11:18:21+0200",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public Payment toEntity(PaymentReq payment) {
        if ( payment == null ) {
            return null;
        }

        Payment.PaymentBuilder payment1 = Payment.builder();

        payment1.orderRef( payment.orderReference() );
        payment1.amount( payment.amount() );
        payment1.paymentMethod( payment.paymentMethod() );
        payment1.orderId( payment.orderId() );

        return payment1.build();
    }
}
