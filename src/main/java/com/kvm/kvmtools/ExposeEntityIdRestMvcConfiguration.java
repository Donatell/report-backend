package com.kvm.kvmtools;

import com.kvm.kvmtools.reports.entity.Module;
import com.kvm.kvmtools.reports.entity.*;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Component
public class ExposeEntityIdRestMvcConfiguration
		implements RepositoryRestConfigurer {
	
	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,
	                                                 CorsRegistry cors) {
		config.exposeIdsFor(PatientList.class);
		config.exposeIdsFor(ColumnTitle.class);
		config.exposeIdsFor(Module.class);
		config.exposeIdsFor(TransneftBase.class);
		config.exposeIdsFor(TransneftPriceCategory.class);
		config.setDefaultPageSize(1000);
	}
}
