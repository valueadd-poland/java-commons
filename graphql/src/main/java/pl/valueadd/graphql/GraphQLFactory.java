package pl.valueadd.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.TypeName;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static graphql.language.FieldDefinition.newFieldDefinition;
import static graphql.language.InputValueDefinition.newInputValueDefinition;
import static java.util.List.of;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 15-11-2020
 */
public class GraphQLFactory {

    final ObjectMapper mapper = new ObjectMapper();

    private final List<GraphQLFetcher> fetchers;

    private final List<GraphQLCreator> creators;

    private final List<GraphQLUpdater> updaters;

    private final List<GraphQLRemover> removers;

    private GraphQL graphQL;

    public GraphQLFactory(
            List<GraphQLFetcher> fetchers,
            List<GraphQLCreator> creators,
            List<GraphQLUpdater> updaters,
            List<GraphQLRemover> removers
    ) {

        this.fetchers = fetchers;
        this.creators = creators;
        this.updaters = updaters;
        this.removers = removers;
        GraphQLSchema graphQLSchema = buildSchema();
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private final TypeRegistry typeStorage = new TypeRegistry();


    private GraphQLSchema buildSchema() {
        var typeRegistry = new TypeDefinitionRegistry();
        var builder = RuntimeWiring.newRuntimeWiring();


        var fetcherTypes = fetchers.stream().map(mutator -> {
            var type = typeStorage.registerAndGetInput(getRequestType(mutator));
            var returnType = typeStorage.registerAndGetOutput(getReturnType(mutator));
            return newFieldDefinition().name(mutator.getName())
                    .type(new TypeName(returnType.getName()))
                    .inputValueDefinition(
                            newInputValueDefinition().name(mutator.getArgName()).type(new TypeName(type.getName())).build()
                    )
                    .build();
        }).collect(Collectors.toUnmodifiableList());

        var createMutatorTypes = creators.stream().map(mutator -> {
            var type = typeStorage.registerAndGetInput(getRequestType(mutator));
            var returnType = typeStorage.registerAndGetOutput(getReturnType(mutator));
            return newFieldDefinition().name(mutator.getName())
                    .type(new TypeName(returnType.getName()))
                    .inputValueDefinition(
                            newInputValueDefinition().name(mutator.getArgName()).type(new TypeName(type.getName())).build()
                    )
                    .build();
        }).collect(Collectors.toUnmodifiableList());

        var updateMutatorTypes = updaters.stream().map(mutator -> {
            var idType = typeStorage.registerAndGetInput(getIDType(mutator));
            var type = typeStorage.registerAndGetInput(getRequestType(mutator));
            var returnType = typeStorage.registerAndGetOutput(getReturnType(mutator));
            return newFieldDefinition().name(mutator.getName())
                    .type(new TypeName(returnType.getName()))
                    .inputValueDefinitions(of(
                            newInputValueDefinition().name("id").type(new TypeName(idType.getName())).build(),
                            newInputValueDefinition().name(mutator.getArgName()).type(new TypeName(type.getName())).build()
                    )).build();
        }).collect(Collectors.toUnmodifiableList());

        var removeMutatorTypes = removers.stream()
                .map(mutator -> {
                    var idType = typeStorage.registerAndGetInput(getIDType(mutator));
                    var returnType = typeStorage.registerAndGetOutput(getReturnType(mutator));
                    return newFieldDefinition().name(mutator.getName())
                            .type(new TypeName(returnType.getName()))
                            .inputValueDefinitions(of(
                                    newInputValueDefinition().name("id").type(new TypeName(idType.getName())).build()
                            )).build();
                }).collect(Collectors.toUnmodifiableList());


        var mutatorTypes = new ArrayList<FieldDefinition>();
        mutatorTypes.addAll(createMutatorTypes);
        mutatorTypes.addAll(updateMutatorTypes);
        mutatorTypes.addAll(removeMutatorTypes);


        for (var mutator : fetchers) {
            builder = builder.type(typeStorage.addResolver(mutator));
        }
        for (var mutator : creators) {
            builder = builder.type(typeStorage.addMutator(mutator));
        }
        for (var mutator : updaters) {
            builder = builder.type(typeStorage.addMutator(mutator));
        }
        for (var mutator : removers) {
            builder = builder.type(typeStorage.addMutator(mutator));
        }

        typeStorage.getInputs().values().forEach(typeRegistry::add);
        typeStorage.getOutputs().values().forEach(typeRegistry::add);
        typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Query").fieldDefinitions(fetcherTypes).build());
        typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Mutation").fieldDefinitions(mutatorTypes).build());

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, builder.build());
    }

    private Class getReturnType(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getRootClass(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }


    private Class getReturnType(GraphQLCreator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getReturnType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }


    private Class getReturnType(GraphQLRemover q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getIDType(GraphQLRemover q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getIDType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getRequestType(GraphQLUpdater q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2];
    }

    private Class getRequestType(GraphQLFetcher q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2];
    }

    private Class getRequestType(GraphQLCreator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }


    public GraphQL buildGraphQL() {
        return graphQL;
    }
}
