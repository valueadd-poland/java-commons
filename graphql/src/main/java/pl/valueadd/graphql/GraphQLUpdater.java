package pl.valueadd.graphql;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLUpdater<T, ID, U> {
    String getName();
    String getArgName();
    T update(ID id, U on);
}
