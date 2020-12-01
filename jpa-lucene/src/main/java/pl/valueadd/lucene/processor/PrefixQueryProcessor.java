package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;

import pl.valueadd.lucene.EntityInfoProvider;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;


@RequiredArgsConstructor
class PrefixQueryProcessor implements QueryProcessor {

    @Override
    public boolean supports(Query query) {
        return query instanceof PrefixQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        PrefixQuery query = (PrefixQuery)q;
        String field = query.getField();
        String value = new String(query.getPrefix().bytes().bytes);
        return criteriaBuilder.like(fieldProvider.getField(field).as(String.class), value + "%");
    }
}
