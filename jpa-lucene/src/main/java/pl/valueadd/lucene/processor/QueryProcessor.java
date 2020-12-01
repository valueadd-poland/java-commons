package pl.valueadd.lucene.processor;

import org.apache.lucene.search.Query;

import pl.valueadd.lucene.EntityInfoProvider;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;


public interface QueryProcessor {

    boolean supports(Query query);

    Predicate process(Query query, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder);
}
