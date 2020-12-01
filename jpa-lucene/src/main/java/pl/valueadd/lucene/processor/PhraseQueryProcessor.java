package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import pl.valueadd.lucene.EntityInfoProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.stream.Collectors;


@RequiredArgsConstructor
class PhraseQueryProcessor implements QueryProcessor {

    @Override
    public boolean supports(Query query) {
        return query instanceof PhraseQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        PhraseQuery query = (PhraseQuery)q;
        String field = query.getField();
        String value = Arrays.stream(query.getTerms()).map(e -> new String(e.bytes().bytes)).collect(Collectors.joining("-"));
        Expression<?> fieldType = fieldProvider.getField(field);
        return criteriaBuilder.equal(fieldType.as(String.class), value);
    }
}
