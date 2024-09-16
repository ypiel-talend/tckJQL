package org.talend.component.showcase.jira.jql.source;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.json.JsonBuilderFactory;

import org.talend.components.http.configuration.Dataset;
import org.talend.components.http.configuration.Datastore;
import org.talend.components.http.configuration.Format;
import org.talend.components.http.configuration.Header;
import org.talend.components.http.configuration.Param;
import org.talend.components.http.configuration.RequestConfig;
import org.talend.components.http.configuration.Retry;
import org.talend.components.http.configuration.auth.Authentication;
import org.talend.components.http.configuration.auth.Authorization;
import org.talend.components.http.configuration.pagination.OffsetLimitStrategyConfig;
import org.talend.components.http.configuration.pagination.Pagination;
import org.talend.components.http.input.AbstractHTTPInput;
import org.talend.components.http.service.I18n;
import org.talend.components.http.service.RecordBuilderService;
import org.talend.components.http.service.httpClient.HTTPClientService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.service.Service;


import static org.talend.sdk.component.api.component.Icon.IconType.CUSTOM;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(value = CUSTOM, custom = "JqlInput") // icon is located at src/main/resources/icons/JqlInput.svg
@Emitter(name = "JqlInput")
@Documentation("TODO fill the documentation for this mapper")
public class JqlInputSource extends AbstractHTTPInput<JqlInputMapperConfiguration> {

    @Service
    private JsonBuilderFactory jsonBuilderFactory;


    public JqlInputSource(@Option("configuration") final JqlInputMapperConfiguration configuration,
                          final HTTPClientService client,
                          final RecordBuilderService recordBuilder,
                          final I18n i18n) {
        super(configuration, client, recordBuilder, i18n);
    }

    @Override
    protected RequestConfig translateConfiguration(JqlInputMapperConfiguration inputConfig) {
        RequestConfig outputConfig = new RequestConfig();

        // set base URL
        Datastore dso = new Datastore();
        dso.setBase(String.format("%s/%s", inputConfig.getDataset().getDatastore().getBaseURL(), "rest/api/2/"));



        // Set authentication
        Authentication auth = new Authentication();
        auth.setType(Authorization.AuthorizationType.Bearer);
        auth.setBearerToken(inputConfig.getDataset().getDatastore().getPat());
        dso.setAuthentication(auth);

        // Configure Query
        Dataset dse = new Dataset();
        dse.setDatastore(dso);
        dse.setMethodType("GET");
        dse.setResource("search");

        // Response is JSON
        dse.setFormat(Format.JSON);

        // Add an expected header
        dse.setHasHeaders(true);
        dse.setHeaders(Collections.singletonList(
                new Header("Content-Type", "application/json", Header.HeaderQueryDestination.MAIN)));


        // Configure the JQL and fields as query parameter
        // to be compatible with pagination
        dse.setHasQueryParams(true);
        String jql = getJQL(inputConfig.getRelation(), inputConfig.getFilters(), inputConfig.getDataset().getProject());
        dse.setQueryParams(Arrays.asList(
                new Param("jql", jql),
                new Param("fields", "id,summary,status,reporter")
        ));

        // Hardcoded loop over all pages
        dse.setHasPagination(true);
        Pagination pagination = new Pagination();
        pagination.setStrategy(Pagination.Strategy.OFFSET_LIMIT);
        OffsetLimitStrategyConfig offsetLimitStrategyConfig = new OffsetLimitStrategyConfig();
        offsetLimitStrategyConfig.setLocation(OffsetLimitStrategyConfig.Location.QUERY_PARAMETERS);
        offsetLimitStrategyConfig.setOffsetParamName("startAt");
        offsetLimitStrategyConfig.setOffsetValue("0");
        offsetLimitStrategyConfig.setLimitParamName("maxResults");
        offsetLimitStrategyConfig.setLimitValue("50");
        offsetLimitStrategyConfig.setElementsPath(".issues");
        pagination.setOffsetLimitStrategyConfig(offsetLimitStrategyConfig);
        dse.setPagination(pagination);

        // Timeouts
        dso.setConnectionTimeout(inputConfig.getDataset().getDatastore().getConnectionTimeout());
        dso.setReceiveTimeout(inputConfig.getDataset().getDatastore().getReadTimeout());


        // Semi-hardcoded retry with exponential backoff
        int factor = inputConfig.getDataset().getFactor();
        if(factor > 0){
            dso.setHasRetry(true);
            Retry retry = new Retry();
            retry.setBackoff(300);
            retry.setFactor(factor);
            retry.setMaxAttempts(3);
            dso.setRetry(retry);
        }

        // Semi hard-coded redirection
        dse.setAcceptRedirections(true);
        dse.setMaxRedirectOnSameURL(3);
        dse.setOnlySameHost(inputConfig.getDataset().isOnlySameHost());

        // Extract desired values
        dse.setOutputKeyValuePairs(true);
        dse.setKeyValuePairs(Arrays.asList(
                new Param("id", " {.response.id}"),
                new Param("status",  "{.response.fields.status.name}"),
                new Param("summary", "{.response.fields.summary}"),
                new Param("reporter","{.response.fields.reporter.name}")
        ));

        outputConfig.setDataset(dse);
        return outputConfig;
    }

    private static String getJQL(JqlInputMapperConfiguration.RELATION relation,
                                 List<JqlInputMapperConfiguration.Filter> filters,
                                 String project){

        String query = "(" + filters.stream().map(f -> String.format(f.getAttribute().getFormat(), escape(f.getValue())))
                .collect(Collectors.joining(String.format(" %S ", relation.name().toUpperCase(Locale.ROOT))));

        query += ") AND project = " + escape(project);

        return query;
    }

    private static String escape(String s) {
        String escaped = s.replace("'", "\\'");
        return "'" + escaped + "'";
    }
}