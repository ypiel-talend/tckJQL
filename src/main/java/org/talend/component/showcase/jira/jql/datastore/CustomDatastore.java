package org.talend.component.showcase.jira.jql.datastore;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("CustomDatastore")
@GridLayout({
    @GridLayout.Row({ "baseURL" }),
    @GridLayout.Row({ "pat" })
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {
        @GridLayout.Row({ "connectionTimeout" }),
        @GridLayout.Row({ "readTimeout" })
})
@Documentation("TODO fill the documentation for this configuration")
public class CustomDatastore implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private java.net.URL baseURL;

    @Option
    @Credential
    @Documentation("TODO fill the documentation for this parameter")
    private String pat;

    @Option
    @Documentation("Connection timeout.")
    @DefaultValue("1000")
    private int connectionTimeout = 1000;

    @Option
    @Documentation("Read timeout.")
    @DefaultValue("1000")
    private int readTimeout = 1000;

}