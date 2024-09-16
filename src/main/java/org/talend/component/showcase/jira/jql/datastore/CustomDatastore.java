package org.talend.component.showcase.jira.jql.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@DataStore("CustomDatastore")
@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "baseURL" }),
    @GridLayout.Row({ "pat" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDatastore implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private java.net.URL baseURL;

    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private String pat;

    public java.net.URL getBaseURL() {
        return baseURL;
    }

    public CustomDatastore setBaseURL(java.net.URL baseURL) {
        this.baseURL = baseURL;
        return this;
    }

    public String getPat() {
        return pat;
    }

    public CustomDatastore setPat(String pat) {
        this.pat = pat;
        return this;
    }
}