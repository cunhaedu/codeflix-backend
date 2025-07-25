package com.cunhaedu.admin.catalogo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ActiveProfiles("test")
@DataJpaTest
@ComponentScan(includeFilters = {
        // Doing this because DataJpaTest does not see the CategoryMySQLGateway
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".[MySQLGateway]")
})
@ExtendWith(CleanUpExtension.class)
public @interface MySQLGatewayTest {}
