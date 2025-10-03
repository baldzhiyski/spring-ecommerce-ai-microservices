package org.baldzhiyski.payment.service.impl;

import lombok.AllArgsConstructor;
import org.baldzhiyski.payment.exception.NotFoundException;
import org.baldzhiyski.payment.mapper.PaymentMapper;
import org.baldzhiyski.payment.payment.res.PaymentRes;
import org.baldzhiyski.payment.repository.PaymentRepository;
import org.baldzhiyski.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentRes getAllPaymentsForCustomer(String orderRef) {
        return this.paymentMapper.toPaymentRes(
                this.paymentRepository.findByPaymentRef(orderRef)
                        .orElseThrow(() -> new NotFoundException("No Payments found for order ref " + orderRef))
        );
    }
}
