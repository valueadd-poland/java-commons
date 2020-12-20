package pl.valueadd.graphql;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 18-11-2020
 */
public interface GraphQLRemover<ID, M> {
    String getName();
    M remove(ID id);
}
