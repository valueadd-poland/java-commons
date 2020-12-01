package pl.valueadd.lucene.processor;

import org.apache.lucene.search.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;


public class QueryProcessorRoot {

    private List<QueryProcessor> processors = List.of(
            new BooleanQueryProcessor(this),
            new PhraseQueryProcessor(),
            new PrefixQueryProcessor(),
            new TermQueryProcessor(),
            new TermRangeQueryProcessor(),
            new WildcardQueryProcessor()
    );

    public Predicate process(Query q, pl.valueadd.lucene.EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        return processors.stream().filter(e->e.supports(q)).findFirst().map(e-> e.process(q, fieldProvider, criteriaBuilder)).orElse(null);
    }
}

