package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import pl.valueadd.lucene.EntityInfoProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.UUID;


@RequiredArgsConstructor
class TermQueryProcessor implements QueryProcessor {

    @Override
    public boolean supports(Query query) {
        return query instanceof TermQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        TermQuery query = (TermQuery)q;
        String field = query.getTerm().field();
        String value = new String(query.getTerm().bytes().bytes);

        Expression<?> fieldRef = fieldProvider.getField(field);
        if(fieldRef.getJavaType().isAssignableFrom(int.class)){
            return criteriaBuilder.equal(fieldProvider.getField(field).as(Integer.class), Integer.parseInt(value));
        }
        if(fieldRef.getJavaType().isAssignableFrom(boolean.class)){
            return criteriaBuilder.equal(fieldProvider.getField(field).as(Boolean.class), Boolean.valueOf(value.toLowerCase()));
        }
        if(fieldRef.getJavaType().isAssignableFrom(long.class)){
            return criteriaBuilder.equal(fieldProvider.getField(field).as(Long.class), Long.parseLong(value));
        }
        if(value.equals("null")){
            return criteriaBuilder.isNull(fieldProvider.getField(field));
        }
        if(fieldRef.getJavaType().isAssignableFrom(UUID.class)){
            return criteriaBuilder.equal(fieldProvider.getField(field), UUID.fromString(value));
        }
        return criteriaBuilder.equal(fieldProvider.getField(field).as(String.class), value);
    }
}
