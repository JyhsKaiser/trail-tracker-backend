package jyhs.trail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "jyhs.trail")
public class UaemTrailTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UaemTrailTrackerApplication.class, args);
    }

}
