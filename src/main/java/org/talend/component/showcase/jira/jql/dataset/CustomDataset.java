package org.talend.component.showcase.jira.jql.dataset;

import java.io.Serializable;

import org.talend.component.showcase.jira.jql.datastore.CustomDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataSet("CustomDataset")
@GridLayout({
    @GridLayout.Row({ "datastore" }),
    @GridLayout.Row({ "project" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "datastore" }),
        @GridLayout.Row({ "onlySameHost" }),
        @GridLayout.Row({ "factor" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDataset implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDatastore datastore;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String project;

    @Option
    @Documentation("Exponential backoff factor.")
    private int factor;

    @Option
    @Documentation("Redirection only on same host")
    private boolean onlySameHost;

}
