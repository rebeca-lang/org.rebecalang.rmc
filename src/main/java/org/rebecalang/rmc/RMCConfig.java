package org.rebecalang.rmc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan(basePackages = { "org.rebecalang.rmc" })
public class RMCConfig {
	
	@Autowired
	ApplicationContext appContext;
	
	protected StatementTranslatorContainer createDefaultStatementTranslatorContainer() {
		StatementTranslatorContainer statementTranslatorContainer = 
				new StatementTranslatorContainer();		
		return statementTranslatorContainer;
	}
	@Bean
	@Qualifier("CORE_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public StatementTranslatorContainer getCoreRebecaStatementTranslatorContainer() {
		return createDefaultStatementTranslatorContainer();
	}
	@Bean
	@Qualifier("TIMED_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public StatementTranslatorContainer getTimedRebecaStatementTranslatorContainer() {
		return createDefaultStatementTranslatorContainer();
	}
	@Bean
	@Qualifier("PROBABILISTIC_TIMED_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public StatementTranslatorContainer getProbabilisticTimedRebecaStatementTranslatorContainer() {
		return createDefaultStatementTranslatorContainer();
	}
	@Bean
	@Qualifier("PROBABILISTIC_REBECA")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public StatementTranslatorContainer getProbabilisticRebecaStatementTranslatorContainer() {
		return createDefaultStatementTranslatorContainer();
	}
	
//	@Bean
//	@Qualifier("CORE_REBECA")
//	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//	public CoreRebecaMethodBodyConvertor getCoreRebecaMethodBodyConvertor() {
//		CoreRebecaMethodBodyConvertor methodBodyConvertor = 
//				new CoreRebecaMethodBodyConvertor(BeanFactoryAnnotationUtils.qualifiedBeanOfType(
//						appContext.getAutowireCapableBeanFactory(), StatementTranslatorContainer.class, "CORE_REBECA"));
//
//		return methodBodyConvertor;
//	}
//
//	@Bean
//	@Qualifier("TIMED_REBECA")
//	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//	public CoreRebecaMethodBodyConvertor getTimedRebecaMethodBodyConvertor() {
//		CoreRebecaMethodBodyConvertor methodBodyConvertor = 
//				new CoreRebecaMethodBodyConvertor(BeanFactoryAnnotationUtils.qualifiedBeanOfType(
//						appContext.getAutowireCapableBeanFactory(), StatementTranslatorContainer.class, "TIMED_REBECA"));
//
//		return methodBodyConvertor;
//	}

}