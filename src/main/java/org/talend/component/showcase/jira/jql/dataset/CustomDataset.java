package org.talend.component.showcase.jira.jql.dataset;

import java.io.Serializable;

import org.talend.component.showcase.jira.jql.datastore.CustomDatastore;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataSet("CustomDataset")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "datastore" }),
    @GridLayout.Row({ "project" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDataset implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDatastore datastore;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String project;

    public CustomDatastore getDatastore() {
        return datastore;
    }

    public CustomDataset setDatastore(CustomDatastore datastore) {
        this.datastore = datastore;
        return this;
    }

    public String getProject() {
        return project;
    }

    public CustomDataset setProject(String project) {
        this.project = project;
        return this;
    }
}