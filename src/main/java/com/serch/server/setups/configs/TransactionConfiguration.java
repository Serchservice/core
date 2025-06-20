package com.serch.server.setups.configs;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TransactionConfiguration.class);
    private final PlatformTransactionManager transactionManager;

    @Around("execution(* com.serch.server.*.*(..))")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        log.info(String.format("Serch Transaction Status::: %s", status));

        Object result;
        try {
            result = joinPoint.proceed();  // Proceed with the method call
            transactionManager.commit(status);  // Commit the transaction if successful
        } catch (Throwable ex) {
            transactionManager.rollback(status);  // Rollback the transaction on error
            log.info(String.format("SERCH:: Exception from TransactionConfiguration: %s", ex.getMessage()));
            throw ex;
        }
        return result;
    }
}