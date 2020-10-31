package com.netsia.bbsl.core.fsm.olt.config;

import com.netsia.bbsl.core.fsm.olt.actions.DeleteDeviceFromDatabase;
import com.netsia.bbsl.core.fsm.olt.actions.DeleteDeviceFromVoltha;
import com.netsia.bbsl.core.fsm.olt.actions.DisableDeviceinVoltha;
import com.netsia.bbsl.core.fsm.action.retry.ResetRetryCounterAction;
import com.netsia.bbsl.core.fsm.action.retry.RetryCheckAction;
import com.netsia.bbsl.core.fsm.action.retry.TimeoutDeleteOltAction;
import com.netsia.bbsl.core.fsm.olt.enums.OltEvents;
import com.netsia.bbsl.core.fsm.olt.listener.OltMachineListener;
import com.netsia.bbsl.core.fsm.olt.enums.OltStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;

@Configuration
@EnableStateMachineFactory(name = "deleteOltFactory")
public class DeleteOltMachineConfiguration extends EnumStateMachineConfigurerAdapter<OltStates, OltEvents> {

    private static final int DELETE_OLT_MACHINE_TIMEOUT_DURATION = 5000; // Timeout duration in milliseconds

    @Autowired
    private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineStateConfigurer<OltStates, OltEvents> states) throws Exception {
        states.withStates()
                .initial(OltStates.STARTED_DELETE)
                .state(OltStates.DISABLED, new DisableDeviceinVoltha(), new ResetRetryCounterAction())
                .state(OltStates.DELETED_FROM_VOLTHA, new DeleteDeviceFromVoltha(), new ResetRetryCounterAction())
                .state(OltStates.DELETED_FROM_DB, new DeleteDeviceFromDatabase(), new ResetRetryCounterAction())
                .state(OltStates.TIMEOUT, new TimeoutDeleteOltAction(), new ResetRetryCounterAction())
                .end(OltStates.DONE);
    }
    /* a
    block
    comment
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OltStates, OltEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(OltStates.STARTED_DELETE)
                .target(OltStates.DISABLED)
                .event(OltEvents.OLT_DELETE_REQUEST)

                .and()
                .withExternal()
                .source(OltStates.DISABLED)
                .target(OltStates.DELETED_FROM_VOLTHA)
                .event(OltEvents.OLT_DISABLED)

                .and()
                .withExternal()
                .source(OltStates.DELETED_FROM_VOLTHA)
                .target(OltStates.DELETED_FROM_DB)
                .event(OltEvents.OLT_DELETED_VOLTHA)

                //2 line comment1
                //2 line comment2
                .and()
                .withExternal()
                .source(OltStates.DELETED_FROM_DB)
                .target(OltStates.DONE)
                .event(OltEvents.OLT_DELETE_DONE)

                .and()
                .withExternal()
                .source(OltStates.DELETED_FROM_VOLTHA)
                .target(OltStates.DELETED_FROM_VOLTHA)
                .event(OltEvents.OLT_DELETE_REQUEST)

                .and()
                .withExternal()
                .source(OltStates.DELETED_FROM_DB)
                .target(OltStates.DELETED_FROM_DB)
                .event(OltEvents.OLT_DELETE_REQUEST)

                .and()

                .withExternal()
                .source(OltStates.DISABLED)
                .target(OltStates.TIMEOUT)
                .event(OltEvents.TIMEOUT)

                .and()

                .withExternal()
                .source(OltStates.DELETED_FROM_VOLTHA)
                .target(OltStates.TIMEOUT)
                .event(OltEvents.TIMEOUT)

                .and()

                .withExternal()
                .source(OltStates.DELETED_FROM_DB)
                .target(OltStates.TIMEOUT)
                .event(OltEvents.TIMEOUT)

                .and()

                .withInternal()
                .source(OltStates.DISABLED)
                .action(new RetryCheckAction())
                .action(new DisableDeviceinVoltha())
                .timer(DELETE_OLT_MACHINE_TIMEOUT_DURATION)

                .and()

                .withInternal()
                .source(OltStates.DELETED_FROM_VOLTHA)
                .action(new RetryCheckAction())
                .action(new DeleteDeviceFromVoltha())
                .timer(DELETE_OLT_MACHINE_TIMEOUT_DURATION)

                .and()

                .withInternal()
                .source(OltStates.DELETED_FROM_DB)
                .action(new RetryCheckAction())
                .action(new DeleteDeviceFromDatabase())
                .timer(DELETE_OLT_MACHINE_TIMEOUT_DURATION)

                .and()
                .withInternal()
                .source(OltStates.TIMEOUT)
                .action(new RetryCheckAction())
                .action(new TimeoutDeleteOltAction())
                .timer(DELETE_OLT_MACHINE_TIMEOUT_DURATION)

                .and()

                .withExternal()
                .source(OltStates.TIMEOUT)
                .target(OltStates.DELETED_FROM_VOLTHA)
                .event(OltEvents.OLT_DISABLED)

                .and()

                .withExternal()
                .source(OltStates.TIMEOUT)
                .target(OltStates.DELETED_FROM_DB)
                .event(OltEvents.OLT_DELETED_VOLTHA)

                .and()

                .withExternal()
                .source(OltStates.TIMEOUT)
                .target(OltStates.DONE)
                .event(OltEvents.OLT_DELETE_DONE)
        ;
    }

    @Bean
    @Qualifier("deleteOltPersister")
    public StateMachineRuntimePersister<OltStates, OltEvents, ?> stateMachineRuntimePersisterOltDelete() {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }


    @Override
    public void configure(StateMachineConfigurationConfigurer<OltStates, OltEvents> config) throws Exception {
        config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersisterOltDelete())
                .and()
                .withConfiguration().listener(new OltMachineListener());
    }

}
