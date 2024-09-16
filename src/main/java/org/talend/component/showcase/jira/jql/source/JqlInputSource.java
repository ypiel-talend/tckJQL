package org.talend.component.showcase.jira.jql.source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.talend.components.common.httpclient.api.BodyFormat;
import org.talend.components.http.configuration.Dataset;
import org.talend.components.http.configuration.Datastore;
import org.talend.components.http.configuration.Format;
import org.talend.components.http.configuration.Header;
import org.talend.components.http.configuration.RequestBody;
import org.talend.components.http.configuration.RequestConfig;
import org.talend.components.http.configuration.auth.Authentication;
import org.talend.components.http.configuration.auth.Authorization;
import org.talend.components.http.input.AbstractHTTPInput;
import org.talend.components.http.service.I18n;
import org.talend.components.http.service.RecordBuilderService;
import org.talend.components.http.service.httpClient.HTTPClientService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;


import org.talend.component.showcase.jira.jql.service.JqlService;

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
        dse.setMethodType("POST");
        dse.setResource("search");

        // Add an expected header
        dse.setHasHeaders(true);
        dse.setHeaders(Collections.singletonList(
                new Header("Content-Type", "application/json", Header.HeaderQueryDestination.MAIN)));

        // Set the JQL in the body
        dse.setHasBody(true);
        RequestBody body = new RequestBody();
        String jsonValue = filters2Json(inputConfig.getRelation(), inputConfig.getFilters(), inputConfig.getDataset().getProject());
        body.setType(BodyFormat.JSON);
        body.setJsonValue(jsonValue);
        dse.setBody(body);

        outputConfig.setDataset(dse);
        return outputConfig;
    }

    private String filters2Json(JqlInputMapperConfiguration.RELATION relation,
                               List<JqlInputMapperConfiguration.Filter> filters,
                                String project) {

        String query = filters.stream()
                .map(f -> String.format(f.getAttribute().getFormat(), escape(f.getValue())))
                .collect(Collectors.joining(String.format(" %S ", relation.name()), "\"", ""));

        StringBuilder jql = new StringBuilder("{\"jql\" :")
                .append(query)
                .append(" AND project = ")
                .append(escape(project))
                .append("\"")
                .append("}");

        return jql.toString();
    }

    private static String escape(String s) {
        String escaped = s.replace("'", "\\'");
        return "'" + escaped + "'";
    }
}