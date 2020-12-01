package pl.valueadd.graphql;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.GraphQL;
import graphql.language.*;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.TypeRuntimeWiring;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_MAP;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @since 15-11-2020
 */
class GraphQLFactory {

    final ObjectMapper mapper = new ObjectMapper();

    private final List<GraphQLResolver> resolvers;

    private final List<GraphQLMutatator> mutators;

    private GraphQL graphQL;

    public GraphQLFactory(
            List<GraphQLResolver> resolvers,
            List<GraphQLMutatator> mutators
    ) {

        this.resolvers = resolvers;
        this.mutators = mutators;
        GraphQLSchema graphQLSchema = buildSchema();
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    List<Class> strigableClasses = List.of(UUID.class, String.class);
    List<Class> ignoreClasses = List.of(String.class, List.class);


    Set<Class> readModels = new HashSet<>();
    Map<Class, List<GraphQLResolver>> resolversPerType = new HashMap<>();


    Set<Class> inputModels = new HashSet<>();
    Map<Class, List<GraphQLMutatator>> mutatorsPerType = new HashMap<>();

    private void registerReadModel(Class clazz){
        if(strigableClasses.contains(clazz)){
            return;
        }
        if(readModels.contains(clazz)){
            return;
        }
        if(ignoreClasses.contains(clazz)){
            return;
        }
        readModels.add(clazz);
        extractClasses(clazz).values().forEach(this::registerReadModel);

    }
    private void registerInputModel(Class clazz){
        if(ignoreClasses.contains(clazz)){
            return;
        }
        inputModels.add(clazz);
    }

    private void registerResolver(GraphQLResolver resolver){
        var rootType = getRootClass(resolver);
        registerReadModel(rootType);
        registerInputModel(getRequestType(resolver));
        registerReadModel(getReturnType(resolver));
        if(!resolversPerType.containsKey(rootType)){
            resolversPerType.put(rootType, new LinkedList<>());
        }
        resolversPerType.get(rootType).add(resolver);

    }
    private void registerMutator(GraphQLMutatator muatator){
        if(!mutatorsPerType.containsKey(Void.class)){
            mutatorsPerType.put(Void.class, new LinkedList<>());
            mutatorsPerType.get(Void.class).add(muatator);
        }
        registerReadModel(getReturnType(muatator));
        registerInputModel(getRequestType(muatator));

    }

    private GraphQLSchema buildSchema() {
        var typeRegistry = new TypeDefinitionRegistry();
        var builder = RuntimeWiring.newRuntimeWiring();


        for (GraphQLResolver resolver : this.resolvers) {
            registerResolver(resolver);
        }
        for (GraphQLMutatator mutator : this.mutators) {
            registerMutator(mutator);
        }


        Map<Class, List<FieldDefinition>> allFields = new HashMap();
        for (Class clazz : readModels) {
            List<FieldDefinition> fields = new LinkedList<>();
            extractTypes(clazz).forEach((name, type) -> {
                fields.add(FieldDefinition.newFieldDefinition().name(name).type(type).build());
            });
            allFields.put(clazz, fields);
        }


        for (Class clazz : inputModels) {
            List<InputValueDefinition> fields = new LinkedList<>();
            extractTypes(clazz).forEach((name, type) -> {
                fields.add(InputValueDefinition.newInputValueDefinition().name(name).type(type).build());
            });

            typeRegistry.add(InputObjectTypeDefinition.newInputObjectDefinition().name(getTypeName(clazz)).inputValueDefinitions(fields).build());

        }

        for (Map.Entry<Class, List<GraphQLResolver>> entry : resolversPerType.entrySet()) {
            Class clazz = entry.getKey();
            for (GraphQLResolver resolver : entry.getValue()) {
                builder = builder.type(TypeRuntimeWiring.newTypeWiring(getTypeName(clazz))
                        .dataFetcher(
                                resolver.getName(),
                                res -> resolver.resolve(res.getSource(), mapArgs(res.getArguments(), getRequestType(resolver)))
                        ));
                List<InputValueDefinition> inputs = new LinkedList<>();
                extractTypes(getRequestType(resolver)).forEach((name, type) -> {
                    inputs.add(InputValueDefinition.newInputValueDefinition()
                            .name(name)
                            .type(type)
                            .build());
                });
                allFields.get(entry.getKey()).add(
                        FieldDefinition.newFieldDefinition().name(resolver.getName())
                                .inputValueDefinitions(inputs)
                                .type(new TypeName(getTypeName(getReturnType(resolver)))).build()
                );
            }
        }

        allFields.forEach((clazz, properties) -> {
            if(!properties.isEmpty()) {
                typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition().name(getTypeName(clazz)).fieldDefinitions(properties).build());
            }
        });

        var mutations = mutators.stream()
                .map(mutator -> {
                    List<InputValueDefinition> inputs = new LinkedList<>();

                    var typeName = getTypeName(getRequestType(mutator));
                    extractTypes(getRequestType(mutator)).forEach((name, type) -> {
                        inputs.add(InputValueDefinition.newInputValueDefinition()
                                .name(name)
                                .type(type)
                                .build());
                    });
                    typeRegistry.add(InputObjectTypeDefinition.newInputObjectDefinition()
                            .name(typeName)
                            .inputValueDefinitions(inputs)
                            .build()
                    );

                    return FieldDefinition.newFieldDefinition()
                            .name(mutator.getName())
                            .type(new TypeName(getTypeName(getReturnType(mutator))))
                            .inputValueDefinition(
                                    InputValueDefinition.newInputValueDefinition().name("payload").type(new TypeName(typeName)).build()
                            )
                            .build();
                }).collect(Collectors.toUnmodifiableList());

        typeRegistry.add(ObjectTypeDefinition.newObjectTypeDefinition().name("Mutation").fieldDefinitions(mutations).build());
        for (GraphQLMutatator mutator : mutators) {
            builder = builder.type(TypeRuntimeWiring.newTypeWiring("Mutation")
                    .dataFetcher(
                            mutator.getName(),
                            res -> mutator.mutate(
                                    mapArgs(res.getArgument("payload"), getRequestType(mutator)
                                    ))
                    ));


        }


        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, builder.build());
    }


    private Object mapArgs(Map<String, Object> res, Class type) {
        return mapper.convertValue(res, type);
    }

    private Map<String, Type> extractTypes(Class type) {
        if(type == null){
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Object.class)) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Void.class)) {
            return EMPTY_MAP;
        }
        Map<String, Type> ret = new HashMap<>();
        for (Field field : type.getDeclaredFields()) {
            Type qlType = new TypeName(getTypeName(field.getType()));
            if(Collection.class.isAssignableFrom(field.getType())){
                var elem = getTypeName((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
                qlType = new ListType(new TypeName(elem));
            }
            ret.put(field.getName(), qlType);
        }
        ret.putAll(extractTypes(type.getSuperclass()));
        return ret;
    }
    private Map<String, Class> extractClasses(Class type) {
        if(type == null){
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Object.class)) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Void.class)) {
            return EMPTY_MAP;
        }
        if (type.isAssignableFrom(Comparator.class)) {
            return EMPTY_MAP;
        }
        Map<String, Class> ret = new HashMap<>();
        for (Field field : type.getDeclaredFields()) {
            Class fieldType = field.getType();
            if(Collection.class.isAssignableFrom(fieldType)){
                fieldType = ((Class)((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
            }
            ret.put(field.getName(), fieldType);
        }
        ret.putAll(extractClasses(type.getSuperclass()));
        return ret;
    }


    private String getTypeName(Class type) {
        if (type.isAssignableFrom(Void.class)) {
            return "Query";
        }
        if(type.isAssignableFrom(UUID.class)){
            return "ID";
        }
        if(strigableClasses.contains(type)){
            return "String";
        }
        if(type.isAssignableFrom(long.class)){
            return "Int";
        }
        if(type.isAssignableFrom(int.class)){
            return "Int";
        }
        if(type.isAssignableFrom(boolean.class)){
            return "Boolean";
        }
        return type.getSimpleName();
    }

    private Class getRootClass(GraphQLResolver q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getReturnType(GraphQLResolver q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }

    private Class getReturnType(GraphQLMutatator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
    }

    private Class getRequestType(GraphQLResolver q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[2];
    }

    private Class getRequestType(GraphQLMutatator q) {
        return (Class<?>) ((ParameterizedType) q.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[1];
    }


    public GraphQL buildGraphQL() {
        return graphQL;
    }
}
