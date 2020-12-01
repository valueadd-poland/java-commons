package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import pl.valueadd.lucene.EntityInfoProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;


@RequiredArgsConstructor
class WildcardQueryProcessor implements QueryProcessor {

    @Override
    public boolean supports(Query query) {
        return query instanceof WildcardQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        WildcardQuery query = (WildcardQuery)q;
        String field = query.getTerm().field();
        String value = new String(query.getTerm().bytes().bytes);
        return criteriaBuilder.like(fieldProvider.getField(field).as(String.class), value.replace("*", "%"));
    }
}
