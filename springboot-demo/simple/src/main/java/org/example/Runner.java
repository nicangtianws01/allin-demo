package org.example;

import io.lettuce.core.RedisBusyException;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.DemoEntity;
import org.example.service.DemoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@EnableCaching
@EnableScheduling
@Slf4j
@SpringBootApplication
public class Runner implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }

    @Resource
    private DataSourceTransactionManager transactionManager;

    @Resource
    private TransactionDefinition transactionDefinition;

    @Resource
    private DemoService service;

    @Override
    public void run(String... args) {
        log.info("application start");
//        List<DemoEntity> list = new ArrayList<>();
//
//        try (
//                InputStream inputStream = Files.newInputStream(Paths.get("D:\\tmp\\MOCK_DATA.csv"));
//                Reader reader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(reader);
//        ) {
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                String[] split = line.split(",");
//                if (split.length < 2 || "name".equals(split[0])) {
//                    log.warn("该数据无效：{}", line);
//                    continue;
//                }
//                DemoEntity demoEntity = new DemoEntity().setName(split[0]).setValue(split[1]);
//                list.add(demoEntity);
//            }
//
//            int taskCount = list.size() / 200 + 1;
//
//            List<List<DemoEntity>> partition = new ArrayList<>();
//            for (int i = 0; i < taskCount; i++) {
//                List<DemoEntity> collect = list.stream().skip(i * 200L).limit(200).collect(Collectors.toList());
//                if (!collect.isEmpty()) {
//                    partition.add(collect);
//                }
//            }
//            CyclicBarrier cyclicBarrier = new CyclicBarrier(partition.size());
//            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
//
//            ExecutorService executorService = Executors.newFixedThreadPool(partition.size());
//
//            CompletableFuture.allOf(partition.stream()
//                    .map(entities -> CompletableFuture.runAsync(() -> {
//                        // 获取事务状态 开启事务
//                        TransactionStatus status = transactionManager.getTransaction(transactionDefinition);
//                        try {
//                            // 这里加判断 如果前面有任务报错 后续的任务就没必要继续执行了，直接去同步点等待事务回滚。
//                            if (!atomicBoolean.get()) {
//                                // 这里的入库是用的mybatisPlus的savebatch，直接this调自身的Transactional注解不生效
//                                service.saveBatch(entities);
//                            }
//                        } catch (Exception e) {
//                            log.error("{}", e.getMessage());
//                            atomicBoolean.getAndSet(true);
//                        }
//                        // 等待其他线程到达同步点
//                        try {
//                            cyclicBarrier.await();
//                        } catch (Exception e) {
//                            log.error("{}", e.getMessage());
//                        }
//                        // 判断 事务是提交 还是 回滚
//                        if (atomicBoolean.get()) {
//                            transactionManager.rollback(status);
//                        } else {
//                            transactionManager.commit(status);
//                            log.info("success");
//                        }
//                        log.info("{} complete", Thread.currentThread().getName());
//                    }, executorService)).toArray(CompletableFuture[]::new)).join();
//            log.info("all complete");
//        } catch (IOException e) {
//            log.error("{}", e.getMessage());
//        }
    }
}
