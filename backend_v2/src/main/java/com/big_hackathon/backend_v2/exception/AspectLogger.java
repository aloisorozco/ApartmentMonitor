package com.big_hackathon.backend_v2.exception;

import org.aspectj.lang.annotation.Around;

import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.big_hackathon.backend_v2.controller.ApartmentController;

@Aspect
@Component
public class AspectLogger {

    private final Logger logger = LoggerFactory.getLogger(ApartmentController.class);

    // TODO: we need to decide if we want to time all methods, or only API methods called within classes annotated with @RestController
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public void log(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        Instant methodCalledAt = Instant.now();
        logger.info("Method " + methodName + " called at " + methodCalledAt.toEpochMilli());

        jp.proceed();

        Instant methodEndedAt = Instant.now();
        logger.info("Method " + methodName + " call finished at " + methodEndedAt.toEpochMilli() + 
            ". Total execution time " + (methodEndedAt.toEpochMilli() - methodCalledAt.toEpochMilli()));
    }
}
