package org.orcid.persistence.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.Query;

import org.orcid.persistence.dao.StatisticsDao;
import org.springframework.transaction.annotation.Transactional;

@PersistenceUnit(name = "statisticManagerFactory")
public class StatisticsDaoImpl implements StatisticsDao {

    @PersistenceContext(unitName = "statistics")
    protected EntityManager entityManager;
    
    @Override
    @Transactional
    public long createHistory(){
        Query query = entityManager.createNativeQuery("insert into statistic_history(id, generation_date) values (nextval('history_seq'), now()) returning id");
        System.out.println(query.executeUpdate());
        System.out.println(query.getFirstResult());
        System.out.println(query.executeUpdate());
        return query.getFirstResult();
    }
    
    @Override
    @Transactional
    public boolean saveStatistic(long id, String name, double value) {
        Query query = entityManager.createNativeQuery("insert into statistic(history_id, name, resulting_value) values (:history_id, :name, :resulting_value)");
        query.setParameter("history_id", id);
        query.setParameter("name", name);
        query.setParameter("resulting_value", value);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    public double getStatistic(long id, String name) {
        // TODO Auto-generated method stub
        return 0;
    }

}
