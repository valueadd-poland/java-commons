package pl.valueadd.lucene;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import pl.valueadd.lucene.exception.MalformedQueryException;
import pl.valueadd.lucene.processor.QueryProcessorRoot;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;


public class LuceneMapper {

    private static QueryProcessorRoot root = new QueryProcessorRoot();

    public static Predicate process(String query, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        try {
            if (query == null || query.isEmpty()) {
                return null;
            }
            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{}, new WhitespaceAnalyzer());
            queryParser.setAllowLeadingWildcard(true);
            queryParser.setSplitOnWhitespace(true);
            queryParser.setAutoGeneratePhraseQueries(true);


            Query q = queryParser.parse(query);

            return root.process(q, fieldProvider, criteriaBuilder);


        } catch (ParseException parseException) {
            throw new MalformedQueryException(parseException.getMessage());
        }
    }
}
