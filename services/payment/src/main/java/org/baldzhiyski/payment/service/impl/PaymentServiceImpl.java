package org.baldzhiyski.payment.service.impl;

import lombok.AllArgsConstructor;
import org.baldzhiyski.payment.mapper.PaymentMapper;
import org.baldzhiyski.payment.payment.req.PaymentReq;
import org.baldzhiyski.payment.repository.PaymentRepository;
import org.baldzhiyski.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public Integer createPayment(PaymentReq payment) {

        // TODO : Valdiate the payment and based on this send the event

        return this.paymentRepository.save(paymentMapper.toEntity(payment)).getId();
    }
}
