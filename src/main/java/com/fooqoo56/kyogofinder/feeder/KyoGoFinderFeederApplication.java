package com.fooqoo56.kyogofinder.feeder;


import static com.fooqoo56.kyogofinder.feeder.appication.config.StepConfig.JOB;


import com.fooqoo56.kyogofinder.feeder.domain.model.PubSubMessage;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ConfigurationPropertiesScan
public class KyoGoFinderFeederApplication {

    public static final String JOB_LAUNCHER = "jobLauncher";
    public static final String DATE_PARAM = "date";

    /**
     * Spring Boot main.
     *
     * @param args args
     */
    public static void main(final String[] args) throws Exception {
        final SpringApplication application =
                new SpringApplication(KyoGoFinderFeederApplication.class);

        application.setWebApplicationType(WebApplicationType.NONE);
        final ConfigurableApplicationContext context = application.run(args);
        final JobLauncher jobLauncher = context.getBean(JOB_LAUNCHER, JobLauncher.class);
        final Job job = context.getBean(JOB, Job.class);

        jobLauncher.run(job, createJobParams());
    }

    /**
     * jobパラメータを取得.
     *
     * @return job
     */
    private static JobParameters createJobParams() {
        return new JobParametersBuilder().addDate(DATE_PARAM, new Date()).toJobParameters();
    }

    /**
     * Cloud Functionの実行関数.
     *
     * @return 関数
     */
    @Bean
    public Consumer<PubSubMessage> pubSubFunction() {
        return message -> {
            // The PubSubMessage data field arrives as a base-64 encoded string and must be decoded.
            // See: https://cloud.google.com/functions/docs/calling/pubsub#event_structure
            final String[] args = {getDecodedMessage(message)};
            try {
                KyoGoFinderFeederApplication.main(args);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * メッセージをデコードする.
     *
     * @param message pubsubメッセージ
     * @return デコードされたdataパラメータ
     */
    private String getDecodedMessage(final PubSubMessage message) {
        if (Objects.nonNull(message)) {
            if (StringUtils.isNoneBlank(message.getData())) {
                return new String(Base64.getDecoder().decode(message.getData()),
                        StandardCharsets.UTF_8);
            }
        }
        return StringUtils.EMPTY;
    }

}
