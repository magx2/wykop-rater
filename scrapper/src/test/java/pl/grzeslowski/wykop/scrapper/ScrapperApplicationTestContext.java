package pl.grzeslowski.wykop.scrapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.filter.AssignableTypeFilter;

@Configuration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CommandLineRunner.class),
        @ComponentScan.Filter(type = FilterType.CUSTOM, value = ScrapperApplicationTestContext.JsonCrudAssignableTypeFilter.class)
})
@EnableAutoConfiguration
public class ScrapperApplicationTestContext {
    static class JsonCrudAssignableTypeFilter extends AssignableTypeFilter  {
        private  JsonCrudAssignableTypeFilter() throws ClassNotFoundException {
            super(Class.forName("pl.grzeslowski.wykop.scrapper.io.JsonCrud"));
        }

    }
}

