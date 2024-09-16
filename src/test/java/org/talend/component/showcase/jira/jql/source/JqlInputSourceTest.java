package org.talend.component.showcase.jira.jql.source;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.talend.component.showcase.jira.jql.dataset.CustomDataset;
import org.talend.component.showcase.jira.jql.datastore.CustomDatastore;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.ComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents(value = "org.talend.component.showcase.jira.jql")
public class JqlInputSourceTest {
    @Injected
    private ComponentsHandler handler;

    @Test
    @Disabled
    void testJQL() throws MalformedURLException {
        CustomDatastore dso = new CustomDatastore();
        dso.setBaseURL(new URL("https://jira.talendforge.org"));
        String jiraPat = System.getProperty("jira.pat", System.getenv("JIRA_PAT"));
        dso.setPat(jiraPat);
        dso.setConnectionTimeout(1000);
        dso.setReadTimeout(1000);

        CustomDataset dse = new CustomDataset();
        dse.setDatastore(dso);
        dse.setProject("TDI");
        dse.setFactor(0);
        dse.setOnlySameHost(false);

        JqlInputMapperConfiguration conf = new JqlInputMapperConfiguration();
        conf.setDataset(dse);

        conf.setRelation(JqlInputMapperConfiguration.RELATION.AND);
        conf.setFilters(List.of(
                new JqlInputMapperConfiguration.Filter(JqlInputMapperConfiguration.ATTRIBUTE.REPORTER, "ypiel"),
                new JqlInputMapperConfiguration.Filter(JqlInputMapperConfiguration.ATTRIBUTE.SUMMARY, "HTTP")
        ));

        RecordBuilderFactory factory = this.handler.findService(RecordBuilderFactory.class);
        Record peter = factory.newRecordBuilder().withString("name", "Peter").build();
        this.handler.setInputData(Collections.singletonList(peter));

        String q = configurationByExample().forInstance(conf).configured().toQueryString();
        Job.components()
                // .component("emitter", "test://emitter") //
                .component("jira", "Jira://JqlInput?" + q)
                .component("out", "test://collector")
                .connections()
                /*.from("emitter")
                .to("jira")*/
                .from("jira")
                .to("out")
                .build()
                .run();

        List<Record> records = this.handler.getCollectedData(Record.class);
        System.out.println("Nb records: " + records.size());
    }

}