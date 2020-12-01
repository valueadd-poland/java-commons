package pl.valueadd.lucene.processor;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import pl.valueadd.lucene.EntityInfoProvider;
import pl.valueadd.lucene.exception.MalformedQueryException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.util.Optional.ofNullable;


@RequiredArgsConstructor
class TermRangeQueryProcessor implements QueryProcessor {

    @Override
    public boolean supports(Query query) {
        return query instanceof TermRangeQuery;
    }

    @Override
    public Predicate process(Query q, EntityInfoProvider fieldProvider, CriteriaBuilder criteriaBuilder) {
        TermRangeQuery query = (TermRangeQuery) q;

        String field = query.getField();

        List<Predicate> predicates = new LinkedList<>();
        Expression<?> fieldRef = fieldProvider.getField(field);
        if (fieldRef.getJavaType().isAssignableFrom(long.class)) {
            Expression<Long> prop = fieldRef.as(long.class);
            ofNullable(query.getLowerTerm()).ifPresent(lowerTerm -> {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(prop, Long.parseLong(new String(lowerTerm.bytes))));
            });
            ofNullable(query.getUpperTerm()).ifPresent(upperTerm -> {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(prop, Long.parseLong(new String(upperTerm.bytes))));
            });
        } else if (fieldRef.getJavaType().isAssignableFrom(int.class)) {
            Expression<Integer> prop = fieldRef.as(int.class);
            ofNullable(query.getLowerTerm()).ifPresent(lowerTerm -> {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(prop, Integer.parseInt(new String(lowerTerm.bytes))));
            });
            ofNullable(query.getUpperTerm()).ifPresent(upperTerm -> {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(prop, Integer.parseInt(new String(upperTerm.bytes))));
            });
        } else if (fieldRef.getJavaType().isAssignableFrom(Timestamp.class)) {
            Expression<Timestamp> prop = fieldRef.as(Timestamp.class);
            ofNullable(query.getLowerTerm()).ifPresent(lowerTerm -> {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(prop, getTimestamp(lowerTerm.bytes)));
            });
            ofNullable(query.getUpperTerm()).ifPresent(upperTerm -> {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(prop, getTimestamp(upperTerm.bytes)));
            });
        } else {
            throw new MalformedQueryException(String.format("Field of type %s could not be used in range query", fieldRef.getJavaType().getSimpleName()));
        }
        if (predicates.isEmpty()) {
            return criteriaBuilder.equal(fieldProvider.getField(field), fieldProvider.getField(field));
        }
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        return criteriaBuilder.and(
                predicates.get(0),
                predicates.get(1)
        );
    }

    private Timestamp getTimestamp(byte[] rawValue) {
        String format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
        String rawDate = new String(rawValue).toUpperCase();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            Date parsedDate = dateFormat.parse(rawDate);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e){
            throw new MalformedQueryException(String.format("Given date %s does not match excepted format: %s", rawDate, format));
        }
    }
}
