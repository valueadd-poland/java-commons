package pl.valueadd.graphql;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLMutatator<T, U> {
    String getName();
    T mutate(U on);
}
