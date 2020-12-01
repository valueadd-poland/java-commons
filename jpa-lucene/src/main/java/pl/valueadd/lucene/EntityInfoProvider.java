package pl.valueadd.lucene;

import lombok.Getter;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import pl.valueadd.lucene.exception.FieldNotFoundException;
import pl.valueadd.lucene.exception.MalformedQueryException;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class EntityInfoProvider {

    @Getter
    private final Root<?> rootType;
    private final List<Root<?>> types = new ArrayList<>();

    public EntityInfoProvider(Root<?> rootType) {
        this.rootType = rootType;
        this.types.add(rootType);
    }

    public EntityInfoProvider(Root<?> rootType, List<Root<?>> types) {
        this.rootType = rootType;
        this.types.add(rootType);
        this.types.addAll(types);
    }

    public Expression<?> getField(String field) {
        FieldNotFoundException last = new FieldNotFoundException("");
        for (Root<?> type : types) {
            try {
                return getField(field, type);
            } catch (FieldNotFoundException e){
                last = e;
            }
        }
        throw last;
    }
    private Expression<?> getField(String field, Root<?> rootType) {
        field = field.replace(".id", "Id");
        if (field.contains("permissions")) {
            throw new MalformedQueryException("Querying via permissions is not supported");
        }

        Path<?> path = rootType;
        for (String s : field.split("\\.")) {
            if (s.endsWith("Id")) {
                try {
                    path = path.get(s);
                    continue;
                } catch (IllegalArgumentException e) { }
                try {
                    path = path.get(s.substring(0, s.length()-2)).get("id");
                } catch (IllegalArgumentException e) {
                    throw new FieldNotFoundException(String.format("Field '%s' does not exists or is not currently supported.", field));
                }
                continue;
            }
            try {
                Path<?> el = path.get(s);
                if (el instanceof PluralAttributePath) {
                    el = rootType.join(s);
                }
                path = el;
            } catch (IllegalArgumentException e) {
                throw new FieldNotFoundException(String.format("Field '%s' does not exists or is not currently supported.", field));
            }
        }
        return path;
    }


}
