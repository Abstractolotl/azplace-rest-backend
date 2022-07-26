package de.abstractolotl.azplace.config;

//import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ReadynessHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        //TODO: implement readyness check for kubernetes
        // ref: https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-probes/#configure-probes)
        // coming with spring-boot 2.3.0: kubernetes-tailored health endpoints:
        // https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#production-ready-kubernetes-probes
        // see also: https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-application-availability-state
        return Health.unknown().build();
    }
}
