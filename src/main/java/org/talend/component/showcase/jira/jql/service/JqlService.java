package org.talend.component.showcase.jira.jql.service;

import org.talend.component.showcase.jira.jql.datastore.CustomDatastore;
import org.talend.components.common.httpclient.api.HTTPClient;
import org.talend.components.common.httpclient.api.HTTPClientException;
import org.talend.components.common.httpclient.api.QueryConfiguration;
import org.talend.components.common.httpclient.api.QueryConfigurationBuilder;
import org.talend.components.common.httpclient.factory.HTTPClientFactory;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class JqlService {

    public final static String ACTION_SUGGESTION_PROJECTS = "ACTION_SUGGESTION_PROJECTS";

    @Service
    private JsonReaderFactory jsonReaderFactory;


    @Suggestions(ACTION_SUGGESTION_PROJECTS)
    public SuggestionValues listProjects(@Option("connection") final CustomDatastore dso) {
        QueryConfiguration queryConfig = QueryConfigurationBuilder.create(dso.getBaseURL() + "/rest/api/2/project")
                .setAuthorizationToken("Bearer", dso.getPat())
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            final HTTPClient.HTTPResponse response = HTTPClientFactory.create(queryConfig).invoke();
            try(JsonReader reader = jsonReaderFactory.createReader(response.getBodyAsStream())) {
                JsonArray projectsArray = reader.readArray();
                List<SuggestionValues.Item> projects = projectsArray.stream().map(v -> v.asJsonObject())
                        .map(o -> new SuggestionValues.Item(o.getString("key"),
                                String.format("%s - %s", o.getString("key"),o.getString("name"))))
                        .collect(Collectors.toList());
                return new SuggestionValues(true, projects);
            }
        } catch (HTTPClientException e) {
            throw new ComponentException("Can't retrieve jira projects.", e);
        }
    }

}