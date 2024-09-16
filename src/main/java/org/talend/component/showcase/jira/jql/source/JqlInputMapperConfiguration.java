package org.talend.component.showcase.jira.jql.source;

import org.talend.component.showcase.jira.jql.dataset.CustomDataset;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;


@Data
@GridLayout({
    @GridLayout.Row({ "dataset" }),
    @GridLayout.Row({ "relation" }),
    @GridLayout.Row({ "filters" })
})
@Documentation("TODO fill the documentation for this configuration")
public class JqlInputMapperConfiguration implements Serializable {
    @Option
    @Documentation("TODO fill the documentation for this parameter")
    private CustomDataset dataset;

    @Option
    @Documentation("JQL filters.")
    private List<Filter> filters = new ArrayList<>();

    @Option
    @Documentation("JQL attribute relation.")
    private RELATION relation;

    @Data
    @GridLayout({
            @GridLayout.Row({ "attribute", "value" })
    })
    public static class Filter{

        @Option
        @Documentation("The JQL attribute.")
        private ATTRIBUTE attribute;

        @Option
        @Documentation("The JQL attribute value.")
        private String value;
    }

    public enum ATTRIBUTE {
        ID("id = '%s'"),
        SUMMARY("summary ~ %s"),
        COMMENT("comment ~ %s"),
        REPORTER("reporter = %s"),
        ASSIGNEE("assignee = %s ");

        private String format;

        ATTRIBUTE(String format){
            this.format = format;
        }

        public String getFormat(){
            return this.format;
        }
    }

    public enum RELATION {
        AND, OR
    }

}