package com.bsep.smart.home.cronjobs;

import com.bsep.smart.home.model.Device;
import com.bsep.smart.home.model.Log;
import com.bsep.smart.home.mongorepository.LogRepository;
import com.bsep.smart.home.repository.DeviceRepository;
import com.bsep.smart.home.rules.CheckDeviceRules;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProcessingCronJob {
    private final DeviceRepository deviceRepository;
    private final LogRepository logRepository;
    private final CheckDeviceRules checkDeviceRules;
    Logger logger = LoggerFactory.getLogger(MessageProcessingCronJob.class);


    @Scheduled(fixedRate = 1000)
    public void processMessagesForDevices() {
        List<Device> devices = deviceRepository.findAll();
        for (Device device : devices) {
            if (isReadPeriodElapsed(device)) {
                processDeviceMessages(device);
                resetLastProcessTime(device);
            }
        }
    }

    @Transactional
    public void processDeviceMessages(Device device) {
        List<Log> logs = logRepository.getLogsByRegexAndPropertyIdAndDeviceIdAndNotProcessed(Pattern.compile(device.getMessageRegex()), String.valueOf(device.getProperty().getId()), String.valueOf(device.getId()));
        logs.forEach(log -> {
            checkDeviceRules.execute(device);
            logger.info("Log with message { " + log.getMessage() + " } has been processed");
            log.setProcessed(true);
        });
        logRepository.saveAll(logs);
    }

    private boolean isReadPeriodElapsed(Device device) {
        long readPeriod = device.getReadPeriod();
        return Objects.isNull(device.getLastLogged()) || Duration.between(device.getLastLogged(), LocalDateTime.now()).toMinutes() >= readPeriod;
    }

    public void resetLastProcessTime(Device device) {
        device.setLastLogged(LocalDateTime.now());
        deviceRepository.save(device);
    }

}
