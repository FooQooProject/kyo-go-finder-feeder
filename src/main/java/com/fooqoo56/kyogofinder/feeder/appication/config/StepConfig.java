package com.fooqoo56.kyogofinder.feeder.appication.config;

import com.fooqoo56.kyogofinder.feeder.appication.job.JobListener;
import com.fooqoo56.kyogofinder.feeder.appication.job.twitter.tasklet.TwitterTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * PriceStep設定クラス.
 */
@Component
@RequiredArgsConstructor
@EnableBatchProcessing
public class StepConfig {

    public static final String JOB = "job";
    private static final String KYO_GO_FINDER_STEP = "KyoGoFinder";
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobListener jobListener;
    private final TwitterTasklet twitterTasklet;

    /**
     * ステップのbean.
     *
     * @return Stepインスタンス
     */
    @Bean(name = KYO_GO_FINDER_STEP)
    public Step kyoGoFinderStep() {
        return stepBuilderFactory.get(KYO_GO_FINDER_STEP).tasklet(twitterTasklet).build();
    }

    /**
     * Jobのbean.
     *
     * @param kyoGoFinderStep Stepインスタンス
     * @return Job
     */
    @Bean(name = JOB)
    public Job job(@Qualifier(KYO_GO_FINDER_STEP) final Step kyoGoFinderStep) {

        return jobBuilderFactory
                .get(JOB)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(kyoGoFinderStep)
                .build();
    }

}
