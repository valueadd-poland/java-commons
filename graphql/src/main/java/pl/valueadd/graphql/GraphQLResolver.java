package pl.valueadd.graphql;


/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLResolver<O, T, P> {
    String getName();
    T resolve(O on, P params);
}
