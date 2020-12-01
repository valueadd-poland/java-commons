package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import pl.valueadd.lucene.EntityInfoProvider;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

@RequiredArgsConstructor
class BooleanQueryProcessor implements QueryProcessor {

    private final QueryProcessorRoot processorRoot;

    @Override
    public boolean supports(Query query) {
        return query instanceof BooleanQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        BooleanQuery query = (BooleanQuery) q;
        List<BooleanClause> clausules = query.clauses();
        if(clausules.isEmpty()){
            return null;
        }
        Predicate predicate = processorRoot.process(clausules.get(0).getQuery(), fieldProvider, criteriaBuilder);
        BooleanClause prev = clausules.get(0);
        for (int i = 1; i < clausules.size(); i++) {
            BooleanClause current = clausules.get(i);
            BooleanClause.Occur occur = prev.getOccur();

            Predicate sub = processorRoot.process(current.getQuery(), fieldProvider, criteriaBuilder);
            if (occur == BooleanClause.Occur.MUST) {
                predicate = criteriaBuilder.and(
                        predicate,
                        sub
                );
            }
            if (occur == BooleanClause.Occur.SHOULD) {
                predicate = criteriaBuilder.or(
                        predicate,
                        sub
                );
            }
            if (occur == BooleanClause.Occur.MUST_NOT) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.not(sub)
                );
            }
            prev = current;
        }
        return predicate;
    }
}
