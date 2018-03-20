package org.orcid.listener.persistence.spring;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidEntityManagerFactory implements FactoryBean<EntityManager> {

	private EntityManagerFactory entityManagerFactory;

	public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Override
	public EntityManager getObject() throws BeansException {
		return SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory, null, true);
	}

	@Override
	public Class<EntityManager> getObjectType() {
		return EntityManager.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
