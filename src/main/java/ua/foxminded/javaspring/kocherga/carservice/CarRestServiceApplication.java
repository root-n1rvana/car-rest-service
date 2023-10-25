package ua.foxminded.javaspring.kocherga.carservice;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.foxminded.javaspring.kocherga.carservice.service.ModelService;
import ua.foxminded.javaspring.kocherga.carservice.service.impl.ModelServiceImpl;

@SpringBootApplication
public class CarRestServiceApplication {

    private final ModelService modelService;

    public CarRestServiceApplication(ModelService modelService) {
        this.modelService = modelService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CarRestServiceApplication.class, args);
    }

    @PostConstruct
    public void init() {
        modelService.init();
    }
}
