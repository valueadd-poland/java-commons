package pl.valueadd.graphql;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLCreator<T, U> {
    String getName();
    String getArgName();
    T create(U on);
}
