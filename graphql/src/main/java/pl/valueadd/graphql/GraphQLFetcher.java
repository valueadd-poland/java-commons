package pl.valueadd.graphql;


/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLFetcher<O, T, P> {
    String getName();
    String getArgName();
    T resolve(O on, P params);
}
